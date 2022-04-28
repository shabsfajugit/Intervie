package com.interview.practicall.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.interview.practicall.model.ImagesModel;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "myinterview.db";
    public static final String TABLE_NAME = "items";
    public static final String TABLE_NAME_DOWNLOADED = "items_down";
    public static final String ICOL_1 = "ID";
    public static final String ICOL_2 = "image";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, image TEXT)");
        db.execSQL("create table " + TABLE_NAME_DOWNLOADED + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, image TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(ImagesModel rModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICOL_2, rModel.getUrl());
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            Cursor resw = getAllData();
            return true;
        }
    }

    public boolean insertDataInDownload(ImagesModel rModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ICOL_2, rModel.getUrl());
        long result = db.insert(TABLE_NAME_DOWNLOADED, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            Cursor resw = getAllData();
            return true;
        }
    }

    @SuppressLint("Range")
    private int getID(String pid, String cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[]{"PID"}, "PID =? AND cost =? ", new String[]{pid, cost}, null, null, null, null);
        if (c.moveToFirst()) //if the row exist then return the id
            return c.getInt(c.getColumnIndex("PID"));
        return -1;
    }

    @SuppressLint("Range")
    public int getCard(String pid, String cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, new String[]{"qty"}, "PID =? AND cost =? ", new String[]{pid, cost}, null, null, null, null);
        if (c.moveToFirst()) { //if the row exist then return the id
            return c.getInt(c.getColumnIndex("qty"));
        } else {
            return -1;
        }
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        return res;
    }

    public Cursor getAllDataDownloaded() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_DOWNLOADED, null);
        return res;
    }


    public void deleteCard() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public Integer deleteRData(String id, String cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer a = db.delete(TABLE_NAME, "PID = ? AND cost =?", new String[]{id, cost});
        Cursor res = getAllData();
        return a;
    }


}