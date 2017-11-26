package com.hzh.slide.back.layout.sample.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.hzh.slide.back.layout.SlideBackLayout;

/**
 * Package: com.hzh.slide.back.layout
 * FileName: BackSlideBackActivity
 * Date: on 2017/11/24  下午9:53
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class BaseSlideBackActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isSlideBackEnable()) {
            SlideBackLayout layout = new SlideBackLayout(this);
            layout.bind(this);
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