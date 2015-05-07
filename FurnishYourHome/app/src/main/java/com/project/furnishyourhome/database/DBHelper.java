package com.project.furnishyourhome.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.project.furnishyourhome.interfaces.DbTableNames;

public class DBHelper extends SQLiteOpenHelper implements DbTableNames {
    protected static final String DB_NAME = "FurnishYourHome";
    protected static final int DB_VERSION = 1;
    protected static final String CREATE_STORE_TABLE = "create table " + TABLE_STORES + " (_id nvarchar2 not null primary key, name nvarchar2 not null, address nvarchar2, email nvarchar2, webpage nvarchar2, customersPhone nvarchar2, workingHours nvarchar2, logo blob, latitude nvarchar2, longitude nvarchar2);";
    protected static final String CREATE_FURNISH_TABLE = "create table " + TABLE_FURNITURES + " (_id nvarchar2 not null primary key, name nvarchar2 not null, material nvarchar2, info nvarchar2, dimensions nvarchar2, price nvarchar2, drawable blob, furnitureId nvarchar2);";
    protected static final String CREATE_TYPE_TABLE = "create table " + TABLE_TYPES + " (_id nvarchar2 not null primary key, type nvarchar2, icon blob);";
    protected static final String CREATE_STOREFURNISH_TABLE = "create table " + TABLE_STORESFURNITURES + " (_id nvarchar2 not null primary key, storeId nvarchar2, furnitureId nvarchar2);";

    protected SQLiteDatabase utilitiesDb;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STORE_TABLE);
        db.execSQL(CREATE_FURNISH_TABLE);
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_STOREFURNISH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FURNITURES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORESFURNITURES);
        onCreate(db);
    }

    public SQLiteDatabase open(){
        return getWritableDatabase();
    }

    /*public void close (){
        close();
    }*/
}
