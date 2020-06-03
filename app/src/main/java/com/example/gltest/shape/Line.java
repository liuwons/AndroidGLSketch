package com.example.gltest.shape;

import android.util.Log;

public class Line extends BaseShape {
    private static final String TAG = Line.class.getSimpleName();

    public float[] postion = new float[4];
    private boolean mStarted = false;
    private boolean mMoved = false;

    public Line(float[] c) {
        color = c;
    }

    @Override
    public void onStart(float x, float y) {
        Log.d(TAG, "onStart  [x]" + x + "  [y]" + y);
        postion[0] = x;
        postion[1] = y;
        mStarted = true;
    }

    @Override
    public void onMove(float x, float y) {
        Log.d(TAG, "onMove  [x]" + x + "  [y]" + y);
        postion[2] = x;
        postion[3] = y;
        mMoved = true;
    }

    @Override
    public void onFinish(float x, float y) {
        Log.d(TAG, "onFinish  [x]" + x + "  [y]" + y);
        postion[2] = x;
        postion[3] = y;
        mMoved = true;
    }

    @Override
    public boolean valid() {
        return mStarted && mMoved;
    }
}
