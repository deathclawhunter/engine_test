package com.base.engine.core;

public class Transform {

	private Transform parent;
	private Matrix4f parentMatrix;
	
	private Vector3f pos;
	private Quaternion rot;
	private Vector3f scale;
	
	private Vector3f oldPos;
	private Quaternion oldRot;
	private Vector3f oldScale;
	
	public Transform() {
		pos = new Vector3f(0, 0, 0);
		rot = new Quaternion(0, 0, 0, 1);
		scale = new Vector3f(1, 1, 1);
		
		// TODO: this can be changed in runtime
		parentMatrix = new Matrix4f().initIdentity();
	}
	
	public boolean hasChanged() {
		
		if (oldPos == null) {
			oldPos = new Vector3f(0, 0, 0).set(pos);;
			oldRot = new Quaternion(0, 0, 0, 0).set(rot);
			oldScale = new Vector3f(0, 0, 0).set(scale);
			return true;
		}
		
		if (parent != null && parent.hasChanged()) {
			return true;
		}
		
		if (!pos.equals(oldPos)) {
			return true;
		}
		
		if (!rot.equals(oldRot)) {
			return true;
		}
		
		if (!scale.equals(oldScale)) {
			return true;
		}
		
		return false;
	}
	
	public Matrix4f getTransformation() {
		Matrix4f translation_ = new Matrix4f().initTranslation(
					pos.getX(), pos.getY(), pos.getZ());
		Matrix4f rotation_ = rot.toRotationMatrix();
		Matrix4f scale_ = new Matrix4f().initScale(scale.getX(),
				scale.getY(), scale.getZ());
		
		if (oldPos != null) {
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		}
		
		return getParentMatrix().mul(translation_.mul(rotation_.mul(scale_)));
	}
	
	private Matrix4f getParentMatrix() {
		if (parent != null && parent.hasChanged()) {
			parentMatrix = parent.getTransformation();
		}
		
		return parentMatrix;
	}
	
	public Vector3f getTransformedPos() {
		return getParentMatrix().transform(pos);
	}
	
	public void setParent(Transform parent) {
		this.parent = parent;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}
	
	public void setPos(float x, float y, float z) {
		this.pos = new Vector3f(x, y, z);
	}

	public Quaternion getRot() {
		return rot;
	}

	public void setRot(Quaternion rot) {
		this.rot = rot;
	}

	public Vector3f getScale() {
		return scale;
	}

	public void setScale(Vector3f scale) {
		this.scale = scale;
	}
	
	public void setScale(float x, float y, float z) {
		this.scale = new Vector3f(x, y, z);
	}
}
