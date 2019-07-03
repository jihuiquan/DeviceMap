package com.example.myapplication.devicemap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.example.myapplication.R;


/**
 * Created by ${JHQ} on 2019/6/24 0024.
 */
public class MUIDeviceLiveCardview extends CardView {

    private MUIDeviceDashboardView mMUIDeviceDashboardView;

    public MUIDeviceLiveCardview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.mui_device_live_cardview, this);
        mMUIDeviceDashboardView = (MUIDeviceDashboardView) findViewById(R.id.device_dashview);
    }

    public void setAngleValue(float angleValue) {
        if (null != mMUIDeviceDashboardView) {
            mMUIDeviceDashboardView.setAngleValue(angleValue);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
