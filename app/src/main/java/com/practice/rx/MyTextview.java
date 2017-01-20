package com.practice.rx;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;


/**
 * Created by gaofeng on 2017-01-20.
 */

public class MyTextview extends TextView {
    private static final String TAG = MyTextview.class.getSimpleName();
    Paint paint=null;
    RectF oval;
    float progress;
    private static final float START_ANGLE=190;
    private static final float SWEEP_ANGLE=160;
    private static final float STROKE_WIDTH=5;
    public MyTextview(Context context) {
        super(context);
    }

    public MyTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setClickable(true);
        paint=new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(0xFFFF0000);

        paint.setAntiAlias(true);
        setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
    }

    public void setProgress(float progress){
        this.progress=progress;
        progress=Math.max(0f,Math.min(progress,1f));
        setText(String.format("%1d",(int)(progress*100)));
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        oval=new RectF(STROKE_WIDTH+getPaddingLeft()/2,STROKE_WIDTH+getPaddingTop()/3,getMeasuredWidth()-STROKE_WIDTH-getPaddingRight()/2,(getMeasuredHeight()-STROKE_WIDTH-getPaddingBottom())*2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(oval,START_ANGLE,SWEEP_ANGLE*progress,false,paint);
        super.onDraw(canvas);
    }
}
