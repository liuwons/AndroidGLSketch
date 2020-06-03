package com.example.gltest.gl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.example.gltest.FileUtils;
import com.example.gltest.data.RenderModel;
import com.example.gltest.shape.Line;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderImpl implements GLSurfaceView.Renderer {
    private static String TAG = GLRenderImpl.class.getSimpleName();

    private static final long FRAME_LOG_INTV = 1000;

    private static final int SIZEOF_FLOAT = 4;
    private static final int BUFFER_SIZE = 1024;

    private long mLastFrameLogTime = 0;
    private int mLastFrameCount = 0;

    private static final String SHADER_ARROW_VERT = "line_vert.glsl";
    private static final String SHADER_ARROW_FRAG = "line_frag.glsl";

    private RenderModel mModel;

    private int mProgram;
    private int mWidth;
    private int mHeight;
    private Context mContext;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mColorBuffer;

    float color[] = { 1.0f, 0f, 0f, 1.0f };

    public GLRenderImpl(Context context, RenderModel model) {
        mContext = context;
        mModel = model;
    }

    public void initGL() {
        comipleAndLinkProgram();

        ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 2 * SIZEOF_FLOAT);
        vertexByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = vertexByteBuffer.asFloatBuffer();

        ByteBuffer colorByteBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE * 4 *SIZEOF_FLOAT);
        colorByteBuffer.order(ByteOrder.nativeOrder());
        mColorBuffer = colorByteBuffer.asFloatBuffer();

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void resize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        initGL();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged:  [w]" + width + "  [h]" + height);
        resize(width, height);
        GLES20.glViewport(0, 0, mWidth, mHeight);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);

        GLES20.glLineWidth(10f);

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

        mLastFrameCount += 1;
        if (System.currentTimeMillis() - mLastFrameLogTime > FRAME_LOG_INTV) {
            Log.d(TAG, "drawFrame  [frame rate]" + mLastFrameCount);
            mLastFrameLogTime = System.currentTimeMillis();
            mLastFrameCount = 0;
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

    private int loadShader(int shaderType, String shaderSource) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES20.glCreateShader(shaderType);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        GLES20.glShaderSource(shader, shaderSource);

        // Compile the shader
        GLES20.glCompileShader(shader);

        // Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    private void comipleAndLinkProgram() {
        String shaderArrowVert = FileUtils.loadAssetFile(mContext, SHADER_ARROW_VERT);
        String shaderArrowFrag = FileUtils.loadAssetFile(mContext, SHADER_ARROW_FRAG);

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, shaderArrowVert);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, shaderArrowFrag);

        // Create the program object
        programObject = GLES20.glCreateProgram();

        if (programObject == 0) {
            return;
        }

        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);

        // Link the program
        GLES20.glLinkProgram(programObject);

        // Check the link status
        GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES20.glGetProgramInfoLog(programObject));
            GLES20.glDeleteProgram(programObject);
            return;
        }

        mProgram = programObject;
    }
}
