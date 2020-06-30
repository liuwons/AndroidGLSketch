package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES20;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.BaseShape;
import com.example.gltest.shape.Path;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class PathRenderer extends BaseRenderer {
    private static final String TAG = PathRenderer.class.getSimpleName();

    private static final String SHADER_BZ_VERT = "bz_vert.glsl";
    private static final String SHADER_BZ_FRAG = "bz_frag.glsl";



    public PathRenderer(Context context,
                        RenderModel model,
                        FloatBuffer vertexBuffer,
                        IntBuffer indexBuffer) {
        super(context, model, vertexBuffer, indexBuffer);
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
        mIndexBuffer.position(0);

        int vertexCount = 0;
        for (BaseShape shape : mModel.shapes) {
            if (shape instanceof  Path) {
                vertexCount += shape.dumpTriangles(vertexCount, mVertexBuffer, mIndexBuffer);
            }
        }

        if (mModel.currentShape != null
            && mModel.currentShape instanceof Path
            && mModel.currentShape.valid()) {
            vertexCount += dumpPath(vertexCount, (Path) mModel.currentShape);
        }

        if (vertexCount < 1) {
            return;
        }

        GLES20.glUseProgram(mProgram);

        int matrixHandle = GLES20.glGetUniformLocation(mProgram, "u_Matrix");
        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mModel.currentMatrix, 0);

        int stride = 15 * 4;
        int colorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
        mVertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int bzPosHandle = GLES20.glGetAttribLocation(mProgram, "a_BzPos");
        mVertexBuffer.position(4);
        GLES20.glEnableVertexAttribArray(bzPosHandle);
        GLES20.glVertexAttribPointer(bzPosHandle, 4, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int bzCtrlHandle = GLES20.glGetAttribLocation(mProgram, "a_BzCtrl");
        mVertexBuffer.position(8);
        GLES20.glEnableVertexAttribArray(bzCtrlHandle);
        GLES20.glVertexAttribPointer(bzCtrlHandle, 4, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int tDataHandle = GLES20.glGetAttribLocation(mProgram, "a_TData");
        mVertexBuffer.position(12);
        GLES20.glEnableVertexAttribArray(tDataHandle);
        GLES20.glVertexAttribPointer(tDataHandle, 1, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int lineWidthHandle = GLES20.glGetAttribLocation(mProgram, "a_LineWidth");
        mVertexBuffer.position(13);
        GLES20.glEnableVertexAttribArray(lineWidthHandle);
        GLES20.glVertexAttribPointer(lineWidthHandle, 1, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int zHandle = GLES20.glGetAttribLocation(mProgram, "a_ZVal");
        mVertexBuffer.position(14);
        GLES20.glEnableVertexAttribArray(zHandle);
        GLES20.glVertexAttribPointer(zHandle, 1, GLES20.GL_FLOAT, false, stride, mVertexBuffer);

        int indexCount = mIndexBuffer.position();
        mIndexBuffer.position(0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_INT, mIndexBuffer);

        GLES20.glDisableVertexAttribArray(colorHandle);
        GLES20.glDisableVertexAttribArray(bzPosHandle);
        GLES20.glDisableVertexAttribArray(bzCtrlHandle);
        GLES20.glDisableVertexAttribArray(tDataHandle);
        GLES20.glDisableVertexAttribArray(lineWidthHandle);
        GLES20.glDisableVertexAttribArray(zHandle);
    }

    private int dumpPath(int vertexPos, Path path) {
        if (path == null || !path.valid()) {
            return 0;
        }

        int vertexCount = 0;

        List<Path.BezierVertex> vertexList = path.dump2VertexList();
        for (int i = 0; i < vertexList.size(); i ++) {
            Path.BezierVertex vertex = vertexList.get(i);
            float t = vertex.t;
            if (vertex.vertexType == Path.BezierVertex.VertexType.START) {
                t = 0.00001f;
            } else if (vertex.vertexType == Path.BezierVertex.VertexType.END){
                t = 10000f;
            }

            mVertexBuffer.put(path.color);
            mVertexBuffer.put(vertex.position);
            mVertexBuffer.put(vertex.ctrl);
            mVertexBuffer.put(t);
            mVertexBuffer.put(path.lineWidth);
            mVertexBuffer.put(path.getZ());

            mVertexBuffer.put(path.color);
            mVertexBuffer.put(vertex.position);
            mVertexBuffer.put(vertex.ctrl);
            mVertexBuffer.put(-t);
            mVertexBuffer.put(path.lineWidth);
            mVertexBuffer.put(path.getZ());

            vertexCount += 2;

            if (i != 0) {
                int startPos = vertexPos + (i-1)*2;
                mIndexBuffer.put(startPos);
                mIndexBuffer.put(startPos + 1);
                mIndexBuffer.put(startPos + 2);
                mIndexBuffer.put(startPos + 1);
                mIndexBuffer.put(startPos + 2);
                mIndexBuffer.put(startPos + 3);
            }
        }

        return vertexCount;
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
