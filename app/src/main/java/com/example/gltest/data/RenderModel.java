package com.example.gltest.data;

import android.graphics.PointF;
import android.opengl.Matrix;
import android.util.Log;
import com.example.gltest.shape.Arrow;
import com.example.gltest.shape.BaseShape;
import com.example.gltest.shape.Line;
import com.example.gltest.shape.Oval;
import com.example.gltest.shape.Path;
import com.example.gltest.shape.Rect;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 渲染数据模型
 * 对model的修改都尽量放到渲染线程
 */
public class RenderModel {
    private static final String TAG = RenderModel.class.getSimpleName();

    private static final float LINE_BORDER_WIDTH_PX = 1f;

    private static final float[] COLOR_RED = {0.96f, 0.29f, 0.27f, 1.0f};
    private static final float[] COLOR_YELLOW = {1.0f, 0.78f, 0.04f, 1.0f};
    private static final float[] COLOR_GREEN = {0.2f, 0.78f, 0.14f, 1.0f};
    private static final float[] COLOR_BLUE = {0.2f, 0.44f, 1.0f, 1.0f};
    private static final float[] COLOR_PURPLE = {0.5f, 0.23f, 0.96f, 1.0f};

    public List<Arrow> arrows = new ArrayList<>();
    public List<Line> lines = new ArrayList<>();
    public List<Oval> ovals = new ArrayList<>();
    public List<Rect> rects = new ArrayList<>();
    public List<Path> paths = new ArrayList<>();

    public BaseShape currentShape;
    public float[] currentColor = new float[4];
    public SketchProcessor.SketchMode currentMode = SketchProcessor.SketchMode.MODE_LINE;
    public float[] currentMatrix = new float[16];
    public float lineBorderWidth = 0f;  // 抗锯齿运算时,线条两边alpha渐变的宽度
    public float viewWidth = 0;
    public float viewHeight = 0;
    public float axisScale = 0f;  // (坐标宽度 / view宽度)

    public ConcurrentLinkedQueue<Operation> operations = new ConcurrentLinkedQueue<>();

    public ConcurrentLinkedQueue<Operation> mRecycleOperations = new ConcurrentLinkedQueue<>();

    public RenderModel() {
        System.arraycopy(COLOR_RED, 0, currentColor, 0, currentColor.length);
        Matrix.orthoM(currentMatrix, 0, -1f, 1f, -1f, 1f, -1f, 1f);
    }

    public void appendOpreation(Operation operation) {
        operations.add(operation);
    }

    public void onTouchDown(PointF point) {
        if (currentShape != null) {
            archive();
        }
        currentShape = createShape();
        currentShape.onStart(point.x, point.y);
    }

    public void onTouchMove(PointF point) {
        if (currentShape == null) {
            currentShape = createShape();
            currentShape.onStart(point.x, point.y);
        } else {
            currentShape.onMove(point.x, point.y);
        }
    }

    public void onTouchUp(PointF point) {
        if (currentShape != null) {
            currentShape.onFinish(point.x, point.y);
            archive();
        }
    }

    public void changeColor(float[] color) {
        Operation operation = obtain();
        operation.command = Operation.Command.CHANGE_COLOR;
        System.arraycopy(color, 0, operation.color, 0, operation.color.length);
        appendOpreation(operation);
    }

    public void changeMode(SketchProcessor.SketchMode mode) {
        Operation operation = obtain();
        operation.command = Operation.Command.CHANGE_SHAPE;
        operation.mode = mode;
        appendOpreation(operation);
    }

    public void changeAxis(PointF axis, float viewWidth, float viewHeight) {
        Log.d(TAG, "changeAxis:  [x]" + axis.x + "  [y]" + axis.y);
        Operation operation = obtain();
        operation.command = Operation.Command.CHANGE_AXIS;
        operation.position.set(axis);
        operation.color[0] = viewWidth;
        operation.color[1] = viewHeight;
        appendOpreation(operation);
    }

    public void prepareDrawingData() {
        while (true) {
            Operation operation = operations.poll();
            if (operation == null) {
                return;
            }

            processOperation(operation);
        }
    }

    private void processOperation(Operation operation) {
        if (operation.command == Operation.Command.CHANGE_COLOR) {
            System.arraycopy(operation.color, 0, currentColor, 0, currentColor.length);
        } else if (operation.command == Operation.Command.CHANGE_SHAPE) {
            currentMode = operation.mode;
        } else if (operation.command == Operation.Command.CHANGE_AXIS) {
            Matrix.orthoM(currentMatrix, 0, -operation.position.x, operation.position.x,
                -operation.position.y, operation.position.y, -1f, 1f);
            // 1px
            viewWidth = operation.color[0];
            viewHeight = operation.color[1];
            lineBorderWidth = operation.position.x * 2 / viewWidth * LINE_BORDER_WIDTH_PX;
            axisScale = operation.position.x * 2 / viewWidth;
            Log.d(TAG, "view [width]" + viewWidth + "  [height]" + viewHeight);
            Log.d(TAG, "line border width: " + lineBorderWidth);
        } else if (operation.command == Operation.Command.TOUCH_EVENT) {
            if (operation.touchAction == Operation.TouchAction.DOWN) {
                onTouchDown(operation.position);
            } else if (operation.touchAction == Operation.TouchAction.MOVE) {
                onTouchMove(operation.position);
            } else if (operation.touchAction == Operation.TouchAction.UP) {
                onTouchUp(operation.position);
            }
        }
        mRecycleOperations.offer(operation);
    }

    public Operation obtain() {
        Operation data = mRecycleOperations.poll();
        if (data == null) {
            data = new Operation();
        }
        return data;
    }

    private void archive() {
        if (currentShape == null) {
            return;
        }

        if (currentShape instanceof Arrow) {
            arrows.add((Arrow)currentShape);
        } else if (currentShape instanceof Line) {
            lines.add((Line)currentShape);
        } else if (currentShape instanceof Oval) {
            ovals.add((Oval)currentShape);
        } else if (currentShape instanceof Rect) {
            rects.add((Rect)currentShape);
        } else if (currentShape instanceof Path) {
            paths.add((Path)currentShape);
        }

        currentShape = null;
    }

    private BaseShape createShape() {
        Log.d(TAG, "createShape");
        if (currentMode == SketchProcessor.SketchMode.MODE_ARROW) {
            Log.d(TAG, "create arrow");
            return new Arrow(currentColor);
        } else if (currentMode == SketchProcessor.SketchMode.MODE_LINE) {
            Log.d(TAG, "create line");
            return new Line(currentColor);
        } else if (currentMode == SketchProcessor.SketchMode.MODE_OVAL) {
            Log.d(TAG, "create oval");
            return new Oval(currentColor);
        } else if(currentMode == SketchProcessor.SketchMode.MODE_PATH) {
            Log.d(TAG, "create path");
            return new Path(currentColor);
        } else {
            Log.d(TAG, "create rect");
            return new Rect(currentColor);
        }
    }

    public static float[] getColor(SketchProcessor.SketchColor color) {
        if (color == SketchProcessor.SketchColor.COLOR_RED) {
            return COLOR_RED;
        } else if (color == SketchProcessor.SketchColor.COLOR_YELLOW) {
            return COLOR_YELLOW;
        } else if (color == SketchProcessor.SketchColor.COLOR_GREEN) {
            return COLOR_GREEN;
        } else if (color == SketchProcessor.SketchColor.COLOR_BLUE) {
            return COLOR_BLUE;
        } else if (color == SketchProcessor.SketchColor.COLOR_PURPLE) {
            return COLOR_PURPLE;
        }
        return COLOR_RED;
    }
}
