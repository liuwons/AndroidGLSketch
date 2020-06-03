package com.example.gltest.shape;

public abstract class BaseShape {
    public float[] color = new float[4];

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public abstract void onStart(float x, float y);
    public abstract void onMove(float x, float y);
    public abstract void onFinish(float x, float y);
    public abstract boolean valid();
}
