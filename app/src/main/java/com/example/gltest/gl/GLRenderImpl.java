package com.example.gltest.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import com.example.gltest.data.RenderModel;
import com.example.gltest.mm.GLBufferManager;
import com.example.gltest.renderer.BaseRenderer;
import com.example.gltest.renderer.CompoundRenderer;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderImpl implements GLSketchRenderer {
    private static String TAG = GLRenderImpl.class.getSimpleName();

    private GLBufferManager mBufferManager;

    private int mWidth;
    private int mHeight;

    private final RenderModel mModel;

    private Context mContext;

    private List<BaseRenderer> mRenderers = new ArrayList<>(10);

    public GLRenderImpl(Context context, RenderModel model) {
        mContext = context;
        mModel = model;
        mBufferManager = new GLBufferManager();
    }

    public void initGL() {
        mBufferManager.create();

        mRenderers.add(new CompoundRenderer(mContext, mModel, mBufferManager.getVertexBuffer(), mBufferManager.getIndexBuffer()));

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void destroyGL() {
        mBufferManager.destroy();
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
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mModel.prepareDrawingData();

        for (BaseRenderer renderer : mRenderers) {
            renderer.onDrawFrame(gl);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        destroyGL();
    }
}
