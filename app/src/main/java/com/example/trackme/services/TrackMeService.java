package com.example.trackme.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.location.LocationManagerCompat;

import com.example.trackme.database.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;
import com.example.trackme.utils.Constants;
import com.example.trackme.viewmodel.HistoryViewModel;

public class TrackMeService extends Service implements LocationListener {
    private static final String TAG = "RECORD_SERVICE";
    // Minumum distance update in meter
    private static final long MIN_DISTANCE_UPDATES = 10;
    // Minimum time in milisecond
    private static final long MIN_TIME_UPDATES = 10 * 1000;

    LocationManager mLocationManager;
    private RecordDetailRepository mRecordDetailRepository;
    private Location mLastLocation;
    private String mRecordId = "";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mLocationManager.removeUpdates(this);
                insertRecordDetail(mLastLocation);
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
        Log.e(TAG,"Location update Start");
        startRecord(intent.getStringExtra(Constants.INTENT_KEY.SEASON_KEY));
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG,"Location update ::" + location.getLongitude() + "::"+location.getLongitude() + "::" +location.getSpeed());
        mLastLocation = location;
        insertRecordDetail(mLastLocation);
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

    private void initServiec() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void startRecord(String recordId) {
        initServiec();
        mRecordId = recordId;
        if (isLocationEnable()) {
            try {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, this);
                mRecordDetailRepository = new RecordDetailRepository(getApplication());
                mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                insertRecordDetail(mLastLocation);
            } catch (java.lang.SecurityException e) {
                Log.i(TAG, "No permission");
            }
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
        mRecordDetailRepository.insert(recordDetail);
    }

    private boolean isLocationEnable() {
        return LocationManagerCompat.isLocationEnabled(mLocationManager);
    }
}
