package com.app.feng.tablerecyclerview;

import android.widget.TextView;

import com.app.feng.fixtablelayout.inter.IDataAdapter;

import java.util.List;

/**
 * Created by feng on 2017/4/4.
 */

public abstract class FixTableAdapter<T> implements IDataAdapter {

    public String[] titles;

    public List<T> data;

//    private boolean mFloatingTitle = false;

    public FixTableAdapter(String[] titles, List<T> data) {
        this.titles = titles;
        this.data = data;
    }

//    public void setFloatingTitle(boolean floatingTitle) {
//        mFloatingTitle = floatingTitle;
//    }
//
//    public boolean isFloatingTitle() {
//        return mFloatingTitle;
//    }

    public void setData(List<T> data) {
        this.data = data;
    }

    @Override
    public String getTitleAt(int pos) {
        return titles[pos];
    }

    @Override
    public int getTitleCount() {
        return titles.length;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    final public void convertData(int position, List<TextView> bindViews) {
//        if (!mFloatingTitle) {
//            if (position == 0) {//非悬浮标题时，第一行为标题；
//                setTitleView(titles, bindViews);
//            }
//
//            index = position - 1;//非悬浮标题，对数据进行偏移，减去标题行所占数。
//        }

        T dataBean = data.get(position);

        setLinerItemView(dataBean, bindViews);
    }

    public void setTitleView(String[] titles, List<TextView> bindViews) {
        if (titles.length == bindViews.size()) {
            for (int i = 0; i < titles.length; i++) {
                bindViews.get(i).setText(titles[i]);
            }
        }
    }

    abstract public void setLinerItemView(T data, List<TextView> textViewList);

    abstract public void setLeftItemView(T data, TextView textView);

    @Override
    public void convertLeftData(int position, TextView bindView) {
//        int index = position;
//        if (!mFloatingTitle) {
//            if (position == 0) {//非悬浮标题时，第一行为标题；
//                bindView.setText(titles[0]);
//            }
//
//            index = position - 1;
//        }

        setLeftItemView(data.get(position), bindView);
    }
}
