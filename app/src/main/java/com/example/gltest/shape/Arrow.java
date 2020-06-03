package com.example.gltest.shape;

import com.example.gltest.geom.Position;

public class Arrow extends BaseShape {
    public Position start;
    public Position end;

    public Arrow(float[] c) {
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
        return true;
    }
}
