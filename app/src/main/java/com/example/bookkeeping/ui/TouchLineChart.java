package com.example.bookkeeping.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;

public class TouchLineChart extends LineChart {
    public TouchLineChart(Context context) {
        super(context);
    }

    public TouchLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}