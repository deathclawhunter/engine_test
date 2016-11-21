package com.base.engine.rendering;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Transform;

public class ForwardAmbient extends Shader {
	private static ForwardAmbient instance;
	
	private ForwardAmbient() {
		super();
		addVertexShaderFromFile("forward-ambient.vs");
		addFragmentShaderFromFile("forward-ambient.fs");
		
		setAttribLocation("position", 0);
		setAttribLocation("texCoord", 1);
		
		compileShader();
		addUniform("MVP");
		addUniform("ambientIntensity");
	}
	
	public static ForwardAmbient getInstance() {
		if (instance != null) {
			return instance;
		}
		
		instance = new ForwardAmbient();
		
		return instance;
	}
	
	public void updateUniforms(Transform transform, Material material, RenderingEngine renderingEngine) {
		
		Matrix4f worldMatrix = transform.getTransformation();
		Matrix4f projectedMatrix = renderingEngine.getMainCamera().getViewProjection().mul(worldMatrix);
		
		material.getTexture("diffuse").bind();
		setUniform("MVP", projectedMatrix);
		setUniform("ambientIntensity", renderingEngine.getAmbientLight());
	}
}
