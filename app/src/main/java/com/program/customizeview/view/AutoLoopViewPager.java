package com.program.customizeview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.program.customizeview.R;

public class AutoLoopViewPager extends ViewPager {

    //切换间隔时长
    public static final long DEFAULT_DURATION=3000;
    public long mDuration=DEFAULT_DURATION;

    public AutoLoopViewPager(@NonNull Context context) {
        this(context,null);
    }

    public AutoLoopViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //读取属性
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AutoLoopStyle);
        //获取属性
        mDuration = t.getInteger(R.styleable.AutoLoopStyle_duration, (int) DEFAULT_DURATION);
        //回收
        t.recycle();
    }

    /**
     * 设置切换时长
     * @param duration  时长 单位毫秒
     */
    public void setDuration(int duration){
        this.mDuration=duration;
    }

    private boolean isLoop = false;

    public void startLoop(){
        isLoop=true;
        //先拿到当前的位置

        post(mTask);
    }

    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            if (isLoop) {
                postDelayed(this,mDuration);
            }
        }
    };

    public void stopLoop(){
        isLoop=false;
        removeCallbacks(mTask);
    }
}
