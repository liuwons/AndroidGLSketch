package com.example.gltest.shape;

import android.util.Log;
import com.example.gltest.geom.Position;
import java.util.List;

public class Path extends BaseShape {
    private static final String TAG = Path.class.getSimpleName();

    public List<Position> points;

    public Path(float[] c) {
        color = c;
    }

    @Override
    public void onStart(float x, float y) {
        Log.d(TAG, "onStart:  [x]" + x + "  [y]" + y);
        points.add(new Position(x, y));
    }

    @Override
    public void onMove(float x, float y) {
        Log.d(TAG, "onMove:  [x]" + x + "  [y]" + y);
        points.add(new Position(x, y));
    }

    @Override
    public void onFinish(float x, float y) {
        Log.d(TAG, "onFinish:  [x]" + x + "  [y]" + y);
        points.add(new Position(x, y));
    }

    @Override
    public boolean valid() {
        return points.size() > 1;
    }
}
