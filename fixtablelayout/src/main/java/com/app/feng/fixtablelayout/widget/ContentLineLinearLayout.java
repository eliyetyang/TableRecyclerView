package com.app.feng.fixtablelayout.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 其将所有子View，单行排列。
 * Created by feng on 2017/3/29.
 */

public class ContentLineLinearLayout extends ViewGroup {

    public ContentLineLinearLayout(Context context) {
        this(context, null);
    }

    public ContentLineLinearLayout(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentLineLinearLayout(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父View 不能限制我们。模式是UNSPECIFIED  宽度所有子View宽总和, 高度取子View最大
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

//        Log.i("feng",
//              "single 调用 onMeasure - widthMode :" + widthMode + " width size:  " + widthSize + " height size " + heightSize);

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            int widthChild = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.UNSPECIFIED);
            int heightChild = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.UNSPECIFIED);

            childView.measure(widthChild, heightChild);
//            Log.d("TAG", "onMeasure: childWidth = " + childView.getMeasuredWidth() + "; i = " + i);
            Log.i("TAG", "onMeasure: height = " + height + "; childViewHeight = " + childView.getMeasuredHeight() + "; i = " + i);
            width += childView.getMeasuredWidth();
            height = Math.max(height, childView.getMeasuredHeight());
        }

        for (int i = 0; i < getChildCount(); i++) {
            TextView childView = (TextView) getChildAt(i);
            childView.setHeight(height);
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int tempLeft = 0;
        int tempHeight = 0;
        //非常简单，每个子View都一行摆放。
//        Log.i("feng","single 调用 onLayout");

        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);

            int tempRight = tempLeft + childView.getMeasuredWidth();
            int tempT = 0;
            int tempB = childView.getMeasuredHeight();
            Log.d("TAG", "onLayout: childHeight = " + childView.getHeight() + "; measureHeight = " + childView.getMeasuredHeight());
            childView.layout(tempLeft, tempT, tempRight, getMeasuredHeight());
            tempLeft += childView.getMeasuredWidth();
        }
    }
}
