package com.hzh.slide.back.layout.sample.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Package: com.hzh.slide.back.layout.sample.util
 * FileName: ActivityUtil
 * Date: on 2017/11/24  下午10:56
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class ActivityUtil {
    /**
     * 设置状态栏
     */
    public static void onStatusBarSet(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.setNavigationBarColor(Color.BLACK);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * Whether the Status bar is hidden or not,the method always helps you get
     * the height of Status bar.
     * 获得状态栏的高度
     *
     * @param context The context to use. Usually your
     *                {@link android.app.Application} or
     *                {@link Activity} object.
     * @return Return the height of Status bar.
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int id = (Integer) (clazz.getField("status_bar_height").get(object));
            statusHeight = context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
