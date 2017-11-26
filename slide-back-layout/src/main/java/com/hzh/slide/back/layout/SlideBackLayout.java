package com.hzh.slide.back.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Package: com.hzh.slide.back.layout
 * FileName: SlideBackLayout
 * Date: on 2017/11/24  下午9:48
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class SlideBackLayout extends FrameLayout {
    // 页面边缘阴影的宽度默认值
    private static final int SHADOW_WIDTH_DP = 16;//左边阴影的宽
    private Activity mActivity;
    private Scroller mScroller;
    // 页面边缘的阴影图
    private Drawable mLeftShadow;
    // 页面边缘阴影的宽度
    private int mShadowWidth;
    private int mInterceptDownX;
    private int mLastInterceptX;
    private int mLastInterceptY;

    private int mTouchDownX;
    private int mLastTouchX;
    private int mLastTouchY;
    private boolean isConsumed = false;

    public SlideBackLayout(Context context) {
        super(context);
        initView(context);
    }

    public SlideBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SlideBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mScroller = new Scroller(context);
        mLeftShadow = getResources().getDrawable(R.drawable.left_shadow);
        mShadowWidth = (int) dpToPixel(context, SHADOW_WIDTH_DP);
    }

    /**
     * 绑定Activity
     */
    public void bindActivity(Activity activity) {
        this.mActivity = activity;
        //包裹Activity的布局
        ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        View child = decorView.getChildAt(0);
        decorView.removeView(child);
        addView(child);
        decorView.addView(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isIntercept = false;
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isIntercept = false;
                mInterceptDownX = x;
                mLastInterceptX = x;
                mLastInterceptY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastInterceptX;
                int deltaY = y - mLastInterceptY;
                // 手指处于屏幕边缘，且横向滑动距离大于纵向滑动距离时，拦截事件
                //屏幕的十分之一的距离；
                if (mInterceptDownX < (getWidth() / 10) && Math.abs(deltaX) > Math.abs(deltaY)) {
                    isIntercept = true;
                } else {
                    isIntercept = false;
                }
                mLastInterceptX = x;
                mLastInterceptY = y;
                break;
            case MotionEvent.ACTION_UP:
                isIntercept = false;
                mInterceptDownX = mLastInterceptX = mLastInterceptY = 0;
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = x;
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastTouchX;
                int deltaY = y - mLastTouchY;
                if (!isConsumed && mTouchDownX < (getWidth() / 10) && Math.abs(deltaX) > Math.abs(deltaY)) {
                    isConsumed = true;
                }
                if (isConsumed) {
                    int rightMovedX = mLastTouchX - (int) ev.getX();
                    // 左侧即将滑出屏幕左侧
                    if (getScrollX() + rightMovedX >= 0) {
                        //限制左滑最大到屏幕左边
                        scrollTo(0, 0);
                    } else {
                        //叠加滑动 ： 负值是往右滑动 ，反之向左
                        scrollBy(rightMovedX, 0);
                    }
                }
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                isConsumed = false;
                mTouchDownX = mLastTouchX = mLastTouchY = 0;
                // 根据手指释放时的位置决定回弹还是关闭
                if (-getScrollX() < getWidth() / 3) {
                    //向右滑动小于屏幕3分之一，回弹到屏幕左侧
                    scrollBackToLeft();
                } else {
                    //否则，向右滑动
                    scrollClose();
                }
                break;
        }
        return true;
    }

    /**
     * 回弹回屏幕左侧
     */
    private void scrollBackToLeft() {
        int startX = getScrollX();
        int dx = -getScrollX();
        /**
         * startX 水平方向滚动的偏移值，以像素为单位。
         *　　startY 垂直方向滚动的偏移值，以像素为单位。
         *　　dx 水平方向滑动的距离，正值会使滚动向左滚动
         *　　dy 垂直方向滑动的距离，正值会使滚动向上滚动
         *   duration: 滑动所需要的时间；
         */
        mScroller.startScroll(startX, 0, dx, 0, 300);//实现弹性的滑动
        invalidate();//重新绘制：这样才会调用 computeScroll()；
    }

    /**
     * 滑动关闭
     */
    private void scrollClose() {
        int startX = getScrollX();
        int dx = -getScrollX() - getWidth();
        mScroller.startScroll(startX, 0, dx, 0, 300);
        invalidate();
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return 转换后的px值
     */
    public static float dpToPixel(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return dp * (displayMetrics.densityDpi / 160F);
    }

    /**
     * 绘制边缘的阴影
     */
    private void drawShadow(Canvas canvas) {
        mLeftShadow.setBounds(0, 0, mShadowWidth, getHeight());
        canvas.save();
        canvas.translate(-mShadowWidth, 0);
        mLeftShadow.draw(canvas);
        canvas.restore();
    }

    @Override
    public void computeScroll() {
        //true说明滚动尚未完成，false说明滚动已经完成
        if (mScroller.computeScrollOffset()) {
            //还没结束，继续滑动
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();//重绘；调用这个：computeScroll()
        } else if (-getScrollX() >= getWidth()) {
            //当滑动出去了，结束activity;
            mActivity.finish();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制阴影
        drawShadow(canvas);
    }
}
