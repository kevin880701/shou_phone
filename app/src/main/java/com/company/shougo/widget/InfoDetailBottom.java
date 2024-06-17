package com.company.shougo.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.adapter.DescriptionAdapter;
import com.company.shougo.adapter.StoreAdapter;
import com.company.shougo.adapter.TravelItemAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.GPSData;
import com.company.shougo.data.StoreCommendData;
import com.company.shougo.data.StoreData;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.BottomInfoDetailBinding;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.fragment.main.FavoriteFragment;
import com.company.shougo.fragment.main.HomeFragment;
import com.company.shougo.fragment.main.MapFragment;
import com.company.shougo.fragment.main.MoreFragment;
import com.company.shougo.fragment.main.SearchMoreFragment;
import com.company.shougo.fragment.main.SearchResultFragment;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.listener.OnCommendListener;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.type.ViewType;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InfoDetailBottom extends RelativeLayout implements View.OnClickListener {

    private final static String TAG = "InfoDetailBottom";

    private BottomInfoDetailBinding binding;

    private ViewType viewType;

    private MainActivity activity;

    private MyFragment myFragment;

    private StoreData storeData;
    private List<StoreCommendData> storeCommendList;

    private CouponData couponData;

    private boolean isCreate = true;

    public InfoDetailBottom(Context context, StoreData storeData, MyFragment myFragment) {
        super(context);

        this.viewType = ViewType.STORE;
        this.myFragment = myFragment;
        this.storeData = storeData;

        activity = (MainActivity) context;

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.bottom_info_detail
                , this
                , true
        );

        binding.getRoot().setOnClickListener(this);
        binding.topView.setOnClickListener(this);
        binding.back.setOnClickListener(this);

//        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//
//                        if (isCreate) {
//                            DisplayMetrics dm = new DisplayMetrics();
//                            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//                            ViewGroup.LayoutParams params = binding.mainView.getLayoutParams();
//                            int height = (int) (dm.heightPixels * 0.8);
//                            if (height > binding.mainView.getHeight()) {
//                                height = binding.mainView.getHeight();
//                            }
//                            params.height = height;
//                            binding.mainView.setLayoutParams(params);
//                        }
//
//                        isCreate = false;
//                    }
//                });

        getStoreInfo(String.valueOf(storeData.getVendor_id()));

    }

    public InfoDetailBottom(Context context, CouponData couponData, MyFragment myFragment) {
        super(context);

        this.viewType = ViewType.DISCOUNT;
        this.myFragment = myFragment;
        this.couponData = couponData;

        activity = (MainActivity) context;

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.bottom_info_detail
                , this
                , true
        );

