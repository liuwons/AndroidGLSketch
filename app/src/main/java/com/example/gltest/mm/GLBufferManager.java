package com.example.gltest.mm;

import android.opengl.GLES20;
import android.util.Log;
import com.example.gltest.GLUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GLBufferManager {
    private static final String TAG = GLBufferManager.class.getSimpleName();

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_INT = 4;
    private static final int BUFFER_SIZE = 1024;

    private static final int FLOAT_PER_VERTEX = 15;

    private FloatBuffer mVertexBuffer;
    private IntBuffer mIndexBuffer;
    private int[] mBufferHandles = new int[2];

    public void create() {
        Log.i(TAG, "create");
        GLES20.glGenBuffers(mBufferHandles.length, mBufferHandles, 0);
        GLUtils.checkError();


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mBufferHandles[0]);
        GLUtils.checkError();

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * FLOAT_PER_VERTEX * SIZEOF_FLOAT);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();
        mVertexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mVertexBuffer.capacity() * SIZEOF_FLOAT, mVertexBuffer, GLES20.GL_DYNAMIC_DRAW);
        GLUtils.checkError();


        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mBufferHandles[1]);
        GLUtils.checkError();
        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 2 * SIZEOF_INT);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer = indexByteBuffer.asIntBuffer();
        mIndexBuffer.position(0);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.capacity() * SIZEOF_INT, mIndexBuffer, GLES20.GL_DYNAMIC_DRAW);
        GLUtils.checkError();
    }

    public void destroy() {
        Log.i(TAG, "destroy");
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

        GLES20.glDeleteBuffers(mBufferHandles.length, mBufferHandles, 0);
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public IntBuffer getIndexBuffer() {
        return mIndexBuffer;
    }
}
