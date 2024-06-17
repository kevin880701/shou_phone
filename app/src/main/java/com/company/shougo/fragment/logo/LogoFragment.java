package com.company.shougo.fragment.logo;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.BuildConfig;
import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.data.TokenData;
import com.company.shougo.databinding.FragmentLogoBinding;
import com.company.shougo.listener.OnPermissionListener;
import com.company.shougo.listener.OnRefreshListener;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.PermissionManager;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LogoFragment extends MyFragment implements OnPermissionListener {

    private final static String TAG = "LogoFragment";

    private FragmentLogoBinding binding;

    private LogoActivity activity;

    private int changeTime = 1500;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {

        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_logo
                , group
                , false
        );

        return binding.getRoot();
    }

    @Override
    public void initView() {

        activity = (LogoActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        activity.getWindow().setStatusBarColor(Color.BLACK);

        PermissionManager.getInstance().setOnPermissionListener(this);

        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        }
        PermissionManager.getInstance().checkPermission(
                activity
                , permissions
                , Parameter.PERMISSION_REQUEST_START);


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            PermissionManager.getInstance().checkPermission(
//                    activity
//                    , new String[]{
//                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.READ_MEDIA_IMAGES
//                    }
//                    , Parameter.PERMISSION_REQUEST_START);
//        } else {
//            PermissionManager.getInstance().checkPermission(
//                    activity
//                    , new String[]{
//                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                            Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_FINE_LOCATION,
////                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    }
//                    , Parameter.PERMISSION_REQUEST_START);
//        }



    }

    private void startChange(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TokenData tokenData = SaveManager.getLogin(activity);
                if (
                        tokenData==null
                        || tokenData.getToken()==null
                        || tokenData.getToken().length()<=0
                ) {
                    goStart();
                }else {
//                    UserManager.getInstance().setTokenData(tokenData);
//                    UserManager.getInstance().setOnRefreshListener(new OnRefreshListener() {
//                        @Override
//                        public void onRefresh() {
//                            activity.login();
//                            UserManager.getInstance().setOnRefreshListener(null);
//                        }
//
//                        @Override
//                        public void onFail() {
//                            goStart();
//                            UserManager.getInstance().setOnRefreshListener(null);
//                        }
//                    });
//                    UserManager.getInstance().reFreshToken(getContext());
                    login();
                }
            }
        }, changeTime);
    }

    private void login(){
        final String tag = "login ";

        String email = SaveManager.getLoginRememberEmail(activity);
        String pwd = SaveManager.getLoginRememberPwd(activity);

        Execute.login(
                email
                , pwd
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        goStart();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    TokenData tokenData = new Gson().fromJson(data, TokenData.class);

                                    if (
                                            tokenData==null
                                            || tokenData.getToken()==null
                                            || tokenData.getToken().length()<=0
                                            || tokenData.getRefresh_token()==null
                                            || tokenData.getRefresh_token().length()<=0
                                    ){
                                        goStart();

                                        return;
                                    }

                                    UserManager.getInstance().setTokenDataGo(activity, tokenData);

                                    activity.login();;

                                }catch (Exception e){
                                    Log.e(TAG,tag + "Exception : " + e.toString());
                                    goStart();
                                }
                            }
                        });
                    }
                }
        );
    }

    private void goStart(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (SaveManager.getFirst(getContext())) {
                    activity.changePage(R.id.logoFrame, new TeachFragment(), false);
                } else {
                    activity.changePage(R.id.logoFrame, new EnterFragment(), false);
                }
            }
        });
    }

    @Override
    public void onPermissionRequest(boolean isSuccess, int requestCode) {

        if (requestCode==Parameter.PERMISSION_REQUEST_START) {
            startChange();
        }

    }

    @Override
    public void onClick(View v) {

    }
}
