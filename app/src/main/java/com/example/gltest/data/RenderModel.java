package com.example.gltest.data;

import com.example.gltest.shape.Arrow;
import com.example.gltest.shape.BaseShape;
import com.example.gltest.shape.Line;
import com.example.gltest.shape.Oval;
import com.example.gltest.shape.Path;
import com.example.gltest.shape.Rect;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderModel {
    public CopyOnWriteArrayList<Arrow> arrows = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Line> lines = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Oval> ovals = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Rect> rects = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<Path> paths = new CopyOnWriteArrayList<>();

    public BaseShape currentShape;

    public void archieve() {
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
}
