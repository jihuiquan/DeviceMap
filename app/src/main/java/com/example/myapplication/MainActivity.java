package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.devicemap.MUIDeviceBean;
import com.example.myapplication.devicemap.MUIDeviceLiveCardview;
import com.example.myapplication.devicemap.MUIDeviceMapView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MUIDeviceMapView muiDeviceMapView = ((MUIDeviceMapView) findViewById(R.id.dmv));
        final LinearLayout showDialogLl = (LinearLayout) findViewById(R.id.show_dialog_ll);
        final MUIDeviceLiveCardview dialogCv = (MUIDeviceLiveCardview) findViewById(R.id.dialog_cv);
        TextView currentBt = (TextView) findViewById(R.id.current_bt);
        muiDeviceMapView.setSizeToSite(2000, 800);
        ArrayList<MUIDeviceBean> deviceBeans = new ArrayList<>();
        MUIDeviceBean deviceBean = new MUIDeviceBean("1", 50, 50, 50, 1, 0, new Random().nextInt(360) + "");
        MUIDeviceBean deviceBean2 = new MUIDeviceBean("2", 600, 300, 80, 0, 1, new Random().nextInt(360) + "");
        MUIDeviceBean deviceBean3 = new MUIDeviceBean("3", 600, 450, 50, 0, 1, new Random().nextInt(360) + "");
        final MUIDeviceBean deviceBean5 = new MUIDeviceBean("5", 400, 500, 20, 1, 1, new Random().nextInt(360) + "");
        deviceBeans.add(deviceBean);
        deviceBeans.add(deviceBean2);
        deviceBeans.add(deviceBean3);
        deviceBeans.add(deviceBean5);
        muiDeviceMapView.refreshMachine(deviceBeans);
        showDialogLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLl.setVisibility(View.GONE);
                dialogCv.setVisibility(View.VISIBLE);
                dialogCv.setAngleValue(3f);
            }
        });
        dialogCv.findViewById(R.id.dialog_exit_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogLl.setVisibility(View.VISIBLE);
                dialogCv.setVisibility(View.GONE);
            }
        });
        currentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCv.setAngleValue(-3f);
                muiDeviceMapView.reset(deviceBean5);
            }
        });
    }
}
