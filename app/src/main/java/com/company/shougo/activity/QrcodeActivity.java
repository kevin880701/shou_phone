package com.company.shougo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.adapter.CouponAdapter;
import com.company.shougo.adapter.StoreAdapter;
import com.company.shougo.databinding.ActivityQrcodeBinding;
import com.company.shougo.listener.OnPermissionListener;
import com.company.shougo.mamager.DialogManager;
import com.company.shougo.mamager.PermissionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class QrcodeActivity extends BaseActivity implements OnPermissionListener {

    private final static String TAG = "QrcodeActivity";

    private ActivityQrcodeBinding binding;
    private BeepManager beepManager;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        getWindow().setStatusBarColor(Color.BLACK);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_qrcode);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString(Parameter.QRCODE_TYPE);

        String title = "";
        String content = "";
        switch (type){
            case Parameter.QRCODE_FAVORITE:
                title = getResources().getString(R.string.scan_favorite);
                content = getResources().getString(R.string.scan_all);
                break;
            case Parameter.QRCODE_WORK:
                title = getResources().getString(R.string.scan_ticket);
                content = getResources().getString(R.string.scan_all);
                break;
            case Parameter.QRCODE_CAR:
                title = getResources().getString(R.string.scan_car);
                content = getResources().getString(R.string.scan_content_car);
                break;
        }
        binding.title.setText(title);
        binding.content.setText(content);

        PermissionManager.getInstance().setOnPermissionListener(this);

        PermissionManager.getInstance().checkPermission(
                this
                , new String[]{
                        Manifest.permission.CAMERA
                }
                , Parameter.PERMISSION_REQUEST_START);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addFavorite(String json){
        final String tag = "addFavorite";

        String id = "";
        String type = "";

        try{
            JSONObject object = new JSONObject(json);
            id = object.getString("id");
            type = object.getString("type");
        }catch (Exception e){
            Log.e(TAG, tag + "json err " + e.toString());
            DialogManager.showConfirmDialog(
                    QrcodeActivity.this
                    , getResources().getString(R.string.fail)
                    , getResources().getString(R.string.add_favorite_fail)
                    , new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.scanner.resume();
                        }
                    }
            );
            return;
        }

        Execute.addFavorite(
                id
                , type
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DialogManager.showConfirmDialog(
                                        QrcodeActivity.this
                                        , getResources().getString(R.string.fail)
                                        , getResources().getString(R.string.add_favorite_fail)
                                        , new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                binding.scanner.resume();
                                            }
                                        }
                                );
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        DialogManager.showConfirmDialog(
                                                QrcodeActivity.this
                                                , getResources().getString(R.string.success)
                                                , getResources().getString(R.string.add_favorite_success)
                                                , new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        binding.scanner.resume();
                                                    }
                                                }
                                        );

                                    }else {
                                        DialogManager.showConfirmDialog(
                                                QrcodeActivity.this
                                                , getResources().getString(R.string.fail)
                                                , getResources().getString(R.string.add_favorite_fail)
                                                , new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        binding.scanner.resume();
                                                    }
                                                }
                                        );

                                    }
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            QrcodeActivity.this
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.add_favorite_fail)
                                            , new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    binding.scanner.resume();
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

    @Override
    public void onBackPressed() {
        Log.e(TAG,"onBackPressed");
        binding.scanner.pause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(){
        beepManager = new BeepManager(this);

        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE);
        binding.scanner.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        binding.scanner.decodeContinuous(callback);
        binding.scanner.getStatusView().setText("");

        binding.scanner.resume();
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            String codeString = result.getText();

            Log.e(TAG,"getString : " + codeString);

            if (codeString==null || codeString.length()<=0){
                return;
            }

            beepManager.playBeepSoundAndVibrate();

            binding.scanner.pause();

            switch (type){
                case Parameter.QRCODE_CAR:
                    loginCar(codeString);
                    break;
                case Parameter.QRCODE_WORK:

                    if (!codeString.contains(Parameter.QRCODE_SPLITE)){

                        binding.fail.setVisibility(View.VISIBLE);
                        binding.success.setVisibility(View.GONE);
                        binding.scanner.resume();

                        return;
                    }

                    String[] code = codeString.split(Parameter.QRCODE_SPLITE);

                    Log.e(TAG, "getCode : " + code[0] + "/" + code[1]);

                    ticketUse(code[0], code[1]);
                    break;
                case Parameter.QRCODE_FAVORITE:
                    addFavorite(codeString);
                    break;
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private void loginCar(String code){
        final String tag = "loginCar ";

        Execute.carLogin(
                code
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        DialogManager.showConfirmDialog(
                                QrcodeActivity.this
                                , getResources().getString(R.string.fail)
                                , getResources().getString(R.string.login_car_fail)
                                , new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        binding.scanner.resume();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);
                        Log.e(TAG,"AAAAAAAAAAAAAAAA : " + data);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        DialogManager.showConfirmDialog(
                                                QrcodeActivity.this
                                                , getResources().getString(R.string.success)
                                                , getResources().getString(R.string.login_car_success)
                                                , new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        binding.scanner.resume();
                                                    }
                                                }
                                        );

                                    }else {
                                        DialogManager.showConfirmDialog(
                                                QrcodeActivity.this
                                                , getResources().getString(R.string.fail)
                                                , getResources().getString(R.string.login_car_fail)
                                                , new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        binding.scanner.resume();
                                                    }
                                                }
                                        );

                                    }
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    DialogManager.showConfirmDialog(
                                            QrcodeActivity.this
                                            , getResources().getString(R.string.fail)
                                            , getResources().getString(R.string.login_car_fail)
                                            , new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    binding.scanner.resume();
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

    private void ticketUse(String code, String token){
        final String tag = "ticketUse ";

        Execute.ticketUse(
                code
                , token
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.fail.setVisibility(View.VISIBLE);
                                binding.success.setVisibility(View.GONE);
                                binding.scanner.resume();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG,tag + "data : " + data);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    if (data.equals("true")){
                                        binding.fail.setVisibility(View.GONE);
                                        binding.success.setVisibility(View.VISIBLE);

                                    }else {
                                        binding.fail.setVisibility(View.VISIBLE);
                                        binding.success.setVisibility(View.GONE);

                                    }
                                }catch (Exception e){
                                    Log.e(TAG, tag + "Exception : " + e.toString());
                                    binding.fail.setVisibility(View.VISIBLE);
                                    binding.success.setVisibility(View.GONE);

                                }
                                binding.scanner.resume();
                            }
                        });

                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionRequest(boolean isSuccess, int requestCode) {
        if (requestCode==Parameter.PERMISSION_REQUEST_START){
            if (isSuccess){
                initView();
            }else {
                onBackPressed();
            }
        }
    }
}