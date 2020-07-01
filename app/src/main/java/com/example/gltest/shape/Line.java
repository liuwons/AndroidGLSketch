package com.example.gltest.shape;

import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Line extends BaseShape {
    private static final String TAG = Line.class.getSimpleName();

    private static final float[] POINT_IDS = { 0.1f, 1.1f, 2.1f, 3.1f };
    private static final float[] FAKE_CTRLS = {1001f, 1001f, 1001f, 1001f};

    public float[] position = new float[4];
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
        position[0] = x;
        position[1] = y;
    }

    @Override
    public void onMove(float x, float y) {
        super.onMove(x, y);
        Log.d(TAG, "onMove  [x]" + x + "  [y]" + y);
        position[2] = x;
        position[3] = y;
        changed = true;
    }

    @Override
    public void onFinish(float x, float y) {
        super.onFinish(x, y);
        Log.d(TAG, "onFinish  [x]" + x + "  [y]" + y);
        position[2] = x;
        position[3] = y;
        changed = true;
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        if (!valid()) {
            return 0;
        }

        if (!changed) {
        }

        return dumpLine2TriangleData(vertexPos, vertexBuffer, indexBuffer);
    }

    private int dumpLine2TriangleData(int vertexPos,
                                      FloatBuffer vertexBuffer,
                                      IntBuffer indexBuffer) {
        for (int i = 0; i < POINT_IDS.length; i++) {
            vertexBuffer.put(position);
            vertexBuffer.put(color);
            vertexBuffer.put(width);
            vertexBuffer.put(POINT_IDS[i]);
            vertexBuffer.put(getZ());
            vertexBuffer.put(FAKE_CTRLS);
            // Log.d(TAG, "dump vertex:  [pos]" + (vertexPos+i) + "  [point id]" + POINT_IDS[i]);
        }

        indexBuffer.put(vertexPos);
        indexBuffer.put(vertexPos + 1);
        indexBuffer.put(vertexPos + 2);
        indexBuffer.put(vertexPos + 1);
        indexBuffer.put(vertexPos + 2);
        indexBuffer.put(vertexPos + 3);

        return 4;
    }
}
