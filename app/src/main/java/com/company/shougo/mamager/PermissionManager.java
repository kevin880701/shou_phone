package com.company.shougo.mamager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.company.shougo.R;
import com.company.shougo.listener.OnPermissionListener;

public class PermissionManager {

    private static PermissionManager permissionManager;

    private String[] per = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.CAMERA
            ,Manifest.permission.ACCESS_COARSE_LOCATION
            ,Manifest.permission.ACCESS_FINE_LOCATION
    };

    private OnPermissionListener onPermissionListener;

    public void setOnPermissionListener(OnPermissionListener onPermissionListener){
        this.onPermissionListener = onPermissionListener;
    }

    public static PermissionManager getInstance(){
        if (permissionManager==null){
            permissionManager = new PermissionManager();
        }

        return permissionManager;

    }

    public void setPermissionManager(String[] per){
        this.per = per;
    }

    /**
     * 權限要求
     * @param permission  需要的權限
     * @param requestCode 事件代碼
     */
    public void checkPermission(AppCompatActivity activity, String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, permission, requestCode);
    }

    /**
     * 權限回應處理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(
            final int requestCode
            , @NonNull String[] permissions
            , @NonNull int[] grantResults
            , final AppCompatActivity activity
    ){
        boolean hasAllGranted = true;
        for (int i = 0; i < grantResults.length; ++i) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                //請求失敗，並且勾選不再詢問時
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {
                    DialogManager.showBaseDialog(
                            activity,
                            activity.getString(R.string.permission_error)
                            , activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                }
                            }, activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissionsGo(false, requestCode);
                                }
                            });
                    //請求失敗，無勾選不再詢問時
                } else {
                    checkPermission(activity,permissions, requestCode);
                }
                break;
            }
        }
        //請求成功
        if (hasAllGranted) {
            requestPermissionsGo(true, requestCode);
        }
    }

    /**
     * 權限要求狀況
     * @param isSuccess  是否同意
     * @param requestCode  事件代碼
     */
    public void requestPermissionsGo(boolean isSuccess, int requestCode) {
        if (onPermissionListener!=null){
            onPermissionListener.onPermissionRequest(isSuccess,requestCode);
        }
    }
}
