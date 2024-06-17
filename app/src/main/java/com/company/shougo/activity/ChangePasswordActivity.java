package com.company.shougo.activity;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.data.ChangePasswordErrData;
import com.company.shougo.data.TokenData;
import com.company.shougo.databinding.ActivityChangePasswordBinding;
import com.company.shougo.databinding.ActivityQrcodeBinding;
import com.company.shougo.listener.OnPermissionListener;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.PermissionManager;
import com.company.shougo.mamager.UserManager;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangePasswordActivity extends BaseActivity {

    private final static String TAG = "ChangePasswordActivity";

    private ActivityChangePasswordBinding binding;
    private BeepManager beepManager;

    private String type;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().setStatusBarColor(Color.BLACK);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);

        binding.newPassword.binding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ss = s.toString();
                if(!Calculation.isPwdValid(ss)){
                    binding.newPassword.binding.error.setText(getResources().getString(R.string.pwd_err));
                    isSuccess = false;
                }else{
                    binding.newPassword.binding.error.setText("");
                    isSuccess = true;
                }
            }
        });


        binding.confirmNewPassword.binding.edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(binding.confirmNewPassword.binding.edit.getText().equals(binding.newPassword.binding.edit.getText())){
                    binding.confirmNewPassword.binding.error.setText("兩次密碼輸入不同");
                    isSuccess = false;
                }else{
                    binding.confirmNewPassword.binding.error.setText("");
                    isSuccess = true;
                }
            }
        });

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.changeFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tag = "changePWD ";
                if (isSuccess){

                    Execute.changePassword(
                            binding.oldPassword.binding.edit.getText().toString()
                            , binding.newPassword.binding.edit.getText().toString()
                            , binding.confirmNewPassword.binding.edit.getText().toString()
                            , new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e(TAG, tag + "onFailure : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            ChangePasswordActivity.this
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.net_not_good)
                                            , null
                                    );
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    final String data = response.body().string();
                                    Log.e(TAG, tag + "data : " + data);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try{
                                                if(data.equals("true")){
                                                    DialogManager.showConfirmDialog(
                                                            ChangePasswordActivity.this
                                                            , getResources().getString(R.string.success)
                                                            , getResources().getString(R.string.change_finish)
                                                            , new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    onBackPressed();
                                                                }
                                                            }
                                                    );
                                                }else{
                                                    ChangePasswordErrData changePasswordErrData = new Gson().fromJson(data, ChangePasswordErrData.class);
                                                    DialogManager.showConfirmDialog(
                                                            ChangePasswordActivity.this
                                                            , getResources().getString(R.string.fail)
                                                            , changePasswordErrData.getMsg()
                                                            , null
                                                    );
                                                }
                                            }catch (Exception e){
                                                Log.e(TAG,tag + "Exception : " + e.toString());
                                                DialogManager.showConfirmDialog(
                                                        ChangePasswordActivity.this
                                                        , getResources().getString(R.string.fail)
                                                        , getResources().getString(R.string.acc_pwd_err)
                                                        , null
                                                );
                                            }
                                        }
                                    });
                                }
                            }
                    );
                }
//                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG,"onBackPressed");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        beepManager = new BeepManager(this);

    }
}