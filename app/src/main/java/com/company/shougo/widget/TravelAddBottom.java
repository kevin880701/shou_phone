package com.company.shougo.widget;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.company.shougo.Execute;
import com.company.shougo.R;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.adapter.TravelAdapter;
import com.company.shougo.adapter.TravelItemAdapter;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.FavoriteTravelData;
import com.company.shougo.data.GPSData;
import com.company.shougo.data.StoreData;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.BottomTravelAddBinding;
import com.company.shougo.db.MyTravelDB;
import com.company.shougo.fragment.main.FavoriteFragment;
import com.company.shougo.fragment.main.KeySearchFragment;
import com.company.shougo.listener.OnEditListener;
import com.company.shougo.listener.OnTravelListener;
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

public class TravelAddBottom extends RelativeLayout implements View.OnClickListener {

    private final static String TAG = "TravelAddBottom";

    private BottomTravelAddBinding binding;

    private MainActivity activity;

    private MyFragment myFragment;

    private FavoriteTravelData favoriteTravelData;

    private List<TravelData> list;

    public TravelAddBottom(Context context, MyFragment myFragment) {
        super(context);

        activity = (MainActivity) context;
        this.myFragment = myFragment;

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.bottom_travel_add
                , this
                , true
        );

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams params = binding.mainView.getLayoutParams();
        params.height = (int)(dm.heightPixels * 0.5);
        binding.mainView.setLayoutParams(params);

        binding.getRoot().setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.mainView.setOnClickListener(this);
        binding.saveTravel.setOnClickListener(this);
        binding.startTravel.setOnClickListener(this);

        setAdapter();
    }

    public TravelAddBottom(Context context, MyFragment myFragment, FavoriteTravelData favoriteTravelData) {
        super(context);

        activity = (MainActivity) context;
        this.myFragment = myFragment;
        this.favoriteTravelData = favoriteTravelData;

        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context)
                , R.layout.bottom_travel_add
                , this
                , true
        );

        binding.title.setText(favoriteTravelData.getName());
        binding.saveTravel.setVisibility(GONE);

        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        ViewGroup.LayoutParams params = binding.mainView.getLayoutParams();
        params.height = (int)(dm.heightPixels * 0.5);
        binding.mainView.setLayoutParams(params);

        binding.getRoot().setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.mainView.setOnClickListener(this);
        binding.saveTravel.setOnClickListener(this);
        binding.startTravel.setOnClickListener(this);

        getTravelDetail();
    }

    public void updateAdapter(){
        if (favoriteTravelData==null){
            setAdapter();
        }else {
            getTravelDetail();
        }
    }

    public void update(){
        final String tag = "updateTravel ";

        List<TravelData> list = new ArrayList<>();

        if (binding.list.getAdapter()!=null){
            list = ((TravelItemAdapter) binding.list.getAdapter()).getList();
        }

        if (
                favoriteTravelData==null
        ){

            myFragment.myTravelDB.deleteAll();

            for (int i=0;i<list.size();i++){
                list.get(i).setSort(i);
            }

            myFragment.myTravelDB.insertAll(list);

            return;
        }

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
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);
                    }
                }
        );
    }

    private void getTravelDetail(){
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
                                list = new ArrayList<>();
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

                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                }

                                setAdapter();
                            }
                        });
                    }
                }
        );

    }

    public void setAdapter(){
        binding.list.setAdapter(null);

        if (favoriteTravelData==null){
            list = myFragment.myTravelDB.getAllByEmail();
        }

        TravelItemAdapter adapter = new TravelItemAdapter(list);
        binding.list.setLayoutManager(new LinearLayoutManager(activity));
        binding.list.setAdapter(adapter);

        adapter.setOnTravelListener(new OnTravelListener() {
            @Override
            public void onClick(int pos) {

                if (adapter.getList().get(pos).getType()==0){
                    CouponData couponData = new CouponData();
                    couponData.setCoupon_id(adapter.getList().get(pos).getCoupon_id());
                    myFragment.showCouponInfo(activity, couponData);
                }else if (adapter.getList().get(pos).getType()==1){
                    StoreData storeData = new StoreData();
                    storeData.setVendor_id(adapter.getList().get(pos).getVendor_id());
                    myFragment.showStoreInfo(activity, storeData);
                }

            }

            @Override
            public void onEdit(int pos) {
                activity.changePage(R.id.mainFrame, new KeySearchFragment(), true);
            }

            @Override
            public void onDelete(int pos) {
                if (favoriteTravelData==null) {
                    myFragment.myTravelDB.deleteById(adapter.getList().get(pos).getId());
                }

                adapter.delete(pos);
            }
        });

        ItemTouchHelper.Callback callback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                //监控上下左右
                int swipFlag = 0;//ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                int dragflag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;

                return makeMovementFlags(dragflag,swipFlag);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                recyclerView.getAdapter().notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                //item移动后  也要移动数据
                Collections.swap(adapter.getList(),viewHolder.getAdapterPosition(),target.getAdapterPosition());

                recyclerView.getAdapter().notifyItemChanged(target.getAdapterPosition());
                recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());

