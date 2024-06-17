package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.adapter.StoreAdapter;
import com.company.shougo.data.CategoryData;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.FragmentSearchMoreBinding;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.type.ViewType;
import com.company.shougo.widget.InfoDetailBottom;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchMoreFragment extends MyFragment {

    private final static String TAG = "SearchMoreFragment";

    private FragmentSearchMoreBinding binding;

    private MainActivity activity;

    private String title = "", key ="";

    private List<CategoryData> categoryList;

    private int selectPos = -1;

    public SearchMoreFragment(String title,String key){
        this.title = title;
        this.key = key;
    }

    public SearchMoreFragment(List<CategoryData> categoryList, String title){
        this.categoryList = categoryList;
        this.title = title;
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        if (isFirstCreate) {
            binding = DataBindingUtil.inflate(
                    inflater
                    , R.layout.fragment_search_more
                    , group
                    , false
            );
        }
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

        if (categoryList==null) {
            binding.title.setText(key + " " +  title + getResources().getString(R.string.s_search_result));
            if (title.equals(getResources().getString(R.string.best_distance))) {
                getNearStore(key);
            } else if (title.equals(getResources().getString(R.string.hot))) {
                getHotTicket(key);
            } else if (title.equals(getResources().getString(R.string.best_evaluation))) {
                getBestStore(key);
            }
        }else {

            String title = "";

            for (int i=0;i<categoryList.size();i++){
                title = title + " " + categoryList.get(i).getName();
            }

            if (this.title.equals(getResources().getString(R.string.best_distance))) {
                getNearStoreByCategory(categoryList);
            } else if (this.title.equals(getResources().getString(R.string.hot))) {
                getHotTicketByCategory(categoryList);
            } else if (this.title.equals(getResources().getString(R.string.best_evaluation))) {
                getBestStoreByCategory(categoryList);
            }

            binding.title.setText(title + " " + this.title  + getResources().getString(R.string.s_search_result));

        }
    }

    private void getNearStoreByCategory(List<CategoryData> list){
        final String tag = "getNearStore ";

        Execute.getNearStoreByCategory(
                list
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<StoreData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            StoreData storeData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreData.class
                                            );

                                            list.add(storeData);
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addNearList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getHotTicketByCategory(List<CategoryData> list){
        final String tag = "getHotTicket ";

        Execute.getHotTicketByCategory(
                list
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<CouponData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try{
                                            CouponData couponData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , CouponData.class
                                            );

                                            list.add(couponData);
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addHotList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getBestStoreByCategory(List<CategoryData> list){
        final String tag = "getBestStore ";

        Execute.getBestStoreByCategory(
                list
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<StoreData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            StoreData storeData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreData.class
                                            );

                                            list.add(storeData);
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addBestList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }


    private void getNearStore(String key){
        final String tag = "getNearStore ";

        Execute.getNearStoreByKey(
                key
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<StoreData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            StoreData storeData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreData.class
                                            );

                                            list.add(storeData);
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addNearList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
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
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<CouponData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try{
                                            CouponData couponData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , CouponData.class
                                            );

                                            list.add(couponData);
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addHotList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
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
                , "0"
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<StoreData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            StoreData storeData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreData.class
                                            );

                                            list.add(storeData);
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addBestList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    public void setFav(boolean isFav){
        if (binding.list.getAdapter() instanceof CouponAdapter){
            ((CouponAdapter) binding.list.getAdapter()).getList().get(selectPos)
                    .setFavorites(isFav);
            ((CouponAdapter) binding.list.getAdapter()).notifyItemChanged(selectPos);
        }else if (binding.list.getAdapter() instanceof  StoreAdapter){
            ((StoreAdapter) binding.list.getAdapter()).getList().get(selectPos).setFavorites(isFav);
            ((StoreAdapter) binding.list.getAdapter()).notifyItemChanged(selectPos);
        }
    }

    private void addNearList(List<StoreData> list){

        binding.list.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list,false);

        binding.list.setLayoutManager(
                new GridLayoutManager(activity, 2)
        );
        binding.list.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectPos = pos;
                showStoreInfo(activity, adapter.getList().get(pos));
            }

            @Override
            public void onFavClick(int pos) {
                if (adapter.getList().get(pos).isFavorites()) {
                    delFavorite(adapter, pos, "1");
                }else {
                    addFavorite(adapter, pos, "1");
                }
            }
        });
    }

    private void addHotList(List<CouponData> list){

        binding.list.setAdapter(null);

        CouponAdapter adapter = new CouponAdapter(list, false, false);

        binding.list.setLayoutManager(
                new GridLayoutManager(activity, 2)
        );
        binding.list.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectPos = pos;
                showCouponInfo(activity, adapter.getList().get(pos));
            }

            @Override
            public void onFavClick(int pos) {
                if (adapter.getList().get(pos).isFavorites()) {
                    delFavorite(adapter, pos, "0");
                }else {
                    addFavorite(adapter, pos, "0");
                }
            }
        });
    }

    private void addBestList(List<StoreData> list){

        binding.list.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list, false);

        binding.list.setLayoutManager(
                new GridLayoutManager(activity, 2)
        );
        binding.list.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectPos = pos;
                showStoreInfo(activity, adapter.getList().get(pos));
            }

            @Override
            public void onFavClick(int pos) {
                if (adapter.getList().get(pos).isFavorites()) {
                    delFavorite(adapter, pos, "1");
                }else {
                    addFavorite(adapter, pos, "1");
                }
            }
        });
    }

    private void addFavorite(final RecyclerView.Adapter adapter, int pos, String type){
        final String tag = "addFavorite";

        String id = "";
        if (adapter instanceof StoreAdapter){
            id = String.valueOf(((StoreAdapter) adapter).getList().get(pos).getVendor_id());
        }else if (adapter instanceof CouponAdapter){
            id = String.valueOf(((CouponAdapter) adapter).getList().get(pos).getCoupon_id());
        }

        Execute.addFavorite(
                id
                , type
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        if (adapter instanceof StoreAdapter){
                                            ((StoreAdapter) adapter).getList().get(pos).setFavorites(true);
                                            ((StoreAdapter) adapter).notifyItemChanged(pos);
                                        }else if (adapter instanceof CouponAdapter){
                                            ((CouponAdapter) adapter).getList().get(pos).setFavorites(true);
                                            ((CouponAdapter) adapter).notifyItemChanged(pos);
                                        }
                                    }
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void delFavorite(final RecyclerView.Adapter adapter, int pos, String type){
        final String tag = "delFavorite";

        String id = "";
        if (adapter instanceof StoreAdapter){
            id = String.valueOf(((StoreAdapter) adapter).getList().get(pos).getVendor_id());
        }else if (adapter instanceof CouponAdapter){
            id = String.valueOf(((CouponAdapter) adapter).getList().get(pos).getCoupon_id());
        }

        Execute.delFavorite(
                id
                , type
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        if (adapter instanceof StoreAdapter){
                                            ((StoreAdapter) adapter).getList().get(pos).setFavorites(false);
                                            ((StoreAdapter) adapter).notifyItemChanged(pos);
                                        }else if (adapter instanceof CouponAdapter){
                                            ((CouponAdapter) adapter).getList().get(pos).setFavorites(false);
                                            ((CouponAdapter) adapter).notifyItemChanged(pos);
                                        }
                                    }
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
        }

    }
}
