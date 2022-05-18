package com.miaxis.face.view.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DrawTextView extends AppCompatEditText {


    /**
     * 绘制画笔
     */
    private Paint mPaint = new Paint();


    public DrawTextView(Context context) {
        super(context);
        init();
    }

    public DrawTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(0xffFF0000);
        mPaint.setStrokeWidth(dip2px(getContext(), 5));
    }

    private Map<Path, String> fingerPath = new HashMap<>();
    private Path path;
    private Handler mHandler = new Handler();
    private float temp_x;
    private float temp_y;

    private boolean editMod = false;

    private boolean isMove = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                path = new Path();
                clearFocus();
                setCursorVisible(true);
                path.moveTo(temp_x = event.getX(), temp_y = event.getY());
                Log.e("onTouchEvent", "按下");
                //                mHandler.removeCallbacksAndMessages(null);
                //                mHandler.postDelayed(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        Log.e("super  onTouchEvent", "event:" + event.toString());
                //                        setFocusable(true);
                //                        event.setAction(MotionEvent.ACTION_DOWN);
                //                        DrawTextView.super.onTouchEvent(event);
                //                    }
                //                }, 1000);
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_MOVE:
                //Log.e("onTouchEvent", "ACTION_MOVE");
                if (Math.abs(event.getX() - temp_x) <= 20 && Math.abs(event.getY() - temp_y) <= 20) {
                    Log.e("onTouchEvent", "移动   静止");
                    return true;
                } else {
                    isMove = true;
                    clearFocus();
                    setCursorVisible(false);
                    Log.e("onTouchEvent", "移动   移动");
                    mHandler.removeCallbacksAndMessages(null);
                    if (path != null) {
                        path.lineTo(event.getX(), event.getY());
                        fingerPath.put(path, "");
                    }
                    invalidate();
                }
                return super.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacksAndMessages(null);
                Log.e("onTouchEvent", "抬起");
                if (isMove){
                    return true;
                }else {
                    return super.onTouchEvent(event);
                }
        }
        return super.onTouchEvent(event);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Set<Map.Entry<Path, String>> entries = fingerPath.entrySet();
        for (Map.Entry<Path,String > entry :entries ) {
        	canvas.drawPath(entry.getKey(),mPaint);
        }
    }

    /**
     * 清空画布
     */
    public void clear() {
        fingerPath.clear();
        invalidate();
    }

    /**
     * 获取绘制图案的 Bitmap
     */
    public Bitmap getDrawBitmap() {
        Bitmap bitmap;
        try {
            setDrawingCacheEnabled(true);
            buildDrawingCache();
            bitmap = Bitmap.createBitmap(getDrawingCache(), 0, 0, getMeasuredWidth(), getMeasuredHeight(), null, false);
        } finally {
            setDrawingCacheEnabled(false);
            destroyDrawingCache();
        }
        return bitmap;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((double) dipValue * (double) scale + 0.5);
    }
}