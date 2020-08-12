package com.example.gltest;

import android.opengl.GLES20;
import android.util.Log;

public class GLUtils {
    public static void checkError() {
        int error = GLES20.glGetError();
        if (error != 0) {
            Throwable t = new Throwable();
            Log.e("GL", "GL error: " + error, t);
        }
    }
}
