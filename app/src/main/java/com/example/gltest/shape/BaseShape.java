package com.example.gltest.shape;

import android.graphics.PointF;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BaseShape {
    private static int sCurrentShapeCount = 1;

    public float[] color = new float[4];
    public int order;

    private boolean mStarted = false;
    private boolean mMoved = false;
    private boolean mFinished = false;

    private PointF mStartPoint = new PointF();
    private PointF mEndPoint = new PointF();

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
        mStartPoint.set(x, y);
    }

    public void onMove(float x, float y) {
        mMoved = true;
        mEndPoint.set(x, y);
    }

    public void onFinish(float x, float y) {
        mFinished = true;
        mEndPoint.set(x, y);
    }

    public boolean valid() {
        return mStarted && (mMoved || mFinished);
    }

    public float getZ() {
        return 1.0f - order * 1.0f / sCurrentShapeCount;
    }

    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        return 0;
    }

    public float length() {
        return PointF.length(mStartPoint.x - mEndPoint.x, mStartPoint.y - mEndPoint.y);
    }
}
