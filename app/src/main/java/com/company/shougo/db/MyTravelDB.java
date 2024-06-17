package com.company.shougo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.company.shougo.data.TravelData;
import com.company.shougo.listener.OnTravelChangeListener;
import com.company.shougo.mamager.UserManager;

import java.util.ArrayList;
import java.util.List;

public class MyTravelDB {

    public static final String TAG = "MyTravelDB";

    public static final String TABLE_NAME = "my_travel";
    public static final String KEY_ID = "_id";
    public static final String EMAIL = "email";
    public static final String TYPE = "type"; //0:優惠券 1:店家 2:特約停車場 3:自訂地址
    public static final String VENDOR_ID = "vendor_id";
    public static final String STORE_NAME = "store_name";
    public static final String STORE_LOGO = "store_logo";
    public static final String COUPON_ID = "coupon_id";
    public static final String COUPON_NAME = "coupon_name";
    public static final String COUPON_LOGO = "coupon_logo";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String ADDRESS = "address";
    public static final String CUSTOM_ADDRESS = "custom_address";
    public static final String PARKING_ADDRESS = "parking_address";
    public static final String DISTANCE = "distance";
    public static final String SORT = "sort";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EMAIL + " TEXT NOT NULL, "
                    + TYPE + " TEXT NOT NULL, "
                    + VENDOR_ID + " TEXT, "
                    + STORE_NAME + " TEXT, "
                    + STORE_LOGO + " TEXT, "
                    + COUPON_ID + " TEXT, "
                    + COUPON_NAME + " TEXT, "
                    + COUPON_LOGO + " TEXT, "
                    + LAT + " TEXT NOT NULL, "
                    + LNG + " TEXT NOT NULL, "
                    + ADDRESS + " TEXT, "
                    + CUSTOM_ADDRESS + " TEXT, "
                    + PARKING_ADDRESS + " TEXT, "
                    + SORT + " INTEGER NOT NULL, "
                    + DISTANCE + " TEXT NOT NULL) ";

    private OnTravelChangeListener onTravelChangeListener;

    public void setOnTravelChangeListener(OnTravelChangeListener onTravelChangeListener){
        this.onTravelChangeListener = onTravelChangeListener;
    }

    private static SQLiteDatabase db;

    public MyTravelDB(Context context){
        db = MyTravelDBHelper.getDatabase(context);
    }

