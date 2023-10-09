package com.app.feng.tablerecyclerview;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.app.feng.fixtablelayout.FixTableLayout;
import com.app.feng.fixtablelayout.inter.ILoadMoreListener;
import com.app.feng.tablerecyclerview.bean.DataBean;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public String[] title = {"姓名", "手机号", "会籍顾问", "本月上课次数", "上月上课次数", "近90天上课次数", "近120天上课次数",
            "卡数量", "剩余资产余额"};

    public List<DataBean> data = new ArrayList<>();

    public String[][] dataCopy = {
            {"张萌萌", "13683567890", "柚子", "6", "2", "10", "10",
                    "10", "1000.00"}};

    int currentPage = 1;
    int totalPage = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        for (int i = 0; i < 80; i++) {
            data.add(new DataBean("张萌萌", "13683567890", "柚子", "6", "2", "10", "10", "10",
                    String.valueOf(100.0 * i)));
        }

        final FixTableLayout fixTableLayout = (FixTableLayout) findViewById(R.id.fixTableLayout);

        // 一定要设置Adapter 否则看不到TableLayout
        final FixTableAdapter fixTableAdapter = new FixTableAdapter<DataBean>(title, data) {

            @Override
            public void setLinerItemView(DataBean dataBean, List<TextView> bindViews) {
                bindViews.get(0)
                        .setText(dataBean.id);
                bindViews.get(1)
                        .setText(dataBean.data1);
                bindViews.get(2)
                        .setText(dataBean.data2);
                bindViews.get(3)
                        .setText(dataBean.data3);
                bindViews.get(4)
                        .setText(dataBean.data4);
                bindViews.get(5)
                        .setText(dataBean.data5);
                bindViews.get(6)
                        .setText(dataBean.data6);
                bindViews.get(7)
                        .setText(dataBean.data7);
                bindViews.get(8)
                        .setText(dataBean.data8);
            }

            @Override
            public void setLeftItemView(DataBean data, TextView bindView) {
                bindView.setText(data.id);
            }
        };

//        fixTableAdapter.setFloatingTitle(false);

        int[] itemWidthArray = new int[]{
                dip2px(75),
                dip2px(126),
                dip2px(82),
                dip2px(82),
                dip2px(82),
                dip2px(87),
                dip2px(92),
                dip2px(72),
                dip2px(70)

        };
        fixTableLayout.setTitleItemWidthArray(itemWidthArray);
        fixTableLayout.setContentItemWidthArray(itemWidthArray);
        fixTableLayout.setAdapter(fixTableAdapter);

        //LoadMoreData如果要设置 请在setAdapter之后
        fixTableLayout.enableLoadMoreData();

        fixTableLayout.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void loadMoreData(final Message message) {
                Log.i("feng", " 加载更多 Data --- ");
                // 请自己开启线程加载数据，并通过message发送给fixTableLayout
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPage <= totalPage) {
                            for (int i = 0; i < 50; i++) {
                                data.add(new DataBean("update_id", "update_data", "data2", "data3", "data4", "data5",
                                        "data6", "data7", "data8"));
                            }
                            currentPage++;
                            message.arg1 = 50; // 更新了50条数据
                        } else {
                            message.arg1 = 0;
                        }
                        message.sendToTarget();
                    }
                }).start();
            }
        });
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
