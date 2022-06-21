package com.example.bookkeeping.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.example.bookkeeping.R;
import com.example.bookkeeping.tools.LiveDataBus;

public class LoginButton extends androidx.appcompat.widget.AppCompatButton {

    private int width;
    private int heigh;

    //背景
    private GradientDrawable backDrawable;
    //是否绘制圆形
    private boolean isMorphing;

    private int startAngle;

    private Paint paint;

    private ValueAnimator arcValueAnimator;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LoginButton(Context context) {
        super(context);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LoginButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public LoginButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init(Context context) {
        isMorphing = false;
        //设置圆角
        backDrawable = new GradientDrawable();
        int colorDrawable = getResources().getColor(R.color.blue_primary);
        String loginbuttoncolor = LiveDataBus.getInstance().with("loginbuttoncolor", String.class).getValue();
        switch (loginbuttoncolor == null ? "蓝色": loginbuttoncolor) {
            case "蓝色":
                colorDrawable = getResources().getColor(R.color.blue_primary);
                break;
            case "绿色":
                colorDrawable = getResources().getColor(R.color.grenn_primary);
                break;

            case "橙色":
                colorDrawable = getResources().getColor(R.color.orange_primary);
                break;

            case "粉色":
                colorDrawable = getResources().getColor(R.color.pink_primary);
                break;
        }
        backDrawable.setColor(colorDrawable);
        backDrawable.setCornerRadius(120);
        setBackground(backDrawable);

        setText("登陆");

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.white));
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heighMode = MeasureSpec.getMode(heightMeasureSpec);
        int heighSize = MeasureSpec.getSize(heightMeasureSpec);
        //判断是否的确切的大小
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        if (heighMode == MeasureSpec.EXACTLY) {
            heigh = heighSize;
        }
    }

    public void startAnim() {
        isMorphing = true;

        setText("");
        //控制值的变化,达到实现动画效果
        ValueAnimator valueAnimator = ValueAnimator.ofInt(width, heigh);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int leftOffset = (width - value) / 2;
                int rightOffset = width - leftOffset;
                //设置大小
                backDrawable.setBounds(leftOffset, 0, rightOffset, heigh);
            }
        });
        //设置圆角
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backDrawable, "cornerRadius", 120, heigh / 2);

        AnimatorSet animatorSet = new AnimatorSet();
        //时长
        animatorSet.setDuration(500);
        //两个动画同时执行
        animatorSet.playTogether(valueAnimator, objectAnimator);
        animatorSet.start();

        //画中间的白色圆圈
        showArc();
    }

    public void gotoNew() {
        isMorphing = false;
        arcValueAnimator.cancel();
        setVisibility(GONE);
    }

    //恢复原来的样式
    public void regainBackground() {
        setVisibility(VISIBLE);
        backDrawable.setCornerRadius(120);
        setBackground(backDrawable);
        setText("登陆");
        isMorphing = false;

        ValueAnimator valueAnimator = ValueAnimator.ofInt(width, heigh);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //设置大小
                backDrawable.setBounds(0, 0, width, heigh);
            }
        });
        //设置圆角
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(backDrawable, "cornerRadius", 120, heigh / 2);

        AnimatorSet animatorSet = new AnimatorSet();
        //时长
        animatorSet.setDuration(500);
        //两个动画同时执行
        animatorSet.playTogether(valueAnimator, objectAnimator);
        animatorSet.start();
    }

    private void showArc() {
        //从什么地方到终点
        arcValueAnimator = ValueAnimator.ofInt(0, 1080);
        //监听值的变化趋势
        arcValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngle = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        //设置动画播放的速度
        arcValueAnimator.setInterpolator(new LinearInterpolator());
        //重复次数
        arcValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //时长
        arcValueAnimator.setDuration(3000);
        arcValueAnimator.start();


    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (isMorphing == true) {
            final RectF rectF = new RectF(getWidth() * 5 / 12, getHeight() / 7, getWidth() * 7 / 12, getHeight() - getHeight() / 7);
            //绘制圆形
            canvas.drawArc(rectF, startAngle, 270, false, paint);
        }
    }
}
