package com.example.gltest.data;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SketchProcessor implements View.OnTouchListener {
    private static final String TAG = SketchProcessor.class.getSimpleName();

    private RenderModel mRenderData;

    private int mWidth;
    private int mHeight;
    private float mScale = -1f;

    public SketchProcessor(RenderModel data) {
        mRenderData = data;
    }

    public void onAvailable(int w, int h) {
        onSizeChanged(w, h);
    }

    public void onSizeChanged(int w, int h) {
        Log.d(TAG, "onSizeChanged:  [w]" + w + "  [h]" + h);
        mWidth = w;
        mHeight = h;

        float x = 1.0f;
        float y = 1.0f;

        if (mWidth > mHeight) {
            x = mWidth * 1.0f / mHeight;
        } else {
            y = mHeight * 1.0f / mWidth;
        }

        mScale = x * 2.0f / mWidth;
        mRenderData.changeAxis(new PointF(x, y));
    }

    public void onDestroy() {
        mWidth = 0;
        mHeight = 0;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mWidth == 0 || mHeight == 0 || mScale < 0) {
            Log.w(TAG, "onTouch skipped");
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        x = (x - mWidth / 2.0f) * mScale;
        y = (mHeight / 2.0f - y) * mScale;

        Operation operation = mRenderData.obtain();
        operation.command = Operation.Command.TOUCH_EVENT;
        operation.position.x = x;
        operation.position.y = y;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            operation.touchAction = Operation.TouchAction.MOVE;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            operation.touchAction = Operation.TouchAction.MOVE;
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            operation.touchAction = Operation.TouchAction.UP;
        }

        mRenderData.appendOpreation(operation);

        return true;
    }

    public void setMode(SketchMode mode) {
        mRenderData.changeMode(mode);
    }

    public void setColor(SketchColor color) {
        mRenderData.changeColor(RenderModel.getColor(color));
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
