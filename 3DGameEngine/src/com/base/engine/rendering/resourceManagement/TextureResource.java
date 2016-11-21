package com.base.engine.rendering.resourceManagement;

import static org.lwjgl.opengl.GL15.glDeleteBuffers;

public class TextureResource {
	private int id;
	private int refCount;
	
	public TextureResource(int id) {
		this.id = id;
		refCount = 1;
	}
	
	@Override
	protected void finalize() {
		glDeleteBuffers(id);
	}

	public int getId() {
		return this.id;
	}
	
	public void addReference() {
		refCount++;
	}
	
	public boolean removeReference() {
		refCount--;
		
		return refCount == 0;
	}
}
