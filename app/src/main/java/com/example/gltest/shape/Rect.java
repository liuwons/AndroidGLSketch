package com.example.gltest.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Rect extends BaseShape {
    public float[] points = {-2f, -2f, -2f, -2f};  // a.x, a.y, b.x, b.y

    private boolean mChanged = true;
    private float[] mDumpVertexArray = new float[16];

    private boolean mMoved = false;

    public Rect(float[] c) {
        System.arraycopy(c, 0, color, 0, color.length);
    }

    @Override
    public void onStart(float x, float y) {
        points[0] = x;
        points[1] = y;
    }

    @Override
    public void onMove(float x, float y) {
        points[2] = x;
        points[3] = y;
        mChanged = true;
        mMoved = true;
    }

    @Override
    public void onFinish(float x, float y) {
        points[2] = x;
        points[3] = y;
        mChanged = true;
        mMoved = true;
    }

    @Override
    public boolean valid() {
        return mMoved && (points[0] != points[2] || points[1] != points[3]);
    }

    public int dumpTriangleData(FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        if (!valid()) {
            return 0;
        }

        return 0;
    }

    public float[] dumpVertex() {
        if (mChanged) {
            mDumpVertexArray[0] = points[0];
            mDumpVertexArray[1] = points[1];
            mDumpVertexArray[2] = points[2];
            mDumpVertexArray[3] = points[1];

            mDumpVertexArray[4] = points[2];
            mDumpVertexArray[5] = points[1];
            mDumpVertexArray[6] = points[2];
            mDumpVertexArray[7] = points[3];

            mDumpVertexArray[8] = points[2];
            mDumpVertexArray[9] = points[3];
            mDumpVertexArray[10] = points[0];
            mDumpVertexArray[11] = points[3];

            mDumpVertexArray[12] = points[0];
            mDumpVertexArray[13] = points[3];
            mDumpVertexArray[14] = points[0];
            mDumpVertexArray[15] = points[1];
            mChanged = false;
        }
        return mDumpVertexArray;
    }
}
