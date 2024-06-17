package com.company.shougo.mamager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.company.shougo.Parameter;
import com.company.shougo.activity.BaseActivity;
import com.company.shougo.listener.OnPermissionListener;
import com.company.shougo.listener.OnPhotoListener;

import java.io.File;

public class CameraManager {

    public static Uri outputFileUri;
    public static String outputFilePath;

    private static OnPhotoListener onPhotoListener;

    public static boolean isCamera = false;

    public static void setOnPhotoListener(OnPhotoListener onPhotoListener){
        CameraManager.onPhotoListener = onPhotoListener;
    }

    public static void onCameraOk(Context context){
        if (onPhotoListener!=null && (outputFileUri!=null || outputFilePath!=null)){
            String path = outputFilePath;
            if (isCamera) {
                path = Calculation.getPathByUri(context, outputFileUri);
            }
            onPhotoListener.onGetPhoto(path);
        }
    }

    public static void checkCameraPer(BaseActivity activity){
        PermissionManager.getInstance().setOnPermissionListener(new OnPermissionListener() {
            @Override
            public void onPermissionRequest(boolean isSuccess, int requestCode) {
                if (requestCode == Parameter.RESULT_CAMERA_PER && isSuccess){
                    callCamera(activity);
                }
                PermissionManager.getInstance().setOnPermissionListener(null);
            }
        });
        PermissionManager.getInstance().checkPermission(
                activity
                ,new String[]{
                        Manifest.permission.CAMERA
                }
                , Parameter.RESULT_CAMERA_PER
        );
    }

    public static void callCamera(BaseActivity activity){
        isCamera = true;
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        outputFileUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);   //  設定輸出位置
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); //  照片質量
//        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //  照片自動旋轉
        activity.startActivityForResult(intent, Parameter.RESULT_CAMERA);
    }

    public static void checkAlbumPer(BaseActivity activity){
        PermissionManager.getInstance().setOnPermissionListener(new OnPermissionListener() {
            @Override
            public void onPermissionRequest(boolean isSuccess, int requestCode) {
                if (requestCode == Parameter.RESULT_ALBUM_PER && isSuccess){
                    callAlbum(activity);
                }
                PermissionManager.getInstance().setOnPermissionListener(null);
            }
        });

        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
        }
        PermissionManager.getInstance().checkPermission(
                activity
                , permissions
                , Parameter.RESULT_ALBUM_PER);

//        PermissionManager.getInstance().checkPermission(
//                activity
//                ,new String[]{
//                        Manifest.permission.READ_EXTERNAL_STORAGE
////                        ,Manifest.permission.WRITE_EXTERNAL_STORAGE
//                }
//                , Parameter.RESULT_ALBUM_PER
//        );
    }

    public static void talkToAlbum(BaseActivity activity, String filePath){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        activity.sendBroadcast(intent);
    }

    public static void callAlbum(BaseActivity activity){
        isCamera = false;
        outputFilePath = null;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, Parameter.RESULT_ALBUM);
    }

}
