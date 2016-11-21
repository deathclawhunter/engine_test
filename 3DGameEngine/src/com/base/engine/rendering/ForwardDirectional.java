package com.base.engine.rendering;

import com.base.engine.components.BaseLight;
import com.base.engine.components.DirectionalLight;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;

public class ForwardDirectional extends Shader {
	private static ForwardDirectional instance;
	
	private ForwardDirectional() {
		super();
		addVertexShaderFromFile("forward-directional.vs");
		addFragmentShaderFromFile("forward-directional.fs");
		
		setAttribLocation("position", 0);
		setAttribLocation("texCoord", 1);
		setAttribLocation("normal", 2);
		
		compileShader();
		addUniform("model");
		addUniform("MVP");
		
		addUniform("specularIntensity");
		addUniform("specularPower");
		addUniform("eyePos");
		
		addUniform("directionalLight.base.color");
		addUniform("directionalLight.base.intensity");
		addUniform("directionalLight.direction");
	}
	
	public static ForwardDirectional getInstance() {
		if (instance != null) {
			return instance;
		}
		
		instance = new ForwardDirectional();
		
		return instance;
	}
	
	public void updateUniforms(Transform transform, Material material, RenderingEngine renderingEngine) {
		
		Matrix4f worldMatrix = transform.getTransformation();
		Matrix4f projectedMatrix = renderingEngine.getMainCamera().getViewProjection().mul(worldMatrix);
		
		material.getTexture("diffuse").bind();
		setUniform("model", worldMatrix);
		setUniform("MVP", projectedMatrix);
		
		setUniformf("specularIntensity", material.getFloat("specularIntensity"));
		setUniformf("specularPower", material.getFloat("specularPower"));
		
		setUniform("eyePos", renderingEngine.getMainCamera().getTransform().getTransformedPos());
		
		setUniformDirectionalLight("directionalLight", (DirectionalLight) renderingEngine.getActiveLight());
	}
	
	public void setUniformDirectionalLight(String uniformName, DirectionalLight directionalLight) {
		setUniformBaseLight(uniformName + ".base", (BaseLight) directionalLight);
		setUniform(uniformName + ".direction", directionalLight.getDirection());
	}
	
	public void setUniformBaseLight(String uniformName, BaseLight baseLight) {
		setUniform(uniformName + ".color", baseLight.getColor());
		setUniformf(uniformName + ".intensity", baseLight.getIntensity());
	}
}
