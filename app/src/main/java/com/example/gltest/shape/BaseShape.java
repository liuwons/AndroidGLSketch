package com.example.gltest.shape;

public class BaseShape {
    private static int sCurrentShapeCount = 1;

    public float[] color = new float[4];
    public int order;

    private boolean mStarted = false;
    private boolean mMoved = false;
    private boolean mFinished = false;

    public BaseShape(float[] c) {
        order = sCurrentShapeCount;
        sCurrentShapeCount += 1;
        System.arraycopy(c, 0, color, 0, color.length);
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public void onStart(float x, float y) {
        mStarted = true;
    }

    public void onMove(float x, float y) {
        mMoved = true;
    }

    public void onFinish(float x, float y) {
        mFinished = true;
    }

    public boolean valid() {
        return mStarted && (mMoved || mFinished);
    }

    public float getZ() {
        return 1.0f - order * 1.0f / sCurrentShapeCount;
    }
}
