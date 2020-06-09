package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.util.Log;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.Path;
import java.nio.FloatBuffer;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PathRenderer extends BaseRenderer {
    private static final String TAG = PathRenderer.class.getSimpleName();

    private static final String SHADER_BZ_VERT = "bz_vert.glsl";
    private static final String SHADER_BZ_FRAG = "bz_frag.glsl";

    private int mAttrColorHandle;
    private int mAttrBzPosHandle;
    private int mAttrBzCtrlHandle;
    private int mAttrTDataHandle;

    private static final int BZ_ARRAY_LEN = 4;
    private float[] mBzTValArray = new float[BZ_ARRAY_LEN];

    public PathRenderer(Context context,
                        RenderModel model,
                        FloatBuffer vertexBuffer,
                        FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, colorBuffer);

        mBzTValArray[0] = 0f;
        float stride = 1.0f / BZ_ARRAY_LEN;
        for (int i = 1; i < BZ_ARRAY_LEN-1; i ++) {
            mBzTValArray[i] = mBzTValArray[i-1] + stride;
        }
        mBzTValArray[BZ_ARRAY_LEN-1] = 1.0f;
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

        mVertexBuffer.position(0);

        int pointCount = 0;
        for (Path path : mModel.paths) {
            pointCount += dumpPath(path);
        }

        if (mModel.currentShape != null
            && mModel.currentShape instanceof Path
            && mModel.currentShape.valid()) {
            pointCount += dumpPath((Path) mModel.currentShape);
        }

        GLES20.glUseProgram(mProgram);
        GLES20.glLineWidth(10f);

        mAttrColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        mAttrBzPosHandle = GLES20.glGetAttribLocation(mProgram, "a_BzPos");
        mAttrBzCtrlHandle = GLES20.glGetAttribLocation(mProgram, "a_BzCtrl");
        mAttrTDataHandle = GLES20.glGetAttribLocation(mProgram, "a_TData");

        mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(mAttrColorHandle);
        GLES20.glVertexAttribPointer(mAttrColorHandle, 4, GLES20.GL_FLOAT, false, 4 * 13, mVertexBuffer);

        mVertexBuffer.position(4);
        GLES20.glEnableVertexAttribArray(mAttrBzPosHandle);
        GLES20.glVertexAttribPointer(mAttrBzPosHandle, 4, GLES20.GL_FLOAT, false, 4 * 13, mVertexBuffer);

        mVertexBuffer.position(8);
        GLES20.glEnableVertexAttribArray(mAttrBzCtrlHandle);
        GLES20.glVertexAttribPointer(mAttrBzCtrlHandle, 4, GLES20.GL_FLOAT, false, 4 * 13, mVertexBuffer);

        mVertexBuffer.position(12);
        GLES20.glEnableVertexAttribArray(mAttrTDataHandle);
        GLES20.glVertexAttribPointer(mAttrTDataHandle, 1, GLES20.GL_FLOAT, false, 4 * 13, mVertexBuffer);

        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, pointCount);

        GLES20.glDisableVertexAttribArray(mAttrColorHandle);
        GLES20.glDisableVertexAttribArray(mAttrBzPosHandle);
        GLES20.glDisableVertexAttribArray(mAttrBzCtrlHandle);
        GLES20.glDisableVertexAttribArray(mAttrTDataHandle);
    }

    private int dumpPath(Path path) {
        if (path == null || !path.valid()) {
            return 0;
        }
        int pointCount = 0;
        List<Path.CubicBezier> bezierLst = path.calcBezierLines();
        for (Path.CubicBezier bezier : bezierLst) {
            pointCount += dumpCubicBezierLine(path.color, bezier.pos, bezier.ctrl);
        }
        return pointCount;
    }

    private int dumpCubicBezierLine(float[] color, float[] pos, float[] ctrl) {
        for (int i = 0; i < BZ_ARRAY_LEN; i ++) {
            mVertexBuffer.put(color);
            mVertexBuffer.put(pos);
            mVertexBuffer.put(ctrl);
            mVertexBuffer.put(mBzTValArray[i]);
        }
        return BZ_ARRAY_LEN;
    }

    private String floatArr2Str(float[] data) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (int i = 0; i < data.length; i ++) {
            stringBuilder.append(data[i]);
            if (i != data.length-1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
