package com.example.gltest.gl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;
import com.example.gltest.data.SketchProcessor;

public class SketchTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    private static final String TAG = SketchTextureView.class.getSimpleName();

    private GLSurfaceView.Renderer mRenderer;
    private GLThread mGLThread;
    private SketchProcessor mSketchProcessor;

    public SketchTextureView(Context context) {
        super(context);
        init();
    }

    public SketchTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setRender(GLSurfaceView.Renderer render) {
        mRenderer = render;
    }

    public void setProcessor(SketchProcessor processor) {
        mSketchProcessor = processor;
        setOnTouchListener(processor);
    }

    private void init() {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable:  [w]" + width + "  [h]" + height);

        mSketchProcessor.onAvailable(width, height);
        if (mGLThread != null) {
            mGLThread.finish();
        }
        mGLThread = new GLThread(surface, mRenderer, width, height);
        mGLThread.start();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged:  [w]" + width + "  [h]" + height);
        mSketchProcessor.onSizeChanged(width, height);
        mGLThread.onWindowResize(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        mSketchProcessor.onDestroy();
        mGLThread.finish();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


}
