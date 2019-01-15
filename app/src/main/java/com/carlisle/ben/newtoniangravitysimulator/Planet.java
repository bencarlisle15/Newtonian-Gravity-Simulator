package com.carlisle.ben.newtoniangravitysimulator;

class Planet {
	private Position position;
	private float xSpeed = 0;
	private float ySpeed = 0;
	private float mass;
	private final int color;

	Planet(Position position, float m, int c) {
		this.position = position;
		mass = m;
		color = c;
	}

	int getColor() {
		return color;
	}

	void setMass(float m) {
		mass = m;
	}

	Position getPosition() {
		return position;
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

	void setPosition(Position position) {
	    this.position = position;
    }

	void setSpeed(float xS, float yS) {
		xSpeed = xS;
		ySpeed = yS;
	}
}
