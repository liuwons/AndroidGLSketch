package com.example.gltest;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderImpl implements GLSurfaceView.Renderer {
    private static String TAG = GLRenderImpl.class.getSimpleName();

    private static final String SHADER_ARROW_VERT = "arrow_vert.glsl";
    private static final String SHADER_ARROW_FRAG = "arrow_frag.glsl";

    private int mProgram;
    private int mWidth;
    private int mHeight;
    private Context mContext;

    private FloatBuffer mVertexBuffer;

    float triangleCoords[] = {
        0.5f,  0.5f, 0.0f, // top
        -0.5f, -0.5f, 0.0f, // bottom left
        0.5f, -0.5f, 0.0f  // bottom right
    };

    float color[] = { 1.0f, 1.0f, 1.0f, 1.0f };

    public GLRenderImpl(Context context) {
        mContext = context;
    }

    public void initGL() {
        comipleAndLinkProgram();

        //申请底层空间
        ByteBuffer bb = ByteBuffer.allocateDirect(
            triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        //将坐标数据转换为FloatBuffer，用以传入给OpenGL ES程序
        mVertexBuffer = bb.asFloatBuffer();
        mVertexBuffer.put(triangleCoords);
        mVertexBuffer.position(0);

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
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

        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer);

        int colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
        GLES20.glUniform4fv(colorHandle, 1, color, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(positionHandle);
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

        // Bind vPosition to attribute 0
        GLES20.glBindAttribLocation(programObject, 0, "a_position");
        GLES20.glBindAttribLocation(programObject, 1, "a_texCoords");

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