    public void insert(TravelData travelData){

        List<TravelData> list = getAllByEmail();
        int sort = list.size();

        ContentValues cv = new ContentValues();
        cv.put(EMAIL, UserManager.getInstance().getUserData().getEmail());
        cv.put(TYPE, travelData.getType());
        cv.put(VENDOR_ID, travelData.getVendor_id());
        cv.put(STORE_NAME, travelData.getVendor_name());
        cv.put(STORE_LOGO, travelData.getVendor_logo());
        cv.put(COUPON_ID, travelData.getCoupon_id());
        cv.put(COUPON_NAME, travelData.getCoupon_name());
        cv.put(COUPON_LOGO, travelData.getCoupon_logo());
        cv.put(LAT, travelData.getLat());
        cv.put(LNG, travelData.getLng());
        cv.put(ADDRESS, travelData.getAddress());
        cv.put(CUSTOM_ADDRESS, travelData.getCustom_address());
        cv.put(PARKING_ADDRESS, travelData.getParking_address());
        cv.put(DISTANCE, travelData.getDistance());
        cv.put(SORT, sort);

        db.insert(TABLE_NAME, null, cv);

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public void insertAll(List<TravelData> list){

        if (list.size()<=0){
            if (onTravelChangeListener!=null){
                onTravelChangeListener.onChange();
            }
            return;
        }

        String sql = "INSERT INTO " + TABLE_NAME + " ("
                + EMAIL + ","
                + TYPE + ","
                + VENDOR_ID + ","
                + STORE_NAME + ","
                + STORE_LOGO + ","
                + COUPON_ID + ","
                + COUPON_NAME + ","
                + COUPON_LOGO + ","
                + LAT + ","
                + LNG + ","
                + ADDRESS + ","
                + CUSTOM_ADDRESS + ","
                + PARKING_ADDRESS + ","
                + DISTANCE + ","
                + SORT + " ) VALUES ";

        for (int i=0;i<list.size();i++){
            sql = sql + "( '" + UserManager.getInstance().getUserData().getEmail() + "',"
                    + "'" + list.get(i).getType() + "',"
                    + "'" + list.get(i).getVendor_id() + "',"
                    + "'" + list.get(i).getVendor_name() + "',"
                    + "'" + list.get(i).getVendor_logo() + "',"
                    + "'" + list.get(i).getCoupon_id() + "',"
                    + "'" + list.get(i).getCoupon_name() + "',"
                    + "'" + list.get(i).getCoupon_logo() + "',"
                    + "'" + list.get(i).getLat() + "',"
                    + "'" + list.get(i).getLng() + "',"
                    + "'" + list.get(i).getAddress() + "',"
                    + "'" + list.get(i).getCustom_address() + "',"
                    + "'" + list.get(i).getParking_address() + "',"
                    + "'" + list.get(i).getDistance() + "',"
                    + list.get(i).getSort() + ") ";

            if (i<list.size()-1){
                sql = sql + ",";
            }

        }

        try {
            db.execSQL(sql);
        }catch (Exception e){
            Log.e(TAG, "insertAll : " + sql);
        }

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public void updateEmail(String oldEmail){
        ContentValues cv = new ContentValues();
        cv.put(EMAIL, UserManager.getInstance().getUserData().getEmail());

        String where = EMAIL + "='" + oldEmail + "'";

        db.update(TABLE_NAME, cv, where, null);

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public void updateSort(TravelData data1, TravelData data2){
        ContentValues cv = new ContentValues();
        cv.put(SORT, data2.getSort());

        String where = KEY_ID + "=" + data1.getId();

        db.update(TABLE_NAME, cv, where, null);

        ContentValues cv1 = new ContentValues();
        cv1.put(SORT, data1.getSort());

        String where1 = KEY_ID + "=" + data2.getId();

        db.update(TABLE_NAME, cv1, where1, null);

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public void deleteById(long keyId){
        String where = KEY_ID + "=" + keyId;

        db.delete(TABLE_NAME, where, null);

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public void deleteAll(){
        String where = EMAIL + "='" + UserManager.getInstance().getUserData().getEmail() + "'";

        db.delete(TABLE_NAME, where, null);

        if (onTravelChangeListener!=null){
            onTravelChangeListener.onChange();
        }
    }

    public List<TravelData> getAllByEmail(){

        List<TravelData> list = new ArrayList<>();
        if (
                UserManager.getInstance().getUserData()==null
                || UserManager.getInstance().getUserData().getEmail()==null
        ){
            return list;
        }

        String where = EMAIL + "='" + UserManager.getInstance().getUserData().getEmail() + "'";
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, SORT + " ASC");

        while (cursor.moveToNext()){
            list.add(getRecord(cursor));
        }

        return list;
    }

    public TravelData getRecord(Cursor cursor){
        TravelData travelData = new TravelData();
        travelData.setId(cursor.getLong(cursor.getColumnIndex(KEY_ID)));
        travelData.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(TYPE))));
        String vendorId = cursor.getString(cursor.getColumnIndex(VENDOR_ID));
        if (vendorId!=null && vendorId.length()>0){
            travelData.setVendor_id(Integer.parseInt(vendorId));
        }
        travelData.setVendor_name(cursor.getString(cursor.getColumnIndex(STORE_NAME)));
        travelData.setVendor_logo(cursor.getString(cursor.getColumnIndex(STORE_LOGO)));
        String couponId = cursor.getString(cursor.getColumnIndex(COUPON_ID));
        if (couponId!=null && couponId.length()>0){
            travelData.setCoupon_id(Integer.parseInt(couponId));
        }
        travelData.setCoupon_name(cursor.getString(cursor.getColumnIndex(COUPON_NAME)));
        travelData.setCoupon_logo(cursor.getString(cursor.getColumnIndex(COUPON_LOGO)));
        String lat = cursor.getString(cursor.getColumnIndex(LAT));
        if (lat!=null && lat.length()>0){
            travelData.setLat(Double.parseDouble(lat));
        }
        String lng = cursor.getString(cursor.getColumnIndex(LNG));
        if (lng!=null && lng.length()>0){
            travelData.setLng(Double.parseDouble(lng));
        }
        travelData.setAddress(cursor.getString(cursor.getColumnIndex(ADDRESS)));
        travelData.setCustom_address(cursor.getString(cursor.getColumnIndex(CUSTOM_ADDRESS)));
        travelData.setParking_address(cursor.getString(cursor.getColumnIndex(PARKING_ADDRESS)));
        String distance = cursor.getString(cursor.getColumnIndex(DISTANCE));
        if (distance!=null && distance.length()>0){
            travelData.setDistance(Double.parseDouble(distance));
        }
        travelData.setSort(cursor.getInt(cursor.getColumnIndex(SORT)));
        return travelData;
    }

}
