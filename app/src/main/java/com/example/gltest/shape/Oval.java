package com.example.gltest.shape;

import com.example.gltest.geom.Position;

public class Oval extends BaseShape {
    public Position position;
    public float longAxis;
    public float shortAxis;

    public Oval(float[] c) {
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
