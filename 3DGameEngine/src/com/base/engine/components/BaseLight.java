package com.base.engine.components;

import com.base.engine.core.Transform;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Shader;

public class BaseLight extends GameComponent {
	private Vector3f color;
	private float intensity;
	private Shader shader;
	private Transform transform;
	
	public BaseLight(Vector3f color, float intensity) {
		this.color = color;
		this.intensity = intensity;
	}
	
	protected void setShader(Shader shader) {
		this.shader = shader;
	}
	
	public Shader getShader() {
		return this.shader;
	}

	public Vector3f getColor() {
		return color;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}

	public float getIntensity() {
		return intensity;
	}

	public void setIntensity(float intensity) {
		this.intensity = intensity;
	}
	
	@Override
	public void addToRenderingEngine(RenderingEngine renderingEngine) {
		renderingEngine.addLight(this);
	}
}
