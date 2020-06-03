package com.example.gltest.shape;

import com.example.gltest.geom.Position;

public class Rect extends BaseShape {
    public Position leftTop;
    public float width;
    public float height;

    public Rect(float[] c) {
        color = c;
    }

    @Override
    public void onStart(float x, float y) {

    }

    @Override
    public void onMove(float x, float y) {

    }

    @Override
    public void onFinish(float x, float y) {

    }

    @Override
    public boolean valid() {
        return false;
    }
}
