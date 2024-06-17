package com.company.shougo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.company.shougo.R;
import com.company.shougo.databinding.WidgetMemItemBinding;

public class MemItemWidget extends LinearLayout {

    private WidgetMemItemBinding binding;

    private int type = 0;

    public MemItemWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_mem_item
                , this
                , true
        );

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MemItemWidget);
        type = typedArray.getInt(R.styleable.MemItemWidget_typed, 0);

        initView();
    }

    private void initView(){
        switch (type){
            case 0:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_notification)
                        .into(binding.img);
                binding.title.setText(R.string.notification);
                break;
            case 1:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_feedback)
                        .into(binding.img);
                binding.title.setText(R.string.opinion);
                break;
            case 2:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_privacy)
                        .into(binding.img);
                binding.title.setText(R.string.privacy_title);
                break;
            case 3:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_cooperate_shop)
                        .into(binding.img);
                binding.title.setText(R.string.cooperation_store);
                break;
            case 4:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_car)
                        .into(binding.img);
                binding.title.setText(R.string.my_car);
                break;
            case 5:
                Glide.with(getContext())
                        .load(R.drawable.setting_ic_remove)
                        .into(binding.img);
                binding.title.setText(R.string.remove_account);
                break;
        }
    }

}
