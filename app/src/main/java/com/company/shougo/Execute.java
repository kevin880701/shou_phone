package com.company.shougo;

import android.graphics.Bitmap;
import android.util.Log;

import com.company.shougo.data.CategoryData;
import com.company.shougo.mamager.Calculation;
import com.company.shougo.mamager.UserManager;

import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

public class Execute {

    private final static String TAG = "Execute";

    private static void connect(Request request, Callback callback){
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30,TimeUnit.SECONDS)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    private static void post(String api, RequestBody body, Callback callback){

        String token = "";

        if (UserManager.getInstance().isLogin()){
            token = UserManager.getInstance().getTokenData().getToken();
        }

        final Request request = new Request.Builder()
                .url(Parameter.API + api)
                .post(body)
                .addHeader("content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();

        if (!api.contains("UploadMemberFile")) {
            Log.e(TAG, "post : " + request.toString() + "\nbody : " + bodyToString(request));
        }

        connect(request,callback);

    }

    private static void post(String api, JSONObject object, Callback callback){

        String token = "";

        if (UserManager.getInstance().isLogin()){
            token = UserManager.getInstance().getTokenData().getToken();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json")
                , object.toString()
        );

        final Request request = new Request.Builder()
                .url(Parameter.API + api)
                .post(body)
                .addHeader("content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();

        if (!api.contains("UploadMemberFile")) {
            Log.e(TAG, "post : " + request.toString() + "\nbody : " + bodyToString(request));
        }

        connect(request,callback);

    }

    private static void put(String api, JSONObject object, Callback callback){

        String token = "";

        if (UserManager.getInstance().isLogin()){
            token = UserManager.getInstance().getTokenData().getToken();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json")
                , object.toString()
        );

        final Request request = new Request.Builder()
                .url(Parameter.API + api)
                .put(body)
                .addHeader("content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();

        if (!api.contains("UploadMemberFile")) {
            Log.e(TAG, "post : " + request.toString() + "\nbody : " + bodyToString(request));
        }

        connect(request,callback);

    }

    private static void delete(String api, JSONObject object, Callback callback){

        String token = "";

        if (UserManager.getInstance().isLogin()){
            token = UserManager.getInstance().getTokenData().getToken();
        }

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json")
                , object.toString()
        );

        final Request request = new Request.Builder()
                .url(Parameter.API + api)
                .delete(body)
                .addHeader("content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();

        if (!api.contains("UploadMemberFile")) {
            Log.e(TAG, "delete : " + request.toString() + "\nbody : " + bodyToString(request));
        }

        connect(request,callback);

    }

    private static void get(String api,Callback callback){

        String token = "";

        if (UserManager.getInstance().isLogin()){
            token = UserManager.getInstance().getTokenData().getToken();
        }
        Log.v("QQQQQQQQQQQQQ","" + token);

        final Request request = new Request.Builder()
                .url(Parameter.API + api)
                .get()
                .addHeader("content-Type", "application/json")
                .addHeader("Authorization", token)
                .build();

        Log.e(TAG,"get : " + request.toString() + "\nbody : " + bodyToString(request));

        connect(request,callback);

    }

    private static String bodyToString(final Request request){
        try{
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            String s = buffer.readUtf8() + "\n\n";
            if (s.contains("image/jpeg")){
                s = "is upload image";
            }
            return s;
        }catch (Exception e){
            Log.e(TAG,"bodyToString : " + e.toString());
            return "did not work";
        }
    }

    public static void register(
            String name
            , String email
            , String password
            , Callback callback
    ){
        String api = "/v1/user/add";

        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("email", email);
            object.put("password", password);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void refreshToken(
            String refresh_token
            , Callback callback
    ){
        String api = "/v1/user/token/refresh";

        JSONObject object = new JSONObject();
        try {
            object.put("refresh_token", refresh_token);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void removeAccount(
            Callback callback
    ){
        String api = "/v1/user2/User";

        JSONObject object = new JSONObject();
        delete(api, object, callback);
    }

    public static void login(
            String email
            , String password
            , Callback callback
    ){
        String api = "/v1/user2/token";

        JSONObject object = new JSONObject();
        try {
            object.put("email", email);
            object.put("password", password);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void category(
            Callback callback
    ){
        String api = "/v1/search/category/0";

        get(api, callback);
    }

    public static void userInfo(
            Callback callback
    ){
        String api = "/v1/user/info";

        get(api, callback);
    }

    public static void recommendStoreAddr(
            String limit
            , String address
            , Callback callback
    ){
        String api = "/v1/search/store/recommend/addr/" + limit + "?address=" + address;

        get(api, callback);
    }

    public static void recommendStore(
            String limit
            , String lat
            , String lng
            , Callback callback
    ){
        String api = "/v1/search/store/recommend/" + limit + "?lat=" + lat + "&lng=" + lng;

        get(api, callback);
    }

    public static void recommendCoupon(
            String lat
            , String lng
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/coupon/recommend/near/" + limit + "?lat=" + lat + "&lng=" + lng;

        get(api, callback);
    }

    public static void favoriteStore(
            Callback callback
    ){
        String api = "/v1/favorites/store";

        get(api, callback);
    }

    public static void favoriteCoupon(
            Callback callback
    ){
        String api = "/v1/favorites/coupon";

        get(api, callback);
    }

    public static void addFavorite(
            String id
            , String type
            , Callback callback
    ){
        String api = "/v1/favorites/add";

        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("type", type);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void delFavorite(
            String id
            , String type
            , Callback callback
    ){
        String api = "/v1/favorites/del";

        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("type", type);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        delete(api, object, callback);
    }

    public static void getStoreComment(
            String store_id
            , Callback callback
    ){
        String api = "/v1/comment/store?store_id=" + store_id;

        get(api, callback);
    }

    public static void getUserComment(
            String store_id
            , Callback callback
    ){
        String api = "/v1/comment/store/user?store_id=" + store_id;

        get(api, callback);
    }

    public static void addComment(
            String store_id
            , String star
            , String comment
            , Callback callback
    ){
        String api = "/v1/comment/store";

        JSONObject object = new JSONObject();
        try {
            object.put("vendor_id", store_id);
            object.put("star", star);
            object.put("comments", comment);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void getStoreInfo(
            String storeId
            , String lat
            , String lng
            , Callback callback
    ){
        String api = "/v1/search/store/dtl_desc?id=" + storeId + "&lat=" + lat + "&lng=" + lng;

        get(api, callback);
    }

    public static void getStoreCoupon(
            String storeId
            , Callback callback
    ){
        String api = "/v1/search/store/coupon?id=" + storeId;

        get(api, callback);
    }

    public static void getNearTicket(
            String limit
            , String address
            , Callback callback
    ){
        String api = "/v1/search/coupon/near/addr/" + limit + "?address=" + address;

        get(api, callback);
    }

    public static void getStoreByKey(
            String search
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/sat/name/" + limit + "?search=" + search;

        get(api, callback);

    }

    public static void getNearStoreByKey(
            String search
            , String lat
            , String lng
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/near/name/"+limit+"?search="+search+"&lat="+lat+"&lng="+lng;

        get(api, callback);
    }

    public static void getHotTicketByKey(
            String search
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/hot/name/"+limit+"?search="+search;

        get(api, callback);
    }

    public static void getCouponInfo(
            String id
            , Callback callback
    ){
        String api = "/v1/search/coupon/dtl?id=" + id;

        get(api, callback);
    }

    public static void getCity(
            Callback callback
    ){
        String api = "/v1/search/city";

        get(api, callback);
    }

    public static void getBestStoreByCategory(
            List<CategoryData> list
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/sat/category/" + limit + "?";

        for (int i=0;i<list.size();i++){
            if (i!=0){
                api = api + "&";
            }

            api = api + "category_id=" + list.get(i).getCategory_id();
        }

        get(api,callback);

    }

    public static void getNearStoreByCategory(
            List<CategoryData> list
            , String lat
            , String lng
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/near/category/" + limit + "?";

        for (int i=0;i<list.size();i++){
            if (i!=0){
                api = api + "&";
            }

            api = api + "category_id=" + list.get(i).getCategory_id();
        }

        api = api + "&lat=" + lat + "&lng=" + lng;

        get(api,callback);

    }

    public static void getHotTicketByCategory(
            List<CategoryData> list
            , String limit
            , Callback callback
    ){
        String api = "/v1/search/hot/category/" + limit + "?";

        for (int i=0;i<list.size();i++){
            if (i!=0){
                api = api + "&";
            }

            api = api + "category_id=" + list.get(i).getCategory_id();
        }

        get(api,callback);

    }

    public static void addTravel(
            JSONObject travel
            , Callback callback
    ){
        String api = "/v1/favorites/waypoint/add";

        post(api, travel, callback);
    }

    public static void getTravelList(
            Callback callback
    ){
        String api = "/v1/favorites/waypoint";

        get(api,callback);
    }

    public static void getTravelDetail(
            String waypoint_id
            , String lat
            , String lng
            , Callback callback
    ){
        String api = "/v1/favorites/waypoint/dtl?waypoint_id=" + waypoint_id + "&lat=" + lat + "&lng=" + lng;

        get(api, callback);
    }

    public static void updateTravel(
            JSONObject travel
            , Callback callback
    ){
        String api = "/v1/favorites/waypoint/upd";

        put(api, travel, callback);
    }

    public static void deleteTravel(
            String waypoint_id
            , Callback callback
    ){
        String api = "/v1/favorites/waypoint/del";

        JSONObject object = new JSONObject();
        try {
            object.put("waypoint_id", waypoint_id);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        delete(api, object, callback);

    }

    public static void updateNotifyToken(
            String token
            , Callback callback
    ){
        String api = "/v1/user/upd/firebase_token";

        JSONObject object = new JSONObject();
        try {
            object.put("firebase_token", token);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);

    }

    public static void testNotify(
            Callback callback
    ){
        String api = "/v1/user/test/firebase";

        get(api, callback);
    }

    public static void uploadImage(
            String path,
            Callback callback
    ){
        String api = "/v1/user/upd/image";

        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);

        File file = new File(Calculation.compress(path));

        if (file != null) {
            body.addFormDataPart("file", "img.jpg", RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
        RequestBody formBody = body.build();

        post(api, formBody, callback);

    }

    public static void uploadInfo(
            String name
            , String email
            , Callback callback
    ){
        String api = "/v1/user/upd";

        JSONObject object = new JSONObject();
        try {
            object.put("name", name);
            object.put("email", email);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        put(api, object, callback);
    }

    public static void getFeedbackCategory(
            Callback callback
    ){
        String api = "/v1/feedback/category";

        get(api, callback);
    }

    public static void addFeedback(
            String feedback_category_id
            , String comment
            , Callback callback
    ){
        String api = "/v1/feedback";

        JSONObject object = new JSONObject();
        try {
            object.put("feedback_category_id", feedback_category_id);
            object.put("comment", comment);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void getWorkStore(
            Callback callback
    ){
        String api = "/v1/user/customer";

        get(api, callback);
    }

    public static void carLogin(
            String device_id
            , Callback callback
    ){
        String api = "/v1/user/qrlogin";

        JSONObject object = new JSONObject();
        try {
            object.put("device_id", device_id);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

    public static void ticketUse(
            String code
            , String token
            , Callback callback
    ){
        String api = "/v1/search/coupon/use";

        JSONObject object = new JSONObject();
        try {
            object.put("code", code);
            object.put("use_token", token);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);

    }

    public static void findCar(
            Callback callback
    ){
        String api = "/v1/user/findcar";

        get(api, callback);
    }



    public static void changePassword(
            String old_password
            , String new_password
            , String confirm_new_password
            , Callback callback
    ){
        String api = "/v1/user/upd/pwd";

        JSONObject object = new JSONObject();
        try {
            object.put("old_password", old_password);
            object.put("new_password", new_password);
            object.put("confirm_new_password", confirm_new_password);
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        post(api, object, callback);
    }

}
