package com.carlisle.ben.newtoniangravitysimulator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;

import java.util.ArrayList;

public class DragMode extends CreationMode {

    @Override
    public String getMessage() {
        return "Mode Changed to Drag and Drop";
    }

    @Override
    public Mode getNextMode() {
        return new SpeedMode();
    }

    public void drawNewPlanet(Canvas canvas, int height, int width, Paint g) {
        if (currentPosition != null) {
            g.setColor(currentColor);
            currentPosition = fitPlanetInsideScreen(height, width, currentPosition);
            if (currentPosition == null) {
                reset();
            } else {
                canvas.drawCircle(currentPosition.getX(), currentPosition.getY(), (int) (currentMass * 10), g);
                currentMass += 0.05;
            }
        }
    }

    @Override
    public void onTouch(MotionEvent e, int height, int width, ArrayList<Planet> planets) {
        super.onTouch(e, height, width, planets);
        if (e.getActionMasked() == MotionEvent.ACTION_UP) {
            currentPosition = fitPlanetInsideScreen(height, width, currentPosition);
            planets.add(new Planet(currentPosition, currentMass, currentColor));
            reset();
        }
    }
}
