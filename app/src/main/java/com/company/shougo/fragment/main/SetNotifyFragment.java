package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.databinding.FragmentSetNotifyBinding;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.widget.MyFragment;

public class SetNotifyFragment extends MyFragment {

    private final static String TAG = "SetNotifyFragment";

    private FragmentSetNotifyBinding binding;

    private MainActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_set_notify
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

        binding.notify.setChecked(SaveManager.getNotify(activity));

        binding.notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SaveManager.saveNotify(activity, isChecked);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
        }
    }
}
