package com.example.gltest.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import com.example.gltest.FileUtils;
import com.example.gltest.data.RenderModel;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = BaseRenderer.class.getSimpleName();

    protected Context mContext;
    protected RenderModel mModel;
    protected FloatBuffer mVertexBuffer;
    protected IntBuffer mIndexBuffer;

    protected int mProgram;

    public BaseRenderer(Context context, RenderModel model, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        mContext = context;
        mModel = model;
        mVertexBuffer = vertexBuffer;
        mIndexBuffer = indexBuffer;
    }

    protected String getVertexShaderAssetPath() {
        return "";
    }

    protected String getFragmentShaderAssetPath() {
        return "";
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        comipleAndLinkProgram();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {

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
        String shaderVertAssetPath = getVertexShaderAssetPath();
        String shaderFragAssetPath = getFragmentShaderAssetPath();
        Log.d(TAG, "load shader:  [vert]" + shaderVertAssetPath + "  [frag]" + shaderFragAssetPath);

        String shaderVert = FileUtils.loadAssetFile(mContext, shaderVertAssetPath);
        String shaderFrag = FileUtils.loadAssetFile(mContext, shaderFragAssetPath);

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, shaderVert);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, shaderFrag);

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
