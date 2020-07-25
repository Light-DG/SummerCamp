package com.example.customviewtest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class LineView extends View {
    Paint mPaint = new Paint();
    private int flag = 3;


    public void initPaint(){
        mPaint.setColor(Color.parseColor("#E0E0E0"));
        mPaint.setStrokeWidth(10f);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public LineView(Context context) {
        this(context,null);
    }

    public LineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (flag == 1){
            canvas.drawLines(new float[]{
                    0,0,60,15,
                    60,15,120,0
            },mPaint);
            canvas.drawPoint(60,(float) 15.2,mPaint);
        }else if (flag == 2){
            canvas.drawLine(0,(float) 7.5,120,(float)7.5,mPaint);
        }else if (flag ==3){
            canvas.drawLines(new float[]{
                    1,15,60,1,
                    60,1,120,15,
            },mPaint);
            canvas.drawPoint(60,0,mPaint);
        }
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
        invalidate();
    }

}
