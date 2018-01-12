package com.datatrees.rawdatacentral.service.dubbo.economic.taobao.util;

/**
 * Created by guimeichao on 18/1/11.
 */
public enum QRCodeStatus {
    QR_CODE_STATUS_FAIL(-100,"二维码状态获取失败"),
    QR_CODE_STATUS_WATING(100,"等待用户扫描二维码"),
    QR_CODE_STATUS_READY(101,"用户已扫描二维码，等待确认"),
    QR_CODE_STATUS_EXPIRE(102,"当前二维码已过期，请刷新"),
    QR_CODE_STATUS_SUCCESS(200,"用户已确认登录");
    private int    statusCode;
    private String statusMessage;

    private QRCodeStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public String toString() {
        return String.valueOf(this.statusCode);
    }

    public static void main(String[] args) {

    }
}
