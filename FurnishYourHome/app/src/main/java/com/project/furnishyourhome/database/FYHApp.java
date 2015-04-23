package com.project.furnishyourhome.database;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;


public class FYHApp extends Application {
    private SQLiteDatabase db = null;
    private UtilitiesDb utilitiesDb = null;

    public SQLiteDatabase getDB(){
        if(db == null){
            db = new DBHelper(getApplicationContext(), null, null, 0).open();
        }
        return db;
    }

    public UtilitiesDb getUtilitiesDb(){
        if(utilitiesDb == null){
            utilitiesDb = new UtilitiesDb(getDB());
        }
        return utilitiesDb;
    }
}
