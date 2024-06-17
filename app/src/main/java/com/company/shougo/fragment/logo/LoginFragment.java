package com.company.shougo.fragment.logo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.TokenData;
import com.company.shougo.databinding.FragmentLoginBinding;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginFragment extends MyFragment {

    private final static String TAG = "LoginFragment";

    private FragmentLoginBinding binding;
    private LogoActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_login
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

        binding.forget.setOnClickListener(this);
        binding.register.setOnClickListener(this);
        binding.login.setOnClickListener(this);

        boolean isRem = SaveManager.getLoginRememberIs(activity);
        binding.remember.setChecked(isRem);
        if (isRem){
            binding.email.setText(SaveManager.getLoginRememberEmail(activity));
            binding.pwd.setText(SaveManager.getLoginRememberPwd(activity));
        }
    }

    private void login(){
        final String tag = "login ";

        boolean emailSuccess = binding.email.isSuccess();
        boolean pwdSuccess = binding.pwd.isSuccess();

        if (
                !emailSuccess
                || !pwdSuccess
        ){
            return;
        }

        SaveManager.saveLoginRemember(
                activity
                , binding.email.getText()
                , binding.pwd.getText()
                , binding.remember.isChecked()
        );

        Execute.login(
                binding.email.getText()
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
                                                , getResources().getString(R.string.acc_pwd_err)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.forget:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Parameter.FORGET_URL));
                activity.startActivity(intent);
                break;
            case R.id.register:
                activity.changePage(R.id.logoFrame, new RegisterFragment(), true);
                break;
            case R.id.login:
                login();
                break;
        }
    }
}
