package com.example.gltest.shape;

import android.graphics.PointF;

public class Oval extends BaseShape {
    public PointF position;
    public float longAxis;
    public float shortAxis;

    public Oval(float[] c) {
        super(c);
    }

}
