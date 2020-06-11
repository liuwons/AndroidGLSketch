package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.Line;
import com.example.gltest.shape.Rect;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LineRenderer extends BaseRenderer {
    private static final String TAG = LineRenderer.class.getSimpleName();

    private static final String SHADER_LINE_VERT = "line_vert.glsl";
    private static final String SHADER_LINE_FRAG = "line_frag.glsl";

    public LineRenderer(Context context, RenderModel model, FloatBuffer vertexBuffer, IntBuffer indexBuffer, FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, indexBuffer, colorBuffer);
    }

    @Override
    protected String getVertexShaderAssetPath() {
        return SHADER_LINE_VERT;
    }

    @Override
    protected String getFragmentShaderAssetPath() {
        return SHADER_LINE_FRAG;
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

        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mModel.currentMatrix, 0);

        mVertexBuffer.position(0);
        mIndexBuffer.position(0);
        mColorBuffer.position(0);


        int vertexCount = 0;
        // lines
        for (Line line : mModel.lines) {
            vertexCount += line.dumpTriangleData(vertexCount, mVertexBuffer, mIndexBuffer);
        }

        // rectangles
        for (Rect rect : mModel.rects) {
            vertexCount += rect.dumpTriangleData(mVertexBuffer, mIndexBuffer);
        }

        if (mModel.currentShape != null && mModel.currentShape.valid()) {
            if (mModel.currentShape instanceof Line) {
                vertexCount += ((Line) mModel.currentShape).dumpTriangleData(vertexCount, mVertexBuffer, mIndexBuffer);
            } else if (mModel.currentShape instanceof Rect) {
                vertexCount += ((Rect) mModel.currentShape).dumpTriangleData(mVertexBuffer, mIndexBuffer);
            }
        }

        mVertexBuffer.position(0);
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 4, GLES20.GL_FLOAT, false, 10 * 4, mVertexBuffer);

        mVertexBuffer.position(4);
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 10 * 4, mVertexBuffer);

        mVertexBuffer.position(8);
        int widthHandle = GLES20.glGetAttribLocation(mProgram, "vLineWidth");
        GLES20.glEnableVertexAttribArray(widthHandle);
        GLES20.glVertexAttribPointer(widthHandle, 1, GLES20.GL_FLOAT, false, 10 * 4, mVertexBuffer);

        mVertexBuffer.position(9);
        int idHandle = GLES20.glGetAttribLocation(mProgram, "vPointID");
        GLES20.glEnableVertexAttribArray(idHandle);
        GLES20.glVertexAttribPointer(idHandle, 1, GLES20.GL_FLOAT, false, 10 * 4, mVertexBuffer);

        int indexCount = mIndexBuffer.position();
        mIndexBuffer.position(0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_INT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(widthHandle);
        GLES20.glDisableVertexAttribArray(idHandle);
    }
}
