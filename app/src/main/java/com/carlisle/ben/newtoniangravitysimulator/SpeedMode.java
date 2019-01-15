package com.carlisle.ben.newtoniangravitysimulator;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

class SpeedMode extends CreationMode {

    private Position originalPosition;

    @Override
    public String getMessage() {
        return "Mode Changed to Speed Shooter";
    }

    @Override
    public Mode getNextMode() {
        return new CameraMode();
    }

    @Override
    public void drawNewPlanet(Canvas canvas, int height, int width, Paint g) {
        if (currentPosition != null) {
            g.setColor(currentColor);
            originalPosition = fitPlanetInsideScreen(height, width, originalPosition);
            if (currentPosition == null) {
                reset();
            } else {
                canvas.drawCircle(originalPosition.getX(), originalPosition.getY(), currentMass * 10, g);
                currentMass += 0.05;
                g.setColor(Color.WHITE);
                g.setStrokeWidth((int) (currentMass));
                g.setStrokeCap(Paint.Cap.ROUND);
                canvas.drawLine(2 * originalPosition.getX() - currentPosition.getX(), 2 * originalPosition.getY() - currentPosition.getY(), currentPosition.getX(), currentPosition.getY(), g);
            }
        }
    }

    @Override
    public void onTouch(MotionEvent e, int height, int width, ArrayList<Planet> planets) {
        super.onTouch(e, height, width, planets);
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                originalPosition = currentPosition;
                break;
            case MotionEvent.ACTION_UP:
                originalPosition = fitPlanetInsideScreen(height, width, originalPosition);
                Planet planet = new Planet(originalPosition, currentMass, currentColor);
                planets.add(planet);
                planet.setSpeed((originalPosition.getX() - currentPosition.getX()) / 100, (originalPosition.getY() - currentPosition.getY()) / 100);
                reset();
                break;
        }
    }

    @Override
    public void reset() {
        super.reset();
        originalPosition = null;
    }
}