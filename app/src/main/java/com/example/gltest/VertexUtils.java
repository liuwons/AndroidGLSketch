package com.example.gltest;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexUtils {
    private static final String TAG = VertexUtils.class.getSimpleName();

    private static final float[] POINT_IDS = { 0.1f, 1.1f, 2.1f, 3.1f };
    private static final float[] FAKE_CTRLS = {100000f, 100000f, 100000f, 100000f};

    public static int dumpLine2TriangleData(int vertexPos, float[] linePosition,
                                            float width,
                                            float[] color,
                                            float z,
                                            FloatBuffer vertexBuffer,
                                            IntBuffer indexBuffer) {
        for (int i = 0; i < POINT_IDS.length; i++) {
            vertexBuffer.put(linePosition);
            vertexBuffer.put(color);
            vertexBuffer.put(width);
            vertexBuffer.put(POINT_IDS[i]);
            vertexBuffer.put(z);
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
