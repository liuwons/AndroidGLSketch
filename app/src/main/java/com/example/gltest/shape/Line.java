package com.example.gltest.shape;

import android.util.Log;
import com.example.gltest.VertexUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Line extends BaseShape {
    private static final String TAG = Line.class.getSimpleName();

    public float[] postion = new float[4];
    public float width = 0.01f;

    private float[] dumpData = new float[10];
    private boolean changed = true;

    public Line(float[] c) {
        super(c);
    }

    @Override
    public void onStart(float x, float y) {
        super.onStart(x, y);
        Log.d(TAG, "onStart  [x]" + x + "  [y]" + y);
        postion[0] = x;
        postion[1] = y;
    }

    @Override
    public void onMove(float x, float y) {
        super.onMove(x, y);
        Log.d(TAG, "onMove  [x]" + x + "  [y]" + y);
        postion[2] = x;
        postion[3] = y;
        changed = true;
    }

    @Override
    public void onFinish(float x, float y) {
        super.onFinish(x, y);
        Log.d(TAG, "onFinish  [x]" + x + "  [y]" + y);
        postion[2] = x;
        postion[3] = y;
        changed = true;
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        if (!valid()) {
            return 0;
        }

        if (!changed) {
        }

        return VertexUtils.dumpLine2TriangleData(vertexPos, postion, width, color, getZ(),
            vertexBuffer, indexBuffer);
    }
}
