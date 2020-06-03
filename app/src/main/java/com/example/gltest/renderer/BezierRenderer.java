package com.example.gltest.renderer;

import android.content.Context;
import com.example.gltest.data.RenderModel;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BezierRenderer extends BaseRenderer {

    private static final String SHADER_BZ_VERT = "bz_vert.glsl";
    private static final String SHADER_BZ_FRAG = "bz_frag.glsl";

    public BezierRenderer(Context context,
                          RenderModel model,
                          FloatBuffer vertexBuffer,
                          FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, colorBuffer);
    }

    @Override
    protected String getVertexShaderAssetPath() {
        return SHADER_BZ_VERT;
    }

    @Override
    protected String getFragmentShaderAssetPath() {
        return SHADER_BZ_FRAG;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        super.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
    }
}
