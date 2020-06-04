package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES20;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.Line;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PathRenderer extends BaseRenderer {

    private static final String SHADER_BZ_VERT = "bz_vert.glsl";
    private static final String SHADER_BZ_FRAG = "bz_frag.glsl";

    public PathRenderer(Context context,
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

        GLES20.glUseProgram(mProgram);

        GLES20.glLineWidth(10f);
        GLES20.glHint(GLES10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);

        mVertexBuffer.position(0);
        mColorBuffer.position(0);
        int lineCount = 0;
        synchronized (mModel) {
            lineCount = mModel.lines.size();
            for (Line line : mModel.lines) {
                mVertexBuffer.put(line.postion);
                mColorBuffer.put(line.color);
                mColorBuffer.put(line.color);
            }
            if (mModel.currentShape instanceof Line && mModel.currentShape.valid()) {
                mVertexBuffer.put(((Line)mModel.currentShape).postion);
                mColorBuffer.put(((Line)mModel.currentShape).color);
                mColorBuffer.put(((Line)mModel.currentShape).color);
                lineCount += 1;
            }
        }

        mVertexBuffer.position(0);
        mColorBuffer.position(0);

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer);

        GLES20.glDrawArrays(GLES20.GL_LINES, 0, lineCount*2);
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
    }
}
