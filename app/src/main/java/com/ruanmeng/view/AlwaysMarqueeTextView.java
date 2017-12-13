package com.ruanmeng.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * TextView实现文字滚动需要以下几个要点：
 * 1.文字长度长于可显示范围：android:singleLine=”true”
 * 2.设置可滚到，或显示样式：android:ellipsize=”marquee”
 * 3.TextView只有在获取焦点后才会滚动显示隐藏文字，因此需要在包中新建一个类，继承TextView。
 *   重写isFocused方法，这个方法默认行为是，如果TextView获得焦点，方法返回true，失去焦点则返回false。
 *   跑马灯效果估计也是用这个方法判断是否获得焦点，所以把它的返回值始终设置为true
 * 4.复写onFocusChanged(),并且里面什么也不做
 *
 * Created by 小卷毛 on 2016/5/5 0005.
 */
public class AlwaysMarqueeTextView extends AppCompatTextView {

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) { }
}
