package com.example.gltest.shape;

import android.graphics.PointF;
import android.util.Log;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Path extends BaseShape {
    private static final String TAG = Path.class.getSimpleName();

    private static final float CTRL_VALUE_A = 0.2f;
    private static final float CTRL_VALUE_B = 0.2f;

    public List<PointF> points = new ArrayList<>();

    private List<CubicBezier> mCubicBeziers = new LinkedList<>();
    private volatile boolean mBeziersNeedUpdate = true;

    public Path(float[] c) {
        color = c;
    }

    @Override
    public void onStart(float x, float y) {
        Log.d(TAG, "onStart:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
        points.add(new PointF(x, y));
    }

    @Override
    public void onMove(float x, float y) {
        Log.d(TAG, "onMove:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
        mBeziersNeedUpdate = true;
    }

    @Override
    public void onFinish(float x, float y) {
        Log.d(TAG, "onFinish:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
        points.add(new PointF(x, y));
        mBeziersNeedUpdate = true;
    }

    @Override
    public boolean valid() {
        return points.size() > 3;
    }

    public List<CubicBezier> calcBezierLines() {
        if (!mBeziersNeedUpdate) {
            return mCubicBeziers;
        }

        mCubicBeziers.clear();
        int lineCount = points.size() - 3;
        if (lineCount < 1) {
            return mCubicBeziers;
        }
        for (int i = 1; i < points.size()-2; i ++) {
            PointF ctrl1 = new PointF();
            PointF ctrl2 = new PointF();
            calcCtrlPoint(i, ctrl1, ctrl2);
            mCubicBeziers.add(new CubicBezier(points.get(i), points.get(i+1), ctrl1, ctrl2));
        }

        mBeziersNeedUpdate = false;
        return mCubicBeziers;
    }

    private void calcCtrlPoint(int currentIndex, PointF ctrl1, PointF ctrl2) {
        ctrl1.x = points.get(currentIndex).x +
            (points.get(currentIndex + 1).x - points.get(currentIndex - 1).x) * CTRL_VALUE_A;
        ctrl1.y = points.get(currentIndex).y +
            (points.get(currentIndex + 1).y - points.get(currentIndex - 1).y) * CTRL_VALUE_A;
        ctrl2.x = points.get(currentIndex + 1).x -
            (points.get(currentIndex + 2).x - points.get(currentIndex).x) * CTRL_VALUE_B;
        ctrl2.y = points.get(currentIndex + 1).y -
            (points.get(currentIndex + 2).y - points.get(currentIndex).y) * CTRL_VALUE_B;
    }

    public static class CubicBezier {
        public float[] pos = new float[4];
        public float[] ctrl = new float[4];

        public CubicBezier(PointF s, PointF e, PointF c1, PointF c2) {
            pos[0] = s.x;
            pos[1] = s.y;
            pos[2] = e.x;
            pos[3] = e.y;
            ctrl[0] = c1.x;
            ctrl[1] = c1.y;
            ctrl[2] = c2.x;
            ctrl[3] = c2.y;
        }
    }

}
