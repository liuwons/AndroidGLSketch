package com.example.gltest.gl;

public class BezierRender {
    private GLRenderImpl mRenderer;

    public float[] start = new float[2];
    public float[] end = new float[2];
    public float[] control1 = new float[2];
    public float[] control2 = new float[2];

    public BezierRender(GLRenderImpl renderer) {
        mRenderer = renderer;
    }

    public void render() {
    }
}
