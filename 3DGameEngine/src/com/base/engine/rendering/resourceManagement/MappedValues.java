package com.base.engine.rendering.resourceManagement;

import java.util.HashMap;

import com.base.engine.core.Vector3f;

public abstract class MappedValues {
	private HashMap<String, Vector3f> vectorHashMap = new HashMap<String, Vector3f>();
	private HashMap<String, Float> floatHashMap = new HashMap<String, Float>();
	
	public void addVector3f(String name, Vector3f vector3f) {
		vectorHashMap.put(name, vector3f);
	}
	
	public void addFloat(String name, Float floatValue) {
		floatHashMap.put(name, floatValue);
	}
	
	public Vector3f getVector3f(String name) {
		return vectorHashMap.get(name);
	}
	
	public float getFloat(String name) {
		return floatHashMap.get(name);
	}
}
