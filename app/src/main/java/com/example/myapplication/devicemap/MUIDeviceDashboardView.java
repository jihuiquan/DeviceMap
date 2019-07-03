package com.example.myapplication.devicemap;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.myapplication.R;


/**
 * Created by ${JHQ} on 2019/6/21 0021.
 */
public class MUIDeviceDashboardView extends View {


    /**
     * 控件宽高
     */
    private int mWidth;
    private int mHeight;
    /**
     * 圆心所在位置
     */
    private float mCx;
    private float mCy;
    /**
     * 大刻度值
     */
    private String[] mNum = {"8", "6", "4", "2", "1", "0", "1", "2", "4", "6", "8"};

    /**
     * 指针角度
     */
    private float mAngleValue = 3f;
    private float mLastAngle;
    private float mRadius;
    private final Paint mLinePaint;
    private final Paint mTextPaint;
    private float mAnimatedValue;
    private ValueAnimator mValueAnimator;

    public MUIDeviceDashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(getResources().getColor(R.color.mui_color_dash_text));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mCx = mWidth / 2f;
        mCy = mHeight - 5f;
        mRadius = (mCx > mCy ? mCy : mCx);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.mui_color_dash_bg));
        canvas.save();
        canvas.rotate(-90f, mCx, mCy);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(getResources().getColor(R.color.mui_color_dash_text));
        mLinePaint.setStrokeWidth(1f);
        mTextPaint.setTextSize(14f);
        for (int i = 0; i < 51; i++) {
            if (i != 0) {
                canvas.rotate(180f / 50f, mCx, mCy);
            }
            if (i % 5 == 0) {
                canvas.drawText(mNum[i / 5], mCx, mCy - mRadius + 25f, mTextPaint);
            } else {
                //刻度线
                canvas.drawLine(mCx, mCy - mRadius + 20f, mCx, mCy - mRadius + 25f, mLinePaint);
            }
        }
        canvas.restore();
        mTextPaint.setTextSize(24f);
        //角度值
        canvas.drawText(mAngleValue + "°", mCx, mCy, mTextPaint);

        //指针
        canvas.save();
        canvas.rotate(mAnimatedValue, mCx, mCy);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(getResources().getColor(R.color.mui_color_dash_text));
        mLinePaint.setStrokeWidth(1f);
        canvas.drawRoundRect(mCx - 1f, mCy - mRadius + 17f, mCx + 1f, mCy - 40f, 6f, 6f, mLinePaint);
        canvas.restore();
    }

    public void setAngleValue(float angleValue) {
        if (null != mValueAnimator && !mValueAnimator.isRunning()) {
            mValueAnimator.setFloatValues(mLastAngle, conversionAngle(angleValue));
            mValueAnimator.start();
            return;
        }
        mValueAnimator = ValueAnimator.ofFloat(mLastAngle, conversionAngle(angleValue));
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mLastAngle = mAnimatedValue;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(2000);
        mValueAnimator.start();
    }

    private float conversionAngle(float angleValue) {
        float temp_angle;
        if (Math.abs(angleValue) >= 0 && Math.abs(angleValue) < 2f) {
            temp_angle = (Math.abs(angleValue) / 2f) * ((90f / 5f) * 2f);
        } else {
            temp_angle = ((90f / 5f) * 2f) + ((Math.abs(angleValue) - 2f) / 6f) * ((90f / 5f) * 3f);
        }
        return angleValue > 0 ? temp_angle : -temp_angle;
    }

    public void cleanCache() {
        if (null != mValueAnimator) {
            mValueAnimator.cancel();
        }
        mValueAnimator = null;
    }
}
