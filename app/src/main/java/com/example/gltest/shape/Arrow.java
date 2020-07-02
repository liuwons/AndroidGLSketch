package com.example.gltest.shape;

import android.util.Log;
import com.example.gltest.VertexUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Arrow extends BaseShape {
    private static final String TAG = Arrow.class.getSimpleName();

    private static final int OVAL_POINT_COUNT = 16;
    private static final int TOTAL_POINT_COUNT = 1 + OVAL_POINT_COUNT + 7;  // 1圆心 + 尾部圆16个 + 箭头控制点7个
    private static final float[] POINT_IDS = new float[TOTAL_POINT_COUNT];
    private static final float[] FAKE_CTRLS = {1201f, 1201f, 1201f, OVAL_POINT_COUNT+0.1f};

    static {
        POINT_IDS[0] = -1f;
        float angleStride = 1.0f / OVAL_POINT_COUNT;
        for (int i = 1; i <= OVAL_POINT_COUNT; i ++) {
            POINT_IDS[i] = i * angleStride;
        }
        for (int i = OVAL_POINT_COUNT + 1; i < TOTAL_POINT_COUNT; i ++) {
            POINT_IDS[i] = i + 0.5f;
        }
        Log.i(TAG, "pointers: " + VertexUtils.floatArr2Str(POINT_IDS));
    }

    public float[] position = new float[4];
    public float width = 0.05f;  // 箭头最宽处宽度

    private float[] dumpData = new float[10];
    private boolean changed = true;

    public Arrow(float[] c) {
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
    public boolean valid() {
        return super.valid() && length() > width;
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        for (int i = 0; i < TOTAL_POINT_COUNT; i ++) {
            vertexBuffer.put(position);
            vertexBuffer.put(color);
            vertexBuffer.put(width);
            vertexBuffer.put(POINT_IDS[i]);
            vertexBuffer.put(getZ());
            vertexBuffer.put(FAKE_CTRLS);
        }

        // 尾部圆
        for (int i = 0; i < OVAL_POINT_COUNT; i ++) {
            indexBuffer.put(vertexPos);
            indexBuffer.put(vertexPos + i + 1);
            if (i == OVAL_POINT_COUNT-1) {
                indexBuffer.put(vertexPos + 1);
            } else {
                indexBuffer.put(vertexPos + i + 2);
            }
        }

        // 箭头体
        int bodyOffset = 1 + OVAL_POINT_COUNT;
        indexBuffer.put(vertexPos + bodyOffset);
        indexBuffer.put(vertexPos + bodyOffset + 1);
        indexBuffer.put(vertexPos + bodyOffset + 2);

        indexBuffer.put(vertexPos + bodyOffset + 1);
        indexBuffer.put(vertexPos + bodyOffset + 2);
        indexBuffer.put(vertexPos + bodyOffset + 3);

        // 箭头
        indexBuffer.put(vertexPos + bodyOffset + 4);
        indexBuffer.put(vertexPos + bodyOffset + 5);
        indexBuffer.put(vertexPos + bodyOffset + 6);

        return TOTAL_POINT_COUNT;
    }
}
