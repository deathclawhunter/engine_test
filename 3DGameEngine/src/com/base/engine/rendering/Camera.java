package com.base.engine.rendering;

import com.base.engine.core.Input;
import com.base.engine.core.Time;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class Camera {
	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	
	private Vector3f pos;
	private Vector3f forward;
	private Vector3f up;
	
	private boolean mouseLocked = false;
	private float sensitivity = 0.1f;
	private Vector2f centerPosition = new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2);
	
	public Camera(Vector3f pos, Vector3f forward, Vector3f up) {
		this.pos = pos;
		this.forward = forward;
		this.up = up;
		up.normalized();
		forward.normalized();
	}
	
	public Camera() {
		this(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, 1, 0));
	}
	
	public void input() {
		float movAmt = (float) (10 * Time.getDelta());
		float rotAmt = (float) (100 * Time.getDelta());
		
		if(Input.getKey(Input.KEY_ESCAPE)) {
			Input.setCursor(true);
			mouseLocked = false;
		} else if(Input.getMouseDown(0)) {
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked = true;
		} else if (Input.getKey(Input.KEY_W)) {
			move(getForward(), movAmt);
		} else if (Input.getKey(Input.KEY_S)) {
			move(getForward(), -movAmt);
		} else if (Input.getKey(Input.KEY_A)) {
			move(getLeft(), movAmt);
		} else if (Input.getKey(Input.KEY_D)) {
			move(getRight(), movAmt);
		} else if (Input.getKey(Input.KEY_UP)) {
			rotateX(-rotAmt);
		} else if (Input.getKey(Input.KEY_DOWN)) {
			rotateX(rotAmt);
		} else if (Input.getKey(Input.KEY_LEFT)) {
			rotateY(-rotAmt);
		} else if (Input.getKey(Input.KEY_RIGHT)) {
			rotateY(rotAmt);
		}
		
		if (mouseLocked) {
			Vector2f deltaPos = Input.getMousePosition().sub(centerPosition);
			
			boolean rotY = deltaPos.getX() != 0;
			boolean rotX = deltaPos.getY() != 0;
			
			if (rotY) {
				rotateY(deltaPos.getX() * sensitivity);
			} else if (rotX) {
				rotateX(-deltaPos.getY() * sensitivity);
			}
			
			if (rotY || rotX) {
				Input.setMousePosition(new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2));
			}
		}
	}
	
	public void move(Vector3f dir, float amt) {
		pos = pos.add(dir.mul(amt));
	}
	
	public void rotateX(float angle) {
		Vector3f Haxis = yAxis.cross(forward).normalized();
		
		forward = forward.rotate(angle, Haxis).normalized();
		
		up = forward.cross(Haxis).normalized();
	}
	
	public void rotateY(float angle) {
		Vector3f Haxis = yAxis.cross(forward).normalized();
		
		forward = forward.rotate(angle, yAxis).normalized();
		
		up = forward.cross(Haxis).normalized();
	}

	public Vector3f getLeft() {
		return forward.cross(up).normalized();
	}
	
	public Vector3f getRight() {
		return up.cross(forward).normalized();		
	}
	
	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

	public Vector3f getForward() {
		return forward;
	}

	public void setForward(Vector3f forward) {
		this.forward = forward;
	}

	public Vector3f getUp() {
		return up;
	}

	public void setUp(Vector3f up) {
		this.up = up;
	}
}
