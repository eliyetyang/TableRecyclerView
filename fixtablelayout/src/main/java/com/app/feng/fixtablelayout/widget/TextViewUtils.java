package com.app.feng.fixtablelayout.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.feng.fixtablelayout.adapter.TableAdapter;

/**
 * Created by feng on 2017/4/4.
 */

public class TextViewUtils {

    public static TextView generateTextView(
            Context context,
            String text,
            int viewWidth,
            TableAdapter.ParametersHolder parameters) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setTextView(textView, text, viewWidth, parameters);
        return textView;
    }

    public static void setTextView(
            TextView textView,
            String text,
            int viewWidth,
            TableAdapter.ParametersHolder parameters) {
        textView.setText(text);
        textView.setWidth(viewWidth);
        textView.setLines(parameters.getLineCount());
        textView.setGravity(parameters.getGravity());
//        textView.setMinWidth(width);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
            }
        });
        textView.setTextColor(parameters.getTextColor());
        textView.setTextSize(parameters.getTextSize());

        textView.setPadding(parameters.getLrPadding(), parameters.getTbPadding(), parameters.getLrPadding(), parameters.getTbPadding());
    }
}
