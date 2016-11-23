package com.base.engine.rendering;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.components.PointLight;
import com.base.engine.components.SpotLight;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;
import com.base.engine.core.Util;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.resourceManagement.ShaderResource;

public class Shader {
	private ShaderResource resource;
	
	private static HashMap<String, ShaderResource> loadedShaders = new HashMap<String, ShaderResource>();
	private String fileName;
	
	public Shader(String fileName) {
		this.fileName = fileName;
		
		ShaderResource oldResource = loadedShaders.get(fileName);
		if (oldResource != null) {
			resource = oldResource;
			resource.addReference();
		} else {
			resource = new ShaderResource();
			
			String vertexShaderText = loadShader(fileName + ".vs");
			String fragmentShaderText = loadShader(fileName + ".fs");
			addVertexShader(vertexShaderText);
			addFragmentShader(fragmentShaderText);
			
			addAllAttributes(vertexShaderText);
			
			compileShader();
			
			addAllUniforms(vertexShaderText);
			addAllUniforms(fragmentShaderText);
			
			loadedShaders.put(fileName, resource);
		}
	}
	
	public void addVertexShader(String text) {
		addProgram(text, GL_VERTEX_SHADER);
	}
	
	public void addGeometryShader(String text) {
		addProgram(text, GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShader(String text) {
		addProgram(text, GL_FRAGMENT_SHADER);
	}
	
	public void addVertexShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_VERTEX_SHADER);
	}
	
