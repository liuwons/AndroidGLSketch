package com.example.gltest.gl;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

public class GLThread extends Thread {
    private static final String TAG = GLThread.class.getSimpleName();

    private static final int FRAME_RATE = 60;
    private static final int FRAME_INTV = 1000 / FRAME_RATE;

    private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
    private static final int EGL_OPENGL_ES2_BIT = 4;

    private SurfaceTexture mSurface;
    private GLSurfaceView.Renderer mRenderer;

    private long mLastRenderTime = 0;
    private volatile boolean finished;

    private EGL10 egl;
    private EGLDisplay eglDisplay;
    private EGLConfig eglConfig;
    private EGLContext eglContext;
    private EGLSurface eglSurface;
    private GL gl;
    private int width;
    private int height;
    private volatile boolean sizeChanged = true;

    public GLThread(SurfaceTexture surfaceTexture, GLSurfaceView.Renderer renderer, int w, int h) {
        Log.d(TAG, "create GLThread  [FRAME_INTV]" + FRAME_INTV + "  [w]" + w + "  [h]" + h);
        mSurface = surfaceTexture;
        mRenderer = renderer;
        width = w;
        height = h;
    }

    public synchronized void onWindowResize(int w, int h) {
        width = w;
        height = h;
        sizeChanged = true;
    }

    public void finish() {
        finished = true;
    }

    @Override
    public void run() {
        super.run();

        initGL();
        GL10 gl10 = (GL10) gl;
        mRenderer.onSurfaceCreated(gl10, eglConfig);
        while (!finished) {
            checkCurrent();
            if (sizeChanged) {
                createSurface();
                mRenderer.onSurfaceChanged(gl10, width, height);
                sizeChanged = false;
            }

            mRenderer.onDrawFrame(gl10);
            if (!egl.eglSwapBuffers(eglDisplay, eglSurface)) {
                throw new RuntimeException("Cannot swap buffers");
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - mLastRenderTime < FRAME_INTV) {
                try {
                    Thread.sleep(FRAME_INTV - (currentTime - mLastRenderTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mLastRenderTime = currentTime;
        }
        finishGL();
    }

    private void destroySurface() {
        if (eglSurface != null && eglSurface != EGL10.EGL_NO_SURFACE) {
            egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
            egl.eglDestroySurface(eglDisplay, eglSurface);
            eglSurface = null;
        }
    }

    /**
     * Create an egl surface for the current SurfaceHolder surface. If a surface
     * already exists, destroy it before creating the new surface.
     *
     * @return true if the surface was created successfully.
     */
    public boolean createSurface() {
        /*
         * Check preconditions.
         */
        if (egl == null) {
            throw new RuntimeException("egl not initialized");
        }
        if (eglDisplay == null) {
            throw new RuntimeException("eglDisplay not initialized");
        }
        if (eglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }

        /*
         *  The window size has changed, so we need to create a new
         *  surface.
         */
        destroySurface();

        /*
         * Create an EGL surface we can render into.
         */

        try {
            eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig, mSurface, null);
        } catch (IllegalArgumentException e) {
            // This exception indicates that the surface flinger surface
            // is not valid. This can happen if the surface flinger surface has
            // been torn down, but the application has not yet been
            // notified via SurfaceHolder.Callback.surfaceDestroyed.
            // In theory the application should be notified first,
            // but in practice sometimes it is not. See b/4588890
            Log.e(TAG, "eglCreateWindowSurface", e);
            return false;
        }

        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
            int error = egl.eglGetError();
            if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
                Log.e(TAG, "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
            }
            return false;
        }

        /*
         * Before we can issue GL commands, we need to make sure
         * the context is current and bound to a surface.
         */
        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            /*
             * Could not make the context current, probably because the underlying
             * SurfaceView surface has been destroyed.
             */
            Log.e(TAG, "eglMakeCurrent failed " + GLUtils.getEGLErrorString(egl.eglGetError()));
            return false;
        }

        return true;
    }

    private void checkCurrent() {
        if (!eglContext.equals(egl.eglGetCurrentContext())
            || !eglSurface.equals(egl
            .eglGetCurrentSurface(EGL10.EGL_DRAW))) {
            checkEglError();
            if (!egl.eglMakeCurrent(eglDisplay, eglSurface,
                eglSurface, eglContext)) {
                throw new RuntimeException(
                    "eglMakeCurrent failed "
                        + GLUtils.getEGLErrorString(egl
                        .eglGetError()));
            }
            checkEglError();
        }
    }

    private void checkEglError() {
        final int error = egl.eglGetError();
        if (error != EGL10.EGL_SUCCESS) {
            Log.e("PanTextureView", "EGL error = 0x" + Integer.toHexString(error));
        }
    }
    private void finishGL() {
        egl.eglDestroyContext(eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);
        egl.eglDestroySurface(eglDisplay, eglSurface);
    }

    private void initGL() {
        egl = (EGL10) EGLContext.getEGL();

        eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed "
                + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        int[] version = new int[2];
        if (!egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed " +
                GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        eglConfig = chooseEglConfig();
        if (eglConfig == null) {
            throw new RuntimeException("eglConfig not initialized");
        }

        eglContext = createContext(egl, eglDisplay, eglConfig);

        createSurface();

        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw new RuntimeException("eglMakeCurrent failed "
                + GLUtils.getEGLErrorString(egl.eglGetError()));
        }

        gl = eglContext.getGL();
    }


    EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay, EGLConfig eglConfig) {
        int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
        return egl.eglCreateContext(eglDisplay, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
    }

    private EGLConfig chooseEglConfig() {
        int[] configsCount = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        int[] configSpec = getConfig();
        if (!egl.eglChooseConfig(eglDisplay, configSpec, configs, 1, configsCount)) {
            throw new IllegalArgumentException("eglChooseConfig failed " +
                GLUtils.getEGLErrorString(egl.eglGetError()));
        } else if (configsCount[0] > 0) {
            return configs[0];
        }
        return null;
    }

    private int[] getConfig() {
        return new int[] {
            EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
            EGL10.EGL_RED_SIZE, 8,
            EGL10.EGL_GREEN_SIZE, 8,
            EGL10.EGL_BLUE_SIZE, 8,
            EGL10.EGL_ALPHA_SIZE, 8,
            EGL10.EGL_DEPTH_SIZE, 16,
            EGL10.EGL_STENCIL_SIZE, 0,
            EGL10.EGL_NONE
        };
    }
}