package com.example.trackme.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.trackme.model.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;
import com.example.trackme.utils.Constants;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class LocationTrackService extends Service {

    private static final String TAG = "RECORD_SERVICE";
    private static final int COUNT_TIME = 1000; // 1 second
    // Minimum distance update in meter
    private static final long MIN_DISTANCE_UPDATES = 10;
    // Minimum time in millisecond
    private static final long MIN_TIME_UPDATES = 1000;
    private long counter = 0;
    private Location mLastLocation;
    private Timer timer;
    private LocationManager mLocationManager;
    private RecordDetailRepository mRecordDetailRepository;
    private String mRecordId = "";
    private int mRouteNo;
    private int routeState = RecordDetail.STATE_START;

    private ObservableEmitter<Long> pressureObserver;
    private Observable<Long> pressureObservable;

    private Binder binder = new LocalBinder();
    private Handler handler = new Handler(msg -> {
        updateLocation();
        return true;
    });

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged");
            if (mLastLocation == null) {
                routeState = RecordDetail.STATE_START;
                insertRecordDetail(location, routeState);
            } else {
                routeState = RecordDetail.STATE_ROUTE;
                if (location.distanceTo(mLastLocation) >= MIN_DISTANCE_UPDATES)
                    insertRecordDetail(location, routeState);
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
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        mRecordId = intent.getStringExtra(Constants.INTENT_KEY.SEASON_KEY);
        mRouteNo = intent.getIntExtra(Constants.INTENT_KEY.SEASON_SUB_KEY, 0);
        getLastLocation();
        return START_NOT_STICKY;
    }

    private void initService() {
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mRecordDetailRepository = new RecordDetailRepository(getApplication());
        }
    }

    @SuppressLint("MissingPermission")
    public void updateLocation() {
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_UPDATES, MIN_DISTANCE_UPDATES, locationListener);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLastLocation != null) {
            insertRecordDetail(mLastLocation, routeState);
        } else {
            updateLocation();
        }
    }

    public Observable<Long> observePressure() {
        if (pressureObservable == null) {
            pressureObservable = Observable.create(emitter -> pressureObserver = emitter);
            pressureObservable = pressureObservable.share();
        }
        return pressureObservable;
    }

    private void insertRecordDetail(Location location, int state) {
        RecordDetail recordDetail = new RecordDetail();
        recordDetail.setRecordId(mRecordId);
        recordDetail.setRouteLat(location.getLatitude());
        recordDetail.setRouteLng(location.getLongitude());
        recordDetail.setTimeInMili(System.currentTimeMillis());
        recordDetail.setRouteNo(mRouteNo);
        recordDetail.setRouteState(state);
        mRecordDetailRepository.insert(recordDetail);
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                counter++;
                if (pressureObserver != null)
                    pressureObserver.onNext(counter);
                Log.i(TAG, "TIME ::" + counter);
                if (counter % 10 == 0) {
                    handler.sendEmptyMessage(0);
                }
            }
        }, COUNT_TIME, COUNT_TIME);
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public class LocalBinder extends Binder {
        public LocationTrackService getService() {
            return LocationTrackService.this;
        }
    }
}
