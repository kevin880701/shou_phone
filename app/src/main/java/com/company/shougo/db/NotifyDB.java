package com.company.shougo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.company.shougo.data.NotifyData;
import com.company.shougo.mamager.UserManager;

import java.util.ArrayList;
import java.util.List;

public class NotifyDB {
    public static final String TAG = "NotifyDB";

    public static final String TABLE_NAME = "notify_table";
    public static final String KEY_ID = "_id";
    public static final String READ = "read"; //0:未讀 1:已讀
    public static final String TITLE = "title";
    public static final String MSG = "msg";
    public static final String VENDOR_ID = "vendor_id";
    public static final String COUPON_ID = "coupon_id";
    public static final String EMAIL = "email";
    public static final String DATETIME = "dateTime";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EMAIL + " TEXT NOT NULL, "
                    + READ + " INTEGER NOT NULL, "
                    + VENDOR_ID + " INTEGER, "
                    + COUPON_ID + " INTEGER, "
                    + TITLE + " TEXT, "
                    + DATETIME + " TEXT, "
                    + MSG + " TEXT) ";

    private static SQLiteDatabase db;

    public NotifyDB(Context context){
        db = NotifyDBHelper.getDatabase(context);
    }

    public void insert(NotifyData notifyData){
        ContentValues cv = new ContentValues();
        cv.put(READ, notifyData.getRead());
        cv.put(EMAIL, notifyData.getEmail());
        cv.put(TITLE, notifyData.getTitle());
        cv.put(MSG, notifyData.getMsg());
        cv.put(VENDOR_ID, notifyData.getVendor_id());
        cv.put(COUPON_ID, notifyData.getCoupon_id());
        cv.put(DATETIME, notifyData.getDateTime());

        db.insert(TABLE_NAME, null, cv);
    }

    public void update(NotifyData notifyData){
        ContentValues cv = new ContentValues();
        cv.put(READ, notifyData.getRead());

        String where = KEY_ID + "=" + notifyData.getId();

        db.update(TABLE_NAME, cv, where, null);
    }

    public List<NotifyData> getNoReadByEmail(){

        List<NotifyData> list = new ArrayList<>();
        String where = EMAIL + "='" + UserManager.getInstance().getUserData().getEmail()
                + "' AND " + READ + "= 0";

        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null);

        while (cursor.moveToNext()){
            list.add(getRecord(cursor));
        }

        return list;
    }

    public List<NotifyData> getAllByEmail(){

        List<NotifyData> list = new ArrayList<>();
        String where = EMAIL + "='" + UserManager.getInstance().getUserData().getEmail() + "'";

        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null);

        while (cursor.moveToNext()){
            list.add(getRecord(cursor));
        }

        return list;
    }

    public NotifyData getRecord(Cursor cursor){
        NotifyData notifyData = new NotifyData();
        notifyData.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
        notifyData.setRead(cursor.getInt(cursor.getColumnIndex(READ)));
        notifyData.setVendor_id(cursor.getInt(cursor.getColumnIndex(VENDOR_ID)));
        notifyData.setCoupon_id(cursor.getInt(cursor.getColumnIndex(COUPON_ID)));
        notifyData.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
        notifyData.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
        notifyData.setMsg(cursor.getString(cursor.getColumnIndex(MSG)));
        notifyData.setDateTime(cursor.getString(cursor.getColumnIndex(DATETIME)));

        return notifyData;
    }

}
