package com.example.tomovico.stuffcollector.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tomovico.stuffcollector.data.StuffContract;

public class StuffDbHelper extends SQLiteOpenHelper {

    // String konstanta za database name
    public static final String DATABASE_NAME = "stuff.db";

    // String konstanta za database version
    public static final int DATABASE_VERSION = 1;

    // String konstanta za SQL izraz koji kreira tabelu u bazi
    private String SQL_CREATE = "CREATE TABLE "
            + StuffContract.StuffEntry.TABLE_NAME
            + " (" + StuffContract.StuffEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StuffContract.StuffEntry.COLUMN_STUFF_NAME + " TEXT NOT NULL, "
            + StuffContract.StuffEntry.COLUMN_STUFF_PRODUCER + " TEXT NOT NULL, "
            + StuffContract.StuffEntry.COLUMN_STUFF_CIJENA + " INTEGER NOT NULL, "
            + StuffContract.StuffEntry.COLUMN_STUFF_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
            + StuffContract.StuffEntry.COLUMN_STUFF_TYPE + " INTEGER NOT NULL DEFAULT 0, "
            //+ StuffContract.StuffEntry.COLUMN_STUFF_PICTURE + " " ne znam za sada kako sa slikom
            + StuffContract.StuffEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
            + StuffContract.StuffEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL, "
            + StuffContract.StuffEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL)";

    public StuffDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("StuffDbHelper", "Kreiram bazu!");
        Log.e("StuffDbHelper", "SQL = " + SQL_CREATE);
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StuffContract.StuffEntry.TABLE_NAME);
        onCreate(db);
    }
}
