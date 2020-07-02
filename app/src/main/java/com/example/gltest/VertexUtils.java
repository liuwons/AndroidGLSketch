package com.example.gltest;

public class VertexUtils {
    private static final String TAG = VertexUtils.class.getSimpleName();

    public static String floatArr2Str(float[] data) {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (int i = 0; i < data.length; i ++) {
            stringBuilder.append(data[i]);
            if (i != data.length-1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
