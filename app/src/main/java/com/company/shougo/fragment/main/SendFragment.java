package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.databinding.FragmentHomeBinding;
import com.company.shougo.databinding.FragmentSendBinding;
import com.company.shougo.widget.MyFragment;

public class SendFragment extends MyFragment {

    private final static String TAG = "SendFragment";

    private FragmentSendBinding binding;

    private MainActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_send
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
    }

    @Override
    public void onClick(View v) {

    }
}
