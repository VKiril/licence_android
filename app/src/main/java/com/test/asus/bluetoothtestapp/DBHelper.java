package com.test.asus.bluetoothtestapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by asus on 01.06.2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "licence_database.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LOG_TAG", "--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table mytable ("
                + "id integer primary key autoincrement,"
                + "first_name text,"
                + "last_name text,"
                + "birth_day text,"
                + "user_name text,"
                + "body_mass int,"
                + "password text,"+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