//                TravelItemAdapter travelAdapter = (TravelItemAdapter) recyclerView.getAdapter();
//
//                if (favoriteTravelData==null) {
//                    myTravelDB.updateSort(
//                            travelAdapter.getList().get(viewHolder.getAdapterPosition())
//                            , travelAdapter.getList().get(target.getAdapterPosition())
//                    );
//                }

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //暂不处理  这个主要是做左右拖动的回调
            }

            @Override
            public boolean canDropOver(RecyclerView recyclerView, RecyclerView.ViewHolder current, RecyclerView.ViewHolder target) {
                //需要手动重写的方法,返回true
                //当前item可以被拖动到目标位置后,直接落到target上,后面的item也接着落
                return true;
            }

            @Override
            public boolean isLongPressDragEnabled() {
                //是否開啟長按拖動
                //返回true  可以實現長按拖動排序和拖動動畫
                return true;
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);

        itemTouchHelper.attachToRecyclerView(binding.list);

    }

    private void addTravel(String name){
        final String tag = "addTravel ";

        List<TravelData> list = ((TravelItemAdapter) binding.list.getAdapter()).getList();
        JSONObject object = new JSONObject();
        try {
            object.put("name", name);

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

        Execute.addTravel(
                object
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
                                binding.list.setAdapter(null);
                                myFragment.myTravelDB.deleteAll();

                                DialogManager.showConfirmDialog(
                                        activity
                                        , getResources().getString(R.string.success)
                                        , getResources().getString(R.string.success_favorite)
                                        , new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (myFragment instanceof FavoriteFragment){
                                                    myFragment.onResume();
                                                }

                                                activity.onBackPressed();
                                            }
                                        }
                                );
                            }
                        });
                    }
                }
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveTravel:

                if (((TravelItemAdapter) binding.list.getAdapter()).getList().size()<=0){
                    return;
                }

                DialogManager.showEditDialog(
                        activity
                        , getResources().getString(R.string.plz_give_travel_name)
                        , new OnEditListener() {
                            @Override
                            public void onEdit(String edit) {
                                if (edit.replace(" ", "").length()<=0){
                                    return;
                                }

                                addTravel(edit);

                            }
                        }
                );
                break;
            case R.id.startTravel:

                String url = "https://www.google.co.in/maps/dir/"
                        + GPSManager.getInstance(activity).getLat()
                        + ","
                        + GPSManager.getInstance(activity).getLng()
                        ;

                List<TravelData> list = ((TravelItemAdapter) binding.list.getAdapter()).getList();

                for (int i=0;i<list.size();i++){
                    url = url + "/" + list.get(i).getLat() + "," + list.get(i).getLng();
                }

                Intent intent = new Intent(
                        Intent.ACTION_VIEW
                        , Uri.parse(url));
                intent.setPackage("com.google.android.apps.maps");
                activity.startActivity(intent);
                break;
            case R.id.mainView:

                break;
            case R.id.back:
            default:
                activity.onBackPressed();
                break;
        }
    }
}
