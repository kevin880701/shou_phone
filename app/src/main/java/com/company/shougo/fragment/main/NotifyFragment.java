package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.NotifyAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.NotifyData;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.FragmentNotifyBinding;
import com.company.shougo.db.NotifyDB;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.widget.MyFragment;

import java.util.ArrayList;
import java.util.List;

public class NotifyFragment extends MyFragment {

    private final static String TAG = "NotifyFragment";

    private FragmentNotifyBinding binding;
    private MainActivity activity;

    private NotifyDB notifyDB;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_notify
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

        backView = binding.bottomDialog;
        notifyDB = new NotifyDB(activity);

        binding.back.setOnClickListener(this);

        addNotifyList();
    }

    private void addNotifyList(){

        List<NotifyData> list = notifyDB.getAllByEmail();

        NotifyAdapter adapter = new NotifyAdapter(list);
        binding.notifyList.setLayoutManager(new LinearLayoutManager(activity));
        binding.notifyList.setAdapter(adapter);

        adapter.setOnAdapterItemListener(new OnAdapterItemListener() {
            @Override
            public void onItemClick(int pos) {
                adapter.getList().get(pos).setRead(1);
                adapter.notifyItemChanged(pos);

                NotifyData notifyData = adapter.getList().get(pos);

                notifyDB.update(notifyData);

                if (notifyData.getCoupon_id()>=0){
                    CouponData couponData = new CouponData();
                    couponData.setCoupon_id(notifyData.getCoupon_id());
                    showCouponInfo(activity, couponData);
                }else if (notifyData.getVendor_id()>=0){
                    StoreData storeData = new StoreData();
                    storeData.setVendor_id(notifyData.getVendor_id());
                    showStoreInfo(activity, storeData);
                }
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
