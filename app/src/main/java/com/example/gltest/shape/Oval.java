package com.example.gltest.shape;

import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Oval extends BaseShape {
    private static final String TAG = Oval.class.getSimpleName();

    private static final int SLICE_COUNT = 100;
    private static final float[] POINT_IDS = new float[SLICE_COUNT]; // 0.0 ~ 1.0
    private static final float[] FAKE_CTRLS = {10001f, 10001f, 10001f, 10001f};
    private static final float[] FAKE_CTRLS2 = {10001f, -10001f, 10001f, 10001f};

    static {
        float stride = 1.0f / (SLICE_COUNT - 2);  //边界特殊处理
        POINT_IDS[0] = 0f;
        for (int i = 1; i < SLICE_COUNT; i++) {
            POINT_IDS[i] = POINT_IDS[i-1] + stride;
        }
    }

    public float[] position = new float[4];
    public float width = 0.01f;

    private float[] dumpData = new float[10];
    private boolean changed = true;

    public Oval(float[] c) {
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
        for (int i = 0; i < SLICE_COUNT; i ++) {
            vertexBuffer.put(position);
            vertexBuffer.put(color);
            vertexBuffer.put(width);
            vertexBuffer.put(POINT_IDS[i]);
            vertexBuffer.put(getZ());
            vertexBuffer.put(FAKE_CTRLS);

            vertexBuffer.put(position);
            vertexBuffer.put(color);
            vertexBuffer.put(width);
            vertexBuffer.put(POINT_IDS[i]);
            vertexBuffer.put(getZ());
            vertexBuffer.put(FAKE_CTRLS2);

            if (i != 0) {
                int startPos = vertexPos + (i-1)*2;
                indexBuffer.put(startPos);
                indexBuffer.put(startPos + 1);
                indexBuffer.put(startPos + 2);
                indexBuffer.put(startPos + 1);
                indexBuffer.put(startPos + 2);
                indexBuffer.put(startPos + 3);
            }
        }
        return SLICE_COUNT * 2;
    }
}
