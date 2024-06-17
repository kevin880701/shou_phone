package com.company.shougo.mamager;

import android.content.Context;
import android.util.Log;

import com.company.shougo.Execute;
import com.company.shougo.data.TokenData;
import com.company.shougo.data.UserData;
import com.company.shougo.listener.OnRefreshListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserManager {

    private final static String TAG = "UserManager";

    private static UserManager userManager;

    private TokenData tokenData;

    private UserData userData;

    private Timer refreshTimer;

    private OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;
    }

    public static UserManager getInstance(){
        if (userManager==null){
            userManager = new UserManager();
        }

        return userManager;
    }

    public boolean isLogin(){
        if (
                tokenData==null
                || tokenData.getToken()==null
                || tokenData.getToken().length()<=0
        ) {
            return false;
        }

        return true;
    }

    public TokenData getTokenData() {
        return tokenData;
    }

    public void setTokenDataGo(Context context,TokenData tokenData) {

        tokenData.setGetTimeZone(new Date().getTime());

        this.tokenData = tokenData;

        SaveManager.saveLogin(context, tokenData);

        waitRefresh(context);
    }

    public void setTokenData(TokenData tokenData) {
        this.tokenData = tokenData;
    }

    public void waitRefresh(Context context){

        if (!isLogin()){
            return;
        }

        long nowTime = new Date().getTime();

        Log.e(TAG,
                "nowTime : " + nowTime
                + " | getTime : " + tokenData.getGetTimeZone()
                + " | sec : " + tokenData.getExpires_sec()
                );

        if (nowTime>(tokenData.getGetTimeZone() + tokenData.getExpires_sec() * 800)){
            reFreshToken(context);
        }else {

            long lessTime = ((tokenData.getGetTimeZone() + tokenData.getExpires_sec() * 800) - nowTime);

            Log.e(TAG, "lessTime : " + lessTime);

            cancelTimer();

            refreshTimer = new Timer();
            refreshTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    reFreshToken(context);
                }
            }, lessTime);

        }

    }

    private void cancelTimer(){
        if (refreshTimer!=null){
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }

    public void reFreshToken(Context context){

        final String tag = "reFreshToken ";

        if (!isLogin()){
            return;
        }

        Execute.refreshToken(
                tokenData.getRefresh_token()
                , new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, tag + "onFailure : " + e.toString());
                        if (onRefreshListener!=null){
                            onRefreshListener.onFail();
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String data = response.body().string();
                        Log.e(TAG, tag + "data : " + data);

                        try{
                            tokenData = new Gson().fromJson(data, TokenData.class);

                            setTokenDataGo(context, tokenData);

                            if (isLogin() && onRefreshListener!=null){
                                onRefreshListener.onRefresh();
                            }else if (onRefreshListener!=null){
                                onRefreshListener.onFail();
                            }

                        }catch (Exception e){
                            Log.e(TAG, tag + "Exception : " + e.toString());
                            if (onRefreshListener!=null){
                                onRefreshListener.onFail();
                            }
                        }

                    }
                }
        );

    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }
}