//        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
//                new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//
//                        if (isCreate) {
//                            DisplayMetrics dm = new DisplayMetrics();
//                            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//                            ViewGroup.LayoutParams params = binding.mainView.getLayoutParams();
//                            int height = (int) (dm.heightPixels * 0.8);
//                            if (height > binding.mainView.getHeight()) {
//                                height = binding.mainView.getHeight();
//                            }
//                            params.height = height;
//                            binding.mainView.setLayoutParams(params);
//                        }
//
//                        isCreate = false;
//                    }
//                });

        binding.getRoot().setOnClickListener(this);
        binding.topView.setOnClickListener(this);
        binding.back.setOnClickListener(this);

        getCouponInfo();

    }

    private void initStore(){
        binding.title.setText(getResources().getString(R.string.store_info));
        Glide.with(activity)
                .load(storeData.getLogo())
                .into(binding.detailView.img);
        binding.detailView.watchStore.setVisibility(View.GONE);
        binding.detailView.evaView.setVisibility(View.VISIBLE);
        binding.detailView.addTravel.setVisibility(View.VISIBLE);
        binding.detailView.checkDiscount.setVisibility(View.VISIBLE);
        binding.detailView.usedImg.setVisibility(GONE);
        binding.detailView.givStoreEva.setVisibility(GONE);
        binding.detailView.overImg.setVisibility(GONE);
        binding.detailView.dateView.setVisibility(GONE);
        binding.detailView.ticketInfoView.setVisibility(GONE);
        binding.detailView.useDiscount.setVisibility(GONE);

        binding.detailView.infoTitle.setText(getResources().getString(R.string.info));

        binding.detailView.allEva.setOnClickListener(this);
        binding.detailView.giveEva.setOnClickListener(this);
        binding.detailView.showInMap.setOnClickListener(this);
        binding.detailView.parkShowInMap.setOnClickListener(this);
        binding.detailView.favorite.setOnClickListener(this);
        binding.detailView.checkDiscount.setOnClickListener(this);
        binding.detailView.watchStore.setOnClickListener(this);
        binding.detailView.addTravel.setOnClickListener(this);
        binding.detailView.useDiscount.setOnClickListener(this);

        binding.detailView.name.setText(storeData.getStore_name());
        binding.detailView.score.setText(String.valueOf(storeData.getStar()));
        binding.detailView.place.setText(storeData.getAddress());
        binding.detailView.location.setText(String.format("%.1f", storeData.getDistance()) + getResources().getString(R.string.miles));
        binding.detailView.info.setText(storeData.getDescription());

        double parkDis = 0;

        if (storeData.getParking()!=null && storeData.getParking().length()>0){
            binding.detailView.parkInfo.setText(getResources().getString(R.string.have_park));
            binding.detailView.parkShowInMap.setVisibility(VISIBLE);
            binding.detailView.showInMapLine.setVisibility(VISIBLE);
        }else{
            binding.detailView.parkInfo.setText(getResources().getString(R.string.no_park));
            binding.detailView.parkShowInMap.setVisibility(GONE);
            binding.detailView.showInMapLine.setVisibility(GONE);
        }
        binding.detailView.parkLocation.setText(String.format("%.1f", parkDis) + getResources().getString(R.string.miles));
        binding.detailView.parkImg.setVisibility(GONE);
        binding.detailView.parkLocation.setVisibility(GONE);

        if (storeData.isFavorites()){
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
        }else {
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_default);
        }

        addDescriptionList();

        changeAddTravelBtn();

        getStoreCommend();
    }

    public void addDescriptionList() {
        if (storeData.getDesc().size()<=0) {
            binding.detailView.descriptionTitle.setVisibility(GONE);
            binding.detailView.descriptionList.setVisibility(GONE);
        }else {
            binding.detailView.descriptionTitle.setVisibility(VISIBLE);
            binding.detailView.descriptionList.setVisibility(VISIBLE);
        }

        binding.detailView.descriptionList.setAdapter(null);

        DescriptionAdapter adapter = new DescriptionAdapter(storeData.getDesc(), activity);

        binding.detailView.descriptionList.setLayoutManager(new LinearLayoutManager(getContext()));
        // 避免NestedScrolling滑動卡頓問題
        binding.detailView.descriptionList.setNestedScrollingEnabled(false);

        binding.detailView.descriptionList.setAdapter(adapter);

    }

    private void getStoreCommend(){
        final String tag = "getStoreCommend ";

        Execute.getStoreComment(
                String.valueOf(storeData.getVendor_id())
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.detailView.allEva.setText("0" + getResources().getString(R.string.s_eva));
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
                                    storeCommendList = new ArrayList<>();
                                    JSONArray array = new JSONArray(data);

                                    double score = 0;
                                    for (int i=0;i<array.length();i++){
                                        try{
                                            StoreCommendData storeCommendData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , StoreCommendData.class
                                            );

                                            score+=storeCommendData.getStar();

                                            storeCommendList.add(storeCommendData);
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    if (storeCommendList.size()>0) {
                                        score = score / storeCommendList.size();
                                    }

                                    binding.detailView.score.setText(String.format("%.1f", score));

                                    binding.detailView.allEva.setText(String.valueOf(storeCommendList.size()) + getResources().getString(R.string.s_eva));

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    binding.detailView.allEva.setText("0" + getResources().getString(R.string.s_eva));
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getCouponInfo(){
        final String tag = "getCouponInfo ";

        Execute.getCouponInfo(
                String.valueOf(couponData.getCoupon_id())
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{

                                    CouponData data1 = new Gson().fromJson(
                                            data
                                            , CouponData.class
                                    );

//                                    data1.setFavorites(couponData.isFavorites());
                                    couponData = data1;

                                    getStoreInfo(String.valueOf(couponData.getVendor_id()));
                                }catch (Exception e){
                                    Log.e(TAG,tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void initCoupon(){
        binding.title.setText(getResources().getString(R.string.discount_info));
        Glide.with(activity)
                .load(couponData.getProfile_image())
                .into(binding.detailView.img);
        binding.detailView.watchStore.setVisibility(View.VISIBLE);
        binding.detailView.dateView.setVisibility(VISIBLE);
        binding.detailView.ticketInfoView.setVisibility(VISIBLE);

        if (couponData.getStatus()==1 && couponData.getUse_status()==1){
            binding.detailView.evaView.setVisibility(View.GONE);
            binding.detailView.addTravel.setVisibility(View.VISIBLE);
            binding.detailView.usedImg.setVisibility(GONE);
            binding.detailView.givStoreEva.setVisibility(GONE);
            binding.detailView.overImg.setVisibility(GONE);

//            if (couponData.getUses_total() < couponData.getTotal()){
                binding.detailView.useDiscount.setVisibility(VISIBLE);
//            }else{
//                binding.detailView.useDiscount.setVisibility(GONE);
//            }

        }else if (couponData.getStatus()==0){
            binding.detailView.evaView.setVisibility(View.VISIBLE);
            binding.detailView.addTravel.setVisibility(View.GONE);
            binding.detailView.usedImg.setVisibility(GONE);
            binding.detailView.givStoreEva.setVisibility(VISIBLE);
            binding.detailView.overImg.setVisibility(VISIBLE);
            binding.detailView.useDiscount.setVisibility(GONE);
        }else if (couponData.getUse_status()==0){
            binding.detailView.evaView.setVisibility(View.VISIBLE);
            binding.detailView.addTravel.setVisibility(View.GONE);
            binding.detailView.usedImg.setVisibility(VISIBLE);
            binding.detailView.givStoreEva.setVisibility(VISIBLE);
            binding.detailView.overImg.setVisibility(GONE);
            binding.detailView.useDiscount.setVisibility(GONE);
        }

        binding.detailView.checkDiscount.setVisibility(View.GONE);

        binding.detailView.showInMap.setOnClickListener(this);
        binding.detailView.parkShowInMap.setOnClickListener(this);
        binding.detailView.evaView.setOnClickListener(this);
        binding.detailView.checkDiscount.setOnClickListener(this);
        binding.detailView.watchStore.setOnClickListener(this);
        binding.detailView.addTravel.setOnClickListener(this);
        binding.detailView.favorite.setOnClickListener(this);
        binding.detailView.useDiscount.setOnClickListener(this);

        binding.detailView.name.setText(couponData.getStore_name() + " " + couponData.getName());
        binding.detailView.infoTitle.setText(getResources().getString(R.string.info_ticket));

        if (couponData.isFavorites()){
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
        }else {
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_default);
        }

        binding.detailView.place.setText(storeData.getAddress());
        binding.detailView.location.setText(String.format("%.1f", storeData.getDistance()) + getResources().getString(R.string.miles));
        binding.detailView.info.setText(storeData.getDescription());

        binding.detailView.date.setText(
                couponData.getDate_start().replace("-", ".")
                + "-"
                + couponData.getDate_end().replace("-", ".")
        );
        binding.detailView.ticketInfo.setText(couponData.getDescription());

        double parkDis = 0;

        if (storeData.getParking()!=null && storeData.getParking().length()>0){
            binding.detailView.parkInfo.setText(getResources().getString(R.string.have_park));
            binding.detailView.parkShowInMap.setVisibility(VISIBLE);
            binding.detailView.showInMapLine.setVisibility(VISIBLE);
        }else{
            binding.detailView.parkInfo.setText(getResources().getString(R.string.no_park));
            binding.detailView.parkShowInMap.setVisibility(GONE);
            binding.detailView.showInMapLine.setVisibility(GONE);
        }
        binding.detailView.parkLocation.setText(String.format("%.1f", parkDis) + getResources().getString(R.string.miles));
        binding.detailView.parkImg.setVisibility(GONE);
        binding.detailView.parkLocation.setVisibility(GONE);

//        addDescriptionList();

        changeAddTravelBtn();
    }

    private void changeAddTravelBtn(){
        if (Calculation.isAddLocalTravel(getContext(), this.storeData.getVendor_id())){
            binding.detailView.addTravel.setBackgroundResource(R.drawable.bg35_black_gray);
            binding.detailView.addTravelText.setText(getResources().getString(R.string.already_add_travel));
            binding.detailView.addTravelImg.setVisibility(GONE);
        }
    }

    private void getStoreInfo(String storeId){
        final String tag = "getStoreInfo ";

        Execute.getStoreInfo(
                storeId
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
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
                                    StoreData data1 = new Gson().fromJson(
                                            data
                                            , StoreData.class
                                    );

                                    if (storeData!=null) {
                                        data1.setFavorites(storeData.isFavorites());
                                    }

                                    storeData = data1;

                                    switch (viewType){
                                        case STORE:
                                            initStore();
                                            break;
                                        case DISCOUNT:
                                            initCoupon();
                                            break;
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

    private void addFavorite(String id, String type){
        final String tag = "addFavorite";

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
                                    if (type.equals("0")){
                                        InfoDetailBottom.this.couponData.setFavorites(true);

                                        if (myFragment instanceof HomeFragment) {
                                            ((HomeFragment) myFragment).updateCouponFavorite(InfoDetailBottom.this.couponData);
                                        }else if (myFragment instanceof MoreFragment){
                                            ((MoreFragment) myFragment).updateCouponFavorite(InfoDetailBottom.this.couponData);
                                        }else if (myFragment instanceof FavoriteFragment){
                                            ((FavoriteFragment) myFragment).addCoupon(InfoDetailBottom.this.couponData);
                                        }else if (myFragment instanceof SearchResultFragment){
                                            ((SearchResultFragment) myFragment).setFav(true);
                                        }

                                    }else if (type.equals("1")) {
                                        InfoDetailBottom.this.storeData.setFavorites(true);

                                        if (myFragment instanceof HomeFragment) {
                                            ((HomeFragment) myFragment).updateStoreFavorite(InfoDetailBottom.this.storeData);
                                        }else if (myFragment instanceof MoreFragment){
                                            ((MoreFragment) myFragment).updateStoreFavorite(InfoDetailBottom.this.storeData);
                                        }else if (myFragment instanceof FavoriteFragment){
                                            ((FavoriteFragment) myFragment).addStore(InfoDetailBottom.this.storeData);
                                        }else if (myFragment instanceof SearchResultFragment){
                                            ((SearchResultFragment) myFragment).setFav(true);
                                        }else if (myFragment instanceof SearchMoreFragment){
                                            ((SearchMoreFragment) myFragment).setFav(true);
                                        }
                                    }

                                    binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void delFavorite(String id, String type){
        final String tag = "delFavorite";

        Log.e(TAG, tag + " : " + id);

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
                                    if (type.equals("0")){
                                        InfoDetailBottom.this.couponData.setFavorites(false);

                                        if (myFragment instanceof HomeFragment) {
                                            ((HomeFragment) myFragment).updateCouponFavorite(InfoDetailBottom.this.couponData);
                                        }else if (myFragment instanceof MoreFragment){
                                            ((MoreFragment) myFragment).updateCouponFavorite(InfoDetailBottom.this.couponData);
                                        }else if (myFragment instanceof FavoriteFragment){
                                            ((FavoriteFragment) myFragment).removeFav();
                                        }else if (myFragment instanceof SearchResultFragment){
                                            ((SearchResultFragment) myFragment).setFav(false);
                                        }

                                    }else {
                                        InfoDetailBottom.this.storeData.setFavorites(false);

                                        if (myFragment instanceof HomeFragment) {
                                            ((HomeFragment) myFragment).updateStoreFavorite(InfoDetailBottom.this.storeData);
                                        }else if (myFragment instanceof MoreFragment){
                                            ((MoreFragment) myFragment).updateStoreFavorite(InfoDetailBottom.this.storeData);
                                        }else if (myFragment instanceof FavoriteFragment){
                                            ((FavoriteFragment) myFragment).removeFav();
                                        }else if (myFragment instanceof SearchResultFragment){
                                            ((SearchResultFragment) myFragment).setFav(false);
                                        }else if (myFragment instanceof SearchMoreFragment){
                                            ((SearchMoreFragment) myFragment).setFav(false);
                                        }
                                    }

                                    binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_default);
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getUserComment(){
        final String tag = "getUserComment ";

        DialogManager.showLoadDialog(activity);

        Execute.getUserComment(
                String.valueOf(storeData.getVendor_id())
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        DialogManager.cancelLoadDialog(activity);

                        DialogManager.showEvaDialog(
                                activity
                                , storeData.getStore_name()
                                , null
                                , new OnCommendListener() {
                                    @Override
                                    public void onSend(String comment, int star) {
                                        addComment(comment, star);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        DialogManager.cancelLoadDialog(activity);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    StoreCommendData commendData = new Gson().fromJson(
                                            data
                                            , StoreCommendData.class
                                    );

                                    DialogManager.showEvaDialog(
                                            activity
                                            , storeData.getStore_name()
                                            , commendData
                                            , new OnCommendListener() {
                                                @Override
                                                public void onSend(String comment, int star) {
                                                    addComment(comment, star);
                                                }
                                            });
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());

                                    DialogManager.showEvaDialog(
                                            activity
                                            , storeData.getStore_name()
                                            , null
                                            , new OnCommendListener() {
                                                @Override
                                                public void onSend(String comment, int star) {
                                                    addComment(comment, star);
                                                }
                                            }
                                    );
                                }
                            }
                        });
                    }
                }
        );
    }

    private void addComment(String comment, int star){
        final String tag = "addComment ";

        if (
                star<=0
        ){
            return;
        }

        Execute.addComment(
                String.valueOf(storeData.getVendor_id())
                , String.valueOf(star)
                , comment
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,tag +"onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getStoreCommend();
                            }
                        });
                    }
                }
        );

    }

    private void addTravel(boolean isPark){

        if (isPark){

            TravelData parkData = new TravelData();
            parkData.setType(2);
            parkData.setVendor_id(storeData.getVendor_id());
            parkData.setVendor_name(storeData.getStore_name());
            parkData.setVendor_logo(storeData.getLogo());
//            GPSData gpsData = GPSManager.getInstance(getContext()).getLocationFromAddress(
//                    getContext()
//                    , storeData.getParking()
//            );
            parkData.setLat(storeData.getP_lat());
            parkData.setLng(storeData.getP_lng());
            parkData.setParking_address(storeData.getAddress());

            myFragment.myTravelDB.insert(parkData);
        }

        TravelData travelData = new TravelData();
        switch (viewType){
            case STORE:
                travelData.setType(1);
                travelData.setVendor_id(storeData.getVendor_id());
                travelData.setVendor_name(storeData.getStore_name());
                travelData.setVendor_logo(storeData.getLogo());
                travelData.setLat(storeData.getLat());
                travelData.setLng(storeData.getLng());
                travelData.setAddress(storeData.getAddress());
                break;
            case DISCOUNT:
                travelData.setType(0);
                travelData.setVendor_id(storeData.getVendor_id());
                travelData.setVendor_name(storeData.getStore_name());
                travelData.setVendor_logo(storeData.getLogo());
                travelData.setCoupon_id(couponData.getCoupon_id());
                travelData.setCoupon_name(couponData.getName());
                travelData.setCoupon_logo(couponData.getProfile_image());
                travelData.setLat(storeData.getLat());
                travelData.setLng(storeData.getLng());
                travelData.setAddress(storeData.getAddress());
                break;
        }
        myFragment.myTravelDB.insert(travelData);

        DialogManager.showConfirmDialog(
                activity
                , getResources().getString(R.string.success)
                , getResources().getString(R.string.add_travel_success)
                ,null
        );

        changeAddTravelBtn();
    }

    private void getStoreCoupon(){
        final String tag = "getStoreCoupon ";

        Execute.getStoreCoupon(
                String.valueOf(storeData.getVendor_id())
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    JSONArray array = new JSONArray(data);
                                    List<CouponData> list = new ArrayList<>();
                                    for (int i=0;i<array.length();i++){
                                        try{
                                            list.add(new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , CouponData.class
                                            ));
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    if (list.size()<=0){
                                        DialogManager.showConfirmDialog(
                                                activity
                                                , getResources().getString(R.string.fail)
                                                , getResources().getString(R.string.no_ticket)
                                                , null
                                        );
                                    }else if (list.size()==1){
                                        myFragment.showCouponInfo(activity, list.get(0));
                                    }else {
                                        DialogManager.showTicketDialog(
                                                activity
                                                , storeData.getStore_name()
                                                , list
                                                , new OnAdapterItemListener() {
                                                    @Override
                                                    public void onItemClick(int pos) {
                                                        myFragment.showCouponInfo(activity, list.get(pos));
                                                    }
                                                }
                                        );
                                    }

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            activity
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.no_ticket)
                                            , null
                                    );
                                }
                            }
                        });
                    }
                }
        );
    }

    private void getFavoriteTravel(boolean isPark){
        final String tag = "getFavoriteTravel ";

        Execute.getTravelList(new Callback() {
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
                            List<FavoriteTravelData> list = new ArrayList<>();

                            JSONArray array = new JSONArray(data);

                            for (int i=0;i<array.length();i++){
                                try{
                                    list.add(new Gson().fromJson(
                                            array.getJSONObject(i).toString()
                                            , FavoriteTravelData.class
                                    ));
                                }catch (Exception e){
                                    Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                }
                            }

                            if (list.size()<=0){
                                addTravel(isPark);
                            }else{
                                FavoriteTravelData favoriteTravelData = new FavoriteTravelData();
                                favoriteTravelData.setName("＋ " + getResources().getString(R.string.my_travel));
                                list.add(favoriteTravelData);
                                showSelect(list, isPark);
                            }

                        }catch (Exception e){
                            Log.e(TAG, tag + "Exception : " + e.toString());
                        }
                    }
                });
            }
        });
    }

    private void showSelect(List<FavoriteTravelData> list, boolean isPark){
        DialogManager.showTravelList(
                activity
                , list
                , new OnAdapterItemListener() {
                    @Override
                    public void onItemClick(int pos) {
                        if (pos==list.size()-1){
                            addTravel(isPark);
                            return;
                        }

                        getTravelDetail(list.get(pos), isPark);
                    }
                }
        );
    }

    private void getTravelDetail(FavoriteTravelData favoriteTravelData, boolean isPark){
        final String tag = "getTravelDetail ";

        Execute.getTravelDetail(
                String.valueOf(favoriteTravelData.getWaypoint_id())
                , String.valueOf(GPSManager.getInstance(activity).getLat())
                , String.valueOf(GPSManager.getInstance(activity).getLng())
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<TravelData> list = new ArrayList<>();
                                try{
                                    JSONObject object = new JSONObject(data);
                                    JSONArray array = object.getJSONArray("waypoints");

                                    for (int i=0;i<array.length();i++){
                                        try{
                                            list.add(new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , TravelData.class
                                            ));
                                        }catch (Exception e){
                                            Log.e(TAG,tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                    if (list.size()>0) {
                                        Collections.sort(list, new Comparator<TravelData>() {
                                            public int compare(TravelData o1, TravelData o2) {
                                                return o1.getSort() - o2.getSort();
                                            }
                                        });
                                    }

                                    if (isPark){

                                        TravelData parkData = new TravelData();
                                        parkData.setType(2);
                                        parkData.setVendor_id(storeData.getVendor_id());
                                        parkData.setVendor_name(storeData.getStore_name());
                                        parkData.setVendor_logo(storeData.getLogo());
//                                        GPSData gpsData = GPSManager.getInstance(getContext()).getLocationFromAddress(
//                                                getContext()
//                                                , storeData.getParking()
//                                        );
                                        parkData.setLat(storeData.getP_lat());
                                        parkData.setLng(storeData.getP_lng());
                                        parkData.setParking_address(storeData.getAddress());
                                        parkData.setSort(list.get(list.size()-1).getSort()+ 1);

                                        list.add(parkData);
                                    }

                                    TravelData travelData = new TravelData();
                                    switch (viewType){
                                        case STORE:
                                            travelData.setType(1);
                                            travelData.setVendor_id(storeData.getVendor_id());
                                            travelData.setVendor_name(storeData.getStore_name());
                                            travelData.setVendor_logo(storeData.getLogo());
                                            travelData.setLat(storeData.getLat());
                                            travelData.setLng(storeData.getLng());
                                            travelData.setAddress(storeData.getAddress());
                                            break;
                                        case DISCOUNT:
                                            travelData.setType(0);
                                            travelData.setVendor_id(storeData.getVendor_id());
                                            travelData.setVendor_name(storeData.getStore_name());
                                            travelData.setVendor_logo(storeData.getLogo());
                                            travelData.setCoupon_id(couponData.getCoupon_id());
                                            travelData.setCoupon_name(couponData.getName());
                                            travelData.setCoupon_logo(couponData.getProfile_image());
                                            travelData.setLat(storeData.getLat());
                                            travelData.setLng(storeData.getLng());
                                            travelData.setAddress(storeData.getAddress());
                                            break;
                                    }
                                    travelData.setSort(list.get(list.size()-1).getSort()+ 1);

                                    list.add(travelData);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }

                                update(favoriteTravelData, list);
                            }
                        });
                    }
                }
        );

    }

    public void update(FavoriteTravelData favoriteTravelData, List<TravelData> list){
        final String tag = "updateTravel ";

        JSONObject object = new JSONObject();
        try {
            object.put("waypoint_id", favoriteTravelData.getWaypoint_id());
            object.put("name", favoriteTravelData.getName());

            JSONArray array = new JSONArray();
            for (int i=0;i<list.size();i++){
                JSONObject data = new JSONObject();
                data.put("type", list.get(i).getType());
                data.put("vendor_id", list.get(i).getVendor_id());
                data.put("coupon_id", list.get(i).getCoupon_id());
                data.put("address", list.get(i).getAddress());
                data.put("sort", i);

                array.put(data);
            }

            object.put("waypoints", array);

        }catch (Exception e){
            Log.e(TAG, tag + "object : " + e.toString());
        }

        Execute.updateTravel(
                object
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,tag + "onFailure : " + e.toString());

                        DialogManager.showConfirmDialog(
                                activity
                                , getResources().getString(R.string.fail)
                                , getResources().getString(R.string.add_travel_fail)
                                ,null
                        );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);

                        try{

                            if (data.equals("true")){
                                DialogManager.showConfirmDialog(
                                        activity
                                        , getResources().getString(R.string.success)
                                        , getResources().getString(R.string.add_travel_success)
                                        ,null
                                );
                            }else {

                                DialogManager.showConfirmDialog(
                                        activity
                                        , getResources().getString(R.string.fail)
                                        , getResources().getString(R.string.add_travel_fail)
                                        ,null
                                );
                            }

                        }catch (Exception e){
                            Log.e(TAG, tag + "Exception : " + e.toString());
                            DialogManager.showConfirmDialog(
                                    activity
                                    , getResources().getString(R.string.fail)
                                    , getResources().getString(R.string.add_travel_fail)
                                    ,null
                            );
                        }

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
            case R.id.topView:

                break;
            case R.id.favorite:
                switch (viewType){
                    case STORE:
                        if (storeData.isFavorites()){
                            delFavorite(String.valueOf(storeData.getVendor_id()), "1");
                        }else{
                            addFavorite(String.valueOf(storeData.getVendor_id()), "1");
                        }
                        break;
                    case DISCOUNT:
                        if (couponData.isFavorites()){
                            delFavorite(String.valueOf(couponData.getCoupon_id()), "0");
                        }else{
                            addFavorite(String.valueOf(couponData.getCoupon_id()), "0");
                        }
                        break;
                }
                break;
            case R.id.watchStore:
                myFragment.showStoreInfo(activity, storeData);
                break;
            case R.id.allEva:
                DialogManager.showEvaListDialog(
                        activity
                        , storeCommendList
                        , storeData.getStore_name()
                        , new OnCommendListener() {
                            @Override
                            public void onSend(String comment, int star) {
                                addComment(comment, star);
                            }
                        }
                );
                break;
            case R.id.giveEva:
            case R.id.givStoreEva:
                getUserComment();
                break;
            case R.id.showInMap:
            case R.id.parkShowInMap:
                activity.changePage(R.id.mainFrame, new MapFragment(viewType, storeData, couponData), true);
                break;
            case R.id.checkDiscount:
                getStoreCoupon();
                break;
            case R.id.addTravel:

                if (storeData.getParking()!=null && storeData.getParking().length()>0){
                    DialogManager.showSelectDialog(
                            activity
                            , storeData.getStore_name()
                            , getResources().getString(R.string.have_parking)
                            , new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFavoriteTravel(true);
                                }
                            }
                            , new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getFavoriteTravel(false);
                                }
                            }
                    );
                }else {
                    getFavoriteTravel(false);
                }
                break;
            case R.id.useDiscount:
                DialogManager.showQrcodeDialog(
                        activity
                        , couponData.getCode()
                );
                break;
            default:
                activity.onBackPressed();
                break;
        }
    }
}
