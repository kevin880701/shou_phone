package com.company.shougo.fragment.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.activity.QrcodeActivity;
import com.company.shougo.adapter.CheckAdapter;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.adapter.StoreAdapter;
import com.company.shougo.adapter.TravelAdapter;
import com.company.shougo.data.CategoryData;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.StoreData;
import com.company.shougo.databinding.FragmentFavoriteBinding;
import com.company.shougo.databinding.FragmentHomeBinding;
import com.company.shougo.databinding.PopOtherBinding;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.listener.OnTravelListener;
import com.company.shougo.mamager.DialogManager;
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

public class FavoriteFragment extends MyFragment {

    private final static String TAG = "FavoriteFragment";

    private FragmentFavoriteBinding binding;

    private MainActivity activity;

    private List<CategoryData> city;
    private List<CategoryData> categoryList;

    private int selectPos = -1;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        if (isFirstCreate) {
            binding = DataBindingUtil.inflate(
                    inflater
                    , R.layout.fragment_favorite
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

        binding.group.setOnCheckedChangeListener(onCheckChangeListener);
        binding.category.setOnClickListener(this);
        binding.scan.setOnClickListener(this);

        getCity();
        getCategory();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding.ticket.isChecked()){
            binding.group.clearCheck();
            binding.ticket.toggle();
        }else if (binding.store.isChecked()){
            binding.group.clearCheck();
            binding.store.toggle();
        }else if (binding.attractions.isChecked()){
            binding.group.clearCheck();
            binding.attractions.toggle();
        }else if (binding.travel.isChecked()){
            binding.group.clearCheck();
            binding.travel.toggle();
        }else{
            binding.group.clearCheck();
            binding.ticket.toggle();
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            binding.ticketList.setVisibility(View.GONE);
            binding.storeList.setVisibility(View.GONE);
            binding.attractionsList.setVisibility(View.GONE);
            binding.travelList.setVisibility(View.GONE);

            binding.category.setVisibility(View.VISIBLE);

            switch (checkedId){
                case R.id.ticket:
                    selectPos = -1;
                    binding.ticketList.setAdapter(null);

                    getFavoriteCoupon();
                    break;
                case R.id.store:
                    binding.storeList.setAdapter(null);

                    getFavoriteStore(true);
                    break;
                case R.id.attractions:
                    binding.attractionsList.setAdapter(null);

                    getFavoriteStore(false);
                    break;
                case R.id.travel:
                    binding.category.setVisibility(View.GONE);
                    binding.travelList.setAdapter(null);

                    getTravelList();
                    break;
            }
        }
    };

