package com.company.shougo.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.activity.QrcodeActivity;
import com.company.shougo.data.GPSData;
import com.company.shougo.data.NotifyData;
import com.company.shougo.databinding.WidgetTopBarBinding;
import com.company.shougo.db.NotifyDB;
import com.company.shougo.fragment.main.NotifyFragment;
import com.company.shougo.mamager.UserManager;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class TopBarWidget extends ConstraintLayout implements View.OnClickListener {

    private final static String TAG = "TopBarWidget";

    private WidgetTopBarBinding binding;

    private MainActivity activity;

    private GPSData gpsData;

    public TopBarWidget(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_top_bar
                , this
                , true
        );

        activity = (MainActivity) context;

        Glide.with(context)
                .load(R.drawable.default_logo)
                .apply(
                        RequestOptions.circleCropTransform()
                        .placeholder(R.drawable.icon_memberhoto)
                        .error(R.drawable.icon_memberhoto)
                )
                .into(binding.headImg);

        binding.scan.setOnClickListener(this);
        binding.notify.setOnClickListener(this);

        binding.headImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Execute.testNotify(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailire : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, "data : " + data);
                    }
                });
            }
        });
    }

    public void setUser(){
        if (UserManager.getInstance().getUserData()==null){
            return;
        }

        Glide.with(activity)
                .load(UserManager.getInstance().getUserData().getImage())
                .apply(
                        RequestOptions.circleCropTransform()
                                .placeholder(R.drawable.icon_memberhoto)
                                .error(R.drawable.icon_memberhoto)
                )
                .into(binding.headImg);

        binding.account.setText(UserManager.getInstance().getUserData().getFirstname());

        NotifyDB notifyDB = new NotifyDB(activity);

        List<NotifyData> list = notifyDB.getNoReadByEmail();

        if (list.size()<=0){
            binding.notify.setImageResource(R.drawable.home_ic_notification_default);
        }else {
            binding.notify.setImageResource(R.drawable.home_btn_notification_badge_default);
        }
    }

    public GPSData getGpsData(){
        return gpsData;
    }

    public void setLocation(GPSData gpsData){
        this.gpsData = gpsData;
        if(gpsData.getAddress() == null){
            binding.location.setText("GPS讀取中....");
        }else{
            binding.location.setText(gpsData.getAddress());
        }

    }

    public void setOnLocationClick(OnClickListener onLocationClick){
        binding.locationView.setOnClickListener(onLocationClick);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan:
                Intent goScan = new Intent();
                goScan.setClass(activity, QrcodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Parameter.QRCODE_TYPE, Parameter.QRCODE_FAVORITE);
                goScan.putExtras(bundle);
                activity.startActivity(goScan);
                break;
            case R.id.notify:
                activity.changePage(R.id.mainFrame, new NotifyFragment(), true);
                break;
        }
    }
}
