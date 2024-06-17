package com.company.shougo;

import android.os.Environment;

public class Parameter {

    //eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJsaXUiLCJpZCI6MjQsImV4cCI6MTYyNzYxMzA5NSwicm9sZXMiOiIifQ.w_lx2iPDC1sRXhni_txtLmtcI5vcnWppclcuSciDD59bARrBXcssOVJyDhiSyeGH_PlIT8YgnSHv3oKbLdjI-Q

    public final static int PERMISSION_REQUEST_START = 10001;
    public final static int RESULT_CAMERA_PER = 10002;
    public final static int RESULT_CAMERA = 10003;
    public final static int RESULT_ALBUM_PER = 10004;
    public final static int RESULT_ALBUM = 10005;

    public final static String FORGET_URL = "https://shougo.cwoiot.com/opencart/index.php?route=account/forgotten";

    public final static int SEARCH_SIZE = 10;

    public final static String API = "https://shougo.cwoiot.com/shougo";

    public final static String NOTIFY_TO_VENDOR = "NOTIFY_TO_VENDOR";
    public final static String NOTIFY_TO_COUPON = "NOTIFY_TO_COUPON";

    public final static String QRCODE_TYPE = "qrcode_type";
    public final static String QRCODE_CAR = "qrcode_car";
    public final static String QRCODE_FAVORITE = "qrcode_FAVORITE";
    public final static String QRCODE_WORK = "qrcode_WORK";

    public final static String GET_NOTIFY = "get_notify";

    public final static String QRCODE_SPLITE = "shougo_qrcode_splite";
}
