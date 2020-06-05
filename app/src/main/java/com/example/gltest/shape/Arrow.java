package com.example.gltest.shape;

import android.graphics.PointF;

public class Arrow extends BaseShape {
    public PointF start;
    public PointF end;

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