    private void getCity(){
        final String tag = "getCity ";

        city = new ArrayList<>();

        Execute.getCity(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, tag + "onFailure : " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                Log.e(TAG, tag + "data : " + data);

                try{
                    JSONArray array = new JSONArray(data);

                    for (int i=0;i<array.length();i++) {
                        CategoryData categoryData = new CategoryData();
                        categoryData.setName(array.getString(i));
                        city.add(categoryData);
                    }
                }catch (Exception e){
                    Log.e(TAG, tag + "Exception : " + e.toString());
                }
            }
        });
    }

    private void getCategory(){

        final String tag = "getCategory ";

        Execute.category(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, tag + "onFailure : " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                Log.e(TAG,tag + "data : " +data);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            categoryList = new ArrayList<>();

                            JSONArray array = new JSONArray(data);

                            for (int i=0;i<array.length();i++){
                                try {
                                    CategoryData categoryData = new Gson().fromJson(
                                            String.valueOf(array.getJSONObject(i))
                                            , CategoryData.class
                                    );

                                    categoryList.add(categoryData);
                                }catch (Exception e){
                                    Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                }
                            }

                        }catch (Exception e){
                            Log.e(TAG, tag + "Exception : " + e.toString());
                        }
                    }
                });
            }
        });
    }

    private void getTravelList(){
        final String tag = "getTravelList ";

        Execute.getTravelList(
                new Callback() {
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
                                List<FavoriteTravelData> list = new ArrayList<>();
                                try{
                                    JSONArray array = new JSONArray(data);

                                    for (int i=0;i<array.length();i++){
                                        try{
                                            list.add(new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , FavoriteTravelData.class
                                            ));
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                }catch (Exception e){
                                    Log.e(TAG,tag + "Excepiton : " + e.toString());
                                }

                                setTravelList(list);
                            }
                        });
                    }
                }
        );
    }

    private void setTravelList(List<FavoriteTravelData> list){

        TravelAdapter adapter = new TravelAdapter(list);
        binding.travelList.setLayoutManager(new LinearLayoutManager(activity));
        binding.travelList.setAdapter(adapter);

        adapter.setOnTravelListener(new OnTravelListener() {
            @Override
            public void onClick(int pos) {
                showMyTravelByNet(activity, adapter.getList().get(pos));
            }

            @Override
            public void onEdit(int pos) {

            }

            @Override
            public void onDelete(int pos) {
                DialogManager.showSelectDialog(
                        activity
                        , getResources().getString(R.string.del_travel_title)
                        , getResources().getString(R.string.del_travel_content)
                        , new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delTravel(adapter, pos);
                            }
                        }
                        , null
                );
            }
        });

        binding.travelList.setVisibility(View.VISIBLE);
    }

    private void delTravel(TravelAdapter adapter, int pos){
        final String tag = "delTravel ";

        Execute.deleteTravel(
                String.valueOf(adapter.getList().get(pos).getWaypoint_id())
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
                                        adapter.remove(pos);
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

    private void getFavoriteCoupon(){
        final String tag = "getFavoriteCoupon ";

        Execute.favoriteCoupon(
                new Callback() {
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
                                        try {
                                            CouponData couponData = new Gson().fromJson(
                                                    String.valueOf(array.getJSONObject(i))
                                                    , CouponData.class
                                            );

                                            couponData.setFavorites(true);

                                            if (isCategoryCoupon(couponData) && isLocationCoupon(couponData)) {
                                                list.add(couponData);
                                            }
                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }

                                setTicketList(list);
                            }
                        });
                    }
                }
        );
    }

    private boolean isCategoryCoupon(CouponData couponData){
        if (categoryList==null || categoryList.size()<=0){
            return true;
        }

        boolean isSelect = false;

        for (int i=0;i<categoryList.size();i++){
            if (categoryList.get(i).isSelect()) {
                isSelect = true;
                if (couponData.getCategory_id() == categoryList.get(i).getCategory_id()) {
                    return true;
                }
            }
        }

        if (!isSelect){
            return true;
        }

        return false;
    }

    private boolean isLocationCoupon(CouponData couponData){
        if (city==null || city.size()<=0){
            return true;
        }

        boolean isSelect = false;

        for (int i=0;i<city.size();i++){
            if (city.get(i).isSelect()) {

                String name = city.get(i).getName();
                String name2 = name;
                if (name2.contains("臺")){
                    name2 = name2.replace("臺", "台");
                }else if (name2.contains("台")){
                    name2 = name2.replace("台", "臺");
                }

                isSelect = true;
                if (
                        couponData.getAddress() != null
                        && (
                                couponData.getAddress().contains(name)
                                || couponData.getAddress().contains(name2)
                        )
                ) {
                    return true;
                }
            }
        }

        if (!isSelect){
            return true;
        }

        return false;
    }

    private void setTicketList(List<CouponData> list){

        CouponAdapter adapter = new CouponAdapter(list, false, true);
        binding.ticketList.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.ticketList.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {

                selectPos = pos;

                if (
                        adapter.getList().get(pos).getStatus()==1
                        && adapter.getList().get(pos).getUse_status()==1
                ) {
                    showCouponInfo(activity, adapter.getList().get(pos));
                }
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

        binding.ticketList.setVisibility(View.VISIBLE);

    }

    private void getFavoriteStore(boolean isStore){
        final String tag = "getFavoriteStore ";

        Execute.favoriteStore(
                new Callback() {
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

                                            storeData.setFavorites(true);

                                            if (
                                                    isStore
                                                    && storeData.getCategory_id()!=61
                                                    && isCategoryStore(storeData)
                                                    && isLocationStore(storeData)
                                            ){
                                                list.add(storeData);
                                            }else if (
                                                    !isStore
                                                    && storeData.getCategory_id()==61
                                                    && isCategoryStore(storeData)
                                                    && isLocationStore(storeData)
                                            ) {
                                                list.add(storeData);
                                            }

                                        }catch (Exception e){
                                            Log.e(TAG, tag + "err " + i + " : " + e.toString());
                                        }
                                    }

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }

                                if (isStore) {
                                    setStoreList(list);
                                }else {
                                    setAttList(list);
                                }
                            }
                        });
                    }
                }
        );
    }

    private boolean isCategoryStore(StoreData storeData){
        if (categoryList==null || categoryList.size()<=0){
            return true;
        }

        boolean isSelect = false;

        for (int i=0;i<categoryList.size();i++){
            if (categoryList.get(i).isSelect()) {
                isSelect = true;
                if (storeData.getCategory_id() == categoryList.get(i).getCategory_id()) {
                    return true;
                }
            }
        }

        if (!isSelect){
            return true;
        }

        return false;
    }

    private boolean isLocationStore(StoreData storeData){
        if (city==null || city.size()<=0){
            return true;
        }

        boolean isSelect = false;

        for (int i=0;i<city.size();i++){
            if (city.get(i).isSelect()) {

                String name = city.get(i).getName();
                String name2 = name;
                if (name2.contains("臺")){
                    name2 = name2.replace("臺", "台");
                }else if (name2.contains("台")){
                    name2 = name2.replace("台", "臺");
                }

                isSelect = true;
                if (
                        storeData.getAddress() != null
                        && (
                            storeData.getAddress().contains(name)
                            || storeData.getAddress().contains(name2)
                        )
                ) {
                    return true;
                }
            }
        }

        if (!isSelect){
            return true;
        }

        return false;
    }

    private void setStoreList(List<StoreData> list){

        StoreAdapter adapter = new StoreAdapter(list, false);
        binding.storeList.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.storeList.setAdapter(adapter);

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

        binding.storeList.setVisibility(View.VISIBLE);

    }

    private void setAttList(List<StoreData> list){

        StoreAdapter adapter = new StoreAdapter(list, false);
        binding.attractionsList.setLayoutManager(new GridLayoutManager(activity, 2));
        binding.attractionsList.setAdapter(adapter);

        adapter.setOnFavItemListener(new OnFavItemListener() {
            @Override
            public void onItemClick(int pos) {
                selectPos = pos;
                showStoreInfo(activity,adapter.getList().get(pos));
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

        binding.attractionsList.setVisibility(View.VISIBLE);

    }

    private void showPop(){
        PopOtherBinding otherBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                ,R.layout.pop_other
                ,null
                ,false
        );

        otherBinding.list.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CheckAdapter adapter = new CheckAdapter(categoryList);
        otherBinding.list.setAdapter(adapter);
        otherBinding.list.setItemViewCacheSize(categoryList.size());
        otherBinding.list.setNestedScrollingEnabled(false);

        adapter.setOnAdapterItemListener(new OnAdapterItemListener() {
            @Override
            public void onItemClick(int pos) {
                adapter.getList().get(pos).setSelect(!adapter.getList().get(pos).isSelect());
                adapter.notifyItemChanged(pos);
            }
        });

        otherBinding.locationList.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CheckAdapter locationAdapter = new CheckAdapter(city);
        otherBinding.locationList.setAdapter(locationAdapter);
        otherBinding.locationList.setItemViewCacheSize(city.size());
        otherBinding.locationList.setNestedScrollingEnabled(false);

        locationAdapter.setOnAdapterItemListener(new OnAdapterItemListener() {
            @Override
            public void onItemClick(int pos) {
                locationAdapter.getList().get(pos).setSelect(!locationAdapter.getList().get(pos).isSelect());
                locationAdapter.notifyItemChanged(pos);
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)(dm.widthPixels / 3 * 2.3);
        int height = (int)(dm.heightPixels / 3 * 2);

        if (binding.getRoot().getHeight()<height){
            height = binding.getRoot().getHeight();
        }

        final PopupWindow popupWindow = new PopupWindow(
                otherBinding.getRoot()
                ,width
                , height
        );
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(binding.category, -width + binding.category.getWidth(), 10);

        otherBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                if (binding.ticket.isChecked()){
                    binding.group.clearCheck();
                    binding.ticket.toggle();
                }else if (binding.store.isChecked()){
                    binding.group.clearCheck();
                    binding.store.toggle();
                }else if (binding.attractions.isChecked()){
                    binding.group.clearCheck();
                    binding.attractions.toggle();
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
                                            ((StoreAdapter) adapter).remove(pos);
                                        }else if (adapter instanceof CouponAdapter){
                                            ((CouponAdapter) adapter).remove(pos);
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

    public void removeFav(){
        if (binding.ticket.isChecked()){
            ((CouponAdapter) binding.ticketList.getAdapter()).getList().remove(selectPos);
            ((CouponAdapter) binding.ticketList.getAdapter()).notifyItemRemoved(selectPos);
        }else if (binding.store.isChecked()){
            ((StoreAdapter) binding.storeList.getAdapter()).getList().remove(selectPos);
            ((StoreAdapter) binding.storeList.getAdapter()).notifyItemRemoved(selectPos);
        }else if (binding.attractions.isChecked()){
            ((StoreAdapter) binding.attractionsList.getAdapter()).getList().remove(selectPos);
            ((StoreAdapter) binding.attractionsList.getAdapter()).notifyItemRemoved(selectPos);
        }
    }

    public void addCoupon(CouponData data){
        if (binding.ticket.isChecked()){
            ((CouponAdapter) binding.ticketList.getAdapter()).getList().add(data);
            ((CouponAdapter) binding.ticketList.getAdapter()).notifyItemInserted(0);
            ((CouponAdapter) binding.ticketList.getAdapter()).notifyDataSetChanged();
        }
    }

    public void addStore(StoreData data){
        if (binding.store.isChecked()){
            ((StoreAdapter) binding.ticketList.getAdapter()).getList().add(data);
            ((StoreAdapter) binding.ticketList.getAdapter()).notifyItemInserted(0);
            ((StoreAdapter) binding.ticketList.getAdapter()).notifyDataSetChanged();
        }else if (binding.attractions.isChecked()){
            ((StoreAdapter) binding.attractionsList.getAdapter()).getList().add(data);
            ((StoreAdapter) binding.attractionsList.getAdapter()).notifyItemInserted(0);
            ((StoreAdapter) binding.attractionsList.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.category:
                showPop();
                break;
            case R.id.scan:
                Intent goScan = new Intent();
                goScan.setClass(activity, QrcodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Parameter.QRCODE_TYPE, Parameter.QRCODE_FAVORITE);
                goScan.putExtras(bundle);
                activity.startActivity(goScan);
                break;
        }
    }
}
