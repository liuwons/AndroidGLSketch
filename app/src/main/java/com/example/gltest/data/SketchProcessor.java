package com.example.gltest.data;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.example.gltest.shape.Arrow;
import com.example.gltest.shape.BaseShape;
import com.example.gltest.shape.Line;
import com.example.gltest.shape.Oval;
import com.example.gltest.shape.Path;
import com.example.gltest.shape.Rect;

public class SketchProcessor implements View.OnTouchListener {
    private static final String TAG = SketchProcessor.class.getSimpleName();

    private static final float[] COLOR_RED = {0.96f, 0.29f, 0.27f, 1.0f};
    private static final float[] COLOR_YELLOW = {1.0f, 0.78f, 0.04f, 1.0f};
    private static final float[] COLOR_GREEN = {0.2f, 0.78f, 0.14f, 1.0f};
    private static final float[] COLOR_BLUE = {0.2f, 0.44f, 1.0f, 1.0f};
    private static final float[] COLOR_PURPLE = {0.5f, 0.23f, 0.96f, 1.0f};

    private SketchMode mCurrentMode = SketchMode.MODE_LINE;
    private SketchColor mCurrentColor = SketchColor.COLOR_RED;
    private RenderModel mRenderData;

    private int mWidth;
    private int mHeight;

    public SketchProcessor(RenderModel data) {
        mRenderData = data;
    }

    public void onAvailable(int w, int h) {
        mWidth = w;
        mHeight = h;
    }

    public void onSizeChanged(int w, int h) {
        mWidth = w;
        mHeight = h;
    }

    public void onDestroy() {
        mWidth = 0;
        mHeight = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mWidth == 0 || mHeight == 0) {
            Log.w(TAG, "onTouch skipped");
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        x = 2 * x / mWidth - 1.0f;
        y = 1.0f - 2 * y / mHeight;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mRenderData.currentShape != null) {
                mRenderData.archieve();
            }
            mRenderData.currentShape = createShape();
            mRenderData.currentShape.onStart(x, y);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mRenderData.currentShape == null) {
                mRenderData.currentShape = createShape();
                mRenderData.currentShape.onStart(x, y);
            } else {
                mRenderData.currentShape.onMove(x, y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (mRenderData.currentShape != null) {
                mRenderData.currentShape.onFinish(x, y);
                mRenderData.archieve();
            }
        }

        return true;
    }

    public void setMode(SketchMode mode) {
        mCurrentMode = mode;
    }

    public void setColor(SketchColor color) {
        mCurrentColor = color;
    }

    private BaseShape createShape() {
        Log.d(TAG, "createShape");
        if (mCurrentMode == SketchMode.MODE_ARROW) {
            Log.d(TAG, "create arrow");
            return new Arrow(getColor());
        } else if (mCurrentMode == SketchMode.MODE_LINE) {
            Log.d(TAG, "create line");
            return new Line(getColor());
        } else if (mCurrentMode == SketchMode.MODE_OVAL) {
            Log.d(TAG, "create oval");
            return new Oval(getColor());
        } else if(mCurrentMode == SketchMode.MODE_PATH) {
            Log.d(TAG, "create path");
            return new Path(getColor());
        } else {
            Log.d(TAG, "create rect");
            return new Rect(getColor());
        }
    }

    private float[] getColor() {
        if (mCurrentColor == SketchColor.COLOR_RED) {
            return COLOR_RED;
        } else if (mCurrentColor == SketchColor.COLOR_YELLOW) {
            return COLOR_YELLOW;
        } else if (mCurrentColor == SketchColor.COLOR_GREEN) {
            return COLOR_GREEN;
        } else if (mCurrentColor == SketchColor.COLOR_BLUE) {
            return COLOR_BLUE;
        } else if (mCurrentColor == SketchColor.COLOR_PURPLE) {
            return COLOR_PURPLE;
        }
        return COLOR_RED;
    }

    public enum SketchMode {
        MODE_ARROW,
        MODE_LINE,
        MODE_OVAL,
        MODE_RECT,
        MODE_PATH
    }

    public enum SketchColor {
        COLOR_RED,
        COLOR_YELLOW,
        COLOR_GREEN,
        COLOR_BLUE,
        COLOR_PURPLE
    }

}
