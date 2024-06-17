package com.company.shougo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.company.shougo.data.NotifyData;

public class NotifyDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "NotifyDB";
    public static final int VERSION = 1;
    private static SQLiteDatabase database;

    public static SQLiteDatabase getDatabase(Context context){
        if (database==null || !database.isOpen()){
            database = new NotifyDBHelper(context, DATABASE_NAME, null, VERSION).getWritableDatabase();
        }

        return database;
    }

    public NotifyDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NotifyDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NotifyDB.TABLE_NAME);
        onCreate(db);
    }
}
