package com.hzh.slide.back.layout.sample;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.hzh.slide.back.layout.sample.base.BackSlideBackActivity;
import com.hzh.slide.back.layout.sample.util.ActivityUtil;

public class SecondActivity extends BackSlideBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ActivityUtil.onStatusBarSet(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        View statusView = findViewById(R.id.statusView);
        ViewGroup.LayoutParams params = statusView.getLayoutParams();
        params.height = ActivityUtil.getStatusHeight(this);
        statusView.setLayoutParams(params);
        setSupportActionBar(toolbar);
    }
}
