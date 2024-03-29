package com.base.engine.components;

import com.base.engine.core.Input;
import com.base.engine.core.Vector3f;

public class FreeMove extends GameComponent {
	float speed;
	int forwardKey;
	int backKey;
	int leftKey;
	int rightKey;
	
	public FreeMove(float speed) {
		this(speed, Input.KEY_W, Input.KEY_S, Input.KEY_A, Input.KEY_D);
	}
	
	public FreeMove(float speed, int forwardKey, int backKey,
			int leftKey, int rightKey) {
		this.speed = speed;
		this.forwardKey = forwardKey;
		this.backKey = backKey;
		this.leftKey = leftKey;
		this.rightKey = rightKey;
	}
	
	@Override
	public void input(float delta) {
		float movAmt = (float) (speed * delta);
		
		if (Input.getKey(forwardKey)) {
			move(getTransform().getRot().getForward(), movAmt);
		} else if (Input.getKey(backKey)) {
			move(getTransform().getRot().getForward(), -movAmt);
		} else if (Input.getKey(leftKey)) {
			move(getTransform().getRot().getLeft(), movAmt);
		} else if (Input.getKey(rightKey)) {
			move(getTransform().getRot().getRight(), movAmt);
		}
	}
	
	public void move(Vector3f dir, float amt) {
		getTransform().setPos(getTransform().getPos().add(dir.mul(amt)));
	}
}
