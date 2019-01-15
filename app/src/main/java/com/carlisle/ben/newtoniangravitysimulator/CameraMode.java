package com.carlisle.ben.newtoniangravitysimulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

public class CameraMode extends Mode {

    private Planet lockedPlanet = null;
    private float zoom = 1.0f;
    private float xBoundsModifier;
    private float yBoundsModifier;
    private long lastTap = 0L;
    private Position lastPosition;
    private float lastRadius = 0f;

    @Override
    public String getMessage() {
        return "Mode Changed to Camera Mode";
    }

    @Override
    public Mode getNextMode() {
        return new DragMode();
    }

    @Override
    public Rect getBounds(int height, int width) {
        setModifiers(height, width);
        return new Rect((int) (-xBoundsModifier * zoom), (int) (-yBoundsModifier * zoom), (int) ((width - xBoundsModifier) * zoom), (int) ((height - yBoundsModifier) * zoom));
    }

    private void setModifiers(int height, int width) {
        int shorterSide = height > width ? width : height;
        if (lockedPlanet != null) {
            zoom = shorterSide / (lockedPlanet.getMass() * 30f);
            xBoundsModifier = (lockedPlanet.getPosition().getX() - lockedPlanet.getMass() * 15);
            yBoundsModifier = (lockedPlanet.getPosition().getY() - lockedPlanet.getMass() * 15);
        }
        xBoundsModifier = Math.max(0, Math.min(xBoundsModifier, width -  width/ zoom));
        yBoundsModifier = Math.max(0, Math.min(yBoundsModifier, height - height / zoom));
        zoom = Math.max(1, Math.min(zoom, 10));
    }

    @Override
    public Position getPlanetPosition(Planet planet) {
        return new Position(zoom * (planet.getPosition().getX() - xBoundsModifier), zoom * (planet.getPosition().getY() - yBoundsModifier));
    }

    @Override
    public float getPlanetSize(Planet planet) {
        return planet.getMass() * 10 * zoom;
    }

    @Override
    public void drawNewPlanet(Canvas canvas, int height, int width, Paint g) {
    }

    @Override
    public void onTouch(MotionEvent e, int height, int width, ArrayList<Planet> planets) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                lastPosition = getCenterPosition(e);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastPosition = getCenterPosition(e);
                lastRadius = getRadius(e);
                break;
            case MotionEvent.ACTION_MOVE:
                Position currentPosition = getCenterPosition(e);
                if (lockedPlanet == null) {
                    xBoundsModifier += (lastPosition.getX() - currentPosition.getX());
                    yBoundsModifier += (lastPosition.getY() - currentPosition.getY());
                    lastPosition = currentPosition;
                    if (e.getPointerCount() >= 2) {
                        zoom += (getRadius(e) - lastRadius) / 500;
                        lastRadius = getRadius(e);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                lastPosition = null;
                lastRadius = -1;
                break;
            case MotionEvent.ACTION_UP:
                Planet touchedPlanet = checkIfOnLockedPlanet(e.getX(), e.getY(), planets);
                if (isDoubleTap()) {
                    reset();
                }
                lockedPlanet = touchedPlanet;
                lastTap = System.currentTimeMillis();
                lastTap = System.currentTimeMillis();
        }
    }

    private float getRadius(MotionEvent e) {
        return (float) Math.sqrt(Math.pow(e.getX(0) - e.getX(1), 2) + Math.pow(e.getY(0) - e.getY(1), 2));
    }

    private boolean isDoubleTap() {
        return System.currentTimeMillis() - lastTap < 200;
    }

    public void reset() {
        xBoundsModifier = 0;
        yBoundsModifier = 0;
        zoom = 1;
        lockedPlanet = null;
    }

    private Planet checkIfOnLockedPlanet(float x, float y, ArrayList<Planet> planets) {
        Planet planet;
        float radius;
        for (int i = 0; i < planets.size(); i++) {
            planet = planets.get(i);
            radius = (float) Math.sqrt(Math.pow(x - zoom * (planet.getPosition().getX() - xBoundsModifier), 2) + Math.pow(y - zoom * (planet.getPosition().getY() - yBoundsModifier), 2));
            if (radius < planet.getMass() * 10) {
                return planet;
            }
        }
        return null;
    }
}
