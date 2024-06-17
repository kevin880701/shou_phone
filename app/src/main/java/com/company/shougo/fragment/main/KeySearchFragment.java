package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.LastlySearchAdapter;
import com.company.shougo.data.GPSData;
import com.company.shougo.databinding.FragmentKeySearchBinding;
import com.company.shougo.listener.OnLastlySearchListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.SearchManager;
import com.company.shougo.widget.MyFragment;

import org.json.JSONArray;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class KeySearchFragment extends MyFragment {

    private final static String TAG = "KeySearchFragment";

    private FragmentKeySearchBinding binding;

    private MainActivity activity;

    private int nearStore=0,hotTicket=0,bestStore=0;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_key_search
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
        binding.search.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        binding.edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        setLastlyList();
    }

    private void setLastlyList(){
        binding.beforeList.setAdapter(null);

        List<String> list = SearchManager.getSearch(activity);
        Collections.reverse(list);
        LastlySearchAdapter adapter = new LastlySearchAdapter(list);

        binding.beforeList.setLayoutManager(new LinearLayoutManager(activity));
        binding.beforeList.setAdapter(adapter);

        adapter.setOnLastlySearchListener(new OnLastlySearchListener() {
            @Override
            public void onClick(int pos) {
                binding.edit.setText(list.get(pos));

                search();
            }

            @Override
            public void onRemove(int pos) {
                SearchManager.removeSearch(activity,list.get(pos));

                adapter.removeItem(pos);
            }
        });

    }

    private void search(){
        String edit = binding.edit.getText().toString();

        if (
                edit.length()<=0
        ){
            binding.showView.setVisibility(View.GONE);
            binding.errorView.setVisibility(View.VISIBLE);
            return;
        }

        binding.showView.setVisibility(View.VISIBLE);
        binding.errorView.setVisibility(View.GONE);

        SearchManager.addSearch(activity, edit);

        setLastlyList();

        getNearStore(edit);

    }

    private void getNearStore(String key){
        final String tag = "getNearStore ";

        Execute.getNearStoreByKey(
                key
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                , "10"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nearStore = 0;
                                getHotTicket(key);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONArray array = new JSONArray(data);
                                    nearStore = array.length();
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    nearStore = 0;
                                }
                                getHotTicket(key);
                            }
                        });
                    }
                }
        );
    }

    private void getHotTicket(String key){
        final String tag = "getHotTicket ";

        Execute.getHotTicketByKey(
                key
                , "10"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hotTicket = 0;
                                getBestStore(key);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONArray array = new JSONArray(data);
                                    hotTicket = array.length();
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    hotTicket = 0;
                                }
                                getBestStore(key);
                            }
                        });
                    }
                }
        );
    }

    private void getBestStore(String key){
        final String tag = "getBestStore ";

        Execute.getStoreByKey(
                key
                , "10"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                bestStore = 0;
                                changePage(key);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONArray array = new JSONArray(data);
                                    bestStore = array.length();
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    bestStore = 0;
                                }
                                changePage(key);
                            }
                        });
                    }
                }
        );
    }

    private void changePage(String key){

        if (nearStore==0 && hotTicket==0 && bestStore==0){
            binding.showView.setVisibility(View.GONE);
            binding.errorView.setVisibility(View.VISIBLE);
        }else {
            activity.changePage(R.id.mainFrame, new SearchResultFragment(key), true);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
            case R.id.search:
                search();
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
