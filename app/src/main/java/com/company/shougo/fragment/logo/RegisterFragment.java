package com.company.shougo.fragment.logo;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.data.TokenData;
import com.company.shougo.databinding.BottomPrivacyBinding;
import com.company.shougo.databinding.FragmentRegisterBinding;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.widget.MyFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterFragment extends MyFragment {

    private final static String TAG = "RegisterFragment";

    private FragmentRegisterBinding binding;

    private LogoActivity activity;

    private BottomSheetDialog dialog;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_register
                , group
                , false
        );
        return binding.getRoot();
    }

    @Override
    public void initView() {
        activity = (LogoActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(getResources().getColor(R.color.bg_gray));

        binding.register.setOnClickListener(this);
        binding.privacy.setOnClickListener(this);
        binding.service.setOnClickListener(this);
        binding.login.setOnClickListener(this);
    }

    private void showBottomSheet(String title, String info){

        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }

        dialog = new BottomSheetDialog(activity);

        BottomPrivacyBinding bottomBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , R.layout. bottom_privacy
                , null
                , false
        );

        bottomBinding.title.setText(title);
        bottomBinding.info.setText(info);

        bottomBinding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        dialog.getBehavior().setPeekHeight(height);

        dialog.setContentView(bottomBinding.getRoot());

        dialog.show();
    }

    private void register(){

        final String tag = "register ";

        boolean emailSuccess = binding.email.isSuccess();
        boolean nameSuccess = binding.name.isSuccess();
        boolean pwdSuccess = binding.pwd.isSuccess();

        if (
                !emailSuccess
                || !nameSuccess
                || !pwdSuccess
                || !binding.agree.isChecked()
        ){
            return;
        }

        Execute.register(
                binding.name.getText()
                , binding.email.getText()
                , binding.pwd.getText()
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        DialogManager.showConfirmDialog(
                                activity
                                , getResources().getString(R.string.fail)
                                , getResources().getString(R.string.net_not_good)
                                , null
                        );
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
                                        DialogManager.showConfirmDialog(
                                                activity
                                                , getResources().getString(R.string.fail)
                                                , getResources().getString(R.string.net_not_good)
                                                , null
                                        );

                                        return;
                                    }

                                    UserManager.getInstance().setTokenDataGo(activity, tokenData);

                                    DialogManager.showConfirmDialog(
                                            activity
                                            , getResources().getString(R.string.success)
                                            , getResources().getString(R.string.you_can_use)
                                            , new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    activity.login();
                                                }
                                            }
                                    );

                                }catch (Exception e){
                                    Log.e(TAG,tag + "Exception : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            activity
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.net_not_good)
                                            , null
                                    );
                                }
                            }
                        });

                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                register();
                break;
            case R.id.service:
                showBottomSheet(
                        getResources().getString(R.string.service)
                        , getResources().getString(R.string.service_info)
                );
                break;
            case R.id.privacy:
                showBottomSheet(
                        getResources().getString(R.string.privacy_title)
                        , getResources().getString(R.string.privacy_info)
                );
                break;
            case R.id.login:
                activity.changePage(R.id.logoFrame, new LoginFragment(), true);
                break;
        }
    }
}
