package com.carlisle.ben.newtoniangravitysimulator;

class Planet {
	private float xPos;
	private float yPos;
	private float xSpeed = 0;
	private float ySpeed = 0;
	private float mass = 1;
	private final int color;

	Planet(float x, float y, float m, int c) {
		xPos = x;
		yPos = y;
		mass = m;
		color = c;
	}

	int getColor() {
		return color;
	}

	void setMass(float m) {
		mass = m;
	}

	float getXPos() {
		return xPos;
	}

	float getYPos() {
		return yPos;
	}

	float getXSpeed() {
		return xSpeed;
	}

	float getYSpeed() {
		return ySpeed;
	}

	float getMass() {
		return mass;
	}

	void setPos(float x, float y) {
		xPos = x;
		yPos = y;
	}

	void setSpeed(float xS, float yS) {
		xSpeed = xS;
		ySpeed = yS;
	}
}
