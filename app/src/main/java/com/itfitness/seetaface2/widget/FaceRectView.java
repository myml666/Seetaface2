package com.itfitness.seetaface2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.seeta.sdk.SeetaRect;

/**
 * 用于绘制识别出来的人脸区域
 */
public class FaceRectView extends View {
    private Paint mPaint;
    private SeetaRect[] faceRects;

    public FaceRectView(Context context) {
        this(context, null);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);//绿色画笔
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("绘制","非空");
        if (faceRects!=null && faceRects.length > 0) {
            Log.e("绘制","非空");
            for (SeetaRect faceRect : faceRects) {
                Log.e("绘制",faceRect.x+"===="+faceRect.y+"====="+faceRect.width+"====="+faceRect.height);
                canvas.drawRect(new RectF(faceRect.x, faceRect.y, faceRect.x + faceRect.width, faceRect.y + faceRect.height), mPaint);
            }
        }else {
            Log.e("绘制","空");
        }
    }

    /**
     * 设置人脸数据
     *
     * @param faceRects
     */
    public void setFaceDatas(SeetaRect[] faceRects) {
        Log.e("设置",faceRects[0].x+"===="+faceRects[0].y+"====="+faceRects[0].width+"====="+faceRects[0].height);
        this.faceRects = faceRects;
        invalidate();
    }
}
