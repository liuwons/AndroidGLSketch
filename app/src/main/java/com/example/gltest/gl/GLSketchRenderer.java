package com.example.gltest.gl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public interface GLSketchRenderer {
    void onSurfaceCreated(GL10 gl, EGLConfig config);
    void onSurfaceChanged(GL10 gl, int width, int height);
    void onDrawFrame(GL10 gl);
    void onSurfaceDestroy();
}
