package com.carlisle.ben.newtoniangravitysimulator;

import android.support.annotation.NonNull;

public class Position  {

    private final float x;
    private final float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @NonNull
    public String toString() {
        return x + ":" + y;
    }
}
