package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Matrix4f;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.RenderingEngine;
import com.base.engine.rendering.Window;

public class Camera extends GameComponent {
	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	
//	private Vector3f pos;
//	private Vector3f forward;
//	private Vector3f up;
	private Matrix4f projection;
	
	private boolean mouseLocked = false;
	private float sensitivity = 0.5f;
	private Vector2f centerPosition = new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2);
	
	public Camera(float fov, float aspect, float zNear, float zFar) {
//		this.pos = new Vector3f(0, 0, 0);
//		this.forward = new Vector3f(0, 0, 1).normalized();
//		this.up = new Vector3f(0, 1, 0).normalized();
		this.projection = new Matrix4f().initPerspective(fov, aspect, zNear, zFar);
	}
	
	public Matrix4f getViewProjection() {
		Matrix4f cameraRotation = getTransform().getTransformedRot().conjugate().toRotationMatrix();
		Vector3f cameraPos = getTransform().getTransformedPos().mul(-1);
		Matrix4f cameraTranslation = new Matrix4f().initTranslation(
				cameraPos.getX(),
				cameraPos.getY(),
				cameraPos.getZ());
		
		return projection.mul(cameraRotation.mul(cameraTranslation));
	}
	
	@Override
	public void input(float delta) {
		float movAmt = (float) (10 * delta);
		float rotAmt = (float) (100 * delta);
		
		if(Input.getKey(Input.KEY_ESCAPE)) {
			Input.setCursor(true);
			mouseLocked = false;
		} else if(Input.getMouseDown(0)) {
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked = true;
		} else if (Input.getKey(Input.KEY_W)) {
			move(getTransform().getRot().getForward(), movAmt);
		} else if (Input.getKey(Input.KEY_S)) {
			move(getTransform().getRot().getForward(), -movAmt);
		} else if (Input.getKey(Input.KEY_A)) {
			move(getTransform().getRot().getLeft(), movAmt);
		} else if (Input.getKey(Input.KEY_D)) {
			move(getTransform().getRot().getRight(), movAmt);
		} else if (Input.getKey(Input.KEY_DOWN)) {
			getTransform().rotate(getTransform().getRot().getRight(),
					(float) Math.toRadians(rotAmt));
		} else if (Input.getKey(Input.KEY_UP)) {
			getTransform().rotate(getTransform().getRot().getRight(),
					(float) Math.toRadians(-rotAmt));
		} else if (Input.getKey(Input.KEY_RIGHT)) {
			getTransform().setRot(getTransform().getRot().mul(new Quaternion(yAxis,
					(float) Math.toRadians(rotAmt))).normalized());
		} else if (Input.getKey(Input.KEY_LEFT)) {
			getTransform().setRot(getTransform().getRot().mul(new Quaternion(yAxis,
					(float) Math.toRadians(-rotAmt))).normalized());
		}
		
		if (mouseLocked) {
			Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
			
			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;
			
			if (rotY) {
				getTransform().rotate(yAxis, (float) Math.toRadians(deltaPos.getX() * sensitivity));
			} else if (rotX) {
				getTransform().rotate(getTransform().getRot().getRight(),
						(float) Math.toRadians(-deltaPos.getY() * sensitivity));
			}
			
			if (rotY || rotX) {
				Input.setMousePosition(new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2));
			}
		}
	}
	
	public void move(Vector3f dir, float amt) {
		getTransform().setPos(getTransform().getPos().add(dir.mul(amt)));
	}
//	
//	public void rotateX(float angle) {
//		Vector3f Haxis = yAxis.cross(forward).normalized();
//		
//		forward = forward.rotate(angle, Haxis).normalized();
//		
//		up = forward.cross(Haxis).normalized();
//	}
//	
//	public void rotateY(float angle) {
//		Vector3f Haxis = yAxis.cross(forward).normalized();
//		
//		forward = forward.rotate(angle, yAxis).normalized();
//		
//		up = forward.cross(Haxis).normalized();
//	}
//
//	public Vector3f getLeft() {
//		return forward.cross(up).normalized();
//	}
//	
//	public Vector3f getRight() {
//		return up.cross(forward).normalized();		
//	}
//	
//	public Vector3f getPos() {
//		return pos;
//	}
//
//	public void setPos(Vector3f pos) {
//		this.pos = pos;
//	}

//	public Vector3f getForward() {
//		return forward;
//	}
//
//	public void setForward(Vector3f forward) {
//		this.forward = forward;
//	}
//
//	public Vector3f getUp() {
//		return up;
//	}
//
//	public void setUp(Vector3f up) {
//		this.up = up;
//	}
	
	@Override
	public void addToRenderingEngine(RenderingEngine renderingEngine) {
		renderingEngine.addCamera(this);
	}
}
