package com.company.shougo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.Utils;
import com.company.shougo.data.CouponData;
import com.company.shougo.data.TokenData;
import com.company.shougo.databinding.ActivityMainBinding;
import com.company.shougo.fragment.main.FavoriteFragment;
import com.company.shougo.fragment.main.HomeFragment;
import com.company.shougo.fragment.main.MapFragment;
import com.company.shougo.fragment.main.MemberFragment;
import com.company.shougo.fragment.main.SearchMoreFragment;
import com.company.shougo.fragment.main.SearchResultFragment;
import com.company.shougo.fragment.main.SendFragment;
import com.company.shougo.fragment.main.TaxiFragment;
import com.company.shougo.listener.OnRefreshListener;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.CameraManager;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.GPSManager;
import com.company.shougo.mamager.PermissionManager;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.UserManager;
import com.company.shougo.type.ViewType;
import com.company.shougo.widget.InfoDetailBottom;
import com.company.shougo.widget.MyFragment;
import com.company.shougo.widget.teach.Teach1View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().setStatusBarColor(Color.BLACK);

        GPSManager.getInstance(this);

        UserManager.getInstance().setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onFail() {
                logout();
            }
        });

        uploadNotifyToken();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        binding.tabGroup.setOnCheckedChangeListener(tabListener);

        binding.home.toggle();

        if (!Utils.isGPSEnable(this)) {
            Utils.showGPSDialog(this);
        }
    }

    private final RadioGroup.OnCheckedChangeListener tabListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            switch (checkedId){
                case R.id.home:
                    changePage(R.id.mainFrame, new HomeFragment(), false);
                    break;
//                case R.id.taxi:
//                    changePage(R.id.mainFrame, new TaxiFragment(), false);
//                    break;
//                case R.id.send:
//                    changePage(R.id.mainFrame, new SendFragment(), false);
//                    break;
                case R.id.favorite:
                    changePage(R.id.mainFrame, new FavoriteFragment(), false);
                    break;
                case R.id.member:
                    changePage(R.id.mainFrame, new MemberFragment(), false);
                    break;
            }

        }
    };

    private void uploadNotifyToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();

                        Log.e(TAG, "token : " + token);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateToken(token);
                            }
                        });
                    }
                });
    }

    private void updateToken(String token){
        final String tag = "updateToken ";

        Execute.updateNotifyToken(
                token
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent getIntent = getIntent();

        if (getIntent!=null){
            Bundle bundle = getIntent.getExtras();
            if (bundle!=null) {

                int coupon_id = bundle.getInt(Parameter.NOTIFY_TO_COUPON);
                int vendor_id = bundle.getInt(Parameter.NOTIFY_TO_VENDOR);

                CouponData couponData = new CouponData();
                couponData.setCoupon_id(coupon_id);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if (getNowFragment()!=null) {
//                            ((MyFragment) getNowFragment()).showCouponInfo(
//                                    MainActivity.this
//                                    , couponData
//                            );
//                        }
                    }
                }, 700);
            }
        }
    }

    @Override
    public void onBackPressed() {

        MyFragment fragment = (MyFragment) getNowFragment();

        if (fragment instanceof MapFragment){
            ((MapFragment) fragment).isSearch = false;
        }

        if (fragment.isShowBottomSheet()){
            fragment.cancelBottomSheet();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        Log.e(TAG, resultCode + " | " + requestCode);

        if (resultCode==RESULT_OK){
            switch (requestCode){
                case Parameter.RESULT_CAMERA:
                    CameraManager.onCameraOk(this);
                    break;
                case Parameter.RESULT_ALBUM:
                    Uri uri = data.getData();
                    if (uri!=null) {
                        if (DocumentsContract.isDocumentUri(this, uri)) {
                            String docId = DocumentsContract.getDocumentId(uri);
                            Log.e("checkAlbumUri", String.valueOf(uri));
                            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                                String id = docId.split(":")[1];
                                String selection = MediaStore.Images.Media._ID+"="+id;
                                CameraManager.outputFilePath = Calculation.getImagePath(this,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                            }else if ("com.android.providers.media.downloads".equals(uri.getAuthority())){
                                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads.public_downloads"),
                                        Long.valueOf(docId));
                                CameraManager.outputFilePath = Calculation.getImagePath(this,contentUri,null);
                            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                                CameraManager.outputFilePath = uri.getPath();
                            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                                CameraManager.outputFilePath = uri.getPath();
                            }
                        }
                        CameraManager.onCameraOk(this);
                    }
                    break;
            }
        }

    }

    public void showTeach1(){
        cancelBottomSheet();
        Teach1View teach1View = new Teach1View(this);
        showBottomSheet(teach1View);
    }

    public void showBottomSheet(View view){
        binding.bottomDialog.addView(view);
    }

    public void cancelBottomSheet(){
        if (binding.bottomDialog.getChildCount()>0) {
            binding.bottomDialog.removeViewAt(binding.bottomDialog.getChildCount() - 1);
        }
    }

    public void logout(){

        TokenData tokenData = new TokenData();

        SaveManager.saveLogin(this, tokenData);

        Intent logout = new Intent();
        logout.setClass(this, LogoActivity.class);
        startActivity(logout);
        finish();
    }
}