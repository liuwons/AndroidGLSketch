package com.example.gltest;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SketchTextureView textureView = new SketchTextureView(this );
        textureView.setRender(new GLRenderImpl(this));

        setContentView(textureView);
    }
}
