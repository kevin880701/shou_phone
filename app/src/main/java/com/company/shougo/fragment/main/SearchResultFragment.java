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
import com.company.shougo.databinding.FragmentSearchResultBinding;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.type.SearchType;
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

public class SearchResultFragment extends MyFragment {

    private static final String TAG = "SearchResultFragment";

    private FragmentSearchResultBinding binding;

    private MainActivity activity;

    private String title = "";

    private List<CategoryData> categoryList;
    private int selectNear = -1, selectHot = -1, selectBest = -1;
    private SearchType searchType = SearchType.NEAR;

    public SearchResultFragment(String title){
        this.title = title;
    }

    public SearchResultFragment(List<CategoryData> categoryList){
        this.categoryList = categoryList;
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
                    , R.layout.fragment_search_result
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

        binding.moreNearStore.setOnClickListener(this);
        binding.moreHot.setOnClickListener(this);
        binding.moreBest.setOnClickListener(this);
        binding.back.setOnClickListener(this);

        String title = "";

        if (categoryList==null) {
            title = this.title;

            getNearStore(title);
            getHotTicket(title);
            getBestStore(title);
        }else{
            for (int i=0;i<categoryList.size();i++){
                title = title + " " + categoryList.get(i).getName();
            }

            getNearStoreByCategory(categoryList);
            getHotTicketByCategory(categoryList);
            getBestStoreByCategory(categoryList);
        }

        binding.title.setText(title + getResources().getString(R.string.s_search_result));
    }

