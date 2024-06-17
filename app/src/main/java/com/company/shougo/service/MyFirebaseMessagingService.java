package com.company.shougo.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.company.shougo.Execute;
import com.company.shougo.Parameter;
import com.company.shougo.R;
import com.company.shougo.activity.LogoActivity;
import com.company.shougo.activity.MainActivity;
import com.company.shougo.data.NotifyData;
import com.company.shougo.db.NotifyDB;
import com.company.shougo.mamager.SaveManager;
import com.company.shougo.mamager.UserManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = "MyFirebaseMessagingService";

    private static int id = 2;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");

    @SuppressLint("WrongConstant")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage var1) {
        final String tag = "onMessageReceived ";

        Log.e(TAG, tag);

        if (var1.getData().size()>0){
            Log.e(TAG,tag + var1.getData());
            try{
                Map<String, String> params = var1.getData();
                JSONObject object = new JSONObject(params);

                String title = object.getString("title");
                String msg = object.getString("msg");
                int vendor_id = object.getInt("vendor_id");
                int coupon_id = object.getInt("coupon_id");
                String email = object.getString("email");

                NotifyData notifyData = new NotifyData();
                notifyData.setTitle(title);
                notifyData.setMsg(msg);
                notifyData.setVendor_id(vendor_id);
                notifyData.setCoupon_id(coupon_id);
                notifyData.setEmail(email);
                notifyData.setDateTime(sdf.format(new Date()));

                NotifyDB notifyDB = new NotifyDB(getApplicationContext());
                notifyDB.insert(notifyData);

                Intent talkIntent = new Intent(Parameter.GET_NOTIFY);
                sendBroadcast(talkIntent);

                if (!SaveManager.getNotify(getApplicationContext())){
                    return;
                }

                PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

                if (!powerManager.isInteractive()){ // if screen is not already on, turn it on (get wake_lock for 10 seconds)
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MH24_SCREENLOCK");
                    wl.acquire(10000);
                    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl_cpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MH24_SCREENLOCK");
                    wl_cpu.acquire(10000);
                }

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                Intent intent = new Intent(getApplicationContext(), LogoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(Parameter.NOTIFY_TO_VENDOR, notifyData.getVendor_id());
                bundle.putInt(Parameter.NOTIFY_TO_COUPON, notifyData.getCoupon_id());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pending = PendingIntent.getActivity(getApplicationContext(),id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification;
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    String CHANNEL_ID = "com.company.shougo";
                    NotificationChannel notificationChannel = new NotificationChannel(
                            CHANNEL_ID,"shougo notify",NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                    notificationChannel.enableVibration(true);
                    notificationChannel.enableLights(true);
                    notificationManager.createNotificationChannel(notificationChannel);

                    notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                            .setContentIntent(pending)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentTitle(title)
                            .setContentText(msg)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setCategory(Notification.CATEGORY_MESSAGE)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setContentIntent(pending)
//                    .setFullScreenIntent(pending,false)
                            .setAutoCancel(true)
                            .build();
                }else {
                    notification = new NotificationCompat.Builder(this)
                            .setContentIntent(pending)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setContentTitle(title)
                            .setContentText(msg)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                            .setVisibility(Notification.VISIBILITY_PUBLIC)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setCategory(Notification.CATEGORY_MESSAGE)
                            .setContentIntent(pending)
//                    .setFullScreenIntent(pending,false)
                            .setAutoCancel(true)
                            .build();
                }
                notificationManager.notify(id,notification);
                id+=1;
                if (id>1000) id = 2;
            }catch (Exception e){
                Log.e(TAG, tag + "Exception : " + e.toString());
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String var1) {

        final String tag = "onNewToken ";

        updateToken(var1);

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

}
