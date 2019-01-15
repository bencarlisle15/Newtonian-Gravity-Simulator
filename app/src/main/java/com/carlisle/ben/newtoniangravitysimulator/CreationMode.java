package com.carlisle.ben.newtoniangravitysimulator;

import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

abstract class CreationMode extends Mode {

	Position currentPosition;
	float currentMass = 1;
	int currentColor = -1;

	@Override
	public Rect getBounds(int height, int width) {
		return new Rect(0, 0, width, height);
	}

	@Override
	public Position getPlanetPosition(Planet planet) {
		return planet.getPosition();
	}

	@Override
	public float getPlanetSize(Planet planet) {
		return planet.getMass() * 10;
	}

	@Override
	public void onTouch(MotionEvent e, int height, int width, ArrayList<Planet> planets) {
		if (currentColor == -1) {
			currentColor = DrawView.colorChooser();
		}
		currentPosition = getCenterPosition(e);
	}

	Position fitPlanetInsideScreen(int height, int width, Position currentPosition) {
		float currentX = currentPosition.getX();
		float currentY = currentPosition.getY();
		if (currentX < currentMass * 10) {
			currentX = (currentMass * 10);
		} else if (currentX > width - currentMass * 10) {
			currentX = (width - currentMass * 10);
		}
		if (currentY < currentMass * 10) {
			currentY = currentMass * 10;
		} else if (currentY > height - currentMass * 10) {
			currentY = height - currentMass * 10;
		}
		if (currentMass * 20 >= width || currentMass * 20 >= height) {
			return null;
		}
		return new Position(currentX, currentY);
	}

	@Override
	public void reset() {
		currentPosition = null;
		currentMass = 1;
		currentColor = -1;
	}
}
