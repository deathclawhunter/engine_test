package com.base.engine.rendering;

import com.base.engine.core.Transform;

public class ForwardAmbient extends Shader {
	private static ForwardAmbient instance;
	
	private ForwardAmbient() {
		super("forward-ambient");
	}
	
	public static ForwardAmbient getInstance() {
		if (instance != null) {
			return instance;
		}
		
		instance = new ForwardAmbient();
		
		return instance;
	}
	
	public void updateUniforms(Transform transform, Material material, RenderingEngine renderingEngine) {
		
		super.updateUniforms(transform, material, renderingEngine);
	}
}
