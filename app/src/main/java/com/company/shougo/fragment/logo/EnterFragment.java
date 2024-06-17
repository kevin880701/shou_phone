package com.company.shougo.fragment.logo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.databinding.FragmentEnterBinding;
import com.company.shougo.widget.MyFragment;

public class EnterFragment extends MyFragment {

    private static final String TAG = "EnterFragment";

    private FragmentEnterBinding binding;

    private LogoActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_enter
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

        binding.register.setOnClickListener(this);
        binding.login.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                activity.changePage(R.id.logoFrame, new RegisterFragment(), true);
                break;
            case R.id.login:
                activity.changePage(R.id.logoFrame, new LoginFragment(), true);
                break;
        }
    }
}
