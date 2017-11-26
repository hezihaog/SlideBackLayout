package com.hzh.slide.back.layout.sample.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.hzh.slide.back.layout.SlideBackLayout;

/**
 * Package: com.hzh.slide.back.layout
 * FileName: BackSlideBackActivity
 * Date: on 2017/11/24  下午9:53
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class BackSlideBackActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isSlideBackEnable()) {
            SlideBackLayout layout = new SlideBackLayout(this);
            layout.bindActivity(this);
        }
    }

    /**
     * 是否可以滑动返回，子类重写即可
     *
     * @return true为可滑动返回，false为不可滑动返回
     */
    protected boolean isSlideBackEnable() {
        return true;
    }
}
