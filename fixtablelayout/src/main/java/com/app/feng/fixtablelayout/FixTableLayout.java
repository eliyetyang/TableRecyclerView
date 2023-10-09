package com.app.feng.fixtablelayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.app.feng.fixtablelayout.adapter.TableAdapter;
import com.app.feng.fixtablelayout.inter.IDataAdapter;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.app.feng.fixtablelayout.widget.SingleLineItemDecoration;
import com.app.feng.fixtablelayout.widget.TableLayoutManager;

import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by feng on 2017/4/2.
 */
public class FixTableLayout extends FrameLayout {
    public static final int MESSAGE_FIX_TABLE_LOAD_COMPLETE = 1001;

    RecyclerView recyclerView;
    HorizontalScrollView titleView;
    RecyclerView leftViews;
    TextView left_top_view;
    View leftViewShadow;
    View topViewShadow;
    FrameLayout fl_load_mask;

    int divider_height;
    int divider_color;
    int col_1_color;
    int col_2_color;
    int title_bg_color;
    int content_text_color;
    int item_width;
    int[] contentItemWidthArray;
    int item_tbPadding;
    int item_lfPadding;
    int item_gravity;
    float item_text_size;

    int title_text_color;
    int title_tbPadding;
    int title_lrPadding;
    int[] titleItemWidthArray;
    int title_gravity;
    float title_text_size;

    int titleLineCount = 1;
    int contentLineCount = 2;

    boolean show_left_shadow = false;
    boolean show_top_shadow = false;
    boolean show_left_view = false;
    boolean show_left_title_view = false;
    boolean floating_title = false;

    private IDataAdapter dataAdapter;

    private boolean isLoading = false;
    private ILoadMoreListener loadMoreListener;
    private boolean hasMoreData = true;

    public FixTableLayout(Context context) {
        this(context, null);
    }

    public FixTableLayout(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixTableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FixTableLayout);

        divider_height = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_divider_height,
                getResources().getDimensionPixelOffset(R.dimen.divider_default_value));
        divider_color = array.getColor(R.styleable.FixTableLayout_fixtable_divider_color,
                Color.BLACK);
        col_1_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_1_color,
                Color.WHITE);
        col_2_color = array.getColor(R.styleable.FixTableLayout_fixtable_column_2_color,
                Color.WHITE);

        content_text_color = array.getColor(R.styleable.FixTableLayout_fixtable_content_text_color, Color.BLACK);
        item_width = array.getDimensionPixelOffset(R.styleable.FixTableLayout_fixtable_item_width,
                getResources().getDimensionPixelOffset(R.dimen.item_width_default_value));
        item_tbPadding = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_item_top_bottom_padding, 0);
        item_lfPadding = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_item_left_right_padding, 0);
        item_gravity = array.getInteger(R.styleable.FixTableLayout_fixtable_item_gravity, 0);
        item_text_size = array.getDimension(R.styleable.FixTableLayout_fixtable_content_text_size, 15f);
        contentLineCount = array.getInteger(R.styleable.FixTableLayout_fixtable_show_content_line_count, 2);

        title_bg_color = array.getColor(R.styleable.FixTableLayout_fixtable_title_bg_color, Color.GRAY);
        title_tbPadding = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_title_top_bottom_padding, 0);
        title_lrPadding = array.getDimensionPixelOffset(
                R.styleable.FixTableLayout_fixtable_title_left_right_padding, 0);
        title_gravity = array.getInteger(R.styleable.FixTableLayout_fixtable_title_gravity, Gravity.LEFT | Gravity.CENTER_VERTICAL);
        title_text_color = array.getColor(R.styleable.FixTableLayout_fixtable_title_text_color, Color.BLACK);
        title_text_size = array.getDimension(R.styleable.FixTableLayout_fixtable_title_text_size, 13f);
        titleLineCount = array.getInteger(R.styleable.FixTableLayout_fixtable_show_title_line_count, 1);

        show_left_shadow = array.getBoolean(
                R.styleable.FixTableLayout_fixtable_show_left_view_shadow, false);
        show_top_shadow = array.getBoolean(
                R.styleable.FixTableLayout_fixtable_show_title_view_shadow, false);
        show_left_view = array.getBoolean(
                R.styleable.FixTableLayout_fixtable_show_left_view, false);
        show_left_title_view = array.getBoolean(
                R.styleable.FixTableLayout_fixtable_show_left_title_view, false);
        floating_title = array.getBoolean(
                R.styleable.FixTableLayout_fixtable_floating_title, false);

        array.recycle();

        View view = inflate(context, R.layout.table_view, null);
        init(view);
        addView(view);
    }

    private void init(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        titleView = (HorizontalScrollView) view.findViewById(R.id.titleView);
        leftViews = (RecyclerView) view.findViewById(R.id.leftViews);
        left_top_view = (TextView) view.findViewById(R.id.left_top_view);
        topViewShadow = view.findViewById(R.id.titleView_shadows);
        leftViewShadow = view.findViewById(R.id.leftView_shadows);
        fl_load_mask = (FrameLayout) view.findViewById(R.id.load_mask);

        TableLayoutManager t1 = new TableLayoutManager();
        recyclerView.setLayoutManager(t1);

//        Log.i("feng"," -- t : " + t1.toString().substring(54) + " t_left: " + t2.toString()
//                .substring(54));
        if (!show_left_title_view) {
            left_top_view.setVisibility(View.GONE);
        }

        if (floating_title) {
            titleView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    recyclerView.onTouchEvent(event);
                    return true;
                }
            });
        }

        leftViewShadow.setVisibility(show_left_shadow && show_left_view ? View.VISIBLE : View.GONE);
        topViewShadow.setVisibility(show_top_shadow && show_left_title_view ? View.VISIBLE : View.GONE);

        SingleLineItemDecoration itemDecoration = new SingleLineItemDecoration(divider_height, divider_color);
        recyclerView.addItemDecoration(itemDecoration);

        //titleView 只做横向滚动   leftView 只做纵向滚动
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                titleView.scrollBy(dx, 0);
                if (show_left_view) {
                    leftViews.scrollBy(0, dy);
                }
            }
        });

        if (show_left_view) {
            TableLayoutManager t2 = new TableLayoutManager();
            leftViews.setLayoutManager(t2);
            leftViews.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //将事件发送到RV
                    recyclerView.onTouchEvent(event);
                    return true;
                }
            });
            leftViews.addItemDecoration(itemDecoration);
        } else {
            leftViews.setVisibility(View.GONE);
        }
