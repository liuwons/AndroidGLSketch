package com.example.gltest;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.gltest.data.RenderModel;
import com.example.gltest.data.SketchProcessor;
import com.example.gltest.gl.GLRenderImpl;
import com.example.gltest.gl.SketchTextureView;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private SketchTextureView mSketchView;
    private SketchProcessor mSketchProcessor;

    RadioGroup mColorPicker;

    private TextView mBtnArrow;
    private TextView mBtnLine;
    private TextView mBtnOval;
    private TextView mBtnRect;
    private TextView mBtnPath;
    private TextView mBtnRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mColorPicker = findViewById(R.id.color_picker);
        mColorPicker.setOnCheckedChangeListener(this);

        mBtnArrow = findViewById(R.id.btn_arrow);
        mBtnArrow.setOnClickListener(this);
        mBtnLine = findViewById(R.id.btn_line);
        mBtnLine.setOnClickListener(this);
        mBtnLine.setSelected(true);
        mBtnOval = findViewById(R.id.btn_oval);
        mBtnOval.setOnClickListener(this);
        mBtnRect = findViewById(R.id.btn_rect);
        mBtnRect.setOnClickListener(this);
        mBtnPath = findViewById(R.id.btn_path);
        mBtnPath.setOnClickListener(this);
        mBtnRound = findViewById(R.id.btn_round);
        mBtnRound.setOnClickListener(this);

        mSketchView = findViewById(R.id.sketch_view);
        RenderModel model = new RenderModel();
        mSketchView.setRender(new GLRenderImpl(this, model));
        mSketchProcessor = new SketchProcessor(model);
        mSketchView.setProcessor(mSketchProcessor);
        mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_RED);

        mColorPicker.check(R.id.rb_color_red);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_arrow) {
            mBtnArrow.setSelected(true);
            mBtnLine.setSelected(false);
            mBtnOval.setSelected(false);
            mBtnRect.setSelected(false);
            mBtnPath.setSelected(false);
            mBtnRound.setSelected(false);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_ARROW);
        } else if (v.getId() == R.id.btn_line) {
            mBtnArrow.setSelected(false);
            mBtnLine.setSelected(true);
            mBtnOval.setSelected(false);
            mBtnRect.setSelected(false);
            mBtnPath.setSelected(false);
            mBtnRound.setSelected(false);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_LINE);
        } else if (v.getId() == R.id.btn_oval) {
            mBtnArrow.setSelected(false);
            mBtnLine.setSelected(false);
            mBtnOval.setSelected(true);
            mBtnRect.setSelected(false);
            mBtnPath.setSelected(false);
            mBtnRound.setSelected(false);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_OVAL);
        } else if (v.getId() == R.id.btn_rect) {
            mBtnArrow.setSelected(false);
            mBtnLine.setSelected(false);
            mBtnOval.setSelected(false);
            mBtnRect.setSelected(true);
            mBtnPath.setSelected(false);
            mBtnRound.setSelected(false);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_RECT);
        } else if (v.getId() == R.id.btn_path) {
            mBtnArrow.setSelected(false);
            mBtnLine.setSelected(false);
            mBtnOval.setSelected(false);
            mBtnRect.setSelected(false);
            mBtnPath.setSelected(true);
            mBtnRound.setSelected(false);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_PATH);
        } else if (v.getId() == R.id.btn_round) {
            mBtnArrow.setSelected(false);
            mBtnLine.setSelected(false);
            mBtnOval.setSelected(false);
            mBtnRect.setSelected(false);
            mBtnPath.setSelected(false);
            mBtnRound.setSelected(true);
            mSketchProcessor.setMode(SketchProcessor.SketchMode.MODE_ROUND);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.rb_color_red) {
            mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_RED);
        } else if (checkedId == R.id.rb_color_yellow) {
            mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_YELLOW);
        } else if (checkedId == R.id.rb_color_green) {
            mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_GREEN);
        } else if (checkedId == R.id.rb_color_blue) {
            mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_BLUE);
        } else if (checkedId == R.id.rb_color_purple) {
            mSketchProcessor.setColor(SketchProcessor.SketchColor.COLOR_PURPLE);
        }
    }
}
