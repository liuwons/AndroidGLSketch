package com.example.gltest.shape;

import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Round extends BaseShape {
    private static final String TAG = Round.class.getSimpleName();

    private static final int SLICE_COUNT = 32;
    private static final float[] POINT_IDS = new float[SLICE_COUNT]; // 0.0 ~ 1.0
    private static final float[] FAKE_CTRLS = {1301f, 1301f, 1301f, 1301f};

    static {
        float stride = 1.0f / (SLICE_COUNT - 2);  //边界特殊处理
        POINT_IDS[0] = 0f;
        for (int i = 1; i < SLICE_COUNT; i++) {
            POINT_IDS[i] = POINT_IDS[i-1] + stride;
        }
    }

    public float[] position = new float[4];

    private float[] dumpData = new float[10];
    private boolean changed = true;

    public Round(float[] c) {
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
        for (int i = 0; i <= SLICE_COUNT; i ++) {
            vertexBuffer.put(position);
            vertexBuffer.put(color);
            vertexBuffer.put(0);
            if (i == 0) {
                vertexBuffer.put(-1);
            } else {
                vertexBuffer.put(POINT_IDS[i-1]);
            }
            vertexBuffer.put(getZ());
            vertexBuffer.put(FAKE_CTRLS);
        }

        for (int i = 0; i < SLICE_COUNT; i ++) {
            indexBuffer.put(vertexPos);
            indexBuffer.put(vertexPos + i + 1);
            if (i == SLICE_COUNT-1) {
                indexBuffer.put(vertexPos + 1);
            } else {
                indexBuffer.put(vertexPos + i + 2);
            }
        }

        return SLICE_COUNT + 1;
    }
}
