package com.company.shougo.mamager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.company.shougo.data.TokenData;

@SuppressLint("ApplySharedPref")
public class SaveManager {

    private final static String FIRST_USE_APP = "com.company.shougo.first_use_app";

    public static void saveFirst(Context context){
        SharedPreferences preferences = context.getSharedPreferences(FIRST_USE_APP, Context.MODE_PRIVATE);

        preferences.edit()
                .putBoolean(FIRST_USE_APP, false)
                .commit();
    }

    public static boolean getFirst(Context context){
        SharedPreferences preferences = context.getSharedPreferences(FIRST_USE_APP, Context.MODE_PRIVATE);

        return preferences.getBoolean(FIRST_USE_APP, true);
    }

    private final static String NOTIFY = "com.company.shougo.notify";

    public static void saveNotify(Context context, boolean isNotify){
        SharedPreferences preferences = context.getSharedPreferences(NOTIFY, Context.MODE_PRIVATE);

        preferences.edit()
                .putBoolean(NOTIFY, isNotify)
                .commit();
    }

    public static boolean getNotify(Context context){
        SharedPreferences preferences = context.getSharedPreferences(NOTIFY, Context.MODE_PRIVATE);

        return preferences.getBoolean(NOTIFY, true);
    }

    private final static String SEARCH_LOCATION = "com.company.shougo.search_location";

    public static void saveSearchLocation(Context context,String location){
        SharedPreferences preferences = context.getSharedPreferences(SEARCH_LOCATION, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(SEARCH_LOCATION, location)
                .commit();
    }

    public static String getSearchLocation(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SEARCH_LOCATION, Context.MODE_PRIVATE);

        return preferences.getString(SEARCH_LOCATION, "");
    }

    private final static String SEARCH = "com.company.shougo.search";

    public static void saveSearch(Context context,String location){
        SharedPreferences preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(SEARCH, location)
                .commit();
    }

    public static String getSearch(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SEARCH, Context.MODE_PRIVATE);

        return preferences.getString(SEARCH, "");
    }

    private final static String HOME_FIRST = "com.company.shougo.home_first";

    public static void saveHomeFirst(Context context,boolean isFirst){
        SharedPreferences preferences = context.getSharedPreferences(HOME_FIRST, Context.MODE_PRIVATE);

        preferences.edit()
                .putBoolean(HOME_FIRST, isFirst)
                .commit();
    }

    public static boolean getHomeFirst(Context context){
        SharedPreferences preferences = context.getSharedPreferences(HOME_FIRST, Context.MODE_PRIVATE);

        return preferences.getBoolean(SEARCH, true);
    }

    private final static String LOGIN = "com.company.shougo.login";
    private final static String LOGIN_TOKEN = "com.company.shougo.login_token";
    private final static String LOGIN_SEC = "com.company.shougo.login_sec";
    private final static String LOGIN_REFRESH = "com.company.shougo.login_refresh";
    private final static String LOGIN_TIME = "com.company.shougo.login_time";

    public static void saveLogin(Context context, TokenData tokenData){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(LOGIN_TOKEN, tokenData.getToken())
                .putInt(LOGIN_SEC, tokenData.getExpires_sec())
                .putString(LOGIN_REFRESH, tokenData.getRefresh_token())
                .putLong(LOGIN_TIME, tokenData.getGetTimeZone())
                .commit();
    }

    public static TokenData getLogin(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN, Context.MODE_PRIVATE);

        TokenData tokenData = new TokenData();
        tokenData.setToken(preferences.getString(LOGIN_TOKEN, null));
        tokenData.setExpires_sec(preferences.getInt(LOGIN_SEC, 0));
        tokenData.setRefresh_token(preferences.getString(LOGIN_REFRESH, null));
        tokenData.setGetTimeZone(preferences.getLong(LOGIN_TIME, 0));

        return tokenData;
    }

    private final static String LOGIN_REMEMBER = "com.company.shougo.login_remember";
    private final static String LOGIN_REMEMBER_EMAIL = "com.company.shougo.login_remember_email";
    private final static String LOGIN_REMEMBER_PWD = "com.company.shougo.login_remember_pwd";
    private final static String LOGIN_REMEMBER_IS = "com.company.shougo.login_remember_is";

    public static void saveLoginRemember(
            Context context
            , String email
            , String password
            , boolean isRemember
    ){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_REMEMBER, Context.MODE_PRIVATE);

        preferences.edit()
                .putString(LOGIN_REMEMBER_EMAIL, email)
                .putString(LOGIN_REMEMBER_PWD, password)
                .putBoolean(LOGIN_REMEMBER_IS, isRemember)
                .commit();
    }

    public static String getLoginRememberEmail(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_REMEMBER, Context.MODE_PRIVATE);

        return preferences.getString(LOGIN_REMEMBER_EMAIL, "");
    }

    public static String getLoginRememberPwd(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_REMEMBER, Context.MODE_PRIVATE);

        return preferences.getString(LOGIN_REMEMBER_PWD, "");
    }

    public static boolean getLoginRememberIs(Context context){
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_REMEMBER, Context.MODE_PRIVATE);

        return preferences.getBoolean(LOGIN_REMEMBER_IS, false);
    }
}