	public void addGeometryShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_GEOMETRY_SHADER);
	}
	
	public void addFragmentShaderFromFile(String fileName) {
		addProgram(loadShader(fileName), GL_FRAGMENT_SHADER);
	}
	
	public void setAttribLocation(String attributeName, int location) {
		glBindAttribLocation(resource.getProgram(), location, attributeName);
	}
	
	@SuppressWarnings("deprecation")
	public void compileShader() {
		glLinkProgram(resource.getProgram());
		
		if (glGetProgram(resource.getProgram(), GL_LINK_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
		
		glValidateProgram(resource.getProgram());
		
		if (glGetProgram(resource.getProgram(), GL_VALIDATE_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(resource.getProgram(), 1024));
			System.exit(1);
		}
	}
	
	public void bind() {
		glUseProgram(resource.getProgram());
	}
	
	public void updateUniforms(Transform transform, Material material,
			RenderingEngine renderingEngine) {
		// R_ : rendering engine
		// T_ : transform
		// C_ : camera
		// sampler2D is special
		// no prefix : material
		
		for (int i = 0; i < resource.getUniformNames().size(); i++) {
			String uniformName = resource.getUniformNames().get(i);
			String uniformType = resource.getUniformTypes().get(i);
			
			String unprefixedUniformName = uniformName.substring(2);
			
			if (uniformType.equals("sampler2D")) {
				
				int samplerSlot = renderingEngine.getSamplerSlot(uniformName);
				material.getTexture(uniformName).bind(samplerSlot);
				setUniformi(uniformName, samplerSlot);
			} else if (uniformName.startsWith("T_")) {
				Matrix4f worldMatrix = transform.getTransformation();
				if (uniformName.equals("T_MVP")) {
					Matrix4f projectedMatrix = renderingEngine.getMainCamera().getViewProjection().mul(worldMatrix);
					setUniform(uniformName, projectedMatrix);
				} else if (uniformName.equals("T_model")) {
					setUniform(uniformName, worldMatrix);
				} else {
					throw new IllegalArgumentException(uniformName + "," +
										uniformType + " for transform ");
				}
			} else if (uniformName.startsWith("R_")) {
				
				if (uniformType.equals("vec3")) {
					setUniform(uniformName, renderingEngine.getVector3f(unprefixedUniformName));
				} else if (uniformType.equals("float")) {
					setUniformf(uniformName, renderingEngine.getFloat(unprefixedUniformName));
				} else if (uniformType.equals("DirectionalLight")) {
					setUniformDirectionalLight(uniformName,
							(DirectionalLight) renderingEngine.getActiveLight());
				} else if (uniformType.equals("PointLight")) {
					setUniformPointLight(uniformName,
							(PointLight) renderingEngine.getActiveLight());
				} else if (uniformType.equals("SpotLight")) {
					setUniformSpotLight(uniformName,
							(SpotLight) renderingEngine.getActiveLight());
				} else {
					renderingEngine.updateUniformStruct(transform, material, this,
							uniformName, uniformType);
				}
			} else if (uniformName.startsWith("C_")) {
				if (uniformName.equals("C_eyePos")) {
					setUniform(uniformName,
							renderingEngine.getMainCamera().getTransform().getTransformedPos());
				} else {
					throw new IllegalArgumentException(uniformName + "," +
											uniformType + " for camera ");
				}
			} else {
				if (uniformType.equals("vec3")) {
					setUniform(uniformName, material.getVector3f(uniformName));
				} else if (uniformType.equals("float")) {
					setUniformf(uniformName, material.getFloat(uniformName));
				} else {
					throw new IllegalArgumentException(uniformName + "," +
							uniformType + " for material ");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void addProgram(String text, int type) {
		int shader = glCreateShader(type);
		
		if (shader == 0) {
			System.err.println("Shader creation failed: could not find valid memory location when adding shader");
			System.exit(1);
		}
		
		glShaderSource(shader, text);
		glCompileShader(shader);
		
		if (glGetShader(shader, GL_COMPILE_STATUS) == 0) {
			System.err.println(glGetShaderInfoLog(shader, 1024));
			System.exit(1);
		}
		
		glAttachShader(resource.getProgram(), shader);
	}
	
	private class GLSLStruct {
		public String name;
		public String type;
	}
	
	private HashMap<String, List<GLSLStruct>> findUniformStructs(String shaderText) {
		HashMap<String, List<GLSLStruct>> result = new HashMap<String, List<GLSLStruct>>();
		final String STRUCT_KEYWORD = "struct";
		int structStartLocation = shaderText.indexOf(STRUCT_KEYWORD);
		
		while (structStartLocation != -1) {
			
			if(!(structStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(structStartLocation - 1)) || shaderText.charAt(structStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(structStartLocation + STRUCT_KEYWORD.length())))) {
				structStartLocation = shaderText.indexOf(STRUCT_KEYWORD, structStartLocation + STRUCT_KEYWORD.length());
				continue;
			}
			
			int nameBegin = structStartLocation + STRUCT_KEYWORD.length() + 1;
			int braceBegin = shaderText.indexOf("{", nameBegin);
			int braceEnd = shaderText.indexOf("}", braceBegin);
			// int end = shaderText.indexOf(";", begin);
			
			// String structLine = shaderText.substring(begin, end);
			
			String structName = shaderText.substring(nameBegin, braceBegin).trim();
			
			ArrayList<GLSLStruct> glslStructs = new ArrayList<GLSLStruct>();
			
			int componentSemicolonPos = shaderText.indexOf(";", braceBegin);
			while (componentSemicolonPos != -1 && componentSemicolonPos < braceEnd) {
				
				int componentNameEnd = componentSemicolonPos + 1;

				while(Character.isWhitespace(shaderText.charAt(componentNameEnd - 1)) || shaderText.charAt(componentNameEnd - 1) == ';')
					componentNameEnd--;

				int componentNameStart = componentSemicolonPos;

				while(!Character.isWhitespace(shaderText.charAt(componentNameStart - 1)))
					componentNameStart--;

				int componentTypeEnd = componentNameStart;

				while(Character.isWhitespace(shaderText.charAt(componentTypeEnd - 1)))
					componentTypeEnd--;

				int componentTypeStart = componentTypeEnd;

				while(!Character.isWhitespace(shaderText.charAt(componentTypeStart - 1)))
					componentTypeStart--;

				String componentName = shaderText.substring(componentNameStart, componentNameEnd);
				String componentType = shaderText.substring(componentTypeStart, componentTypeEnd);

				GLSLStruct glslStruct = new GLSLStruct();
				glslStruct.name = componentName;
				glslStruct.type = componentType;

				glslStructs.add(glslStruct);

				componentSemicolonPos = shaderText.indexOf(";", componentSemicolonPos + 1);
			}
			
			result.put(structName, glslStructs);
			
			structStartLocation = shaderText.indexOf(STRUCT_KEYWORD,
					structStartLocation + STRUCT_KEYWORD.length());
		}
		
		return result;
	}
	
	public void addAllUniforms(String shaderText) {
		
		HashMap<String, List<GLSLStruct>> structs = findUniformStructs(shaderText);
		
		final String UNIFORM_KEYWORD = "uniform";
		int uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD);
		
		while (uniformStartLocation != -1) {
			
			if(!(uniformStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(uniformStartLocation - 1)) || shaderText.charAt(uniformStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(uniformStartLocation + UNIFORM_KEYWORD.length())))) {
				uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD, uniformStartLocation + UNIFORM_KEYWORD.length());
				continue;
			}
			
			int begin = uniformStartLocation + UNIFORM_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);
			
			String uniformLine = shaderText.substring(begin, end).trim();
			
			int whiteSpacePos = uniformLine.indexOf(' ');
			String uniformName = uniformLine.substring(uniformLine.indexOf(' ') + 1,
					uniformLine.length()).trim();
			String uniformType = uniformLine.substring(0, whiteSpacePos).trim();
			
			resource.getUniformNames().add(uniformName);
			resource.getUniformTypes().add(uniformType);
			addUniform(uniformName, uniformType, structs);
			
			uniformStartLocation = shaderText.indexOf(UNIFORM_KEYWORD,
					uniformStartLocation + UNIFORM_KEYWORD.length());
		}
	}
	
	private void addUniform(String uniformName, String uniformType,
			HashMap<String, List<GLSLStruct>> structs) {
		boolean addThis = true;
		// System.out.println("get components for " + uniformType);
		List<GLSLStruct> structComponents = structs.get(uniformType);
		
		if (structComponents != null) {
			addThis = false;
			
			for (GLSLStruct struct : structComponents) {
				addUniform(uniformName + "." + struct.name,
						struct.type, structs);
			}
		}
		
		if (addThis) {
			
			int uniformLocation = glGetUniformLocation(resource.getProgram(), uniformName);
			
			if (uniformLocation == 0xFFFFFFFF) {
				System.err.println("Error: could not find uniform " + uniformName);
				new Exception().printStackTrace();
				System.exit(1);
			}
			
			resource.getUniforms().put(uniformName, uniformLocation);
		}
	}
	
	public void addAllAttributes(String shaderText) {
		final String ATTRIBUTE_KEYWORD = "attribute";
		int attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD);
		int attribNumber = 0;
		
		while (attributeStartLocation != -1) {
			
			if(!(attributeStartLocation != 0
					&& (Character.isWhitespace(shaderText.charAt(attributeStartLocation - 1)) || shaderText.charAt(attributeStartLocation - 1) == ';')
					&& Character.isWhitespace(shaderText.charAt(attributeStartLocation + ATTRIBUTE_KEYWORD.length())))) {
						attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD, attributeStartLocation + ATTRIBUTE_KEYWORD.length());
						continue;
						
				}
			
			int begin = attributeStartLocation + ATTRIBUTE_KEYWORD.length() + 1;
			int end = shaderText.indexOf(";", begin);
			
			String attributeLine = shaderText.substring(begin, end).trim();
			
			String attributeName = attributeLine.substring(attributeLine.indexOf(' ') + 1,
					attributeLine.length()).trim();
			
			setAttribLocation(attributeName, attribNumber++);
			
			attributeStartLocation = shaderText.indexOf(ATTRIBUTE_KEYWORD,
					attributeStartLocation + ATTRIBUTE_KEYWORD.length());
		}
	}
	
	public void setUniformi(String uniformName, int value) {
		glUniform1i(resource.getUniforms().get(uniformName), value);
	}
	
	public void setUniformf(String uniformName, float value) {
		glUniform1f(resource.getUniforms().get(uniformName), value);
	}
	
	public void setUniform(String uniformName, Vector3f value) {
		glUniform3f(resource.getUniforms().get(uniformName), value.getX(), value.getY(), value.getZ());
	}
	
	public void setUniform(String uniformName, Matrix4f value) {
		glUniformMatrix4(resource.getUniforms().get(uniformName), true,
				Util.createFlippedBuffer(value));
	}
	
	public String loadShader(String fileName) {
		StringBuilder shaderSource = new StringBuilder();
		BufferedReader shaderReader = null;
		final String INCLUDE_DIRECTIVE = "#include";

		try {
			shaderReader = new BufferedReader(new FileReader("./res/shaders/" + fileName));
			
			String line;
			
			while ((line = shaderReader.readLine()) != null) {
				if (line.startsWith(INCLUDE_DIRECTIVE)) {
					shaderSource.append(loadShader(line.substring(INCLUDE_DIRECTIVE.length() + 2, line.length() - 1)));
					// only space between include and file name
				} else {
					shaderSource.append(line).append("\n");
				}
			}
			
			shaderReader.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return shaderSource.toString();
	}
	
	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformBaseLight(uniformName + ".base", (BaseLight) directionalLight);
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
	
	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}
	
	private void setUniformPointLight(String uniformName, PointLight pointLight) {
		setUniformBaseLight(uniformName + ".base", (BaseLight) pointLight);
		setUniformf(uniformName + ".atten.constant", pointLight.getAttenuation().getConstant());
		setUniformf(uniformName + ".atten.linear", pointLight.getAttenuation().getLinear());
		setUniformf(uniformName + ".atten.exponent", pointLight.getAttenuation().getExponent());
		setUniform(uniformName + ".position", pointLight.getTransform().getTransformedPos());
		setUniformf(uniformName + ".range", pointLight.getRange());
	}
	
	private void setUniformSpotLight(String uniformName, SpotLight spotLight) {
		setUniformPointLight(uniformName + ".pointLight", (PointLight) spotLight);
		setUniform(uniformName + ".direction", spotLight.getDirection());
		setUniformf(uniformName + ".cutoff", spotLight.getCutoff());
	}
	
	// Should not need to use hashmap
	@Override
	protected void finalize() {
		if (resource.removeReference() && !fileName.isEmpty()) {
			loadedShaders.remove(fileName);
		}
	}
}
