package com.company.shougo.fragment.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.GPSData;
import com.company.shougo.data.StoreCommendData;
import com.company.shougo.data.StoreData;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.FragmentMapBinding;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.listener.OnCommendListener;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.type.ViewType;
import com.company.shougo.widget.InfoDetailBottom;
import com.company.shougo.widget.MyFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapFragment extends MyFragment implements OnMapReadyCallback {

    private final static String TAG = "MapFragment";

    private FragmentMapBinding binding;

    private MainActivity activity;

    private ViewType viewType;

    private float finalY = 0, topY = 0, beforeY;

    private boolean isUp = true;

    private GoogleMap gMap;

    private SupportMapFragment mapFragment;

    private StoreData storeData;
    private CouponData couponData;

    private List<StoreData> storeList;
    private List<CouponData> couponList;

    private List<Marker> storeMarker;
    private List<Marker> couponMarker;

    private Bitmap storeIcon, storeClickIcon;
    private Bitmap couponIcon, couponClickIcon;
    private Bitmap userIcon;

    private List<StoreCommendData> storeCommendList;

    private Marker beforeMarker;

    public boolean isTarget = false;

    public boolean isSearch = false;

    public MapFragment(ViewType viewType, StoreData storeData, CouponData couponData){
        this.viewType = viewType;
        this.storeData = storeData;
        this.couponData = couponData;

        isTarget = true;
    }

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_map
                , group
                , false
        );

        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        return binding.getRoot();
    }

    @Override
    public void initView() {
        activity = (MainActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        activity.getWindow().setStatusBarColor(Color.BLACK);

        backView = binding.bottomDialog;

        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int mHeight = 96;
        int mWidth = 112;

        if(metrics.widthPixels>1000){
            mHeight = 144;
            mWidth = 168;
        }

        BitmapDrawable sBitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.btn_phonespot_normal);
        Bitmap sb = sBitmapDrawable.getBitmap();
        storeIcon = Bitmap.createScaledBitmap(sb, mHeight, mWidth, false);
        BitmapDrawable sBitmapDrawable2 = (BitmapDrawable) getResources().getDrawable(R.drawable.btn_phonespot_clickhover);
        Bitmap sb2 = sBitmapDrawable2.getBitmap();
        storeClickIcon = Bitmap.createScaledBitmap(sb2, mHeight, mWidth, false);

        BitmapDrawable sBitmapDrawable3 = (BitmapDrawable) getResources().getDrawable(R.drawable.btn_phonecoupon_normal);
        Bitmap sb3 = sBitmapDrawable3.getBitmap();
        couponIcon = Bitmap.createScaledBitmap(sb3, mHeight, mWidth, false);
        BitmapDrawable sBitmapDrawable4 = (BitmapDrawable) getResources().getDrawable(R.drawable.btn_phonecoupon_clickhover);
        Bitmap sb4 = sBitmapDrawable4.getBitmap();
        couponClickIcon = Bitmap.createScaledBitmap(sb4, mHeight, mWidth, false);

        BitmapDrawable sBitmapDrawable5 = (BitmapDrawable) getResources().getDrawable(R.drawable.btn_phonelocation_clickhover);
        Bitmap sb5 = sBitmapDrawable5.getBitmap();
        userIcon = Bitmap.createScaledBitmap(sb5, mHeight, mWidth, false);

        binding.back.setOnClickListener(this);
        binding.search.setOnClickListener(this);

//        switch (viewType){
//            case STORE:
                initStore();
//                break;
//            case DISCOUNT:
//                initCoupon();
//                break;
//        }

        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    binding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    DisplayMetrics dm = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int height = (int)(binding.getRoot().getHeight() * 0.7);
                    ViewGroup.LayoutParams params = binding.mapView.getLayoutParams();
                    params.height = height;
                    binding.mapView.setLayoutParams(params);

                    binding.bottomView.setY((int)(height * 0.9));
                    binding.bottomView.setVisibility(View.VISIBLE);


                    finalY = binding.bottomView.getY();

                    int height2 = (int)(binding.getRoot().getHeight() * 0.8);
                    if (binding.bottomView.getHeight()>height2) {
                        ViewGroup.LayoutParams params2 = binding.bottomView.getLayoutParams();
                        params2.height = height2;
                        binding.bottomView.setLayoutParams(params2);
                    }

                    topY = (int)(binding.getRoot().getHeight() - height2);
                }
            });

        binding.bottomView.setOnTouchListener(onMoveListener);
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.edit.setText(storeData.getAddress());
    }

    public void reSearch(){

        GPSData gpsData = GPSManager.getInstance(activity).getAddress(activity);

        if (
                gpsData.getLat()>0
                && gpsData.getLng()>0
        ){
            binding.edit.setText(gpsData.getAddress());

            getRecommendStore(String.valueOf(gpsData.getLat()), String.valueOf(gpsData.getLng()), false);

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(gpsData.getLat(), gpsData.getLng()), 12));
        }
    }

    private View.OnTouchListener onMoveListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            float touchY = 0, moveY = 0;

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    touchY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:

                    moveY = (int)(event.getRawY() - touchY);

                    if (moveY>beforeY){
                        isUp = false;
                    }else {
                        isUp = true;
                    }

                    beforeY = moveY;

                    if (moveY>=finalY){
                        moveY = finalY;
                    }

                    if (moveY<=topY){
                        moveY = topY;
                    }

                    binding.bottomView.setY(moveY);

                    break;
                case MotionEvent.ACTION_UP:
                    if (isUp){
                        binding.bottomView.setY(topY);
                    }else{
                        binding.bottomView.setY(finalY);
                    }
                    break;
            }

            return true;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                activity.onBackPressed();
                break;
            case R.id.search:
                activity.changePage(R.id.mainFrame, new AddrSearchFragment(), true);
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
                            delFavorite(String.valueOf(couponData.getVendor_id()), "0");
                        }else{
                            addFavorite(String.valueOf(couponData.getVendor_id()), "0");
                        }
                        break;
                }
                break;
            case R.id.watchStore:
                showStoreInfo(activity, storeData);
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
                            , new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addTravel(true);
                                }
                            }
                            , new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addTravel(false);
                                }
                            }
                    );
                }else {
                    addTravel(false);
                }
                break;
            case R.id.useDiscount:
                DialogManager.showQrcodeDialog(
                        activity
                        , couponData.getCode()
                );
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {

                if (beforeMarker!=null){
                    if (beforeMarker.getTag() instanceof StoreData) {
                        beforeMarker.setIcon(BitmapDescriptorFactory.fromBitmap(storeIcon));
                    }else if (beforeMarker.getTag() instanceof CouponData) {
                        beforeMarker.setIcon(BitmapDescriptorFactory.fromBitmap(couponIcon));
                    }
                }

                if (marker.getTag() instanceof StoreData) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(storeClickIcon));

                    MapFragment.this.storeData = (StoreData) marker.getTag();
                    initStore();

                    beforeMarker = marker;
                }else if (marker.getTag() instanceof CouponData) {
                    marker.setIcon(BitmapDescriptorFactory.fromBitmap(couponClickIcon));

                    MapFragment.this.couponData = (CouponData) marker.getTag();
                    initCoupon();

                    beforeMarker = marker;
                }

                return false;
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSearch){
                    reSearch();
                }else {
                    if (storeData!=null) {
                        getRecommendStore(String.valueOf(storeData.getLat()), String.valueOf(storeData.getLng()), true);
                    }
                }
            }
        });
    }

    private void getRecommendStore(String lat, String lng, boolean isZoom){
        final String tag = "getRecommendStore ";

        storeList = new ArrayList<>();
        storeMarker = new ArrayList<>();

        if (gMap!=null) {
            gMap.clear();
        }
        beforeMarker = null;

        if (
                GPSManager.getInstance(activity).getMyLat()!=0
                && GPSManager.getInstance(activity).getMyLng()!=0
        ) {
            gMap.addMarker(
                    new MarkerOptions().position(
                            new LatLng(
                                    GPSManager.getInstance(activity).getMyLat()
                                    , GPSManager.getInstance(activity).getMyLng()
                            )
                    ).icon(BitmapDescriptorFactory.fromBitmap(userIcon))
            );
        }

        Execute.recommendStore(
                "0"
                , lat
                , lng
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

                                    for (int i=0;i<array.length();i++){
                                        StoreData storeData = new Gson().fromJson(
                                                String.valueOf(array.getJSONObject(i))
                                                , StoreData.class
                                        );

                                        Marker marker = null;

                                        storeList.add(storeData);

                                        Bitmap icon = null;

                                        if (
                                                MapFragment.this.storeData != null
                                                        && storeData.getVendor_id() == MapFragment.this.storeData.getVendor_id()
                                        ) {
                                            icon = storeClickIcon;
                                        } else {
                                            icon = storeIcon;
                                        }

                                        marker = gMap.addMarker(
                                                new MarkerOptions().position(
                                                        new LatLng(storeData.getLat(), storeData.getLng())
                                                ).icon(BitmapDescriptorFactory.fromBitmap(icon))
                                        );

                                        marker.setTag(storeData);

                                        storeMarker.add(marker);


                                        if (
                                                MapFragment.this.storeData != null
                                                && storeData.getVendor_id() == MapFragment.this.storeData.getVendor_id()
                                                && beforeMarker==null
                                        ) {
                                            beforeMarker = marker;

                                            if (isZoom) {
                                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                        new LatLng(storeData.getLat(), storeData.getLng()), 12));
                                            }
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

    private void getNearTicket(final String lat, final String lng){
        final String tag = "getNearTicket ";

        if (gMap!=null) {
            gMap.clear();
        }
        beforeMarker = null;

        couponList = new ArrayList<>();
        couponMarker = new ArrayList<>();

        String address = "";

        if (isTarget){
            address = storeData.getAddress();
        }else{
            address = GPSManager.getInstance(activity).getAddress(activity).getAddress();
        }

        Execute.getNearTicket(
                "0"
                , address
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

                                    for (int i=0;i<array.length();i++){
                                        CouponData couponData = new Gson().fromJson(
                                                String.valueOf(array.getJSONObject(i))
                                                , CouponData.class
                                        );
                                        couponList.add(couponData);

                                        Bitmap icon = null;

                                        if (
                                                MapFragment.this.couponData!=null
                                                && couponData.getCoupon_id()==MapFragment.this.couponData.getCoupon_id()
                                        ){
                                            icon = couponClickIcon;
                                        }else {
                                            icon = couponIcon;
                                        }

                                        Marker marker = gMap.addMarker(
                                                new MarkerOptions().position(
                                                        new LatLng(couponData.getLat(), couponData.getLng())
                                                ).icon(BitmapDescriptorFactory.fromBitmap(icon))
                                        );

                                        marker.setTag(couponData);

                                        couponMarker.add(marker);

                                        if (
                                                MapFragment.this.storeData!=null
                                                && couponData.getVendor_id()==MapFragment.this.storeData.getVendor_id()
                                        ){
                                            beforeMarker = marker;

                                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                    new LatLng(couponData.getLat(), couponData.getLng()), 12));
                                        }

                                    }

                                    getRecommendStore(lat, lng, true);

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }
                            }
                        });
                    }
                }

        );
    }

    private void initStore(){
        binding.title.setText(getResources().getString(R.string.store_info));
        binding.hint.setText(storeData.getStore_name());
        Glide.with(activity)
                .load(storeData.getLogo())
                .into(binding.detailView.img);
        binding.detailView.watchStore.setVisibility(View.GONE);
        binding.detailView.evaView.setVisibility(View.VISIBLE);
        binding.detailView.addTravel.setVisibility(View.VISIBLE);
        binding.detailView.checkDiscount.setVisibility(View.VISIBLE);
        binding.detailView.usedImg.setVisibility(View.GONE);
        binding.detailView.givStoreEva.setVisibility(View.GONE);
        binding.detailView.overImg.setVisibility(View.GONE);
        binding.detailView.dateView.setVisibility(View.GONE);
        binding.detailView.ticketInfoView.setVisibility(View.GONE);
        binding.detailView.useDiscount.setVisibility(View.GONE);
        binding.detailView.showInMap.setVisibility(View.GONE);
        binding.detailView.addline.setVisibility(View.GONE);
        binding.detailView.parkShowInMap.setVisibility(View.GONE);
        binding.detailView.showInMapLine.setVisibility(View.GONE);

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
//            binding.detailView.parkShowInMap.setVisibility(View.VISIBLE);
//            binding.detailView.showInMapLine.setVisibility(View.VISIBLE);
        }else{
            binding.detailView.parkInfo.setText(getResources().getString(R.string.no_park));
//            binding.detailView.parkShowInMap.setVisibility(View.GONE);
//            binding.detailView.showInMapLine.setVisibility(View.GONE);
        }
        binding.detailView.parkLocation.setText(String.format("%.1f", parkDis) + getResources().getString(R.string.miles));
        binding.detailView.parkImg.setVisibility(View.GONE);
        binding.detailView.parkLocation.setVisibility(View.GONE);

        if (storeData.isFavorites()){
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
        }else {
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_default);
        }

        changeAddTravelBtn();

        getStoreCommend();
    }

    private void changeAddTravelBtn(){
        if (Calculation.isAddLocalTravel(getContext(), this.storeData.getVendor_id())){
            binding.detailView.addTravel.setBackgroundResource(R.drawable.bg35_black_gray);
            binding.detailView.addTravelText.setText(getResources().getString(R.string.already_add_travel));
            binding.detailView.addTravelImg.setVisibility(View.GONE);
        }
    }

    private void initCoupon(){
        binding.title.setText(getResources().getString(R.string.discount_info));
        binding.hint.setText(storeData.getStore_name());
        Glide.with(activity)
                .load(couponData.getProfile_image())
                .into(binding.detailView.img);
        binding.detailView.watchStore.setVisibility(View.VISIBLE);
        if (couponData.getStatus()==1 && couponData.getUse_status()==1){
            binding.detailView.evaView.setVisibility(View.GONE);
            binding.detailView.addTravel.setVisibility(View.VISIBLE);
            binding.detailView.usedImg.setVisibility(View.GONE);
            binding.detailView.givStoreEva.setVisibility(View.GONE);
            binding.detailView.overImg.setVisibility(View.GONE);

//            if (couponData.getUses_total() < couponData.getTotal()){
            binding.detailView.useDiscount.setVisibility(View.VISIBLE);
//            }else{
//                binding.detailView.useDiscount.setVisibility(GONE);
//            }
        }else if (couponData.getStatus()==0){
            binding.detailView.evaView.setVisibility(View.VISIBLE);
            binding.detailView.addTravel.setVisibility(View.GONE);
            binding.detailView.usedImg.setVisibility(View.GONE);
            binding.detailView.givStoreEva.setVisibility(View.VISIBLE);
            binding.detailView.overImg.setVisibility(View.VISIBLE);
            binding.detailView.useDiscount.setVisibility(View.GONE);
        }else if (couponData.getUse_status()==0){
            binding.detailView.evaView.setVisibility(View.VISIBLE);
            binding.detailView.addTravel.setVisibility(View.GONE);
            binding.detailView.usedImg.setVisibility(View.VISIBLE);
            binding.detailView.givStoreEva.setVisibility(View.VISIBLE);
            binding.detailView.overImg.setVisibility(View.GONE);
            binding.detailView.useDiscount.setVisibility(View.GONE);
        }
        binding.detailView.checkDiscount.setVisibility(View.GONE);
        binding.detailView.dateView.setVisibility(View.VISIBLE);
        binding.detailView.ticketInfoView.setVisibility(View.VISIBLE);
        binding.detailView.showInMap.setVisibility(View.GONE);
        binding.detailView.addline.setVisibility(View.GONE);
        binding.detailView.parkShowInMap.setVisibility(View.GONE);
        binding.detailView.showInMapLine.setVisibility(View.GONE);

        binding.detailView.showInMap.setOnClickListener(this);
        binding.detailView.parkShowInMap.setOnClickListener(this);
        binding.detailView.evaView.setOnClickListener(this);
        binding.detailView.checkDiscount.setOnClickListener(this);
        binding.detailView.watchStore.setOnClickListener(this);
        binding.detailView.addTravel.setOnClickListener(this);
        binding.detailView.useDiscount.setOnClickListener(this);

        binding.detailView.name.setText(couponData.getStore_name() + " " + couponData.getName());
        binding.detailView.date.setText(
                couponData.getDate_start().replace("-", ".")
                        + "-"
                        + couponData.getDate_end().replace("-", ".")
        );
        binding.detailView.ticketInfo.setText(couponData.getDescription());

        if (couponData.isFavorites()){
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_pressed);
        }else {
            binding.detailView.favorite.setImageResource(R.drawable.home_btn_collect_default);
        }

        binding.detailView.place.setText(storeData.getAddress());
        binding.detailView.location.setText(String.format("%.1f", storeData.getDistance()) + getResources().getString(R.string.miles));
        binding.detailView.info.setText(storeData.getDescription());

        double parkDis = 0;

        if (storeData.getParking()!=null && storeData.getParking().length()>0){
            binding.detailView.parkInfo.setText(getResources().getString(R.string.have_park));
//            binding.detailView.parkShowInMap.setVisibility(View.VISIBLE);
//            binding.detailView.showInMapLine.setVisibility(View.VISIBLE);
        }else{
            binding.detailView.parkInfo.setText(getResources().getString(R.string.no_park));
//            binding.detailView.parkShowInMap.setVisibility(View.GONE);
//            binding.detailView.showInMapLine.setVisibility(View.GONE);
        }
        binding.detailView.parkLocation.setText(String.format("%.1f", parkDis) + getResources().getString(R.string.miles));
        binding.detailView.parkImg.setVisibility(View.GONE);
        binding.detailView.parkLocation.setVisibility(View.GONE);
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
                                    MapFragment.this.storeData.setFavorites(true);

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
                                    MapFragment.this.storeData.setFavorites(false);

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
                comment==null
                        || comment.length()<=0
                        || star<=0
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

            myTravelDB.insert(parkData);
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
        myTravelDB.insert(travelData);

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
                                        showCouponInfo(activity, list.get(0));
                                    }else {
                                        DialogManager.showTicketDialog(
                                                activity
                                                , storeData.getStore_name()
                                                , list
                                                , new OnAdapterItemListener() {
                                                    @Override
                                                    public void onItemClick(int pos) {
                                                        showCouponInfo(activity,list.get(pos));
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

}

