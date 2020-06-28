package com.example.gltest.shape;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Rect extends BaseShape {
    public float[] points = {-2f, -2f, -2f, -2f};  // a.x, a.y, b.x, b.y

    private boolean mChanged = true;
    private float[] mDumpVertexArray = new float[16];

    public Rect(float[] c) {
        super(c);
    }

    @Override
    public void onStart(float x, float y) {
        super.onStart(x, y);
        points[0] = x;
        points[1] = y;
    }

    @Override
    public void onMove(float x, float y) {
        super.onMove(x, y);
        points[2] = x;
        points[3] = y;
        mChanged = true;
    }

    @Override
    public void onFinish(float x, float y) {
        super.onFinish(x, y);
        points[2] = x;
        points[3] = y;
        mChanged = true;
    }

    @Override
    public boolean valid() {
        return super.valid() && (points[0] != points[2] || points[1] != points[3]);
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        return super.dumpTriangles(vertexPos, vertexBuffer, indexBuffer);
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
