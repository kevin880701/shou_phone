package com.company.shougo.widget.teach;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.databinding.ViewTeach1Binding;
import com.company.shougo.mamager.SaveManager;

public class Teach1View extends RelativeLayout implements View.OnClickListener {

    private final static String TAG = "Teach1View";

    private ViewTeach1Binding binding;

    private MainActivity activity;

    private float size = 0;

    private int type = 1;

    public Teach1View(Context context) {
        super(context);

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.view_teach_1
                , this
                , true
        );

        activity = (MainActivity) context;

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        size = (float) dm.widthPixels / (float) dm.heightPixels;

        binding.getRoot().setOnClickListener(this);
        binding.next.setOnClickListener(this);

        Log.e(TAG, size + " | " + dm.widthPixels + " | " + dm.heightPixels);

        setView();

    }

    private void setView(){
        switch (type){
            case 1:
                if (size<0.48){
                    binding.img.setImageResource(R.drawable.frame_21_1);
                }else {
                    binding.img.setImageResource(R.drawable.frame_16_1);
                }
                break;
            case 2:
                if (size<0.48){
                    binding.img.setImageResource(R.drawable.frame_21_2);
                }else {
                    binding.img.setImageResource(R.drawable.frame_16_2);
                }
                break;
            case 3:
                if (size<0.48){
                    binding.img.setImageResource(R.drawable.frame_21_3);
                }else {
                    binding.img.setImageResource(R.drawable.frame_16_3);
                }
                break;
            case 4:
                if (size<0.48){
                    binding.img.setImageResource(R.drawable.frame_21_4);
                }else {
                    binding.img.setImageResource(R.drawable.frame_16_4);
                }
                break;
            case 5:
                if (size<0.48){
                    binding.img.setImageResource(R.drawable.frame_21_5);
                }else {
                    binding.img.setImageResource(R.drawable.frame_16_5);
                }
                binding.next.setText(getResources().getString(R.string.start_use));
                break;
            default:
                SaveManager.saveHomeFirst(activity, false);
                activity.cancelBottomSheet();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                type += 1;
                setView();
                break;
        }
    }
}
