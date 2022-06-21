package com.example.bookkeeping.chart;

import android.content.Context;
import android.widget.TextView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.tools.LiveDataBus;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;


public class DetailsMarkerView extends MarkerView {

    private TextView mTvMonth;
    private TextView mTvChart1;

    /**
     * 在构造方法里面传入自己的布局以及实例化控件
     *
     * @param context 上下文
     * @param layoutResource 布局
     */
    public DetailsMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        mTvMonth = findViewById(R.id.chart_layout_text);
        mTvChart1 = findViewById(R.id.chart_layout_text2);
    }

    //每次重绘，会调用此方法刷新数据
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        super.refreshContent(e, highlight);
        try {
            //收入
            if (e.getY() == 0) {
                mTvChart1.setVisibility(GONE);
                mTvMonth.setVisibility(GONE);
            } else {
                mTvChart1.setVisibility(VISIBLE);
                mTvMonth.setVisibility(VISIBLE);
                mTvChart1.setText( LiveDataBus.getInstance().with("chartStr", String.class).getValue()+":" + e.getY()+"元");
            }






















            if (LiveDataBus.getInstance().with("selectMonth", int.class).getValue() < 10) {
                if(e.getX() + 1 < 10){
                    mTvMonth.setText("0"+LiveDataBus.getInstance()
                            .with("selectMonth", int.class).getValue() + "-0" + ((int)(e.getX() + 1)));
                }else{
                    mTvMonth.setText("0"+LiveDataBus.getInstance()
                            .with("selectMonth", int.class).getValue() + "-" + ((int)(e.getX() + 1)));
                }
            } else {
                if(e.getX() + 1 < 10){
                    mTvMonth.setText("0"+LiveDataBus.getInstance()
                            .with("selectMonth", int.class).getValue() + "-0" + ((int)(e.getX() + 1)));
                }else{
                    mTvMonth.setText("0"+LiveDataBus.getInstance()
                            .with("selectMonth", int.class).getValue() + "-" + ((int)(e.getX() + 1)));
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        super.refreshContent(e, highlight);
    }

    //布局的偏移量。就是布局显示在圆点的那个位置
    // -(width / 2) 布局水平居中
    //-(height) 布局显示在圆点上方
    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}