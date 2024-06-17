package com.company.shougo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.company.shougo.R;
import com.company.shougo.data.CategoryData;
import com.company.shougo.databinding.WidgetFastBinding;

public class FastWidget extends LinearLayout {

    private WidgetFastBinding binding;

    private CategoryData categoryData;

    public FastWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.widget_fast
                , this
                , true
        );

    }

    public void init(String title, int img){
        binding.title.setText(title);
        binding.img.setImageResource(img);
    }

    public void init(CategoryData categoryData){
        this.categoryData = categoryData;
        binding.title.setText(categoryData.getName());
        Glide.with(getContext())
                .load(categoryData.getImage())
                .into(binding.img);
    }
}
