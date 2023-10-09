package com.app.feng.fixtablelayout.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.app.feng.fixtablelayout.widget.TextViewUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by feng on 2017/3/28.
 */

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private static final int VIEW_TYPE_TITLE = -1;
    private static final int VIEW_TYPE_CONTENT = 1;

    private HorizontalScrollView titleView;
    private RecyclerView leftViews;
    private TextView left_top_view;

    private ParametersHolder contentParametersHolder;
    private ParametersHolder titleParametersHolder;

    private IDataAdapter dataAdapter;

    private boolean mFloatingTitle;

    private TableAdapter(
            HorizontalScrollView titleView,
            RecyclerView leftViews,
            TextView left_top_view,
            ParametersHolder contentParametersHolder,
            ParametersHolder titleParametersHolder,
            IDataAdapter dataAdapter) {
        super();
        mFloatingTitle = titleView != null;
        this.titleView = titleView;
        this.leftViews = leftViews;
        this.left_top_view = left_top_view;
        this.contentParametersHolder = contentParametersHolder;
        this.titleParametersHolder = titleParametersHolder;
        this.dataAdapter = dataAdapter;

        initViews();
    }

    private void initViews() {
        if (left_top_view != null) {
            left_top_view.setBackgroundColor(titleParametersHolder.bgColor);
            TextViewUtils.setTextView(left_top_view,
                    dataAdapter.getTitleAt(0),
                    titleParametersHolder.widthArray == null || titleParametersHolder.widthArray.length < 0
                            ? titleParametersHolder.defaultWidth
                            : titleParametersHolder.widthArray[0],
                    titleParametersHolder);
        }

        //        SingleLineLinearLayout titleChild = ((SingleLineLinearLayout) titleView.getChildAt(0));

        if (mFloatingTitle) {//非悬浮标题，在按照content进行处理。
            ViewGroup titleChild = ((ViewGroup) titleView.getChildAt(0));

            for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
                int itemWidth = titleParametersHolder.widthArray == null || titleParametersHolder.widthArray.length < i
                        ? titleParametersHolder.defaultWidth
                        : titleParametersHolder.widthArray[i];
                TextView textView = TextViewUtils.generateTextView(titleChild.getContext(),
                        dataAdapter.getTitleAt(i),
                        itemWidth,
                        titleParametersHolder);
                titleChild.addView(textView, i);
            }
            titleChild.setBackgroundColor(titleParametersHolder.bgColor);
        }

        if (leftViews != null) {
            leftViews.setAdapter(new LeftViewAdapter());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!mFloatingTitle && position == 0) {
            return VIEW_TYPE_TITLE;
        } else {
            return VIEW_TYPE_CONTENT;
        }
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        SingleLineLinearLayout singleLineLinearLayout = new SingleLineLinearLayout(
//                parent.getContext());
        LinearLayout linearLayout = new LinearLayout(parent.getContext());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
            if (viewType == VIEW_TYPE_TITLE) {
                int itemWidth = titleParametersHolder.widthArray == null || titleParametersHolder.widthArray.length < i
                        ? titleParametersHolder.defaultWidth
                        : titleParametersHolder.widthArray[i];
                TextView textView = TextViewUtils.generateTextView(parent.getContext(),
                        dataAdapter.getTitleAt(i),
                        itemWidth,
                        titleParametersHolder);
                linearLayout.addView(textView, i);
                linearLayout.setBackgroundColor(titleParametersHolder.bgColor);
            } else {
                int itemWidth = contentParametersHolder.widthArray == null || contentParametersHolder.widthArray.length < i
                        ? contentParametersHolder.defaultWidth
                        : contentParametersHolder.widthArray[i];
                TextView textView = TextViewUtils.generateTextView(parent.getContext(),
                        " ",
                        itemWidth,
                        contentParametersHolder);
                linearLayout.addView(textView, i);
            }
        }

        return new TableViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(TableViewHolder holder, int position) {
        if (!mFloatingTitle && position == 0) {
            return;
        }

        ViewGroup ll_content = (ViewGroup) holder.itemView;


        List<TextView> bindViews = holder.bindViews;

        if (bindViews == null) {
            bindViews = new ArrayList<>();
            for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
                TextView textView = (TextView) ll_content.getChildAt(i);
                bindViews.add(textView);
            }
            holder.bindViews = bindViews;
        }

        for (int i = 0; i < dataAdapter.getTitleCount(); i++) {
            TextView textView = (TextView) ll_content.getChildAt(i);
            bindViews.add(textView);
        }

        //给奇数列设置背景
        setBackgrandForItem(position, ll_content);

        dataAdapter.convertData(position - (mFloatingTitle ? 0 : 1), bindViews);
    }

    private void setBackgrandForItem(int position, ViewGroup ll_content) {
        if (position % 2 != 0) {
            ll_content.setBackgroundColor(contentParametersHolder.col_1_color);
        } else {
            ll_content.setBackgroundColor(contentParametersHolder.col_2_color);
        }
    }

    @Override
    public int getItemCount() {
        return dataAdapter.getItemCount() + (mFloatingTitle ? 0 : 1);
    }

    class LeftViewAdapter extends RecyclerView.Adapter<LeftViewAdapter.LeftViewHolder> {

        @Override
        public LeftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            SingleLineLinearLayout singleLineLinearLayout = new SingleLineLinearLayout(
//                    parent.getContext());

            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int itemWidth = contentParametersHolder.widthArray == null || contentParametersHolder.widthArray.length < 0
                    ? contentParametersHolder.defaultWidth
                    : contentParametersHolder.widthArray[0];
            TextView textView = TextViewUtils.generateTextView(parent.getContext(),
                    " ",
                    itemWidth,
                    contentParametersHolder);

            linearLayout.addView(textView);
            return new LeftViewHolder(linearLayout);
        }

        @Override
        public void onBindViewHolder(LeftViewHolder holder, int position) {
            if (!mFloatingTitle && position == 0) {
                ViewGroup ll_content = (ViewGroup) holder.itemView;

                TextView child = (TextView) ll_content.getChildAt(0);

                setBackgrandForItem(position, ll_content);

                child.setText(dataAdapter.getTitleAt(0));
                return;
            }

            ViewGroup ll_content = (ViewGroup) holder.itemView;

            TextView child = (TextView) ll_content.getChildAt(0);

            setBackgrandForItem(position, ll_content);

            dataAdapter.convertLeftData(position - (mFloatingTitle ? 0 : 1), child);
        }

        @Override
        public int getItemCount() {
            return dataAdapter.getItemCount() + (mFloatingTitle ? 0 : 1);
        }

        class LeftViewHolder extends RecyclerView.ViewHolder {
            LeftViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        List<TextView> bindViews;

        TableViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ParametersHolder {
        int col_1_color;
        int col_2_color;
        int bgColor;
        int textColor;
        int defaultWidth;
        int[] widthArray;//PX
        int tbPadding;
        int lrPadding;
        int gravity;
        float textSize;
        int lineCount;

        public ParametersHolder(int s_color, int b_color, int bgColor,
                                int textColor, float textSize, int lineCount,
                                int defaultWidth,
                                int tbPadding, int lrPadding,
                                int gravity,
                                int[] widthArray) {
            this.col_1_color = s_color;
            this.col_2_color = b_color;
            this.bgColor = bgColor;
            this.textColor = textColor;
            this.textSize = textSize;
            this.defaultWidth = defaultWidth;
            this.widthArray = widthArray;
            this.tbPadding = tbPadding;
            this.lrPadding = lrPadding;
            this.gravity = gravity;
            this.lineCount = lineCount;
        }

        public int getCol_1_color() {
            return col_1_color;
        }

        public int getCol_2_color() {
            return col_2_color;
        }

        public int getBgColor() {
            return bgColor;
        }

        public int getTextColor() {
            return textColor;
        }

        public int getDefaultWidth() {
            return defaultWidth;
        }

        public int[] getWidthArray() {
            return widthArray;
        }

        public int getTbPadding() {
            return tbPadding;
        }

        public int getLrPadding() {
            return lrPadding;
        }

        public int getGravity() {
            return gravity;
        }

        public float getTextSize() {
            return textSize;
        }

        public int getLineCount() {
            return lineCount;
        }
    }

    public static class Builder {
        HorizontalScrollView titleView;
        RecyclerView leftViews;
        TextView left_top_view;

        ParametersHolder contentParametersHolder;
        ParametersHolder titleParametersHolder;
        IDataAdapter dataAdapter;

        public Builder setTitleView(HorizontalScrollView titleView) {
            this.titleView = titleView;
            return this;
        }

        public Builder setLeftViews(RecyclerView leftViews) {
            this.leftViews = leftViews;
            return this;
        }

        public Builder setLeft_top_view(TextView left_top_view) {
            this.left_top_view = left_top_view;
            return this;
        }

        public Builder setTitleParametersHolder(
                ParametersHolder parametersHolder) {
            this.titleParametersHolder = parametersHolder;
            return this;
        }

        public Builder setContentParametersHolder(
                ParametersHolder parametersHolder) {
            this.contentParametersHolder = parametersHolder;
            return this;
        }

        public Builder setDataAdapter(IDataAdapter dataAdapter) {
            this.dataAdapter = dataAdapter;
            return this;
        }

        public TableAdapter create() {
            return new TableAdapter(titleView, leftViews, left_top_view, contentParametersHolder, titleParametersHolder, dataAdapter);
        }
    }

    public void notifyLoadData() {
        notifyDataSetChanged();
        if (leftViews != null) {
            leftViews.getAdapter().notifyDataSetChanged();
        }
    }
}
