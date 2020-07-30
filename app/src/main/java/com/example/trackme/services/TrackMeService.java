package com.example.trackme.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.location.LocationManagerCompat;

import com.example.trackme.database.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;
import com.example.trackme.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

public class TrackMeService extends Service {
    private static final String TAG = "RECORD_SERVICE";
    private static final int NOTIFY_CHANNEL = 1;
    private static final int COUNT_TIME = 1 * 1000; // 1 second
    // Minimum distance update in meter
    private static final long MIN_DISTANCE_UPDATES = 10;
    // Minimum time in millisecond
    private static final long MIN_TIME_UPDATES = 10 * 1000;
    public int counter = 0;
    private Location mLastLocation;

    private Timer timer;
    private TimerTask timerTask;


    LocationManager mLocationManager;
    private RecordDetailRepository mRecordDetailRepository;
    private String mRecordId = "";
    private int mRouteNo;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG,"onLocationChanged");
            if (location.distanceTo(mLastLocation) > MIN_DISTANCE_UPDATES){
                insertRecordDetail(location);
            }
            mLastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    @Override
    public void onCreate() {
        initService();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(locationListener);
                mRecordDetailRepository = null;
            } catch (SecurityException e) {
                Log.i(TAG, "No permission");
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mRecordId = intent.getStringExtra(Constants.INTENT_KEY.SEASON_KEY);
        mRouteNo = intent.getIntExtra(Constants.INTENT_KEY.SEASON_SUB_KEY, 0);
        startTimer();
        return START_STICKY;
    }



    @SuppressLint("MissingPermission")
    private void initService() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (mLocationManager != null) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, locationListener);
                mLastLocation  = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            mRecordDetailRepository = new RecordDetailRepository(getApplication());
        }
    }

    public void stopRecord() {
        this.onDestroy();
    }

    private void insertRecordDetail(Location location) {
        RecordDetail recordDetail = new RecordDetail();
        recordDetail.setRecordId(mRecordId);
        recordDetail.setRouteLat(location.getLatitude());
        recordDetail.setRouteLng(location.getLongitude());
        recordDetail.setTimeInMili(System.currentTimeMillis());
        recordDetail.setRouteNo(mRouteNo);
        recordDetail.setRouteState(RecordDetail.STATE_ROUTE);
        mRecordDetailRepository.insert(recordDetail);
    }

    private boolean isLocationEnable() {
        return LocationManagerCompat.isLocationEnabled(mLocationManager);
    }

    private void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                Log.i("Count", "=========  " + (counter++));
                if (counter%MIN_TIME_UPDATES == 0){
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, locationListener);
                }
            }
        };
        timer.schedule(timerTask, COUNT_TIME, COUNT_TIME);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
