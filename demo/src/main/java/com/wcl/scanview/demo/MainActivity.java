package com.wcl.scanview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.wcl.scanview.ScanView;

public class MainActivity extends Activity implements View.OnClickListener{

    private ScanView scanView;
    private Button buttonPlay;
    private Button buttonStop;
    private Button buttonInter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanView = (ScanView) findViewById(R.id.scanview);
        buttonPlay = (Button) findViewById(R.id.btn_play);
        buttonStop = (Button) findViewById(R.id.btn_stop);
        buttonInter = (Button) findViewById(R.id.btn_inter);

        SeekBar seekBarDuration = (SeekBar) findViewById(R.id.sb_duration);
        SeekBar seekBarLinePadding = (SeekBar) findViewById(R.id.sb_line_padding);
        SeekBar seekBarFramePadding = (SeekBar) findViewById(R.id.sb_frame_padding);
        seekBarDuration.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarLinePadding.setOnSeekBarChangeListener(onSeekBarChangeListener);
        seekBarFramePadding.setOnSeekBarChangeListener(onSeekBarChangeListener);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_model);
        radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        buttonPlay.setOnClickListener(this);
        buttonStop.setOnClickListener(this);
        buttonInter.setOnClickListener(this);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if(checkedId == R.id.rb1){
                scanView.getLayoutParams().width = dpToPx(220);
                scanView.getLayoutParams().height = dpToPx(220);
                scanView.setFramePadding(dpToPx(0));
                scanView.setLinePadding(1);
            }
            else if(checkedId == R.id.rb2){
                scanView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                scanView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                scanView.setFramePadding(dpToPx(20));
                scanView.setLinePadding(0);
            }
        }
    };

    SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if(seekBar.getId() == R.id.sb_duration){
                int duration = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * 5000 + 2500);
                scanView.setDuration(duration);
                return;
            }
            if(seekBar.getId() == R.id.sb_line_padding){
                int linePadding = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * dpToPx(40));
                scanView.setLinePadding(linePadding);
                return;
            }
            if(seekBar.getId() == R.id.sb_frame_padding){
                int framePadding = (int) ((seekBar.getProgress() / (float) seekBar.getMax()) * dpToPx(40));
                scanView.setFramePadding(framePadding);
                return;
            }
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
        if(v == buttonInter){
            scanView.setInterpolator(new DecelerateInterpolator());
            return;
        }
    }

    public int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
