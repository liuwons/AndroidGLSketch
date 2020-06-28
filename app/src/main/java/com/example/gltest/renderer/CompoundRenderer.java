package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES20;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.BaseShape;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CompoundRenderer extends BaseRenderer {
    private static final String SHADER_VERT = "vert.glsl";
    private static final String SHADER_FRAG = "frag.glsl";

    public CompoundRenderer(Context context,
                            RenderModel model,
                            FloatBuffer vertexBuffer,
                            IntBuffer indexBuffer,
                            FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, indexBuffer, colorBuffer);
    }

    @Override
    protected String getVertexShaderAssetPath() {
        return SHADER_VERT;
    }

    @Override
    protected String getFragmentShaderAssetPath() {
        return SHADER_FRAG;
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

        int axisScaleHandle = GLES20.glGetUniformLocation(mProgram, "u_AxisScale");
        GLES20.glUniform1f(axisScaleHandle, mModel.axisScale);
        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mModel.currentMatrix, 0);
        int lineBorderWidthHandle = GLES20.glGetUniformLocation(mProgram, "u_BorderWidth");
        GLES20.glUniform1f(lineBorderWidthHandle, mModel.lineBorderWidth);
        int windowWidthHandle = GLES20.glGetUniformLocation(mProgram, "u_WindowWidth");
        GLES20.glUniform1f(windowWidthHandle, mModel.viewWidth);
        int windowHeightHandle = GLES20.glGetUniformLocation(mProgram, "u_WindowHeight");
        GLES20.glUniform1f(windowHeightHandle, mModel.viewHeight);

        mVertexBuffer.position(0);
        mIndexBuffer.position(0);
        mColorBuffer.position(0);

        int vertexCount = 0;
        for (BaseShape shape : mModel.shapes) {
            vertexCount += shape.dumpTriangles(vertexCount, mVertexBuffer, mIndexBuffer);
        }

        if (mModel.currentShape != null && mModel.currentShape.valid()) {
            mModel.currentShape.dumpTriangles(vertexCount, mVertexBuffer, mIndexBuffer);
        }

        if (vertexCount < 1) {
            return;
        }

        int stride = 15;
        mVertexBuffer.position(0);
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 4, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);

        mVertexBuffer.position(4);
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);

        mVertexBuffer.position(8);
        int widthHandle = GLES20.glGetAttribLocation(mProgram, "vLineWidth");
        GLES20.glEnableVertexAttribArray(widthHandle);
        GLES20.glVertexAttribPointer(widthHandle, 1, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);

        mVertexBuffer.position(9);
        int idHandle = GLES20.glGetAttribLocation(mProgram, "vPointIndicator");
        GLES20.glEnableVertexAttribArray(idHandle);
        GLES20.glVertexAttribPointer(idHandle, 1, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);

        mVertexBuffer.position(10);
        int zHandle = GLES20.glGetAttribLocation(mProgram, "vZ");
        GLES20.glEnableVertexAttribArray(zHandle);
        GLES20.glVertexAttribPointer(zHandle, 1, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);

        mVertexBuffer.position(11);
        int ctrlHandle = GLES20.glGetAttribLocation(mProgram, "vCtrl");
        GLES20.glEnableVertexAttribArray(ctrlHandle);
        GLES20.glVertexAttribPointer(ctrlHandle, 4, GLES20.GL_FLOAT, false, stride * 4, mVertexBuffer);


        int indexCount = mIndexBuffer.position();
        mIndexBuffer.position(0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_INT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(widthHandle);
        GLES20.glDisableVertexAttribArray(idHandle);
        GLES20.glDisableVertexAttribArray(zHandle);
        GLES20.glDisableVertexAttribArray(ctrlHandle);
    }


}
