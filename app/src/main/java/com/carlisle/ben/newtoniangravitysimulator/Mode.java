package com.carlisle.ben.newtoniangravitysimulator;
//
//public enum State {
//    DRAG(0), SPEED(1), CAMERA(2);
//    int value;
//
//    State(int value) {
//        this.value = value;
//    }
//}

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

public abstract class Mode {

    public abstract String getMessage();
    public abstract Mode getNextMode();
    public abstract Rect getBounds(int height, int width);
    public abstract Position getPlanetPosition(Planet planet);
    public abstract float getPlanetSize(Planet planet);
    public abstract void drawNewPlanet(Canvas canvas, int height, int width, Paint g);
    public abstract void onTouch(MotionEvent e, int height, int width, ArrayList<Planet> planets);
    public abstract void reset();

    Position getCenterPosition(MotionEvent e) {
        float sumX = 0;
        float sumY = 0;
        for (int i = 0; i < e.getPointerCount(); i++) {
            sumX += e.getX(i);
            sumY += e.getY(i);
        }
        return new Position(sumX / e.getPointerCount(), sumY / e.getPointerCount());
    }
}