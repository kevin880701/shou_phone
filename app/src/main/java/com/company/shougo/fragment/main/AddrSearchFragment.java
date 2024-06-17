package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.LastlySearchAdapter;
import com.company.shougo.data.GPSData;
import com.company.shougo.databinding.FragmentAddrSearchBinding;
import com.company.shougo.listener.OnLastlySearchListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.SearchManager;
import com.company.shougo.widget.MyFragment;

import java.util.Collections;
import java.util.List;

public class AddrSearchFragment extends MyFragment {

    private final static String TAG = "AddrSearchFragment";

    private FragmentAddrSearchBinding binding;

    private MainActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_addr_search
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

        binding.travelBtn.setFragment(this);
        backView = binding.bottomDialog;

        binding.back.setOnClickListener(this);
        binding.useNowLocation.setOnClickListener(this);
        binding.search.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        binding.edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocation();
                    return true;
                }
                return false;
            }
        });

        setLastlyList();
    }

    private void setLastlyList(){
        binding.beforeList.setAdapter(null);

        List<String> list = SearchManager.getSearchLocation(activity);
        Collections.reverse(list);
        LastlySearchAdapter adapter = new LastlySearchAdapter(list);

        binding.beforeList.setLayoutManager(new LinearLayoutManager(activity));
        binding.beforeList.setAdapter(adapter);

        adapter.setOnLastlySearchListener(new OnLastlySearchListener() {
            @Override
            public void onClick(int pos) {
                binding.edit.setText(list.get(pos));

                searchLocation();
            }

            @Override
            public void onRemove(int pos) {
                SearchManager.removeSearchLocation(activity,list.get(pos));

                adapter.removeItem(pos);
            }
        });

    }

    private void searchLocation(){
        String location = binding.edit.getText().toString();
        GPSData gpsData = GPSManager.getInstance(activity).getLocationFromAddress(activity, location);

        if (
                location.length()<=0
                || !GPSManager.getInstance(activity).isAddress(location)
                || (
                        gpsData.getLat()==0
                        && gpsData.getLng()==0
                    )
        ){
            binding.showView.setVisibility(View.GONE);
            binding.errorView.setVisibility(View.VISIBLE);
            return;
        }

        binding.showView.setVisibility(View.VISIBLE);
        binding.errorView.setVisibility(View.GONE);

        SearchManager.addSearchLocation(activity, location);

        setLastlyList();

        GPSManager.getInstance(activity).setSearch(true, gpsData);

        activity.onBackPressed();

        if (activity.getNowFragment() instanceof MapFragment){
            ((MapFragment) activity.getNowFragment()).isSearch = true;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
            case R.id.useNowLocation:
                GPSManager.getInstance(activity).setSearch(false, null);
                activity.onBackPressed();
                break;
            case R.id.search:
                searchLocation();
                break;
            case R.id.cancel:
                binding.edit.setText("");
                setLastlyList();
                binding.showView.setVisibility(View.VISIBLE);
                binding.errorView.setVisibility(View.GONE);
                break;
        }
    }
}
