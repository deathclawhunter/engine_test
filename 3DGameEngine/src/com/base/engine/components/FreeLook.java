package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Quaternion;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;
import com.base.engine.rendering.Window;

public class FreeLook extends GameComponent {

	public static final Vector3f yAxis = new Vector3f(0, 1, 0);
	private boolean mouseLocked = false;
	private float sensitivity;
	int keyMouseUnlock, keyUp, keyDown, keyLeft, keyRight, keyMouselock;
	private Vector2f centerPosition = new Vector2f(Window.getWidth() / 2, Window.getHeight() / 2);
	
	public FreeLook(float sensitivity) {
		this(sensitivity, Input.KEY_ESCAPE, 0, Input.KEY_UP,
				Input.KEY_DOWN, Input.KEY_LEFT, Input.KEY_RIGHT);
	}
	
	/**
	 * 
	 * @param sensitivity
	 * @param keyMouseUnlock - esc by default
	 * @param keyMouselock - 0 left mouse button by default
	 * @param keyUp
	 * @param keyDown
	 * @param keyLeft
	 * @param keyRight
	 */
	public FreeLook(float sensitivity, int keyMouseUnlock, int keyMouselock,
			int keyUp, int keyDown, int keyLeft, int keyRight) {
		this.sensitivity = sensitivity;
		this.keyMouseUnlock = keyMouseUnlock;
		this.keyMouselock = keyMouselock;
		this.keyUp = keyUp;
		this.keyDown = keyDown;
		this.keyLeft = keyLeft;
		this.keyRight = keyRight;
	}

	@Override
	public void input(float delta) {
		float rotAmt = (float) (sensitivity * delta);
		
		if(Input.getKey(keyMouseUnlock)) {
			Input.setCursor(true);
			mouseLocked = false;
		} else if(Input.getMouseDown(keyMouselock)) {
			Input.setMousePosition(centerPosition);
			Input.setCursor(false);
			mouseLocked = true;
		} else if (Input.getKey(keyDown)) {
			getTransform().rotate(getTransform().getRot().getRight(),
					(float) Math.toRadians(rotAmt));
		} else if (Input.getKey(keyUp)) {
			getTransform().rotate(getTransform().getRot().getRight(),
					(float) Math.toRadians(-rotAmt));
		} else if (Input.getKey(keyRight)) {
			getTransform().setRot(getTransform().getRot().mul(new Quaternion(yAxis,
					(float) Math.toRadians(rotAmt))).normalized());
		} else if (Input.getKey(keyLeft)) {
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
}
