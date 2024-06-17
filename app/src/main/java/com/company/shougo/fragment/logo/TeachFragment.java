package com.company.shougo.fragment.logo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.adapter.CarouselAdaoter;
import com.company.shougo.databinding.FragmentTeachBinding;
import com.company.shougo.databinding.ViewTeachItemBinding;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.widget.MyFragment;

import java.util.ArrayList;
import java.util.List;

public class TeachFragment extends MyFragment {

    private final static String TAG = "TeachFragment";

    private FragmentTeachBinding binding;

    private LogoActivity activity;

    private List<View> viewList;
    private List<RadioButton> tabList;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_teach
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

        viewList = new ArrayList<>();

        ViewTeachItemBinding item1Binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , R.layout.view_teach_item
                , null
                , false
        );

        item1Binding.title1.setText(getString(R.string.teach_1));
        item1Binding.title2.setText("");
        item1Binding.img.setImageResource(R.drawable.launch_img_01);
        item1Binding.passView.setVisibility(View.VISIBLE);
        item1Binding.start.setVisibility(View.INVISIBLE);
        item1Binding.pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.pager.setCurrentItem(1);
            }
        });
        viewList.add(item1Binding.getRoot());

        ViewTeachItemBinding item2Binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , R.layout.view_teach_item
                , null
                , false
        );

        item2Binding.title1.setText(getString(R.string.teach_2));
        item2Binding.title2.setText(getString(R.string.teach_2_1));
        item2Binding.img.setImageResource(R.drawable.launch_img_02);
        item2Binding.passView.setVisibility(View.VISIBLE);
        item2Binding.start.setVisibility(View.INVISIBLE);
        item2Binding.pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.pager.setCurrentItem(2);
            }
        });
        viewList.add(item2Binding.getRoot());

        ViewTeachItemBinding item3Binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                , R.layout.view_teach_item
                , null
                , false
        );

        item3Binding.title1.setText(getString(R.string.teach_3));
        item3Binding.title2.setText(getString(R.string.teach_3_1));
        item3Binding.img.setImageResource(R.drawable.launch_img_03);
        item3Binding.passView.setVisibility(View.INVISIBLE);
        item3Binding.start.setVisibility(View.VISIBLE);
        item3Binding.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveManager.saveFirst(getContext());
                activity.changePage(R.id.logoFrame, new EnterFragment(), false);
            }
        });
        viewList.add(item3Binding.getRoot());

        CarouselAdaoter adapter = new CarouselAdaoter(viewList);
        binding.pager.setAdapter(adapter);

        tabList = new ArrayList<>();

        for (int i=0;i<viewList.size();i++){
            RadioButton radioButton = new RadioButton(getContext());
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(24,24);
            params.setMargins(10,0,10,0);
            radioButton.setLayoutParams(params);
            radioButton.setButtonDrawable(null);
            radioButton.setBackgroundResource(R.drawable.tab_white);
            radioButton.setClickable(false);

            binding.group.addView(radioButton);
            tabList.add(radioButton);
        }

        tabList.get(0).toggle();

        binding.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabList.get(position).toggle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {

    }
}
