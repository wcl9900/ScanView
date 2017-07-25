package com.wcl.scanview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.wcl.scanview.ScanView;

import java.util.ArrayList;
import java.util.List;

/**
 * ScanView使用demo
 * @author 王春龙
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private ScanView scanView;
    private Button buttonPlay;
    private Button buttonStop;

    private Spinner spInter;
    private Spinner spStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanView = (ScanView) findViewById(R.id.scanview);
        buttonPlay = (Button) findViewById(R.id.btn_play);
        buttonStop = (Button) findViewById(R.id.btn_stop);
        spInter = (Spinner) findViewById(R.id.sp_inter);
        spStyle = (Spinner) findViewById(R.id.sp_style);

        SeekBar seekBarDuration = (SeekBar) findViewById(R.id.sb_duration);
        SeekBar seekBarLinePadding = (SeekBar) findViewById(R.id.sb_line_padding);
        SeekBar seekBarFramePadding = (SeekBar) findViewById(R.id.sb_frame_padding);
        SeekBar seekBarLineHeight = (SeekBar) findViewById(R.id.sb_line_height);
        seekBarDuration.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarLinePadding.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarFramePadding.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarLineHeight.setOnSeekBarChangeListener(onSeekBarChangeListener);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_model);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);

        initIntercept();
        initStyle();
    }

    private void initIntercept() {
        final List<InterpolatorItem> interpolatorItemList = new ArrayList<InterpolatorItem>();
        interpolatorItemList.add(new InterpolatorItem("匀速插值器", new LinearInterpolator()));
        interpolatorItemList.add(new InterpolatorItem("减速插值器", new DecelerateInterpolator()));
        interpolatorItemList.add(new InterpolatorItem("加速插值器", new AccelerateInterpolator()));
        spInter.setAdapter(new ArrayAdapter<InterpolatorItem>(this, android.R.layout.simple_list_item_1, interpolatorItemList));
        spInter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                InterpolatorItem interpolatorItem = interpolatorItemList.get(position);
                scanView.setInterpolator(interpolatorItem.interpolator);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initStyle() {
        final List<String> styleList = new ArrayList<String>();
        styleList.add("自定义样式1");
        styleList.add("自定义样式2");
        spStyle.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, styleList));
        spStyle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    scanView.setDrawableFrame(R.drawable.icon_frame);
                    scanView.setDrawableLine(R.drawable.icon_line);
                    scanView.setLineHeight(0.3f);
                }
                else if(position == 1){
                    scanView.setDrawableFrame(R.drawable.shape_frame);
                    scanView.setDrawableLine(R.color.scan_line);
                    scanView.setLineHeight(dpToPx(4));
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.rb1){
                scanView.getLayoutParams().width = dpToPx(220);
                scanView.getLayoutParams().height = dpToPx(220);
                scanView.setFramePadding(0, 0, 0,0);
                scanView.setLinePadding(dpToPx(1), dpToPx(1),dpToPx(1),dpToPx(1));
                scanView.requestLayout();
            }
            else if(checkedId == R.id.rb2){
                scanView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                scanView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                scanView.setFramePadding(dpToPx(20), dpToPx(20),dpToPx(20),dpToPx(20));
                scanView.setLinePadding(0,0,0,0);
                scanView.requestLayout();
            }
        }
    };

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if(seekBar.getId() == R.id.sb_duration){
                int duration = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * 5000 + 2500);
                scanView.setDuration(duration);
                return;
            }
            if(seekBar.getId() == R.id.sb_line_padding){
                int linePadding = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * dpToPx(40));
                scanView.setLinePadding(linePadding, linePadding, linePadding, linePadding);
                return;
            }
            if(seekBar.getId() == R.id.sb_frame_padding){
                int framePadding = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * dpToPx(40));
                scanView.setFramePadding(framePadding, framePadding, framePadding, framePadding);
                return;
            }
            if(seekBar.getId() == R.id.sb_line_height){
                float lineHeightRatio = (seekBar.getProgress() / (float) seekBar.getMax()) * 1.0f;
                scanView.setLineHeight(lineHeightRatio);
                return;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    @Override
    public void onClick(View v) {
        if(v == buttonPlay){
            scanView.play();
            return;
        }
        if(v == buttonStop){
            scanView.stop();
            return;
        }
        if(v == spInter){
            scanView.setInterpolator(new DecelerateInterpolator());
            return;
        }
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private class InterpolatorItem{
        String name;
        Interpolator interpolator;

        public InterpolatorItem(String name, Interpolator interpolator) {
            this.name = name;
            this.interpolator = interpolator;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
