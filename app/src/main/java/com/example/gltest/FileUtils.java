package com.example.gltest;

import android.content.Context;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {
    public static String loadAssetFile(Context context, String assetPath) {
        try {
            InputStream inputStream = context.getResources().getAssets().open(assetPath);
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            String content = new String(bytes, "utf-8");
            inputStream.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
