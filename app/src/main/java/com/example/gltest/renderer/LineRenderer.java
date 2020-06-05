package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES20;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.Line;
import com.example.gltest.shape.Path;
import com.example.gltest.shape.Rect;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LineRenderer extends BaseRenderer {
    private static final String TAG = LineRenderer.class.getSimpleName();

    private static final String SHADER_LINE_VERT = "line_vert.glsl";
    private static final String SHADER_LINE_FRAG = "line_frag.glsl";

    public LineRenderer(Context context, RenderModel model, FloatBuffer vertexBuffer, FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, colorBuffer);
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

    private void dumpLineShaderData(Line line) {
        mVertexBuffer.put(line.postion);
        mColorBuffer.put(line.color);
        mColorBuffer.put(line.color);
    }

    private void dumpRectShaderData(Rect rect) {
        mVertexBuffer.put(rect.dumpVertex());
        for (int i = 0; i < 8; i ++) {
            mColorBuffer.put(rect.color);
        }
    }

    private int dumpPathShaderData(Path path) {
        return path.dumpLineData(mVertexBuffer, mColorBuffer);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);

        GLES20.glUseProgram(mProgram);

        GLES20.glLineWidth(10f);

        mVertexBuffer.position(0);
        mColorBuffer.position(0);


        int lineCount = 0;
        // lines
        lineCount += mModel.lines.size();
        for (Line line : mModel.lines) {
            dumpLineShaderData(line);
        }

        // rectangles
        lineCount += mModel.rects.size() * 4;
        for (Rect rect : mModel.rects) {
            dumpRectShaderData(rect);
        }

        // paths
        for (Path path: mModel.paths) {
            lineCount += dumpPathShaderData(path);
        }

        if (mModel.currentShape instanceof Line && mModel.currentShape.valid()) {
            dumpLineShaderData((Line)mModel.currentShape);
            lineCount += 1;
        } else if (mModel.currentShape instanceof Rect && mModel.currentShape.valid()) {
            dumpRectShaderData((Rect)mModel.currentShape);
            lineCount += 4;
        } else if (mModel.currentShape instanceof Path && mModel.currentShape.valid()) {
            lineCount += dumpPathShaderData((Path)mModel.currentShape);
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