//
//        left_top_view.setVisibility(View.INVISIBLE);
    }

    /**
     * 需要在{@link #setAdapter}之前调用。
     *
     * @param contentItemWidthArray
     */
    public void setContentItemWidthArray(int[] contentItemWidthArray) {
        this.contentItemWidthArray = contentItemWidthArray;
    }

    /**
     * 需要在{@link #setAdapter}之前调用。
     *
     * @param titleItemWidthArray
     */
    public void setTitleItemWidthArray(int[] titleItemWidthArray) {
        this.titleItemWidthArray = titleItemWidthArray;
    }

    public void setAdapter(IDataAdapter dataAdapter) {
        this.dataAdapter = dataAdapter;
        initRecyclerViewAdapter();
    }

    int lastVisablePos = -1;
    FixTableHandler fixTableHandler;

    public void enableLoadMoreData() {
        fixTableHandler = new FixTableHandler(FixTableLayout.this, recyclerView);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当用户滑动到底部并且使用fling手势
                if (!isLoading && hasMoreData &&
                        newState == RecyclerView.SCROLL_STATE_IDLE &&
                        lastVisablePos == recyclerView.getAdapter().getItemCount() - 1) {

                    isLoading = true;
                    fl_load_mask.setVisibility(VISIBLE);

                    if (loadMoreListener != null) {
                        loadMoreListener.loadMoreData(
                                fixTableHandler.obtainMessage(FixTableLayout.MESSAGE_FIX_TABLE_LOAD_COMPLETE));
                    }
                }
                //                    Log.i("feng","滑动到底部 -- 此时的View Bottom：" + recyclerView.getLayoutManager()
                //                            .getDecoratedBottom
                //                            (bottomView) + " recyclerView Height:" +recyclerView.getHeight());

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View bottomView = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                lastVisablePos = recyclerView.getChildAdapterPosition(bottomView);
            }
        });
    }

    /**
     * 只有 enableLoadMoreData()被执行此方法设置才有效果
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private void initRecyclerViewAdapter() {
        TableAdapter.Builder builder = new TableAdapter.Builder();
        TableAdapter tableAdapter = builder.setLeft_top_view(show_left_title_view ? left_top_view : null)
                .setTitleView(floating_title ? titleView : null)
                .setContentParametersHolder(
                        new TableAdapter.ParametersHolder(col_1_color, col_2_color, title_bg_color,
                                content_text_color, item_text_size, contentLineCount,
                                item_width, item_tbPadding, item_lfPadding,
                                item_gravity, contentItemWidthArray))
                .setTitleParametersHolder(
                        new TableAdapter.ParametersHolder(col_1_color, col_2_color, title_bg_color,
                                title_text_color, title_text_size, titleLineCount,
                                item_width, title_tbPadding, title_lrPadding,
                                title_gravity, titleItemWidthArray))
                .setLeftViews(show_left_view ? leftViews : null)
                .setDataAdapter(dataAdapter)
                .create();

        recyclerView.setAdapter(tableAdapter);
    }

    public void dataUpdate() {
        TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
        tableAdapter.notifyLoadData();
    }

    private static class FixTableHandler extends Handler {
        WeakReference<RecyclerView> recyclerViewWeakReference;
        WeakReference<FixTableLayout> fixTableLayoutWeakReference;

        FixTableHandler(FixTableLayout fixTableLayout, RecyclerView recyclerView) {
            recyclerViewWeakReference = new WeakReference<>(recyclerView);
            fixTableLayoutWeakReference = new WeakReference<>(fixTableLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_FIX_TABLE_LOAD_COMPLETE) {
                RecyclerView recyclerView = recyclerViewWeakReference.get();
                FixTableLayout fixTableLayout = fixTableLayoutWeakReference.get();

                TableAdapter tableAdapter = (TableAdapter) recyclerView.getAdapter();
                int startPos = tableAdapter.getItemCount() - 1;
                int loadNum = msg.arg1;
                if (loadNum > 0) {
                    //通知Adapter更新数据
                    tableAdapter.notifyLoadData();

//                    Log.i("fixtablelayout","load more completed loadNum :" + loadNum + "scrollTo " +
//                            ":" + fixTableLayout.lastVisableMask);

                } else {
                    //没有数据了
                    fixTableLayout.hasMoreData = false;
                }

                fixTableLayout.fl_load_mask.setVisibility(GONE);
                fixTableLayout.isLoading = false;
            }
        }
    }
}
