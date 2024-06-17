package com.company.shougo.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.company.shougo.mamager.Calculation;
import com.company.shougo.widget.MyFragment;

public abstract class BaseActivity extends AppCompatActivity {

    public FragmentManager fragmentManager;

    private String nowFragmentPageType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Calculation.adjustFontScale(this, getResources().getConfiguration());
    }

    public void changePage(int layoutId, MyFragment myFragment, boolean isAddToBack) {

        if (myFragment == null) {
            return;
        }

        String tag = myFragment.getSimpleName();

//        if (nowFragmentPageType.equals(tag)) {
//            return;
//        }

        nowFragmentPageType = tag;

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(
                layoutId
                , myFragment
                , tag
        );

        if (isAddToBack) {
            fragmentTransaction.addToBackStack(tag);
        }

//        if (fragmentManager.isStateSaved()) {
//            fragmentTransaction.commitAllowingStateLoss();
//        } else {
            fragmentTransaction.commit();
//        }

    }

    public Fragment getNowFragment() {
        Fragment nowFragment = null;

        if (fragmentManager!=null) {
            for (Fragment fragment : fragmentManager.getFragments()) {
                if (fragment != null && fragment.isVisible()) {
                    nowFragment = fragment;
                    break;
                }
            }
        }

        return nowFragment;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment fragment = getNowFragment();
        if (fragment!=null) {
            nowFragmentPageType = ((MyFragment) getNowFragment()).getSimpleName();
        }else {
            nowFragmentPageType = null;
        }
    }
}
