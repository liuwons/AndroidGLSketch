package com.example.gltest.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.example.gltest.data.RenderModel;
import com.example.gltest.renderer.BaseRenderer;
import com.example.gltest.renderer.LineRenderer;
import com.example.gltest.renderer.PathRenderer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderImpl implements GLSurfaceView.Renderer {
    private static String TAG = GLRenderImpl.class.getSimpleName();

    private static final int SIZEOF_FLOAT = 4;
    private static final int SIZEOF_INT = 4;
    private static final int BUFFER_SIZE = 1024 * 1024;

    private int mWidth;
    private int mHeight;

    private final RenderModel mModel;

    private Context mContext;

    private FloatBuffer mVertexBuffer;
    private IntBuffer mIndexBuffer;
    private FloatBuffer mColorBuffer;

    private int[] mBufferHandles = new int[3];  // vertex data;  color;  vertex index

    private List<BaseRenderer> mRenderers = new ArrayList<>(10);

    public GLRenderImpl(Context context, RenderModel model) {
        mContext = context;
        mModel = model;
    }

    public void initGL() {
        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 2 * SIZEOF_FLOAT);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();

        ByteBuffer indexByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 2 * SIZEOF_INT);
        indexByteBuffer.order(ByteOrder.nativeOrder());
        mIndexBuffer = indexByteBuffer.asIntBuffer();

        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 4 *SIZEOF_FLOAT);
        colorByteBuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = colorByteBuffer.asFloatBuffer();

        mRenderers.add(new LineRenderer(mContext, mModel, mVertexBuffer, mIndexBuffer, mColorBuffer));
        mRenderers.add(new PathRenderer(mContext, mModel, mVertexBuffer, mIndexBuffer, mColorBuffer));

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated");

        initGL();
        for (BaseRenderer renderer : mRenderers) {
            renderer.onSurfaceCreated(gl, config);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged:  [w]" + width + "  [h]" + height);

        resize(width, height);
        GLES20.glViewport(0, 0, mWidth, mHeight);

        for (BaseRenderer renderer : mRenderers) {
            renderer.onSurfaceChanged(gl, width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        long t = System.currentTimeMillis();
        mModel.prepareDrawingData();

        long prepare = System.currentTimeMillis() - t;

        for (BaseRenderer renderer : mRenderers) {
            renderer.onDrawFrame(gl);
        }
        t = System.currentTimeMillis() - t;
        if (t > 5) {
            Log.i("SketchPro", "draw frame cost: " + t + "  [prepare]" + prepare);
        }
    }
}
