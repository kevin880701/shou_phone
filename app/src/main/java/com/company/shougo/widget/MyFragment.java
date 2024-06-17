package com.company.shougo.widget;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.company.shougo.R;
import com.company.shougo.activity.BaseActivity;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.StoreData;
import com.company.shougo.data.TravelData;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.type.ViewType;

public abstract class MyFragment extends Fragment implements View.OnClickListener {

    public abstract String getSimpleName();

    public abstract View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle);

    public abstract void initView();

    public RelativeLayout backView;

    public MyTravelDB myTravelDB;

    @Override
    public abstract void onClick(View v);

    public boolean isFirstCreate = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstCreate = true;

        myTravelDB = new MyTravelDB(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        if (backView==null){
            return;
        }

        if (isShowBottomSheet()){
            if (backView.getChildAt(backView.getChildCount()-1) instanceof TravelAddBottom){
                ((TravelAddBottom) backView.getChildAt(backView.getChildCount()-1)).updateAdapter();
            }
        }

    }

    public void showMyTravel(MainActivity activity){
        TravelAddBottom travelAddBottom = new TravelAddBottom(activity, this);
        showBottomSheet(travelAddBottom);
    }

    public void showMyTravelByNet(MainActivity activity, FavoriteTravelData travelData){
        TravelAddBottom travelAddBottom = new TravelAddBottom(activity, this, travelData);
        showBottomSheet(travelAddBottom);
    }


    public void showStoreInfo(MainActivity activity, StoreData storeData){
        InfoDetailBottom infoDetailBottom = new InfoDetailBottom(activity, storeData, this);
        showBottomSheet(infoDetailBottom);
    }

    public void showCouponInfo(MainActivity activity, CouponData couponData){
        InfoDetailBottom infoDetailBottom = new InfoDetailBottom(activity, couponData, this);
        showBottomSheet(infoDetailBottom);
    }

    public void showBottomSheet(View view){

        if (backView==null){
            return;
        }

        backView.setBackgroundColor(Color.parseColor("#9e000000"));

        Transition t = new Slide(Gravity.BOTTOM);
        TransitionManager.beginDelayedTransition(backView, t);
        backView.addView(view);

    }

    public void cancelBottomSheet(){

        if (backView==null){
            return;
        }

        if (backView.getChildAt(backView.getChildCount()-1) instanceof TravelAddBottom){
            ((TravelAddBottom) backView.getChildAt(backView.getChildCount()-1)).update();
        }

        Transition t = new Slide(Gravity.BOTTOM);

        t.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {

            }

            @Override
            public void onTransitionEnd(Transition transition) {
                TransitionManager.endTransitions(backView);
                if (backView.getChildCount()<=0){
                    backView.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });

        TransitionManager.beginDelayedTransition(backView, t);
        if (backView.getChildCount()>0) {
            backView.removeViewAt(backView.getChildCount() - 1);
        }
    }

    public boolean isShowBottomSheet(){
        if (backView==null){
            return false;
        }

        if (backView.getChildCount()>0){
            return true;
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        isFirstCreate = false;
    }
}
