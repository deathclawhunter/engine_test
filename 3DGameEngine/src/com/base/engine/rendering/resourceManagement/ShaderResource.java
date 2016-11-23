package com.base.engine.rendering.resourceManagement;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glCreateProgram;

import java.util.ArrayList;
import java.util.HashMap;

public class ShaderResource {
	private int program;
	private int refCount;
	
	private HashMap<String, Integer> uniforms;
	private ArrayList<String> uniformNames; // Change into HashMap
	private ArrayList<String> uniformTypes;
	
	public ShaderResource() {
		this.program = glCreateProgram();
		refCount = 1;
		
		if (program == 0) {
			System.err.println("Shader creation failed: could not find valid memory location in constructor");
			System.exit(1);
		}
		
		uniforms = new HashMap<String, Integer>();
		uniformNames = new ArrayList<String>();
		uniformTypes = new ArrayList<String>();
	}
	
	@Override
	protected void finalize() {
		glDeleteBuffers(program);
	}

	public int getProgram() {
		return this.program;
	}
	
	public void addReference() {
		refCount++;
	}
	
	public boolean removeReference() {
		refCount--;
		
		return refCount == 0;
	}

	public HashMap<String, Integer> getUniforms() {
		return uniforms;
	}

	public void setUniforms(HashMap<String, Integer> uniforms) {
		this.uniforms = uniforms;
	}

	public ArrayList<String> getUniformNames() {
		return uniformNames;
	}

	public void setUniformNames(ArrayList<String> uniformNames) {
		this.uniformNames = uniformNames;
	}

	public ArrayList<String> getUniformTypes() {
		return uniformTypes;
	}

	public void setUniformTypes(ArrayList<String> uniformTypes) {
		this.uniformTypes = uniformTypes;
	}
}
