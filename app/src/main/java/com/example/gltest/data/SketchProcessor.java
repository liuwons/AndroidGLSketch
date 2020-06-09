package com.example.gltest.data;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SketchProcessor implements View.OnTouchListener {
    private static final String TAG = SketchProcessor.class.getSimpleName();

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
