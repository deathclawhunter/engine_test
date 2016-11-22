package com.base.engine.rendering;

import java.util.HashMap;

import com.base.engine.rendering.resourceManagement.MappedValues;

public class Material extends MappedValues {
	private HashMap<String, Texture> textureHashMap;
	
	public Material() {
		super();
		
		textureHashMap = new HashMap<String, Texture>();
	}
	
	public void addTexture(String name, Texture texture) {
		textureHashMap.put(name, texture);
	}
	
	public Texture getTexture(String name) {
		return textureHashMap.get(name);
	}
}
