package com.hzh.slide.back.layout.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hzh.slide.back.layout.sample.util.ActivityUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityUtil.onStatusBarSet(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        View statusView = findViewById(R.id.statusView);
        ViewGroup.LayoutParams params = statusView.getLayoutParams();
        params.height = ActivityUtil.getStatusHeight(this);
        statusView.setLayoutParams(params);
        setSupportActionBar(toolbar);
        Button goBtn = (Button) findViewById(R.id.goBtn);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }
}