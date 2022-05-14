package com.miaxis.face.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import org.zz.api.MXFaceInfoEx;


public class RectSurfaceView extends SurfaceView {

    private Context mContext;
    private SurfaceHolder shRect;
    private float zoomRate = 1;
    private int rootWidth;
    private int rootHeight;

    public RectSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        shRect = getHolder();
        setZOrderOnTop(true);
        shRect.setFormat(PixelFormat.TRANSLUCENT);
    }

    public void setZoomRate(float zoomRate) {
        this.zoomRate = zoomRate;
    }

    public void setRootSize(int width, int height) {
        this.rootWidth = width;
        this.rootHeight = height;
    }

    public void drawRect() {
        Canvas canvas = shRect.lockCanvas(null);
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(110, 110, 610, 700, p);
        shRect.unlockCanvasAndPost(canvas);
    }

    public void clearDraw() {
        Canvas canvas = shRect.lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        shRect.unlockCanvasAndPost(canvas);
    }

    public void drawRect(MXFaceInfoEx[] faceInfos, int faceNum) {
        Canvas canvas = shRect.lockCanvas(null);
        if (canvas == null) {
            return;
        }
        canvas.translate(canvas.getWidth(),0);
        canvas.scale(-1,1);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (faceNum != 0) {
//            drawFaceRect(faceInfos, canvas, faceNum);
            drawFaceMirrorRect(faceInfos, canvas, faceNum);
        }
        shRect.unlockCanvasAndPost(canvas);
    }

    private void drawFaceMirrorRect(MXFaceInfoEx[] faceInfos, Canvas canvas, int len) {
        float[] startArrayX = new float[len];
        float[] startArrayY = new float[len];
        float[] stopArrayX = new float[len];
        float[] stopArrayY = new float[len];
        for (int i = 0; i < len; i++) {
            startArrayX[i] = rootWidth - faceInfos[i].x * zoomRate;
            startArrayY[i] = faceInfos[i].y * zoomRate;
            stopArrayX[i] = rootWidth - faceInfos[i].x * zoomRate - faceInfos[i].width * zoomRate;
            stopArrayY[i] = faceInfos[i].y * zoomRate + faceInfos[i].height * zoomRate;
        }
        canvasDrawMirrorLine(canvas, len, startArrayX, startArrayY, stopArrayX, stopArrayY);
    }

    /* 画线 */
    private void canvasDrawMirrorLine(Canvas canvas, int iNum, float[] startArrayX, float[] startArrayY, float[] stopArrayX, float[] stopArrayY) {
        int iLen = 50;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        float startX, startY, stopX, stopY;
        for (int i = 0; i < iNum; i++) {
            startX = startArrayX[i];
            startY = startArrayY[i];
            stopX = stopArrayX[i];
            stopY = stopArrayY[i];
            mPaint.setStrokeWidth(6);// 设置画笔粗细
            canvas.drawLine(startX, startY, startX - iLen, startY, mPaint);
            canvas.drawLine(stopX + iLen, startY, stopX, startY, mPaint);
            canvas.drawLine(startX, startY, startX, startY + iLen, mPaint);
            canvas.drawLine(startX, stopY - iLen, startX, stopY, mPaint);
            canvas.drawLine(stopX, stopY, stopX, stopY - iLen, mPaint);
            canvas.drawLine(stopX, startY + iLen, stopX, startY, mPaint);
            canvas.drawLine(stopX, stopY, stopX + iLen, stopY, mPaint);
            canvas.drawLine(startX - iLen, stopY, startX, stopY, mPaint);
        }
    }

    //* 画人脸框 */
    private void drawFaceRect(MXFaceInfoEx[] faceInfos, Canvas canvas, int len) {
        float[] startArrayX = new float[len];
        float[] startArrayY = new float[len];
        float[] stopArrayX = new float[len];
        float[] stopArrayY = new float[len];
        for (int i = 0; i < len; i++) {
            startArrayX[i] = (faceInfos[i].x * zoomRate);
            startArrayY[i] = (faceInfos[i].y * zoomRate);
            stopArrayX[i] = (faceInfos[i].x * zoomRate + faceInfos[i].width * zoomRate);
            stopArrayY[i] = (faceInfos[i].y * zoomRate + faceInfos[i].height * zoomRate);
        }
        canvasDrawLine(canvas, len, startArrayX, startArrayY, stopArrayX, stopArrayY);
    }

    /* 画线 */
    private void canvasDrawLine(Canvas canvas, int iNum, float[] startArrayX, float[] startArrayY, float[] stopArrayX, float[] stopArrayY) {
        int iLen = 50;
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        float startX, startY, stopX, stopY;
        for (int i = 0; i < iNum; i++) {
            startX = startArrayX[i];
            startY = startArrayY[i];
            stopX = stopArrayX[i];
            stopY = stopArrayY[i];
            mPaint.setStrokeWidth(6);// 设置画笔粗细
            canvas.drawLine(startX, startY, startX + iLen, startY, mPaint);
            canvas.drawLine(stopX - iLen, startY, stopX, startY, mPaint);
            canvas.drawLine(startX, startY, startX, startY + iLen, mPaint);
            canvas.drawLine(startX, stopY - iLen, startX, stopY, mPaint);
            canvas.drawLine(stopX, stopY, stopX, stopY - iLen, mPaint);
            canvas.drawLine(stopX, startY + iLen, stopX, startY, mPaint);
            canvas.drawLine(stopX, stopY, stopX - iLen, stopY, mPaint);
            canvas.drawLine(startX + iLen, stopY, startX, stopY, mPaint);
        }
    }

}
