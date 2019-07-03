package com.example.myapplication.devicemap;

/**
 * Created by ${JHQ} on 2019/6/13 0013.
 */
public class MUIDeviceBean {


    /**
     * 设备唯一id,用于删除，记录上次旋转角度
     */
    private String deviceId;
    /**
     * 塔吊坐标点
     */
    private float cx;
    private float cy;
    /**
     * 塔吊臂长半径
     */
    private float mRadius;
    /**
     * 设备状态(0表示蓝色塔吊，1表示灰色塔吊)
     */
    private int deviceStatus;

    /**
     * 0表示正常绿圆,1表示异常红圆
     */
    private int deviceWorkStatus;
    /**
     * 塔吊角度值
     */
    private String deviceAngle;

    /**
     * 设备本身的颜色
     */
    public static final int DEVICESTATUS_MACHINE_BLUE = 0;
    public static final int DEVICESTATUS_MACHINE_GRAY = 1;
    /**
     * 设备圆圈的颜色
     */
    public static final int DEVICEWORKSTATUS_CIRCLE_GREEN = 0;
    public static final int DEVICEWORKSTATUS_CIRCLE_RED = 1;

    /**
     * @param deviceId         设备Id
     * @param cx               塔吊X坐标点
     * @param cy               塔吊Y坐标点
     * @param radius           塔吊臂长半径
     * @param deviceStatus     设备状态(0表示蓝色塔吊，1表示灰色塔吊)
     * @param deviceWorkStatus 0表示正常绿圆,  1表示异常红圆
     * @param deviceAngle      塔吊角度值
     */
    public MUIDeviceBean(String deviceId, float cx, float cy, float radius, int deviceStatus, int deviceWorkStatus, String deviceAngle) {
        this.deviceId = deviceId;
        this.cx = cx;
        this.cy = cy;
        this.mRadius = radius;
        this.deviceStatus = deviceStatus;
        this.deviceWorkStatus = deviceWorkStatus;
        this.deviceAngle = deviceAngle;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public float getCx() {
        return cx;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public float getCy() {
        return cy;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public int getDeviceWorkStatus() {
        return deviceWorkStatus;
    }

    public void setDeviceWorkStatus(int deviceWorkStatus) {
        this.deviceWorkStatus = deviceWorkStatus;
    }

    public String getDeviceAngle() {
        return deviceAngle;
    }

    public void setDeviceAngle(String deviceAngle) {
        this.deviceAngle = deviceAngle;
    }
}
