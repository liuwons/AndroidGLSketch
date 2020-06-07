package com.example.gltest.renderer;

import android.content.Context;
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

    private int mUniformColorHandle;
    private int mUniformBzPosHandle;
    private int mUniformBzCtrlHandle;
    private int mAttrTDataHandle;

    private static final int BZ_ARRAY_LEN = 128;
    private float[] mBzTValArray = new float[BZ_ARRAY_LEN];

    public PathRenderer(Context context,
                        RenderModel model,
                        FloatBuffer vertexBuffer,
                        FloatBuffer colorBuffer) {
        super(context, model, vertexBuffer, colorBuffer);

        mBzTValArray[0] = 0f;
        float stride = 1.0f / BZ_ARRAY_LEN;
        for (int i = 1; i < BZ_ARRAY_LEN; i ++) {
            mBzTValArray[i] = mBzTValArray[i-1] + stride;
        }
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

        for (Path path : mModel.paths) {
            drawPath(path);
        }

        if (mModel.currentShape != null
            && mModel.currentShape instanceof Path
            && mModel.currentShape.valid()) {
            drawPath((Path) mModel.currentShape);
        }
    }

    private void drawPath(Path path) {
        if (path == null || !path.valid()) {
            return;
        }
        List<Path.CubicBezier> bezierLst = path.calcBezierLines();
        for (Path.CubicBezier bezier : bezierLst) {
            drawCubicBezierLine(path.color, bezier.pos, bezier.ctrl);
        }
    }

    private void drawCubicBezierLine(float[] color, float[] pos, float[] ctrl) {
        Log.d(TAG, "drawCubicBezierLine:  [color]" + floatArr2Str(color) + "  [pos]" + floatArr2Str(pos) + "  [ctrl]" + floatArr2Str(ctrl));
        mUniformColorHandle = GLES20.glGetUniformLocation(mProgram, "u_Color");
        mUniformBzPosHandle = GLES20.glGetUniformLocation(mProgram, "u_BzPos");
        mUniformBzCtrlHandle = GLES20.glGetUniformLocation(mProgram, "u_BzCtrl");
        mAttrTDataHandle = GLES20.glGetAttribLocation(mProgram, "a_TData");

        GLES20.glUniform4fv(mUniformColorHandle, 1, color, 0);
        GLES20.glUniform4fv(mUniformBzPosHandle, 1, pos, 0);
        GLES20.glUniform4fv(mUniformBzCtrlHandle, 1, ctrl, 0);

        mVertexBuffer.position(0);
        mVertexBuffer.put(mBzTValArray);

        mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(mAttrTDataHandle);
        GLES20.glVertexAttribPointer(mAttrTDataHandle, 1, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, BZ_ARRAY_LEN);

        GLES20.glDisableVertexAttribArray(mAttrTDataHandle);
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
