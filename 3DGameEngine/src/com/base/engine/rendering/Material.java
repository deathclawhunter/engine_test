package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.core.Vector3f;

public class Material {
	private HashMap<String, Texture> textureHashMap;
	private HashMap<String, Vector3f> vectorHashMap;
	private HashMap<String, Float> floatHashMap;
	
	public Material() {
		textureHashMap = new HashMap<String, Texture>();
		vectorHashMap = new HashMap<String, Vector3f>();
		floatHashMap = new HashMap<String, Float>();
	}
	
	public void addTexture(String name, Texture texture) {
		textureHashMap.put(name, texture);
	}
	
	public void addVector3f(String name, Vector3f vector3f) {
		vectorHashMap.put(name, vector3f);
	}
	
	public void addFloat(String name, Float floatValue) {
		floatHashMap.put(name, floatValue);
	}
	
	public Texture getTexture(String name) {
		return textureHashMap.get(name);
	}
	
	public Vector3f getVector3f(String name) {
		return vectorHashMap.get(name);
	}
	
	public float getFloat(String name) {
		return floatHashMap.get(name);
	}
}
