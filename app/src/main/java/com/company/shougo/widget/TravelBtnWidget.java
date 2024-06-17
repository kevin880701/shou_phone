package com.company.shougo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.WidgetTravelBtnBinding;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.listener.OnTravelChangeListener;

import java.util.List;

public class TravelBtnWidget extends LinearLayout implements OnTravelChangeListener {

    private WidgetTravelBtnBinding binding;

    private MyFragment myFragment;

    private MainActivity activity;

    public TravelBtnWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_travel_btn
                , this
                , true
        );

        binding.getRoot().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myFragment==null){
                    return;
                }

                myFragment.showMyTravel(activity);
            }
        });

    }

    public void initBtn(){
        post(new Runnable() {
            @Override
            public void run() {
                List<TravelData> list = myFragment.myTravelDB.getAllByEmail();

                if (list.size()>0){
                    binding.img.setImageResource(R.drawable.home_floating_action_btn_badge_default);
                }else {
                    binding.img.setImageResource(R.drawable.home_floating_action_btn_default);
                }
            }
        });

    }

    public void setFragment(MyFragment myFragment){
        this.myFragment = myFragment;
        activity = (MainActivity) myFragment.getActivity();
        this.myFragment.myTravelDB.setOnTravelChangeListener(this);

        initBtn();
    }

    @Override
    public void onChange() {
        initBtn();
    }
}
