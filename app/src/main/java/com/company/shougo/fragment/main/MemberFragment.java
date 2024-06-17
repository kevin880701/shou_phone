package com.company.shougo.fragment.main;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.Utils;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.activity.QrcodeActivity;
import com.company.shougo.adapter.TravelItemAdapter;
import com.company.shougo.data.TravelData;
import com.company.shougo.databinding.FragmentHomeBinding;
import com.company.shougo.databinding.FragmentMemberBinding;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.widget.MyFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MemberFragment extends MyFragment {

    private final static String TAG = "MemberFragment";

    private FragmentMemberBinding binding;

    private MainActivity activity;

    @Override
    public String getSimpleName() {
        return TAG;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup group, Bundle bundle) {
        binding = DataBindingUtil.inflate(
                inflater
                , R.layout.fragment_member
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

        binding.watch.setOnClickListener(this);
        binding.notify.setOnClickListener(this);
        binding.opinion.setOnClickListener(this);
        binding.privacy.setOnClickListener(this);
        binding.cooperation.setOnClickListener(this);
        binding.myCar.setOnClickListener(this);
        binding.removeAccount.setOnClickListener(this);
        binding.logout.setOnClickListener(this);

        setVersion();

        getWorkStore();
    }

    private void getWorkStore(){
        final String tag = "getWorkStore ";

        Execute.getWorkStore(
                new Callback() {
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
                                    if (data.length()>0){
                                        binding.cooperation.setVisibility(View.VISIBLE);
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
    public void onResume() {
        super.onResume();

        binding.name.setText(UserManager.getInstance().getUserData().getFirstname());

        Glide.with(activity)
                .load(UserManager.getInstance().getUserData().getImage())
                .apply(
                        RequestOptions.circleCropTransform()
                                .placeholder(R.drawable.icon_memberhoto)
                                .error(R.drawable.icon_memberhoto)
                )
                .into(binding.headImg);

    }

    private void setVersion(){
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            String version = pInfo.versionName;
            binding.version.setText(getResources().getString(R.string.version) + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void findCar(){
        final String tag = "findCar ";

        Execute.findCar(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,tag + "onFailure : " + e.toString());
                DialogManager.showConfirmDialog(
                        activity
                        , getResources().getString(R.string.fail)
                        , getResources().getString(R.string.find_no_car)
                        , null
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String data = response.body().string();
                Log.e(TAG,tag + "data : " + data);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONObject object = new JSONObject(data);

                            double lat = object.getDouble("lat");
                            double lng = object.getDouble("lng");


                            String url = "https://www.google.co.in/maps/dir";

                            url = url + "/" + GPSManager.getInstance(activity).getLat() + "," + GPSManager.getInstance(activity).getLng();

                            url = url + "/" + lat + "," + lng;

                            Intent intent = new Intent(
                                    Intent.ACTION_VIEW
                                    , Uri.parse(url));
                            intent.setPackage("com.google.android.apps.maps");
                            activity.startActivity(intent);

                        }catch (Exception e){
                            Log.e(TAG,tag + "Exception : " + e.toString());

                            DialogManager.showConfirmDialog(
                                    activity
                                    , getResources().getString(R.string.fail)
                                    , getResources().getString(R.string.find_no_car)
                                    , null
                            );
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.watch:
                activity.changePage(R.id.mainFrame, new MemberInfoFragment(), true);
                break;
            case R.id.notify:
                activity.changePage(R.id.mainFrame, new SetNotifyFragment(), true);
                break;
            case R.id.opinion:
                activity.changePage(R.id.mainFrame, new OpinionFragment(), true);
                break;
            case R.id.privacy:
                activity.changePage(R.id.mainFrame, new PrivacyFragment(), true);
                break;
            case R.id.cooperation:
                Intent goScan = new Intent();
                goScan.setClass(activity, QrcodeActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(Parameter.QRCODE_TYPE, Parameter.QRCODE_WORK);
                goScan.putExtras(bundle);
                activity.startActivity(goScan);
                break;
            case R.id.myCar:
                if (Utils.isGPSEnable(requireContext())) {
                    findCar();
                } else {
                    Utils.showGPSDialog(requireActivity());
                }
                break;
            case R.id.removeAccount:
                DialogManager.showConfirmDialog(
                        activity
                        , getResources().getString(R.string.remove_account)
                        , getResources().getString(R.string.sure_remove_account)
                        , v1 -> removeAccount()
                );
                break;
            case R.id.logout:
                activity.logout();
                break;
        }
    }

    private void removeAccount() {
        final String tag = "removeAccount";

        Execute.removeAccount(
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + " onFailure : " + e.toString());
                        DialogManager.showConfirmDialog(
                                activity
                                , getResources().getString(R.string.fail)
                                , getResources().getString(R.string.remove_fail)
                                , null
                        );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + " data : " + data);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (data.equals("true")) {
                                        activity.logout();
                                    } else {
                                        DialogManager.showConfirmDialog(
                                                activity
                                                , getResources().getString(R.string.fail)
                                                , getResources().getString(R.string.remove_fail)
                                                , null
                                        );
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, tag + " Exception : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            activity
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.remove_fail)
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
