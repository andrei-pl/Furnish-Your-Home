package com.project.furnishyourhome.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.project.furnishyourhome.R;
import com.project.furnishyourhome.models.parse.FurnitureParse;

import java.util.Timer;
import java.util.TimerTask;


public class DataCountService extends Service {
    private static final String TAG = DataCountService.class.getSimpleName();

    private final IBinder mBinder = new MyBinder();
    private Handler serviceHandler;
    private TaskUpdate taskUpdate;
    private int count;
    private boolean isUpdated;
    private ResultReceiver resultReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public DataCountService getService() {
            return DataCountService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, getResources().getString(R.string.app_id), getResources().getString(R.string.app_key));
        taskUpdate = new TaskUpdate();
        count = 0;
        isUpdated = false;
        Log.d(TAG,"StartService: counter");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        resultReceiver = intent.getParcelableExtra("receiver");
        serviceHandler = new Handler();
        serviceHandler.postDelayed(taskUpdate, 10000L);
        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                Thread thread = new Thread(new Task());
                thread.start();
            }
        }, 1000, 60000);

        return Service.START_NOT_STICKY;
    }

    class TaskUpdate implements Runnable {

        @Override
        public void run() {
            if (isUpdated) {
                Bundle bundle = new Bundle();
                bundle.putInt("count", count);
                Log.d(TAG, "count: "+count);
                isUpdated = false;
                //Log.d("dad", "dadad");
                resultReceiver.send(100, bundle);
            }
            serviceHandler.postDelayed(taskUpdate, 10000L);
        }

    }

    class Task implements Runnable {

        @Override
        public void run() {
            final ParseQuery<FurnitureParse> query = ParseQuery.getQuery(FurnitureParse.class);

            try {
                count = query.count();
                isUpdated = true;

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}