package com.example.myapplication.devicemap;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;


import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${JHQ} on 2019/6/12 0012.
 */
public class MUIDeviceMapView extends View {

    /**
     * 屏幕状态(0:未点击,1:单手,否则多手指)
     */
    private int mMode;
    /**
     * 控件实际值
     */
    private float mWidth;
    private float mHeight;

    /**
     * 工地的实际宽高
     */
    private int mSiteWidth;
    private int mSiteHeight;
    /**
     * 计算一米等于多少像素
     */
    private float mPxByMeter;

    /**
     * 遮罩框距离左右的实际距离
     */
    private float mRealMaskX = S_MARGIN_MASK_X;
    /**
     * 遮罩框距离上下的实际距离
     */
    private float mRealMaskY = S_MARGIN_MASK_Y;

    /**
     * 当前缩放范围
     */
    private float mZoom = 1f;

    /**
     * 记录开始双击时的缩放lv
     */
    private float mLastDoubleZoom;
    /**
     * 返回当前记录上一次缩放范围
     */
    private float mZoomLast;
    /**
     * 最小缩放(如:1-0.5,0.5-0.25...)
     */
    private static final float S_ZOOM_MINI = 1.00000001f;
    /**
     * 最大缩放(如:1-2,2-4...)
     */
    private static final float S_ZOOM_MAX = 199.9999999f;
    /**
     * 背景大格子距离
     */
    private float mMainLineDistance;
    /**
     * 背景小格子距离
     */
    private float mLineDistance;
    /**
     * 单指移动距离
     */
    private float mTranslateX;
    private float mTranslateY;

    /**
     * 记录左右两边的真是坐标点
     */
    private float mOriginX;
    private float mOriginY;
    /**
     * 返回当前记录上一次xy值
     */
    private float mTranslateLastX;
    private float mTranslateLastY;
    /**
     * 记录新单指事件位移
     */
    private float mTagTranslateX;
    private float mTagTranslateY;

    /**
     * 塔吊bitmap,蓝塔吊和灰塔吊
     */
    private Bitmap mDeviceBlueBitmap;
    private Bitmap mDeviceGrayBitmap;
    /**
     * 用来计算文字的高
     */
    private Rect mRect1;
    /**
     * 属性动画0-1f
     */
    private float mAnimatedValue;
    /**
     * 返回当前动画值0-1f
     */
    private float mBackAnimatorValue = 1f;

    /**
     * 横向大格子数量,决定格子的距离大小
     */
    private static final int S_HORIZONTALBOXNUM = 8;
    /**
     * 大格子里面多少小格子
     */
    private static final float S_HORIZONTALBOXMINI = 5f;
    /**
     * 边离三角形的margin
     */
    private static final float S_MARGIN_TRIANGLE = 20f;
    /**
     * 三角形的高
     */
    private static final float S_TRIANGLE_HIGH = 12f;
    /**
     * 三角形的中线长度
     */
    private static final float S_TRIANGLE_MID = 8f;
    /**
     * 三角形距离文字的距离
     */
    private static final float S_MARGIN_TRIANGLE_TO_TEXT = 5f;
    /**
     * 遮罩框距离左右的最佳距离
     */
    private static final float S_MARGIN_MASK_X = 80f;
    /**
     * 遮罩框距离上下的最佳距离
     */
    private static final float S_MARGIN_MASK_Y = 120f;
    /**
     * 遮罩距离线的距离
     */
    private static final float S_MARGIN_MASK_TO_LINE = 5f;
    /**
     * 小箭头高
     */
    private static final float S_ARROW_HIGH = 8f;
    /**
     * 小箭头中线
     */
    private static final float S_ARROW_MIND = 4f;
//    /**
//     * 塔吊的小圆
//     */
//    private static final float S_RADIUS_INNER = 10f;
//    /**
//     * 塔吊的中圆
//     */
//    private static final float S_RADIUS_MID = 50f;
//    /**
//     * 塔吊的大圆
//     */
//    private static final float S_RADIUS_ON = 95f;
    /**
     * 箭头指向的小短线中线长度
     */
    private static final float S_ARROW_SHORTLINE_MID = 8f;
    /**
     * 塔吊旋转幻影的弧度
     */
    private static final float S_SHADOW_RADIAN = 20f;

    /**
     * 每一级缩放登记对应的具体值
     */
    private float mLvScale = 2f / 19f;

    /**
     * 是否第一次加载,以所有塔吊自适应布局
     */
    private boolean mFirst = true;

    /**
     * 记录一整个事件流程,判断是否单击事件
     */
    private boolean mIsSingleClick;
    /**
     * 是否需要弹框(弹出经纬度)
     */
    private boolean mNeedDialog;

    /**
     * 上一次点击事件(判断是否双击事件)
     */
    private long mLastClickTime;

    /**
     * 上一次点击坐标x(判断是否双击事件)
     */
    private float mLastClickEventX;

    /**
     * 上一次点击坐标y(判断是否双击事件)
     */
    private float mLastClickEventY;

    /**
     * 弹框的塔吊
     */
    private float mDialogDeviceX;
    private float mDialogDeviceY;

    /**
     * 弹框塔吊的半径像素点
     */
    private float mDialogDeviceRadius;
    /**
     * 弹框内容
     */
    private String mDialogText;
    /**
     * 记录单击事件，判断是否单击事件作用
     */
    private float mSingleEventX;
    private float mSingleEventY;

    /**
     * 绘制弧形，和塔吊用
     */
    private RectF mRectF;
    /**
     * 塔吊list
     */
    List<MUIDeviceBean> mDeviceList = new ArrayList<>();
    /**
     * 上一次的塔吊list
     */
    List<MUIDeviceBean> mDeviceListLast = new ArrayList<>();
    /**
     * 如果是动画没走完，用户保存数据，计算cancel的角度值
     */
    List<MUIDeviceBean> mDeviceListCancelLast = new ArrayList<>();

