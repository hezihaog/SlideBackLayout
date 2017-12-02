package com.hzh.slide.back.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Package: com.hzh.slide.back.layout
 * FileName: SlideBackLayout
 * Date: on 2017/11/24  下午9:48
 * Auther: zihe
 * Descirbe:
 * Email: hezihao@linghit.com
 */

public class SlideBackLayout extends LinearLayout {
    // 页面边缘阴影的宽度默认值
    private static final int SHADOW_WIDTH_DP = 16;//左边阴影的宽
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
    private OnSlideListener listener;
    private View gradualView;
    private View contentOverlay;
    private int screenWidth;
    private int screenHeight;
    //Content视图距离屏幕左上角的距离
    private int[] contentLocation = new int[2];
    private VelocityTracker mVelocityTracker;
    //快速拽甩关闭的速度值
    private static final int FLING_VELOCITY = 3500;
    private int mMaxVelocity;
    private int pointerId;

    public SlideBackLayout(Context context) {
        super(context);
        init(context);
    }

    public SlideBackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideBackLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        //设置允许绘制自己，否则onDraw会跳过不调用
        setWillNotDraw(false);
        mScroller = new Scroller(context);
        mLeftShadow = getResources().getDrawable(R.drawable.left_shadow);
        mShadowWidth = (int) dpToPixel(context, SHADOW_WIDTH_DP);
        //获取屏幕宽高
        screenWidth = (int) getScreenWidth(getContext());
        screenHeight = (int) getScreenHeight(getContext());
        mVelocityTracker = VelocityTracker.obtain();
        mMaxVelocity = ViewConfiguration.get(getContext()).getMaximumFlingVelocity();
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                releaseVelocityTracker();
            }
        });
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
        if (isIntercept) {
            if (listener != null) {
                listener.onEdgeTouch();
            }
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = x;
                mLastTouchX = x;
                mLastTouchY = y;
                //拿取一个触摸点的id
                pointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = x - mLastTouchX;
                int deltaY = y - mLastTouchY;
                if (!isConsumed && mTouchDownX < (getWidth() / 10) && Math.abs(deltaX) > Math.abs(deltaY)) {
                    isConsumed = true;
                }
                if (isConsumed) {
                    int rightMovedX = mLastTouchX - (int) event.getX();
                    // 左侧即将滑出屏幕左侧
                    if (getScrollX() + rightMovedX >= 0) {
                        //限制左滑最大到屏幕左边
                        scrollTo(0, 0);
                    } else {
                        //叠加滑动 ： 负值是往右滑动 ，反之向左
                        scrollBy(rightMovedX, 0);
                        if (listener != null) {
                            listener.onDragging();
                        }
                    }
                }
                mLastTouchX = x;
                mLastTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                isConsumed = false;
                mTouchDownX = mLastTouchX = mLastTouchY = 0;
                //计算速度
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                final float velocityX = mVelocityTracker.getXVelocity(pointerId);
                //如果达到可以惯性拽托的速度值
                if (velocityX >= FLING_VELOCITY) {
                    //并且是从边缘触发的，则关闭界面
                    int distanceX = x - mLastInterceptX;
                    int distanceY = y - mLastInterceptY;
                    if (mInterceptDownX < (getWidth() / 10) && Math.abs(distanceX) > Math.abs(distanceY)) {
                        scrollClose();
                    }
                } else {
                    // 根据手指释放时的位置决定回弹还是关闭
                    if (-getScrollX() < getWidth() / 3) {
                        //向右滑动小于屏幕3分之一，回弹到屏幕左侧
                        scrollBackToLeft();
                    } else {
                        //否则，向右滑动
                        scrollClose();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 回收速度检测器
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
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

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获取屏幕高度，不包括NavigationBar
     *
     * @return
     */
    public static float getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * 获取屏幕宽度
     *
     * @return
     */
    public static float getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
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
        //计算移动时，左边灰色渐变View的渐变比值
        //1、拿取contentOverlay距离屏幕左上角顶点的距离
        contentOverlay.getLocationInWindow(contentLocation);
        //2.拿取X轴向的
        float locationX = contentLocation[0] * 1f;
        //3、拿取屏幕的宽度
        //4、用当前距离和总宽度做比值，计算出对应的渐变（offset / sumWidth = ? / 1）
        //注意：做除法的时候，必须有一方是浮点数，否则整除和整数做除法，结果只会是整数
        gradualView.setAlpha(Math.abs(1 - (locationX / screenWidth)));

        //true说明滚动尚未完成，false说明滚动已经完成
        if (mScroller.computeScrollOffset()) {
            //还没结束，继续滑动
            scrollTo(mScroller.getCurrX(), 0);
            postInvalidate();//重绘；调用这个：computeScroll()
        } else if (-getScrollX() >= getWidth()) {
            //当滑动出去了，关闭界面
            gradualView.setAlpha(0);
            if (listener != null) {
                listener.onScrollToClose();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制阴影
        drawShadow(canvas);
    }

    /**
     * 滑动监听
     */
    public interface OnSlideListener {
        /**
         * 在边缘拽托
         */
        void onEdgeTouch();

        /**
         * 在拖动中
         */
        void onDragging();

        /**
         * 滑动到关闭
         */
        void onScrollToClose();
    }

    public void setOnSlideListener(OnSlideListener listener) {
        this.listener = listener;
    }

    /**
     * 绑定
     *
     * @param parentView 视图的父ViewGroup
     * @param target     视图View
     * @param listener   回调监听
     */
    public void bind(ViewGroup parentView, View target, OnSlideListener listener) {
        contentOverlay = target;
        setOnSlideListener(listener);
        parentView.removeView(target);
        this.setOrientation(LinearLayout.HORIZONTAL);
        gradualView = new View(getContext());
        gradualView.setBackgroundColor(Color.parseColor("#A0000000"));
        LayoutParams params = new LayoutParams(screenWidth, screenHeight);
        params.setMargins(-screenWidth, 0, 0, 0);
        addView(gradualView, params);
        addView(target);
        parentView.addView(this);
    }

    /**
     * 快捷使用Activity绑定
     *
     * @param activity
     */
    public void bind(final Activity activity) {
        if (activity == null) {
            return;
        }
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup contentOverlay = (ViewGroup) decorView.getChildAt(0);
        bind(decorView, contentOverlay, new SlideBackLayout.OnSlideListener() {
            @Override
            public void onEdgeTouch() {
            }

            @Override
            public void onDragging() {
            }

            @Override
            public void onScrollToClose() {
                activity.finish();
            }
        });
    }
}
