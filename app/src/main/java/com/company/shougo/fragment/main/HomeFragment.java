package com.company.shougo.fragment.main;

import static com.company.shougo.mamager.DialogManager.cancelLoadDialog;
import static com.company.shougo.mamager.DialogManager.showLoadDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.CheckAdapter;
import com.company.shougo.adapter.StoreAdapter;
import com.company.shougo.data.CategoryData;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.GPSData;
import com.company.shougo.data.NotifyData;
import com.company.shougo.data.StoreData;
import com.company.shougo.data.UserData;
import com.company.shougo.databinding.FragmentHomeBinding;
import com.company.shougo.databinding.PopOtherBinding;
import com.company.shougo.db.NotifyDB;
import com.company.shougo.listener.OnAdapterItemListener;
import com.company.shougo.listener.OnFavItemListener;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.type.ViewType;
import com.company.shougo.widget.MyFragment;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends MyFragment {

    private final static String TAG = "HomeFragment";

    private FragmentHomeBinding binding;

    private MainActivity activity;

    private Timer getTimer;

    private List<CategoryData> categoryList;

    @Override
    public String getSimpleName() {
        return TAG;
    }
    private Handler handler;
    private ExecutorService executorService;
//    private boolean stopTask = false;
    private boolean firstGetReCommendStore = false;
    private boolean firstGetReCommendCoupon = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        if (isFirstCreate || binding==null) {
            binding = DataBindingUtil.inflate(
                    inflater
                    , R.layout.fragment_home
                    , group
                    , false
            );
        }

        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();
        return binding.getRoot();
    }

    @Override
    public void initView() {
        activity = (MainActivity) getActivity();
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        activity.getWindow().setStatusBarColor(Color.BLACK);

        backView = binding.bottomDialog;

        binding.topBar.setOnLocationClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.changePage(R.id.mainFrame, new AddrSearchFragment(), true);
            }
        });
//        showLoadDialog(activity);

        binding.search.setOnClickListener(this);

        binding.item1.setOnClickListener(this);
        binding.item2.setOnClickListener(this);
        binding.item3.setOnClickListener(this);
        binding.item4.setOnClickListener(this);
        binding.item5.setOnClickListener(this);
        binding.other.setOnClickListener(this);
        binding.moreDiscount.setOnClickListener(this);
        binding.moreAttractions.setOnClickListener(this);
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 刷新完成后停止刷新动画
                        binding.swipeRefreshLayout.setRefreshing(false);
                            getReCommendStore();
                            getReCommendCoupon();

                    }
                }, 1000); // 模拟2秒后刷新完成
                // 在这里执行刷新操作，例如从服务器获取最新数据

            }
        });
        if (!SaveManager.getHomeFirst(activity)){
            activity.showTeach1();
        }

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!(firstGetReCommendStore && firstGetReCommendCoupon)) {
                    // 执行重复的任务
                    GPSData gpsData = GPSManager.getInstance(activity).getAddress(activity);
                    GPSData oldGps = binding.topBar.getGpsData();
                    if (
                            oldGps!=null &&
                                    oldGps.getLat() != 0 &&
                                    oldGps.getLng() != 0
                    ) {
                                getReCommendStore();
                                getReCommendCoupon();
//                        cancelLoadDialog(activity);
                    }


                    // 休眠 delayMillis 毫秒
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getUserInfo(){
        final String tag = "getUserInfo ";

        Execute.userInfo(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.travelBtn.setFragment(HomeFragment.this);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " +data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    UserManager.getInstance().setUserData(
                                            new Gson().fromJson(
                                                    data
                                                    , UserData.class
                                            )
                                    );

                                    binding.topBar.setUser();

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }

                                binding.travelBtn.setFragment(HomeFragment.this);
                            }
                        });
                    }
                }
        );
    }

    private void setFastSearch(){

        final String tag = "setFastSearch ";

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

                            if (categoryList.get(0)!=null) {
                                binding.item1.init(categoryList.get(0));
                            }
                            if (categoryList.get(1)!=null) {
                                binding.item2.init(categoryList.get(1));
                            }
                            if (categoryList.get(2)!=null) {
                                binding.item3.init(categoryList.get(2));
                            }
                            if (categoryList.get(3)!=null) {
                                binding.item4.init(categoryList.get(3));
                            }
                            if (categoryList.get(4)!=null) {
                                binding.item5.init(categoryList.get(4));
                            }

                        }catch (Exception e){
                            Log.e(TAG, tag + "Exception : " + e.toString());
                        }
                    }
                });
            }
        });

        binding.other.init(getString(R.string.other), R.drawable.home_btn_other_default);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.topBar.setUser();
                }
            });
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(Parameter.GET_NOTIFY);
        activity.registerReceiver(receiver, filter);

        getUserInfo();

        setFastSearch();
        GPSManager.getInstance(activity).mLastLocation = null;
        GPSData gpsData = GPSManager.getInstance(activity).getAddress(activity);
        setLocation(gpsData);
    }

    @Override
    public void onStop() {
        super.onStop();

        activity.unregisterReceiver(receiver);
    }

    private void setLocation(final GPSData gpsData){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setLocation(GPSManager.getInstance(activity).getAddress(activity));

                GPSData oldGps = binding.topBar.getGpsData();
                if (
                        oldGps==null
                                || oldGps.getLat()!=gpsData.getLat()
                                || oldGps.getLng()!=gpsData.getLng()
                ) {
                    binding.topBar.setLocation(gpsData);
                }
            }
        }, 1000);
