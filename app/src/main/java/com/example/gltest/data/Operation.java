package com.example.gltest.data;

import android.graphics.PointF;

public class Operation {

    public enum Command {
        CHANGE_COLOR,
        CHANGE_SHAPE,
        CHANGE_AXIS,
        TOUCH_EVENT
    }

    public enum TouchAction {
        DOWN,
        MOVE,
        UP
    }

    public Command command;
    public TouchAction touchAction;
    public SketchProcessor.SketchMode mode;
    public PointF position = new PointF();
    public float[] color = new float[4];
}
