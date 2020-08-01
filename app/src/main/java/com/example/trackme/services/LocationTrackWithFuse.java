package com.example.trackme.services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.trackme.model.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;
import com.example.trackme.utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class LocationTrackWithFuse extends Service {
    private static final String TAG = "RECORD_SERVICE";
    private static final int COUNT_TIME = 1000; // 1 second
    // Minimum distance update in meter
    private static final long MIN_DISTANCE_UPDATES = 10;
    // Minimum time in millisecond
    private static final long MIN_TIME_UPDATES = 10 * 1000;
    private static final long FAST_INTERVAL_TIME_UPDATES = 5 * 1000;
    private long counter = 0;
    private Location mLastLocation;
    private Timer timer;
    private RecordDetailRepository mRecordDetailRepository;
    private String mRecordId = "";
    private int mRouteNo;
    private int routeState = RecordDetail.STATE_START;
    private ObservableEmitter<Long> pressureObserver;
    private Observable<Long> pressureObservable;

    private FusedLocationProviderClient mFuseClient;
    private LocationRequest locationRequest;

    private Binder binder = new LocationTrackWithFuse.LocalBinder();
    private Handler handler = new Handler(msg -> {
        updateLocation();
        return true;
    });

    private LocationCallback locationListener = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                onLocationChanged(location);
            }
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
        if (mFuseClient != null) {
            try {
                mFuseClient.removeLocationUpdates(locationListener);
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
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(MIN_TIME_UPDATES);
            locationRequest.setFastestInterval(FAST_INTERVAL_TIME_UPDATES);
        }
        if (mFuseClient == null) {
            mFuseClient = LocationServices.getFusedLocationProviderClient(this);
            mRecordDetailRepository = new RecordDetailRepository(getApplication());
        }
    }

    @SuppressLint("MissingPermission")
    public void updateLocation() {
        mFuseClient.requestLocationUpdates(locationRequest, locationListener, Looper.myLooper());
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mFuseClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                mLastLocation = task.getResult();
                insertRecordDetail(mLastLocation, routeState);
            } else {
                updateLocation();
            }
        });
    }

    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged");
        if (mLastLocation == null) {
            routeState = RecordDetail.STATE_START;
            insertRecordDetail(location, routeState);
        } else {
            routeState = RecordDetail.STATE_ROUTE;
            Log.i(TAG, "onLocationChanged" + location.distanceTo(mLastLocation));
            if (location.distanceTo(mLastLocation) >= MIN_DISTANCE_UPDATES)
                insertRecordDetail(location, routeState);
        }
        mLastLocation = location;
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
        public LocationTrackWithFuse getService() {
            return LocationTrackWithFuse.this;
        }
    }
}
