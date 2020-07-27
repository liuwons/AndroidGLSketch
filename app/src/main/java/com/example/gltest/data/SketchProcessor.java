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

    private PointF mScalePointer1 = new PointF();
    private PointF mScalePointer2 = new PointF();

    private PointF mDragStart = new PointF();

    private boolean scaling = false;
    private boolean draging = false;

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
        mRenderData.changeAxis(new PointF(x, y), w, h);
    }

    public void onDestroy() {
        mWidth = 0;
        mHeight = 0;
    }

    public void setDraging(boolean d) {
        draging = d;
    }

    public boolean draging() {
        return draging;
    }

    public void setScaling(boolean s) {
        scaling = s;
    }

    public boolean scaling() {
        return scaling;
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

        if (scaling()) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_MOVE) {
                if (event.getPointerCount() > 1) {
                    // calc scale
                    double scale = calcScale(new PointF(event.getX(0), event.getY(0)),
                        new PointF(event.getX(1), event.getY(1)));
                    Operation operation = mRenderData.obtain();
                    operation.command = Operation.Command.SCALE;
                    operation.position.x = (float)scale;
                    mRenderData.appendOpreation(operation);
                }
            } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
                mScalePointer1.set(event.getX(0), event.getY(0));
                mScalePointer2.set(event.getX(1), event.getY(1));
            } else if (action == MotionEvent.ACTION_POINTER_UP) {
                if (event.getPointerCount() > 2) {
                    if (event.getAction() >> 8 == 0) { // pointer 0 up
                        mScalePointer1.set(event.getX(1), event.getY(1));
                        mScalePointer2.set(event.getX(2), event.getY(2));
                    } else if (event.getAction() >> 8 == 1) { // pointer 1 up
                        mScalePointer1.set(event.getX(0), event.getY(0));
                        mScalePointer2.set(event.getX(2), event.getY(2));
                    }
                }
            }
        } else if (draging()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mDragStart.set(x, y);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Operation operation = mRenderData.obtain();
                operation.command = Operation.Command.TRANSLATE;
                operation.position.set(x - mDragStart.x, y - mDragStart.y);
                mRenderData.appendOpreation(operation);
            }
        } else {
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
        }

        return true;
    }

    private double calcScale(PointF p1, PointF p2) {
        double originLen = Math.hypot(mScalePointer1.x - mScalePointer2.x, mScalePointer1.y - mScalePointer2.y);
        if (originLen < 0.0001) {
            return 1;
        }
        double len = Math.hypot(p1.x - p2.x, p1.y - p2.y);
        return len / originLen;
    }

    private float translateX(float rawX) {
        return (rawX - mWidth / 2.0f) * mScale;
    }

    private float translateY(float rawY) {
        return (mHeight / 2.0f - rawY) * mScale;
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
        MODE_PATH,
        MODE_ROUND
    }

    public enum SketchColor {
        COLOR_RED,
        COLOR_YELLOW,
        COLOR_GREEN,
        COLOR_BLUE,
        COLOR_PURPLE
    }

}