    private void getNearStoreByCategory(List<CategoryData> list){
        final String tag = "getNearStore ";

        Execute.getNearStoreByCategory(
                list
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                , "10"
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
                , "10"
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
                , "10"
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
                , "10"
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
                , "10"
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
                , "10"
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

    private void addNearList(List<StoreData> list){

        if (list.size()<=0){
            binding.noNearStore.setVisibility(View.VISIBLE);
            binding.nearList.setVisibility(View.GONE);
            binding.moreNearStore.setVisibility(View.GONE);
        }else {
            binding.noNearStore.setVisibility(View.GONE);
            binding.nearList.setVisibility(View.VISIBLE);
            binding.moreNearStore.setVisibility(View.VISIBLE);
        }

        binding.nearList.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list,true);

        binding.nearList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.nearList.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectNear = pos;
                searchType = SearchType.NEAR;
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

        if (list.size()<=0){
            binding.noHot.setVisibility(View.VISIBLE);
            binding.hotList.setVisibility(View.GONE);
            binding.moreHot.setVisibility(View.GONE);
        }else {
            binding.noHot.setVisibility(View.GONE);
            binding.hotList.setVisibility(View.VISIBLE);
            binding.moreHot.setVisibility(View.VISIBLE);
        }

        binding.hotList.setAdapter(null);

        CouponAdapter adapter = new CouponAdapter(list, true, false);

        binding.hotList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.hotList.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectHot = pos;
                searchType = SearchType.HOT;
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

        if (list.size()<=0){
            binding.noBest.setVisibility(View.VISIBLE);
            binding.bestList.setVisibility(View.GONE);
            binding.moreBest.setVisibility(View.GONE);
        }else {
            binding.noBest.setVisibility(View.GONE);
            binding.bestList.setVisibility(View.VISIBLE);
            binding.moreBest.setVisibility(View.VISIBLE);
        }

        binding.bestList.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list, true);

        binding.bestList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.bestList.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectBest = pos;
                searchType = SearchType.BEST;
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

    public void setFav(boolean isFav){
        switch (searchType){
            case NEAR:
                StoreData storeData = ((StoreAdapter) binding.nearList.getAdapter()).getList().get(selectNear);
                ((StoreAdapter) binding.nearList.getAdapter()).getList().get(selectNear)
                        .setFavorites(isFav);
                ((StoreAdapter) binding.nearList.getAdapter()).notifyItemChanged(selectNear);
                for (int i=0;i<((StoreAdapter) binding.bestList.getAdapter()).getList().size();i++){
                    if (storeData.getVendor_id()==((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).getVendor_id()){
                        ((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).setFavorites(isFav);
                        ((StoreAdapter) binding.bestList.getAdapter()).notifyItemChanged(i);
                    }
                }
                break;
            case HOT:
                ((CouponAdapter) binding.hotList.getAdapter()).getList().get(selectHot)
                        .setFavorites(isFav);
                ((CouponAdapter) binding.hotList.getAdapter()).notifyItemChanged(selectHot);
                break;
            case BEST:
                StoreData storeData2 = ((StoreAdapter) binding.bestList.getAdapter()).getList().get(selectBest);
                ((StoreAdapter) binding.bestList.getAdapter()).getList().get(selectBest)
                        .setFavorites(isFav);
                ((StoreAdapter) binding.bestList.getAdapter()).notifyItemChanged(selectBest);
                for (int i=0;i<((StoreAdapter) binding.nearList.getAdapter()).getList().size();i++){
                    if (storeData2.getVendor_id()==((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).getVendor_id()){
                        ((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).setFavorites(isFav);
                        ((StoreAdapter) binding.nearList.getAdapter()).notifyItemChanged(i);
                    }
                }
                break;
        }
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
                                            StoreData storeData = ((StoreAdapter) adapter).getList().get(pos);
                                            for (int i=0;i<((StoreAdapter) binding.bestList.getAdapter()).getList().size();i++){
                                                if (storeData.getVendor_id()==((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).getVendor_id()){
                                                    ((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).setFavorites(true);
                                                    ((StoreAdapter) binding.bestList.getAdapter()).notifyItemChanged(i);
                                                }
                                            }
                                            for (int i=0;i<((StoreAdapter) binding.nearList.getAdapter()).getList().size();i++){
                                                if (storeData.getVendor_id()==((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).getVendor_id()){
                                                    ((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).setFavorites(true);
                                                    ((StoreAdapter) binding.nearList.getAdapter()).notifyItemChanged(i);
                                                }
                                            }
//                                            ((StoreAdapter) adapter).getList().get(pos).setFavorites(true);
//                                            ((StoreAdapter) adapter).notifyItemChanged(pos);
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
                                            StoreData storeData = ((StoreAdapter) adapter).getList().get(pos);
                                            for (int i=0;i<((StoreAdapter) binding.bestList.getAdapter()).getList().size();i++){
                                                if (storeData.getVendor_id()==((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).getVendor_id()){
                                                    ((StoreAdapter) binding.bestList.getAdapter()).getList().get(i).setFavorites(false);
                                                    ((StoreAdapter) binding.bestList.getAdapter()).notifyItemChanged(i);
                                                }
                                            }
                                            for (int i=0;i<((StoreAdapter) binding.nearList.getAdapter()).getList().size();i++){
                                                if (storeData.getVendor_id()==((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).getVendor_id()){
                                                    ((StoreAdapter) binding.nearList.getAdapter()).getList().get(i).setFavorites(false);
                                                    ((StoreAdapter) binding.nearList.getAdapter()).notifyItemChanged(i);
                                                }
                                            }
//                                            ((StoreAdapter) adapter).getList().get(pos).setFavorites(false);
//                                            ((StoreAdapter) adapter).notifyItemChanged(pos);
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
            case R.id.moreNearStore:
                if (((StoreAdapter) binding.nearList.getAdapter()).getList().size()<=0){
                    return;
                }
                if (categoryList==null) {
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(binding.discount.getText().toString(), title), true);
                }else{
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(categoryList, binding.discount.getText().toString()), true);
                }
                break;
            case R.id.moreHot:
                if (((CouponAdapter) binding.hotList.getAdapter()).getList().size()<=0){
                    return;
                }
                if (categoryList==null) {
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(binding.hot.getText().toString(), title), true);
                }else{
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(categoryList, binding.hot.getText().toString()), true);
                }
                break;
            case R.id.moreBest:
                if (((StoreAdapter) binding.bestList.getAdapter()).getList().size()<=0){
                    return;
                }
                if (categoryList==null) {
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(binding.evaluation.getText().toString(), title), true);
                }else{
                    activity.changePage(R.id.mainFrame, new SearchMoreFragment(categoryList, binding.evaluation.getText().toString()), true);
                }
                break;
        }
    }
}
