package com.example.bookkeeping.tools;

import android.view.View;

public class BindingAdapter {

    //判断控件是否隐藏
    @androidx.databinding.BindingAdapter("isGone")
    public static void bindIsGone(View view,boolean isGone){
        if(isGone){
            view.setVisibility(View.GONE);
        }else{
            view.setVisibility(View.VISIBLE);
        }
    }

}
