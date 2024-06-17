package com.company.shougo.fragment.main;

import android.os.Bundle;
import android.util.Log;
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
import com.company.shougo.data.CouponData;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.FragmentMoreBinding;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.type.ViewType;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MoreFragment extends MyFragment {

    private final static String TAG = "MoreFragment";

    private FragmentMoreBinding binding;

    private MainActivity activity;

    private ViewType viewType;

    public MoreFragment(ViewType viewType){
        this.viewType = viewType;
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_more
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

//        binding.travelBtn.setFragment(this);
        backView = binding.bottomDialog;

        binding.back.setOnClickListener(this);

        String title = "";

        switch (viewType){
            case STORE:
                title = getString(R.string.hot);
                getReCommendStore();
                break;
            case DISCOUNT:
                title = getString(R.string.recommend_discount);
                getReCommendCoupon();
                break;
        }

        binding.title.setText(title);
    }

    private void getReCommendStore(){

        final String tag = "getReCommendStore ";

        GPSManager gpsManager = GPSManager.getInstance(activity);

        Execute.recommendStore(
                "0"
                , String.valueOf(gpsManager.getLat())
                , String.valueOf(gpsManager.getLng())
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
                                    List<StoreData> list = new ArrayList<>();
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            StoreData storeData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreData.class
                                            );

                                            list.add(storeData);
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addStoreList(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void addStoreList(List<StoreData> list){

        binding.list.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list, false);

        binding.list.setLayoutManager(
                new GridLayoutManager(activity, 2)
        );
        binding.list.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
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

    public void updateStoreFavorite(StoreData storeData){
        List<StoreData> list = ((StoreAdapter) binding.list.getAdapter()).getList();

        for (int i=0;i<list.size();i++){
            if (list.get(i).getVendor_id()==storeData.getVendor_id()){
                ((StoreAdapter) binding.list.getAdapter()).update(i, storeData);
                return;
            }
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

    private void getReCommendCoupon(){
        final String tag = "getReCommendCoupon ";

        Execute.recommendCoupon(
                String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                ,"0"
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
                                    List<CouponData> list = new ArrayList<>();
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try {
                                            CouponData couponData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , CouponData.class
                                            );

                                            list.add(couponData);
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    addCoupon(list);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }


    private void addCoupon(List<CouponData> list){

        binding.list.setAdapter(null);

        CouponAdapter adapter = new CouponAdapter(list, true, false);

        binding.list.setLayoutManager(
                new GridLayoutManager(activity, 2)
        );
        binding.list.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
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

    public void updateCouponFavorite(CouponData couponData){
        List<CouponData> list = ((CouponAdapter) binding.list.getAdapter()).getList();

        for (int i=0;i<list.size();i++){
            if (list.get(i).getCoupon_id()==couponData.getCoupon_id()){
                ((CouponAdapter) binding.list.getAdapter()).update(i, couponData);
                return;
            }
        }

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
