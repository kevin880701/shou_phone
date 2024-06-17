package com.company.shougo.fragment.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.ChangePasswordActivity;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.activity.QrcodeActivity;
import com.company.shougo.databinding.FragmentMemberInfoBinding;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.listener.OnPhotoListener;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.CameraManager;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.widget.MyFragment;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberInfoFragment extends MyFragment {

    private final static String TAG = "MemberInfoFragment";

    private FragmentMemberInfoBinding binding;

    private MainActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_member_info
                , group
                , false
        );
        return binding.getRoot();
    }

    @Override
    public void initView() {
        activity = (MainActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(getResources().getColor(R.color.bg_gray));

        binding.back.setOnClickListener(this);
        binding.edit.setOnClickListener(this);
        binding.loginCar.setOnClickListener(this);
        binding.changeHeadImg.setOnClickListener(this);
        binding.changePassword.setOnClickListener(this);

        binding.email.setText(UserManager.getInstance().getUserData().getEmail());
        binding.name.setText(UserManager.getInstance().getUserData().getFirstname());

        Glide.with(activity)
                .load(UserManager.getInstance().getUserData().getImage())
                .apply(
                        RequestOptions.circleCropTransform()
                                .placeholder(R.drawable.icon_memberhoto)
                                .error(R.drawable.icon_memberhoto)
                )
                .into(binding.headImg);
    }

    private void editName(){
        binding.name.setEnabled(!binding.name.isEnabled());
        if (binding.name.isEnabled()){
            binding.name.requestFocus();
        }else{
            String name = binding.name.getText().toString();
            if (
                    name.length()<=0
            ){
                binding.name.setEnabled(!binding.name.isEnabled());

                Toast.makeText(activity, getResources().getString(R.string.name_err), Toast.LENGTH_SHORT).show();
            }else {
                updateName(name);
            }
        }
    }

    private void updateName(String name){
        final String tag = "updateEmail ";

        Execute.uploadInfo(
                name
                , UserManager.getInstance().getUserData().getEmail()
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);

                        String oldName = UserManager.getInstance().getUserData().getFirstname();

                        UserManager.getInstance().getUserData().setFirstname(name);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myTravelDB.updateEmail(oldName);
                            }
                        });

                    }
                }
        );
    }

    private void changeHeadImg(){
        CameraManager.setOnPhotoListener(new OnPhotoListener() {
            @Override
            public void onGetPhoto(String path) {
                Glide.with(activity)
                        .load(path)
                        .apply(
                                RequestOptions.circleCropTransform()
                                        .placeholder(R.drawable.icon_memberhoto)
                                        .error(R.drawable.icon_memberhoto)
                        )
                        .into(binding.headImg);

                uploadHeadImg(path);
            }
        });
        DialogManager.showPhotoDialog(
                activity
        );
    }

    private void uploadHeadImg(String path){
        final String tag = "uploadHeadImg ";

        Execute.uploadImage(
                path
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        if (data!=null && data.length()>0){
                            UserManager.getInstance().getUserData().setImage(data);
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
            case R.id.edit:
                editName();
                break;
            case R.id.changeHeadImg:
                changeHeadImg();
                break;
            case R.id.loginCar:
                Intent intent = new Intent();
                intent.setClass(activity, QrcodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Parameter.QRCODE_TYPE, Parameter.QRCODE_CAR);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                break;
            case R.id.changePassword:

                Intent intentChangePassword = new Intent(activity, ChangePasswordActivity.class);
                activity.startActivity(intentChangePassword);

                break;
        }
    }
}
