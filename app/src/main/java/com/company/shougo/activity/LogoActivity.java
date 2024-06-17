package com.company.shougo.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.databinding.ActivityLogoBinding;
import com.company.shougo.fragment.logo.LogoFragment;
import com.company.shougo.listener.OnPermissionListener;
import com.company.shougo.mamager.PermissionManager;

public class LogoActivity extends BaseActivity {

    private ActivityLogoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().setStatusBarColor(Color.BLACK);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_logo);

        fragmentManager = getSupportFragmentManager();

        changePage(R.id.logoFrame, new LogoFragment(), false);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void login(){
        Intent goMain = new Intent();
        goMain.setClass(this, MainActivity.class);
        goMain.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        Intent getIntent = getIntent();
        if (getIntent!=null){
            Bundle bundle = getIntent.getExtras();
            if (bundle!=null){
                goMain.putExtras(bundle);
            }
        }

        startActivity(goMain);
        finish();
    }
}