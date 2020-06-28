package com.example.gltest.shape;

import android.graphics.PointF;
import android.util.Log;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Path extends BaseShape {
    private static final String TAG = Path.class.getSimpleName();

    private static final float CTRL_VALUE_A = 0.2f;
    private static final float CTRL_VALUE_B = 0.2f;

    public static final int BZ_ARRAY_LEN = 4;
    private static float[] sBzTValArray = new float[BZ_ARRAY_LEN];

    static {
        float stride = 1.0f / (BZ_ARRAY_LEN+1);
        sBzTValArray[0] = stride;
        for (int i = 1; i < BZ_ARRAY_LEN; i ++) {
            sBzTValArray[i] = sBzTValArray[i-1] + stride;
        }
    }

    public List<PointF> points = new ArrayList<>();
    public float lineWidth = 0.03f;

    public List<BezierVertex> mBezierVertexes = new ArrayList<>();
    private List<CubicBezier> mCubicBeziers = new LinkedList<>();
    private volatile boolean mBeziersNeedUpdate = true;

    public Path(float[] c) {
        super(c);
    }

    @Override
    public void onStart(float x, float y) {
        super.onStart(x, y);
        Log.d(TAG, "onStart:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
    }

    @Override
    public void onMove(float x, float y) {
        super.onMove(x, y);
        Log.d(TAG, "onMove:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
        mBeziersNeedUpdate = true;
    }

    @Override
    public void onFinish(float x, float y) {
        super.onFinish(x, y);
        Log.d(TAG, "onFinish:  [x]" + x + "  [y]" + y);
        points.add(new PointF(x, y));
        mBeziersNeedUpdate = true;
    }

    @Override
    public boolean valid() {
        return points.size() > 3;
    }

    @Override
    public int dumpTriangles(int vertexPos, FloatBuffer vertexBuffer, IntBuffer indexBuffer) {
        if (!valid()) {
            return 0;
        }

        int vertexCount = 0;

        List<Path.BezierVertex> vertexList = dump2VertexList();
        for (int i = 0; i < vertexList.size(); i ++) {
            Path.BezierVertex vertex = vertexList.get(i);
            float t = vertex.t;
            if (vertex.vertexType == Path.BezierVertex.VertexType.START) {
                t = 0.00001f;
            } else if (vertex.vertexType == Path.BezierVertex.VertexType.END){
                t = 10000f;
            }

            vertexBuffer.put(vertex.position);
            vertexBuffer.put(color);
            vertexBuffer.put(lineWidth);
            vertexBuffer.put(t);
            vertexBuffer.put(getZ());
            vertexBuffer.put(vertex.ctrl);

            vertexBuffer.put(vertex.position);
            vertexBuffer.put(color);
            vertexBuffer.put(lineWidth);
            vertexBuffer.put(-t);
            vertexBuffer.put(getZ());
            vertexBuffer.put(vertex.ctrl);

            vertexCount += 2;

            if (i != 0) {
                int startPos = vertexPos + (i-1)*2;
                indexBuffer.put(startPos);
                indexBuffer.put(startPos + 1);
                indexBuffer.put(startPos + 2);
                indexBuffer.put(startPos + 1);
                indexBuffer.put(startPos + 2);
                indexBuffer.put(startPos + 3);
            }
        }

        return vertexCount;
    }

    public List<BezierVertex> dump2VertexList() {
        if (!mBeziersNeedUpdate) {
            return mBezierVertexes;
        }
        mBezierVertexes.clear();
        calcBezierLines();
        for (int i = 0; i < mCubicBeziers.size(); i ++) {
            CubicBezier bezier = mCubicBeziers.get(i);

            if (i == 0) {
                BezierVertex startVertex = new BezierVertex();
                startVertex.vertexType = BezierVertex.VertexType.START;
                System.arraycopy(bezier.pos, 0, startVertex.position, 0, 4);
                System.arraycopy(bezier.ctrl, 0, startVertex.ctrl, 0, 4);
                mBezierVertexes.add(startVertex);
            }

            for (int k = 0; k < BZ_ARRAY_LEN; k ++) {
                BezierVertex vertex = new BezierVertex();
                vertex.vertexType = BezierVertex.VertexType.NORMAL;
                System.arraycopy(bezier.pos, 0, vertex.position, 0, 4);
                System.arraycopy(bezier.ctrl, 0, vertex.ctrl, 0, 4);
                vertex.t = sBzTValArray[k];
                mBezierVertexes.add(vertex);
            }

            BezierVertex endVertex = new BezierVertex();
            endVertex.vertexType = BezierVertex.VertexType.END;
            System.arraycopy(bezier.pos, 0, endVertex.position, 0, 4);
            System.arraycopy(bezier.ctrl, 0, endVertex.ctrl, 0, 4);
            mBezierVertexes.add(endVertex);

        }

        mBeziersNeedUpdate = false;
        return mBezierVertexes;
    }

    public List<CubicBezier> calcBezierLines() {
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

    public static class BezierVertex {
        public enum VertexType {
            NORMAL,
            START,
            END
        }

        public VertexType vertexType = VertexType.NORMAL;
        public float[] position = new float[4];
        public float[] ctrl = new float[4];
        public float t;
    }

}
