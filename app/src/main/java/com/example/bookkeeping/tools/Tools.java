package com.example.bookkeeping.tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.example.bookkeeping.R;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tools {
    private static Toast toast = null;

    public static void showToast(Context context, String text) {

        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.cancel();
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void SnackbarShow(View view, String content) {
        Snackbar.make(view, content, Snackbar.LENGTH_LONG).show();
    }

    //获取单月有多少天
    public static int getMonthOfDay(int year, int month) {
        int day = 0;
        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            day = 29;
        } else {
            day = 28;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return day;

        }

        return 0;
    }

    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static Date stringToDate2(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }

    public static Drawable itemImg(Context context, String s) {
        Drawable img = null;
        switch (s) {
            case "三餐":
                img = context.getResources().getDrawable(R.drawable.addimg1);
                break;
            case "零食":
                img = context.getResources().getDrawable(R.drawable.addimg2);
                break;
            case "衣服":
                img = context.getResources().getDrawable(R.drawable.addimg3);
                break;
            case "交通":
                img = context.getResources().getDrawable(R.drawable.addimg4);
                break;
            case "旅游":
                img = context.getResources().getDrawable(R.drawable.addimg5);
                break;
            case "孩子":
                img = context.getResources().getDrawable(R.drawable.addimg6);
                break;
            case "宠物":
                img = context.getResources().getDrawable(R.drawable.addimg7);
                break;
            case "通讯":
                img = context.getResources().getDrawable(R.drawable.addimg8);
                break;
            case "烟酒":
                img = context.getResources().getDrawable(R.drawable.addimg9);
                break;
            case "学习":
                img = context.getResources().getDrawable(R.drawable.addimg10);
                break;
            case "日用":
                img = context.getResources().getDrawable(R.drawable.addimg11);
                break;
            case "住房":
                img = context.getResources().getDrawable(R.drawable.addimg12);
                break;
            case "美妆":
                img = context.getResources().getDrawable(R.drawable.addimg13);
                break;
            case "医疗":
                img = context.getResources().getDrawable(R.drawable.addimg14);
                break;
            case "礼金":
                img = context.getResources().getDrawable(R.drawable.addimg15);
                break;
            case "娱乐":
                img = context.getResources().getDrawable(R.drawable.addimg16);
                break;
            case "请客":
                img = context.getResources().getDrawable(R.drawable.addimg17);
                break;
            case "数码":
                img = context.getResources().getDrawable(R.drawable.addimg18);
                break;
            case "运动":
                img = context.getResources().getDrawable(R.drawable.addimg19);
                break;
            case "办公":
                img = context.getResources().getDrawable(R.drawable.addimg20);
                break;
            case "工资":
                img = context.getResources().getDrawable(R.drawable.addimgshou1);
                break;
            case "兼职":
                img = context.getResources().getDrawable(R.drawable.addimgshou2);
                break;
            case "股票":
                img = context.getResources().getDrawable(R.drawable.addimgshou4);
                break;
            case "基金":
                img = context.getResources().getDrawable(R.drawable.addimgshou5);
                break;
            case "中奖":
                img = context.getResources().getDrawable(R.drawable.addimgshou6);
                break;
            case "营业":
                img = context.getResources().getDrawable(R.drawable.addimgshou7);
                break;
            case "其他":
                img = context.getResources().getDrawable(R.drawable.addimg21);
                break;
            default:
                img = context.getResources().getDrawable(R.drawable.xiaolian);
                break;
        }
        return img;
    }

    public static int itemImgInt(Context context, String s) {
        int img = 0;
        switch (s) {
            case "三餐":
                img = R.drawable.addimg1;
                break;
            case "零食":
                img = R.drawable.addimg2;
                break;
            case "衣服":
                img = R.drawable.addimg3;
                break;
            case "交通":
                img = R.drawable.addimg4;
                break;
            case "旅游":
                img = R.drawable.addimg5;
                break;
            case "孩子":
                img = R.drawable.addimg6;
                break;
            case "宠物":
                img = R.drawable.addimg7;
                break;
            case "通讯":
                img = R.drawable.addimg8;
                break;
            case "烟酒":
                img = R.drawable.addimg9;
                break;
            case "学习":
                img = R.drawable.addimg10;
                break;
            case "日用":
                img = R.drawable.addimg11;
                break;
            case "住房":
                img = R.drawable.addimg12;
                break;
            case "美妆":
                img = R.drawable.addimg13;
                break;
            case "医疗":
                img = R.drawable.addimg14;
                break;
            case "礼金":
                img = R.drawable.addimg15;
                break;
            case "娱乐":
                img = R.drawable.addimg16;
                break;
            case "请客":
                img = R.drawable.addimg17;
                break;
            case "数码":
                img = R.drawable.addimg18;
                break;
            case "运动":
                img = R.drawable.addimg19;
                break;
            case "办公":
                img = R.drawable.addimg20;
                break;
            case "工资":
                img = R.drawable.addimgshou1;
                break;
            case "兼职":
                img = R.drawable.addimgshou2;
                break;
            case "股票":
                img = R.drawable.addimgshou4;
                break;
            case "基金":
                img = R.drawable.addimgshou5;
                break;
            case "中奖":
                img = R.drawable.addimgshou6;
                break;
            case "营业":
                img = R.drawable.addimgshou7;
                break;
            case "其他":
                img = R.drawable.addimg21;
                break;
            default:
                img = R.drawable.xiaolian;
                break;
        }
        return img;
    }
}
