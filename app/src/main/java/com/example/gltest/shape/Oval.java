package com.example.gltest.shape;

import android.graphics.PointF;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Oval extends BaseShape {
    public PointF start = new PointF();
    public PointF end = new PointF();

    public Oval(float[] c) {
        super(c);
    }

    @Override
    public void onStart(float x, float y) {
        super.onStart(x, y);
        start.x = x;
        start.y = y;
    }

    @Override
    public void onMove(float x, float y) {
        super.onMove(x, y);
        end.x = x;
        end.y = y;
    }

    @Override
    public void onFinish(float x, float y) {
        super.onFinish(x, y);
        end.x = x;
        end.y = y;
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        return super.dumpTriangles(vertexPos, vertexBuffer, indexBuffer);
    }
}