    /**
     * 代表我的位置
     */
    private MUIDeviceBean mCurrentDeviceBean;

    /**
     * X,Y缩放等级对应值
     */
//    private String[] mLvY = {"20m", "50m", "100m", "200m", "500m", "1km", "2km", "5km", "10km", "20km", "25km", "50km", "100km", "200km", "500km", "1000km", "2000km", "5000km", "10000km"};
//    private String[] mLvX = {"10m", "30m", "80m", "150m", "400m", "800m", "1km", "2km", "5km", "10km", "20km", "25km", "50km", "100km", "200km", "500km", "1000km", "2000km", "5000km"};

    /**
     * 大格子path和小格子path
     */
    private Path mLineMainPath;
    private Path mLinePath1;
    private Path mLinePath2;
    private Path mLinePath3;
    private Path mLinePath4;
    /**
     * 画笔
     */
    private Paint mCirclePaint;
    private Paint mTextPaint;
    private Paint mBlueLinePaint;
    private Paint mTextBluePaint;
    private Paint mLinePaint;
    private Paint mDialogTextPaint;
    private float oldDist;
    private ValueAnimator mValueAnimator;
    private ValueAnimator mValueBackAnimator;
    private ValueAnimator mValueDoubleClickAnimator;
    private float mDoubleClickAnimatorValue;
    private boolean mHideSite;

