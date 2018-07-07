package com.miaomaio.qrcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import me.dm7.barcodescanner.core.ViewFinderView;

/**
 * Author : zhongwenpeng
 * Email : 1340751953@qq.com
 * Time :  2018/7/6
 * Description :
 */
public class CustomFinderView extends ViewFinderView {

    private static final String TAG = "CustomFinderView";
    /**
     * 画外边框
     */
    private Paint mSquarePaint;
    private Paint mLinePaint;
    private Rect mLineRect;
    private int mChangeScope;

    public CustomFinderView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mChangeScope = (int) dpToPx(5);
        setSquareViewFinder(true);
        setBorderColor(Color.parseColor("#D9AD65"));
        setBorderLineLength((int) dpToPx(10));
        setBorderStrokeWidth((int) dpToPx(2));

        mSquarePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSquarePaint.setColor(Color.parseColor("#D9AD65"));
        mSquarePaint.setStyle(Paint.Style.STROKE);
        mSquarePaint.setStrokeWidth(dpToPx(0.5f));

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.parseColor("#D9AD65"));


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mLineRect = new Rect();
        mLineRect.left = getFramingRect().left;
        mLineRect.right = getFramingRect().right;
        mLineRect.top = getFramingRect().top;
        mLineRect.bottom = (int) (mLineRect.top + dpToPx(50));
    }

    public CustomFinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSquare(canvas);
        drawLine(canvas);
    }

    boolean isInversion = false;
    private void drawLine(Canvas canvas) {
        Log.i(TAG, "drawLine: ");
        //计算范围



        //范围控制
        if (mLineRect.top >= getFramingRect().bottom - dpToPx(50)) {
//            mLineRect.top = getFramingRect().top;
//            mLineRect.bottom = mLineRect.top + 100;
            isInversion = true;

        } else if (mLineRect.top <= getFramingRect().top) {
            isInversion = false;

        }

        if (isInversion) {
            mLineRect.top -= mChangeScope;
            mLineRect.bottom -= mChangeScope;
        } else {
            mLineRect.top += mChangeScope;
            mLineRect.bottom += mChangeScope;
        }


        final LinearGradient linearGradient = new LinearGradient(
                mLineRect.left,
                mLineRect.top,
                mLineRect.left,
                mLineRect.bottom,
                isInversion ? Color.parseColor("#D9AD65") : Color.TRANSPARENT,
                isInversion ? Color.TRANSPARENT : Color.parseColor("#D9AD65"),
                LinearGradient.TileMode.CLAMP);
        mLinePaint.setShader(linearGradient);


        canvas.drawRect(mLineRect, mLinePaint);
    }

    private void drawSquare(Canvas canvas) {
        canvas.drawRect(getFramingRect(), mSquarePaint);
    }

    private float dpToPx(float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,getResources().getDisplayMetrics());
    }
}
