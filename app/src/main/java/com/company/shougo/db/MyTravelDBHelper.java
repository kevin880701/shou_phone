package com.company.shougo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyTravelDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyTravelDB";
    public static final int VERSION = 2;
    private static SQLiteDatabase database;

    public MyTravelDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context){
        if (database==null || !database.isOpen()){
            database = new MyTravelDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MyTravelDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MyTravelDB.TABLE_NAME);
        onCreate(db);
    }
}