//            if (getTimer!=null){
//                getTimer.cancel();
//                getTimer = null;
//            }

//            getTimer = new Timer();
//            getTimer.schedule(new TimerTask() {
//                @Override
//                public void run() {
////                    GPSManager.getInstance(activity).initializeLocationManager(activity);
//                    setLocation(GPSManager.getInstance(activity).getAddress(activity));
//
//                    GPSData oldGps = binding.topBar.getGpsData();
//                    if (
//                            oldGps==null
//                                    || oldGps.getLat()!=gpsData.getLat()
//                                    || oldGps.getLng()!=gpsData.getLng()
//                    ) {
//                        binding.topBar.setLocation(gpsData);
//                    }
//                }
//            }, 1000);
//
//
//
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                GPSData oldGps = binding.topBar.getGpsData();
//                if (
//                        oldGps==null
//                        || oldGps.getLat()!=gpsData.getLat()
//                        || oldGps.getLng()!=gpsData.getLng()
//                ) {
//                    binding.topBar.setLocation(gpsData);
//                }
//            }
//        });
    }

    private void getReCommendStore(){
        ArrayList<String> logoList = new ArrayList<String>();

        final String tag = "getReCommendStore ";

        Execute.recommendStore(
                "10"
                , String.valueOf(binding.topBar.getGpsData().getLat())
                , String.valueOf(binding.topBar.getGpsData().getLng())
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
                                            // ----------------------------
                                            Execute.getStoreInfo(
                                                    String.valueOf(storeData.getVendor_id())
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

                                                                        if (data1.getDesc().get(0).getImage()!=null && data1.getDesc().get(0).getImage().length()>0) {
                                                                            logoList.add(data1.getDesc().get(0).getImage());
                                                                        }else{
                                                                            logoList.add(null);
                                                                        }
                                                                        firstGetReCommendStore = true;



                                                                    }catch (Exception e){
                                                                        Log.e(TAG, tag + "Exception : " + e.toString());
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                            );
                                            // ----------------------------
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

        if (list.size()<=0){
            binding.noAttractions.setVisibility(View.VISIBLE);
            binding.attractionsList.setVisibility(View.GONE);
            binding.moreAttractions.setVisibility(View.GONE);
        }else {
            binding.noAttractions.setVisibility(View.GONE);
            binding.attractionsList.setVisibility(View.VISIBLE);
            binding.moreAttractions.setVisibility(View.VISIBLE);
        }

        binding.attractionsList.setAdapter(null);

        StoreAdapter adapter = new StoreAdapter(list, true);

        binding.attractionsList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.attractionsList.setAdapter(adapter);

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
        List<StoreData> list = ((StoreAdapter) binding.attractionsList.getAdapter()).getList();

        for (int i=0;i<list.size();i++){
            if (list.get(i).getVendor_id()==storeData.getVendor_id()){
                ((StoreAdapter) binding.attractionsList.getAdapter()).update(i, storeData);
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
                ,"10"
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

                                    firstGetReCommendCoupon = true;

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

        if (list.size()<=0){
            binding.noDiscount.setVisibility(View.VISIBLE);
            binding.discountList.setVisibility(View.GONE);
            binding.moreDiscount.setVisibility(View.GONE);
        }else {
            binding.noDiscount.setVisibility(View.GONE);
            binding.discountList.setVisibility(View.VISIBLE);
            binding.moreDiscount.setVisibility(View.VISIBLE);
        }

        binding.discountList.setAdapter(null);

        CouponAdapter adapter = new CouponAdapter(list, true, false);

        binding.discountList.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        binding.discountList.setAdapter(adapter);

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
        List<CouponData> list = ((CouponAdapter) binding.discountList.getAdapter()).getList();

        for (int i=0;i<list.size();i++){
            if (list.get(i).getCoupon_id()==couponData.getCoupon_id()){
                ((CouponAdapter) binding.discountList.getAdapter()).update(i, couponData);
                return;
            }
        }

    }

    private void showPop(){
        PopOtherBinding otherBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext())
                ,R.layout.pop_other
                ,null
                ,false
        );

        otherBinding.locationTitle.setVisibility(View.GONE);
        otherBinding.locationList.setVisibility(View.GONE);

        otherBinding.list.setLayoutManager(new GridLayoutManager(getContext(), 3));
        CheckAdapter adapter = new CheckAdapter(categoryList);
        otherBinding.list.setAdapter(adapter);
        otherBinding.list.setItemViewCacheSize(categoryList.size());

        adapter.setOnAdapterItemListener(new OnAdapterItemListener() {
            @Override
            public void onItemClick(int pos) {
                adapter.getList().get(pos).setSelect(!adapter.getList().get(pos).isSelect());
                adapter.notifyItemChanged(pos);
            }
        });

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int)(dm.widthPixels / 3 * 2.3);

        final PopupWindow popupWindow = new PopupWindow(
                otherBinding.getRoot()
                ,width
                , ViewGroup.LayoutParams.WRAP_CONTENT
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
        popupWindow.showAsDropDown(binding.other, -width + binding.other.getWidth(), 10);

        otherBinding.confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                List<CategoryData> list = ((CheckAdapter) otherBinding.list.getAdapter()).getList();

                for (int i=list.size()-1;i>=0;i--){
                    if (!list.get(i).isSelect()){
                        list.remove(list.get(i));
                    }
                }

                activity.changePage(R.id.mainFrame, new SearchResultFragment(list), true);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search:
                activity.changePage(R.id.mainFrame, new KeySearchFragment(), true);
                break;
            case R.id.item1:
                List<CategoryData> item1 = new ArrayList<>();
                item1.add(categoryList.get(0));
                activity.changePage(R.id.mainFrame, new SearchResultFragment(item1), true);
                break;
            case R.id.item2:
                List<CategoryData> item2 = new ArrayList<>();
                item2.add(categoryList.get(1));
                activity.changePage(R.id.mainFrame, new SearchResultFragment(item2), true);
                break;
            case R.id.item3:
                List<CategoryData> item3 = new ArrayList<>();
                item3.add(categoryList.get(2));
                activity.changePage(R.id.mainFrame, new SearchResultFragment(item3), true);
                break;
            case R.id.item4:
                List<CategoryData> item4 = new ArrayList<>();
                item4.add(categoryList.get(3));
                activity.changePage(R.id.mainFrame, new SearchResultFragment(item4), true);
                break;
            case R.id.item5:
                List<CategoryData> item5 = new ArrayList<>();
                item5.add(categoryList.get(4));
                activity.changePage(R.id.mainFrame, new SearchResultFragment(item5), true);
                break;
            case R.id.other:
                showPop();
                break;
            case R.id.moreDiscount:
                activity.changePage(R.id.mainFrame, new MoreFragment(ViewType.DISCOUNT), true);
                break;
            case R.id.moreAttractions:
                activity.changePage(R.id.mainFrame, new MoreFragment(ViewType.STORE), true);
                break;
        }
    }
}
