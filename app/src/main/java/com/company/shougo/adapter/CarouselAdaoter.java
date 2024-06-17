package com.company.shougo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarouselAdaoter extends PagerAdapter {

    private List<View> viewList;

    public CarouselAdaoter(List<View> viewList){
        this.viewList = viewList;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)   {
//        Log.e("CarouselAdaoter","destroyItem");
        container.removeView((View) object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Log.e("CarouselAdaoter","instantiateItem");
        View view = viewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }
}