    public MUIDeviceMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MUIDeviceMapView);
        if (null != ta) {
            mHideSite = ta.getBoolean(R.styleable.MUIDeviceMapView_devicemap_hide_site, false);
            ta.recycle();
        }
        init();
    }

    private void init() {
        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(1);
        mLinePaint.setColor(Color.BLACK);
        mBlueLinePaint = new Paint();
        mBlueLinePaint.setAntiAlias(true);
        mBlueLinePaint.setStyle(Paint.Style.STROKE);
        mBlueLinePaint.setStrokeWidth(3);
        mBlueLinePaint.setColor(Color.BLACK);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(14);
        mTextPaint.setColor(getResources().getColor(R.color.mui_gray_main_text));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextBluePaint = new Paint();
        mTextBluePaint.setAntiAlias(true);
        mTextBluePaint.setTextSize(14);
        mTextBluePaint.setColor(getResources().getColor(R.color.mui_color_blue));
        mTextBluePaint.setTextAlign(Paint.Align.CENTER);
        mDialogTextPaint = new Paint();
        mDialogTextPaint.setAntiAlias(true);
        mDialogTextPaint.setColor(Color.WHITE);
        mDialogTextPaint.setTextSize(14);
        mDialogTextPaint.setTextAlign(Paint.Align.CENTER);
        mLineMainPath = new Path();
        mLinePath1 = new Path();
        mLinePath2 = new Path();
        mLinePath3 = new Path();
        mLinePath4 = new Path();
        mRect1 = new Rect();
        mRectF = new RectF();
        mDeviceBlueBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.mui_device_device_blue);
        mDeviceGrayBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.mui_device_device_gray);
    }

    /**
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mZoom <= S_ZOOM_MINI) {
            mZoom = S_ZOOM_MINI;
        } else if (mZoom >= S_ZOOM_MAX) {
            mZoom = S_ZOOM_MAX;
        }

        //东南西北
        mLineMainPath.reset();
        mLineMainPath.moveTo(mWidth / 2f, S_MARGIN_TRIANGLE);
        mLineMainPath.lineTo(mWidth / 2f + S_TRIANGLE_MID, S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH);
        mLineMainPath.lineTo(mWidth / 2f - S_TRIANGLE_MID, S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH);
        mLineMainPath.close();
        mCirclePaint.setColor(getResources().getColor(R.color.mui_gray_main));
        canvas.drawPath(mLineMainPath, mCirclePaint);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        float v = (fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.bottom;
        canvas.drawText("北", mWidth / 2f, S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH + v * 2f + S_MARGIN_TRIANGLE_TO_TEXT, mTextPaint);

        mLineMainPath.reset();
        mLineMainPath.moveTo(mWidth / 2f, mHeight - S_MARGIN_TRIANGLE);
        mLineMainPath.lineTo(mWidth / 2f + S_TRIANGLE_MID, mHeight - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH));
        mLineMainPath.lineTo(mWidth / 2f - S_TRIANGLE_MID, mHeight - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH));
        mLineMainPath.close();
        mCirclePaint.setColor(getResources().getColor(R.color.mui_gray_main));
        canvas.drawPath(mLineMainPath, mCirclePaint);
        canvas.drawText("南", mWidth / 2f, mHeight - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH) - S_MARGIN_TRIANGLE_TO_TEXT, mTextPaint);

        mLineMainPath.reset();
        mLineMainPath.moveTo(S_MARGIN_TRIANGLE, mHeight / 2f);
        mLineMainPath.lineTo(S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH, mHeight / 2f - S_TRIANGLE_MID);
        mLineMainPath.lineTo(S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH, mHeight / 2f + S_TRIANGLE_MID);
        mLineMainPath.close();
        mCirclePaint.setColor(getResources().getColor(R.color.mui_gray_main));
        canvas.drawPath(mLineMainPath, mCirclePaint);
        mTextPaint.getTextBounds("西", 0, 1, mRect1);
        canvas.drawText("西", S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH + mRect1.width(), mHeight / 2f + v, mTextPaint);

        mLineMainPath.reset();
        mLineMainPath.moveTo(mWidth - S_MARGIN_TRIANGLE, mHeight / 2f);
        mLineMainPath.lineTo(mWidth - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH), mHeight / 2f - S_TRIANGLE_MID);
        mLineMainPath.lineTo(mWidth - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH), mHeight / 2f + S_TRIANGLE_MID);
        mLineMainPath.close();
        mCirclePaint.setColor(getResources().getColor(R.color.mui_gray_main));
        canvas.drawPath(mLineMainPath, mCirclePaint);
        mTextPaint.getTextBounds("东", 0, 1, mRect1);
        canvas.drawText("东", mWidth - (S_MARGIN_TRIANGLE + S_TRIANGLE_HIGH) - mRect1.width(), mHeight / 2f + v, mTextPaint);

        //有数据传入才调用
//        if (mFirst && mDeviceList.size() > 0) {
//            calculationZoomAndTranslate();
//            mFirst = false;
//        }

        //记录变化后的原点坐标
        float vx = mTranslateLastX + (mTranslateX - mTranslateLastX) * mBackAnimatorValue;
        float vy = mTranslateLastY + (mTranslateY - mTranslateLastY) * mBackAnimatorValue;
        mOriginX = ((mWidth / (mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue)) / 2f - (mWidth / 2f - vx));
        mOriginY = ((mHeight / (mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue)) / 2f - (mHeight / 2f - vy));

        //移动缩放
        canvas.translate(vx, vy);
        mZoom = (mDoubleClickAnimatorValue != 0f ? (mLastDoubleZoom + ((((mLastDoubleZoom + 2f) > S_ZOOM_MAX) ? S_ZOOM_MAX - mLastDoubleZoom : 2f) * mDoubleClickAnimatorValue)) : mZoom);
        canvas.scale(mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue, mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue, mWidth / 2f - vx, mHeight / 2f - vy);

        //竖线
        mLineMainPath.reset();
        mLinePath1.reset();
        mLinePath2.reset();
        mLinePath3.reset();
        mLinePath4.reset();
        for (int i = 0; i < S_HORIZONTALBOXNUM; i++) {
            //主线
            mLineMainPath.moveTo(i * mMainLineDistance, 0f);
            mLineMainPath.lineTo(i * mMainLineDistance, mHeight);
            //副线
            float line1 = i * mMainLineDistance + mLineDistance;
            float line2 = i * mMainLineDistance + mLineDistance * 2f;
            float line3 = i * mMainLineDistance + mLineDistance * 3f;
            float line4 = i * mMainLineDistance + mLineDistance * 4f;
            mLinePath1.moveTo(line1, 0f);
            mLinePath2.moveTo(line2, 0f);
            mLinePath3.moveTo(line3, 0f);
            mLinePath4.moveTo(line4, 0f);
            mLinePath1.lineTo(line1, mHeight);
            mLinePath2.lineTo(line2, mHeight);
            mLinePath3.lineTo(line3, mHeight);
            mLinePath4.lineTo(line4, mHeight);
        }
        mLinePaint.setColor(getResources().getColor(R.color.mui_gray_main_line));
        canvas.drawPath(mLineMainPath, mLinePaint);
        mLinePaint.setColor(getResources().getColor(R.color.mui_gray));
        canvas.drawPath(mLinePath1, mLinePaint);
        canvas.drawPath(mLinePath2, mLinePaint);
        canvas.drawPath(mLinePath3, mLinePaint);
        canvas.drawPath(mLinePath4, mLinePaint);

        //横线
        mLineMainPath.reset();
        mLinePath1.reset();
        mLinePath2.reset();
        mLinePath3.reset();
        mLinePath4.reset();
        int verticalMainDistance = (int) (mHeight / mMainLineDistance + 1);
        for (int i = 0; i < verticalMainDistance; i++) {
            //主线
            mLineMainPath.moveTo(0f, i * mMainLineDistance);
            mLineMainPath.lineTo(mWidth, i * mMainLineDistance);
            //副线
            float line1 = i * mMainLineDistance + mLineDistance;
            float line2 = i * mMainLineDistance + mLineDistance * 2f;
            float line3 = i * mMainLineDistance + mLineDistance * 3f;
            float line4 = i * mMainLineDistance + mLineDistance * 4f;
            mLinePath1.moveTo(0f, line1);
            mLinePath2.moveTo(0f, line2);
            mLinePath3.moveTo(0f, line3);
            mLinePath4.moveTo(0f, line4);
            mLinePath1.lineTo(mWidth, line1);
            mLinePath2.lineTo(mWidth, line2);
            mLinePath3.lineTo(mWidth, line3);
            mLinePath4.lineTo(mWidth, line4);
        }
        mLinePaint.setColor(getResources().getColor(R.color.mui_gray_main_line));
        canvas.drawPath(mLineMainPath, mLinePaint);
        mLinePaint.setColor(getResources().getColor(R.color.mui_gray));
        canvas.drawPath(mLinePath1, mLinePaint);
        canvas.drawPath(mLinePath2, mLinePaint);
        canvas.drawPath(mLinePath3, mLinePaint);
        canvas.drawPath(mLinePath4, mLinePaint);
        if (!mHideSite) {
            //方块遮罩和长度米数
            mLineMainPath.reset();
            mLineMainPath.moveTo(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mWidth - mRealMaskX, mRealMaskY + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mWidth - mRealMaskX, mHeight - mRealMaskY);
            mLineMainPath.lineTo(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mHeight - mRealMaskY);
            mLineMainPath.close();
            mBlueLinePaint.setColor(getResources().getColor(R.color.mui_color_blue));
            mBlueLinePaint.setStrokeWidth(1);
            canvas.drawPath(mLineMainPath, mBlueLinePaint);
            mCirclePaint.setColor(getResources().getColor(R.color.mui_color_blue_1a));
            canvas.drawPath(mLineMainPath, mCirclePaint);

            mLineMainPath.reset();
            mLineMainPath.moveTo(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - (S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID));
            mLineMainPath.moveTo(mWidth - mRealMaskX, mRealMaskY - (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mWidth - mRealMaskX, mRealMaskY - (S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID));
            mLineMainPath.moveTo(mRealMaskX - (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mRealMaskX - (S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID), mRealMaskY + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.moveTo(mRealMaskX - (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mHeight - mRealMaskY);
            mLineMainPath.lineTo(mRealMaskX - (S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID), mHeight - mRealMaskY);
            mBlueLinePaint.setColor(getResources().getColor(R.color.mui_color_blue));
            mBlueLinePaint.setStrokeWidth(1);
            canvas.drawPath(mLineMainPath, mBlueLinePaint);

            mLineMainPath.moveTo(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - S_MARGIN_MASK_TO_LINE);
            mLineMainPath.lineTo(mRealMaskX + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - S_MARGIN_MASK_TO_LINE - S_ARROW_MIND);
            mLineMainPath.lineTo(mRealMaskX + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - S_MARGIN_MASK_TO_LINE + S_ARROW_MIND);

            mLineMainPath.moveTo(mWidth - mRealMaskX, mRealMaskY - S_MARGIN_MASK_TO_LINE);
            mLineMainPath.lineTo(mWidth - (mRealMaskX + S_ARROW_HIGH), mRealMaskY - S_MARGIN_MASK_TO_LINE - S_ARROW_MIND);
            mLineMainPath.lineTo(mWidth - (mRealMaskX + S_ARROW_HIGH), mRealMaskY - S_MARGIN_MASK_TO_LINE + S_ARROW_MIND);

            mLineMainPath.moveTo(mRealMaskX - S_MARGIN_MASK_TO_LINE, mRealMaskY + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mRealMaskX - S_MARGIN_MASK_TO_LINE - S_ARROW_MIND, mRealMaskY + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mRealMaskX - S_MARGIN_MASK_TO_LINE + S_ARROW_MIND, mRealMaskY + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));

            mLineMainPath.moveTo(mRealMaskX - S_MARGIN_MASK_TO_LINE, mHeight - mRealMaskY);
            mLineMainPath.lineTo(mRealMaskX - S_MARGIN_MASK_TO_LINE - S_ARROW_MIND, mHeight - (mRealMaskY + S_ARROW_HIGH));
            mLineMainPath.lineTo(mRealMaskX - S_MARGIN_MASK_TO_LINE + S_ARROW_MIND, mHeight - (mRealMaskY + S_ARROW_HIGH));
            mLineMainPath.close();
            mCirclePaint.setColor(getResources().getColor(R.color.mui_color_blue));
            canvas.drawPath(mLineMainPath, mCirclePaint);

            mLineMainPath.reset();
            mLineMainPath.moveTo(mRealMaskX + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID), mRealMaskY - S_MARGIN_MASK_TO_LINE);
            mLineMainPath.lineTo(mWidth - (mRealMaskX + S_ARROW_HIGH), mRealMaskY - S_MARGIN_MASK_TO_LINE);
            mLineMainPath.moveTo(mRealMaskX - S_MARGIN_MASK_TO_LINE, mRealMaskY + S_ARROW_HIGH + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID));
            mLineMainPath.lineTo(mRealMaskX - S_MARGIN_MASK_TO_LINE, mHeight - (mRealMaskY + S_ARROW_HIGH));
            mBlueLinePaint.setColor(getResources().getColor(R.color.mui_color_blue));
            mBlueLinePaint.setStrokeWidth(1);
            canvas.drawPath(mLineMainPath, mBlueLinePaint);

//        String lvDistanceX = getLvDistance(false);
//        String lvDistanceY = getLvDistance(true);
            if (mSiteWidth != 0 && mSiteHeight != 0) {
                String temp_w = mSiteWidth + "m";
                String temp_h = mSiteHeight + "m";
                mTextBluePaint.setTextSize(14f);
                mTextBluePaint.getTextBounds(temp_w, 0, temp_w.length(), mRect1);
                canvas.drawText(temp_w, mWidth / 2f, mRealMaskY - S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID - 8f, mTextBluePaint);
                canvas.save();
                canvas.rotate(90f, mRealMaskX - S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID + 5f, mHeight / 2f);
                canvas.drawText(temp_h, mRealMaskX - S_MARGIN_MASK_TO_LINE - S_ARROW_SHORTLINE_MID + 5f, mHeight / 2f + mRect1.height(), mTextBluePaint);
                canvas.restore();
            }
        }
        //塔吊
        for (int i = 0; i < mDeviceList.size(); i++) {
            MUIDeviceBean deviceBean = mDeviceList.get(i);
            MUIDeviceBean lastMUIDeviceBean = getDeviceByDeviceId(deviceBean.getDeviceId(), mDeviceListLast);

            //mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID)  //场地原点的像素x
            //mHeight - mRealMaskY                                          //场地原点的像素y

            mCirclePaint.setColor(getResources().getColor(deviceBean.getDeviceWorkStatus() == MUIDeviceBean.DEVICEWORKSTATUS_CIRCLE_GREEN ? R.color.mui_color_green : R.color.mui_color_red));
            canvas.drawCircle(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter, deviceBean.getRadius() * mPxByMeter / 6f, mCirclePaint);
            mCirclePaint.setColor(getResources().getColor(deviceBean.getDeviceWorkStatus() == MUIDeviceBean.DEVICEWORKSTATUS_CIRCLE_GREEN ? R.color.mui_color_green_33 : R.color.mui_color_red_33));
            canvas.drawCircle(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter, deviceBean.getRadius() * mPxByMeter / 2f, mCirclePaint);
            mCirclePaint.setColor(getResources().getColor(deviceBean.getDeviceWorkStatus() == MUIDeviceBean.DEVICEWORKSTATUS_CIRCLE_GREEN ? R.color.mui_color_green_10 : R.color.mui_color_red_10));
            canvas.drawCircle(mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter, deviceBean.getRadius() * mPxByMeter, mCirclePaint);
            canvas.save();
            float angleDiff = getRotateAngle(Float.valueOf(lastMUIDeviceBean == null ? "0" : lastMUIDeviceBean.getDeviceAngle()), Float.valueOf(deviceBean.getDeviceAngle()));
//            canvas.rotate(Float.valueOf(lastMUIDeviceBean == null ? "0" : lastMUIDeviceBean.getDeviceAngle()) + mAnimatedValue * angleDiff, mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter);
            canvas.rotate(mAnimatedValue * 360f, mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter);
            mCirclePaint.setColor(getResources().getColor(deviceBean.getDeviceWorkStatus() == MUIDeviceBean.DEVICEWORKSTATUS_CIRCLE_GREEN ? R.color.mui_color_green_4d : R.color.mui_color_red_4d));
            mRectF.left = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter - deviceBean.getRadius() * mPxByMeter;
            mRectF.top = (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter - deviceBean.getRadius() * mPxByMeter;
            mRectF.right = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter + deviceBean.getRadius() * mPxByMeter;
            mRectF.bottom = (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter + deviceBean.getRadius() * mPxByMeter;
            if (mAnimatedValue != 1f) {
                float absAngleDiff = Math.abs(angleDiff) * mAnimatedValue;
                float rotationAngle;
                if (absAngleDiff < S_SHADOW_RADIAN) {
                    rotationAngle = absAngleDiff;
                } else {
                    rotationAngle = S_SHADOW_RADIAN;
                }
                if (angleDiff > 0) {
//                    canvas.drawArc(mRectF, -94f, -rotationAngle, true, mCirclePaint);
                } else {
//                    canvas.drawArc(mRectF, 266f, rotationAngle, true, mCirclePaint);
                }
                canvas.drawArc(mRectF, -94f, -20f, true, mCirclePaint);
            }
            mCirclePaint.setColor(Color.RED);
            //摇臂的绘制区域
            mRectF.left = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter - ((deviceBean.getRadius() * mPxByMeter * (1.5f / 6f)) * 2f) / 3f;
            mRectF.top = (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter - deviceBean.getRadius() * mPxByMeter;
            mRectF.right = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter + (deviceBean.getRadius() * mPxByMeter * (1.5f / 6f)) / 3f;
            mRectF.bottom = (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter + deviceBean.getRadius() * mPxByMeter / 2f;
            canvas.drawBitmap(deviceBean.getDeviceStatus() == MUIDeviceBean.DEVICESTATUS_MACHINE_BLUE ? mDeviceBlueBitmap : mDeviceGrayBitmap, null, mRectF, mCirclePaint);
            canvas.restore();
            reviseTextSize(((deviceBean.getRadius() * mPxByMeter) * 2f / 5f), deviceBean.getDeviceAngle());
            canvas.drawText(deviceBean.getDeviceAngle() + "°", mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter, (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter + deviceBean.getRadius() * mPxByMeter / 2f, mTextBluePaint);
        }

        //点击事件触发
        if (mNeedDialog) {
            //弹出对话框
            mCirclePaint.setColor(getResources().getColor(R.color.mui_color_dialog));
            mLineMainPath.reset();
            //弹框的实际宽，高
            float v1 = ((mDialogDeviceY - mDialogDeviceRadius / 4f) - (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f))) * 2f / 3f;
            reviseTextSizeByHeight(v1, mDialogText);
            mRectF.left = (mDialogDeviceX - mDialogTextPaint.measureText(mDialogText) / 2f) - ((mDialogDeviceY - mDialogDeviceRadius / 4f) - (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f))) * 1f / 5f;
            mRectF.top = (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f));
            mRectF.right = (mDialogDeviceX + mDialogTextPaint.measureText(mDialogText) / 2f) + ((mDialogDeviceY - mDialogDeviceRadius / 4f) - (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f))) * 1f / 5f;
            mRectF.bottom = (mDialogDeviceY - mDialogDeviceRadius / 4f);
            mLineMainPath.addRoundRect(mRectF, 6f, 6f, Path.Direction.CCW);
            float v3 = ((mDialogDeviceY - mDialogDeviceRadius / 4f) - (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f))) * 40f / 180f;
            mLineMainPath.moveTo(mDialogDeviceX - v3 / 2f, mDialogDeviceY - mDialogDeviceRadius / 4f);
            mLineMainPath.lineTo(mDialogDeviceX, mDialogDeviceY - (mDialogDeviceRadius / 4f - v3));
            mLineMainPath.lineTo(mDialogDeviceX + v3 / 2f, mDialogDeviceY - mDialogDeviceRadius / 4f);
            mLineMainPath.close();
            canvas.drawPath(mLineMainPath, mCirclePaint);

            //坐标
            float v2 = (mDialogDeviceY - mDialogDeviceRadius / 4f) - (mDialogDeviceY - mDialogDeviceRadius / 4f - (mDialogDeviceRadius * 2f / 5f));
            Paint.FontMetrics metrics = mDialogTextPaint.getFontMetrics();
            canvas.drawText(mDialogText, mDialogDeviceX, mDialogDeviceY - mDialogDeviceRadius / 4f - v2 / 2f + ((metrics.bottom - metrics.top) / 2f - metrics.bottom), mDialogTextPaint);
        }
    }

    private void reviseTextSize(float v, String angle) {
        String s = angle + "°";
        for (int i = 18; i > 0; i--) {
            mTextBluePaint.setTextSize(i);
            if (mTextBluePaint.measureText(s) <= v) {
                return;
            }
        }
        mTextBluePaint.setTextSize(1f);
    }

    private void reviseTextSizeByHeight(float v, String coordinate) {
        for (int i = 18; i > 0; i--) {
            mDialogTextPaint.setTextSize(i);
            mDialogTextPaint.getTextBounds(coordinate, 0, coordinate.length(), mRect1);
            if (mRect1.height() <= v) {
                return;
            }
        }
        mDialogTextPaint.setTextSize(1f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            //第一次按下
            case MotionEvent.ACTION_DOWN:
                //记录只为单手移动算移动距离
                mTagTranslateX = (event.getX() - mTranslateX * mZoom);
                mTagTranslateY = (event.getY() - mTranslateY * mZoom);
                mSingleEventX = event.getX();
                mSingleEventY = event.getY();
                mMode = 1;
                mIsSingleClick = true;
                break;
            //最后一个离开
            case MotionEvent.ACTION_UP:
                if (mIsSingleClick && (Math.abs(event.getX() - mSingleEventX) >= 15f || Math.abs(event.getY() - mSingleEventY) >= 15f)) {
                    mIsSingleClick = false;
                }
                mTagTranslateX = 0f;
                mTagTranslateY = 0f;
                mMode = 0;
                if (mIsSingleClick && System.currentTimeMillis() - mLastClickTime < 500 && Math.abs(event.getX() - mLastClickEventX) < 50f && Math.abs(event.getY() - mLastClickEventY) < 50f) {
                    //是双击事件
                    if (mZoom != S_ZOOM_MAX) {
                        startDoubleClickAnim();
                    }
                } else if (mIsSingleClick) {
                    //可消费单击事件,先转化实际坐标点
                    float singleActualX = mWidth / mZoom - mOriginX - (mWidth - event.getX()) / mZoom;
                    float singleActualY = mHeight / mZoom - mOriginY - (mHeight - event.getY()) / mZoom;
                    int sum = 0;
                    for (int i = 0; i < mDeviceList.size(); i++) {
                        MUIDeviceBean deviceBean = mDeviceList.get(i);
                        //点和圆心的距离
                        float sqrt = (float) Math.sqrt((mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter - singleActualX) * (mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter - singleActualX) + ((mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter - singleActualY) * ((mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter - singleActualY));
                        if (deviceBean.getRadius() * mPxByMeter > sqrt) {
                            sum++;
                            //在该圆内
                            mDialogDeviceX = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + deviceBean.getCx() * mPxByMeter;
                            mDialogDeviceY = (mHeight - mRealMaskY) - deviceBean.getCy() * mPxByMeter;
                            mDialogDeviceRadius = deviceBean.getRadius() * mPxByMeter;
                            mDialogText = "(" + deviceBean.getCx() + "," + deviceBean.getCy() + ")";
                        }
                    }
                    mNeedDialog = (sum == 1);
                    invalidate();
                    mLastClickEventX = event.getX();
                    mLastClickEventY = event.getY();
                    mLastClickTime = System.currentTimeMillis();
                }
                break;
            //非最后一个离开
            case MotionEvent.ACTION_POINTER_UP:
                mTagTranslateX = 0f;
                mTagTranslateY = 0f;
                mMode -= 1;
                mIsSingleClick = false;
                mLastClickTime = 0;
                break;
            //非第一次按下
            case MotionEvent.ACTION_POINTER_DOWN:
                mTagTranslateX = 0f;
                mTagTranslateY = 0f;
                oldDist = spacing(event);
                mMode += 1;
                mIsSingleClick = false;
                mLastClickTime = 0;
                break;
            //移动
            case MotionEvent.ACTION_MOVE:
                mIsSingleClick = false;
                if (mMode >= 2) {
                    float newDist = spacing(event);
                    if (newDist > oldDist + 1) {
                        zoom(newDist / oldDist);
                        oldDist = newDist;
                    }
                    if (newDist < oldDist - 1) {
                        zoom(newDist / oldDist);
                        oldDist = newDist;
                    }
                } else if (mMode == 1) {
                    mIsSingleClick = true;
                    if (mTagTranslateX != 0 && mTagTranslateY != 0) {
                        float vx = mTranslateLastX + (((event.getX() - mTagTranslateX) / mZoom) - mTranslateLastX) * mBackAnimatorValue;
                        float vy = mTranslateLastY + (((event.getY() - mTagTranslateY) / mZoom) - mTranslateLastY) * mBackAnimatorValue;
                        float tempOriginX = ((mWidth / (mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue)) / 2f - (mWidth / 2f - vx));
                        float tempOriginY = ((mHeight / (mZoomLast + (mZoom - mZoomLast) * mBackAnimatorValue)) / 2f - (mHeight / 2f - vy));

                        //左右超出范围
                        if (tempOriginX > 0f) {
                            mTranslateX = mWidth / 2f - (mWidth / 2f) / mZoom;
                            mOriginX = 0f;
                            mTagTranslateX = (event.getX() - mTranslateX * mZoom);

                        } else if (mWidth / mZoom - tempOriginX > mWidth) {
                            mTranslateX = (mWidth / 2f) / mZoom - mWidth / 2f;
                            mOriginX = mWidth / mZoom - mWidth;
                            mTagTranslateX = (event.getX() - mTranslateX * mZoom);
                        } else {
                            mTranslateX = (event.getX() - mTagTranslateX) / mZoom;
                        }
                        //上下超出范围
                        if (tempOriginY > 0f) {
                            mTranslateY = mHeight / 2f - (mHeight / 2f) / mZoom;
                            mOriginY = 0f;
                            mTagTranslateY = (event.getY() - mTranslateY * mZoom);
                        } else if (mHeight / mZoom - tempOriginY > mHeight) {
                            mTranslateY = (mHeight / 2f) / mZoom - mHeight / 2f;
                            mOriginY = mHeight / mZoom - mHeight;
                            mTagTranslateY = (event.getY() - mTranslateY * mZoom);
                        } else {
                            mTranslateY = (event.getY() - mTagTranslateY) / mZoom;
                        }
                        invalidate();
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * @param v 转化缩放等级
     */
    private void zoom(float v) {
        float historyZoom = mZoom;
        mZoom *= v;
        if (mZoom <= S_ZOOM_MINI) {
            mTranslateX = 0f;
            mTranslateY = 0f;
            mTagTranslateX = 0f;
            mTagTranslateY = 0f;
            mZoom = S_ZOOM_MINI;
            invalidate();
            return;
        } else if (mZoom >= S_ZOOM_MAX) {
            return;
        }
        if (historyZoom > mZoom) {
            //缩小  mZoom 4  ---0     mTx  500 ---0
            mTranslateX = mTranslateX - mTranslateX / (mZoom * mZoom * mZoom * mZoom);
            mTranslateY = mTranslateY - mTranslateY / (mZoom * mZoom * mZoom * mZoom);
        }
        invalidate();
    }

    /**
     * @param event 计算两点距离
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 转换Zoom和translate
     */
    private void calculationZoomAndTranslate() {
        if (null != mCurrentDeviceBean) {
            float miniX = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + mCurrentDeviceBean.getCx() * mPxByMeter - 3f * mCurrentDeviceBean.getRadius() * mPxByMeter;
            float maxX = mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID) + mCurrentDeviceBean.getCx() * mPxByMeter + 3f * mCurrentDeviceBean.getRadius() * mPxByMeter;
            float miniY = (mHeight - mRealMaskY) - mCurrentDeviceBean.getCy() * mPxByMeter - 3f * mCurrentDeviceBean.getRadius() * mPxByMeter;
            float maxY = (mHeight - mRealMaskY) - mCurrentDeviceBean.getCy() * mPxByMeter + 3f * mCurrentDeviceBean.getRadius() * mPxByMeter;
            mTranslateX = mWidth / 2f - (miniX + maxX) / 2f;
            mTranslateY = mHeight / 2f - (miniY + maxY) / 2f;
            float zX = mWidth / (maxX - miniX);
            float zY = mHeight / (maxY - miniY);
            mZoom = zX < zY ? zX : zY;
            return;
        }
        //返回原始状态
        mTranslateX = 0f;
        mTranslateY = 0f;
        mZoom = 1f;
    }

//    /**
//     * @param isX 根据mZoom获取对应缩放距离
//     * @return
//     */
//    private String getLvDistance(boolean isX) {
//        for (int i = 1; i < mLvX.length + 1; i++) {
//            float v = i * mLvScale;
//            if (v >= mZoom) {
//                if (isX) {
//                    return mLvX[mLvX.length - (i - 1) - 1];
//                }
//                return mLvY[mLvX.length - (i - 1) - 1];
//            }
//        }
//        return "";
//    }

    private MUIDeviceBean getDeviceByDeviceId(String deviceId, List<MUIDeviceBean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (deviceId.equals(list.get(i).getDeviceId())) {
                return list.get(i);
            }
        }
        return null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        mMainLineDistance = mWidth / (float) S_HORIZONTALBOXNUM;
        mLineDistance = mMainLineDistance / S_HORIZONTALBOXMINI;
    }

    /**
     * 计算旋转角度
     *
     * @param last    上一次的角度
     * @param current 当前角度
     */
    private float getRotateAngle(float last, float current) {
        float rotateAngle = 0f;
        if (last < current) {
            //终点大于起始
            if (current - last > 180f) {
                //角度大于180需要逆向
                rotateAngle = -(last + (360f - current));
            } else {
                rotateAngle = (current - last);
            }
        } else {
            //起点大于终点
            if (last - current > 180f) {
                rotateAngle = ((360f - last) + current);
            } else {
                rotateAngle = -(last - current);
            }
        }
        return rotateAngle;
    }

    private void startAnim() {
        if (mDeviceList.size() > 0) {
            if (null != mValueAnimator) {
                mValueAnimator.cancel();
                mValueAnimator.start();
                return;
            }
            mValueAnimator = ValueAnimator.ofFloat(0, 1f);
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
                    if (animation instanceof ValueAnimator) {
                        ValueAnimator valueAnimator = (ValueAnimator) animation;
                        float animatedValue = (float) valueAnimator.getAnimatedValue();
                        if (animatedValue == 1f) {
                            //动画不是cancel结束
                            mDeviceListLast.clear();
                            mDeviceListLast.addAll(mDeviceList);
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (animation instanceof ValueAnimator) {
                        if (mDeviceListLast.size() == 0) {
                            for (int i = 0; i < mDeviceList.size(); i++) {
                                MUIDeviceBean deviceBean = mDeviceList.get(i);
                                deviceBean.setDeviceAngle("0");
                                mDeviceListLast.add(deviceBean);
                            }
                        }
                        ValueAnimator valueAnimator = (ValueAnimator) animation;
                        float animatedValue = (float) valueAnimator.getAnimatedValue();
                        for (int i = 0; i < mDeviceListLast.size(); i++) {
                            MUIDeviceBean deviceBeanLast = mDeviceListLast.get(i);
                            MUIDeviceBean deviceBeanCurrent = getDeviceByDeviceId(deviceBeanLast.getDeviceId(), mDeviceListCancelLast);
                            if (null != deviceBeanLast) {
                                float v = Float.valueOf(deviceBeanLast.getDeviceAngle()) + getRotateAngle(Float.valueOf(deviceBeanLast.getDeviceAngle()), Float.valueOf(null == deviceBeanCurrent ? "0" : deviceBeanCurrent.getDeviceAngle())) * animatedValue;
                                deviceBeanLast.setDeviceAngle(String.valueOf(v));
                            }
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
//            mValueAnimator.setDuration(2000);
            mValueAnimator.setDuration(8000);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mValueAnimator.setRepeatMode(ValueAnimator.RESTART);
            mValueAnimator.start();
        }
    }

    private void startBackToCurrentAnim() {
        if (null != mValueBackAnimator) {
            mValueBackAnimator.start();
            return;
        }
        mValueBackAnimator = ValueAnimator.ofFloat(0, 1f);
        mValueBackAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueBackAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mZoomLast = 0f;
                mTranslateLastX = 0f;
                mTranslateLastY = 0f;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        mValueBackAnimator.setDuration(300);
        mValueBackAnimator.setInterpolator(new LinearInterpolator());
        mValueBackAnimator.start();
    }

    private void startDoubleClickAnim() {
        mLastDoubleZoom = mZoom;
        if (null != mValueDoubleClickAnimator && !mValueDoubleClickAnimator.isRunning()) {
            mValueDoubleClickAnimator.start();
            return;
        }
        mValueDoubleClickAnimator = ValueAnimator.ofFloat(0, 1f);
        mValueDoubleClickAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mDoubleClickAnimatorValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mValueDoubleClickAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mDoubleClickAnimatorValue = 0f;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mValueDoubleClickAnimator.setDuration(200);
        mValueDoubleClickAnimator.setInterpolator(new LinearInterpolator());
        mValueDoubleClickAnimator.start();
    }

    /**
     * @param width  设置场地大小
     * @param height
     */
    public void setSizeToSite(final int width, final int height) {
        post(new Runnable() {
            @Override
            public void run() {
                mHideSite = false;
                mSiteWidth = width;
                mSiteHeight = height;
                //最佳的工地尺寸
                float temp_x = mWidth - (S_MARGIN_MASK_X + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID)) - S_MARGIN_MASK_X;
                float temp_y = mHeight - (S_MARGIN_MASK_Y + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID)) - S_MARGIN_MASK_Y;
                float v2 = ((float) width) / ((float) height);
                float v = temp_x / temp_y;
                if (v2 > v) {
                    //说明实际的宽超长,此时宽应该与最佳的长度等同
                    mRealMaskX = S_MARGIN_MASK_X;
                    mRealMaskY = (temp_x / v2 + S_ARROW_SHORTLINE_MID + S_MARGIN_MASK_TO_LINE - mHeight) / (-2f);
                } else {
                    //实际的高超长或者一样,此时高应该与最佳的长度等同
                    mRealMaskX = (v2 * temp_y + S_ARROW_SHORTLINE_MID + S_MARGIN_MASK_TO_LINE - mWidth) / (-2f);
                    mRealMaskY = S_MARGIN_MASK_Y;
                }
                //算一米等于屏幕的几像素
                mPxByMeter = (mWidth - (mRealMaskX + (S_MARGIN_MASK_TO_LINE + S_ARROW_SHORTLINE_MID)) - mRealMaskX) / ((float) mSiteWidth);
                invalidate();
            }
        });
    }

    /**
     * @param cMap 添加,往原有的添加,会重新自适应布局(所有塔吊自适应在阴影布局中)
     */
    public void addMachine(List<MUIDeviceBean> cMap) {
        if (null == cMap) {
            return;
        }
        mFirst = true;
        mDeviceList.addAll(cMap);
        startAnim();
    }

    /**
     * @param cMap 刷新,清理原有的,布局只在未自适应的时候，执行一次自适应布局
     */
    public void refreshMachine(List<MUIDeviceBean> cMap) {
        if (null == cMap) {
            return;
        }
        mDeviceListCancelLast.clear();
        mDeviceListCancelLast.addAll(mDeviceList);
        mDeviceList.clear();
        mDeviceList.addAll(cMap);
        startAnim();
    }

    /**
     * 重置原来的状态
     */
    public void reset() {
        mCurrentDeviceBean = null;
        if (null != mValueBackAnimator) {
            if (mValueBackAnimator.isRunning()) {
                return;
            }
        }
        mTranslateLastX = mTranslateX;
        mTranslateLastY = mTranslateY;
        mZoomLast = mZoom;
        calculationZoomAndTranslate();
        startBackToCurrentAnim();
    }

    /**
     * 返回到当前我的位置
     */
    public void reset(MUIDeviceBean deviceBean) {
        mCurrentDeviceBean = deviceBean;
        if (null != mValueBackAnimator) {
            if (mValueBackAnimator.isRunning()) {
                return;
            }
        }
        mTranslateLastX = mTranslateX;
        mTranslateLastY = mTranslateY;
        mZoomLast = mZoom;
        calculationZoomAndTranslate();
        startBackToCurrentAnim();
    }

    /**
     * @param index 根据index删除,(不对布局自适应操作)
     */
    public void deleteMachine(int index) {
        mDeviceList.remove(index);
        invalidate();
    }

    /**
     * @param deviceId 根据deviceId删除,(不对布局自适应操作)
     */
    public void deleteMachine(String deviceId) {
        for (int i = 0; i < mDeviceList.size(); i++) {
            if (deviceId.equals(mDeviceList.get(i).getDeviceId())) {
                mDeviceList.remove(i);
                startAnim();
                return;
            }
        }
    }

    /**
     * 清理内存
     */
    public void cleanCache() {
        if (null != mValueAnimator) {
            mValueAnimator.cancel();
        }
        if (null != mValueBackAnimator) {
            mValueBackAnimator.cancel();
        }
        if (null != mValueDoubleClickAnimator) {
            mValueDoubleClickAnimator.cancel();
        }
        if (null != mDeviceBlueBitmap) {
            mDeviceBlueBitmap.recycle();
        }
        if (null != mDeviceGrayBitmap) {
            mDeviceGrayBitmap.recycle();
        }
        mValueAnimator = null;
        mValueBackAnimator = null;
        mValueDoubleClickAnimator = null;
        mDeviceBlueBitmap = null;
        mDeviceGrayBitmap = null;
    }
}