package com.example.trackme.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackme.R;
import com.example.trackme.model.Record;
import com.example.trackme.model.RecordDetail;
import com.example.trackme.services.LocationTrackService;
import com.example.trackme.utils.Constants;
import com.example.trackme.utils.DateUtils;
import com.example.trackme.utils.FileUtil;
import com.example.trackme.utils.PermissionUtils;
import com.example.trackme.utils.Utils;
import com.example.trackme.viewmodel.TrackMeViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

public class TrackMeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {
    private static final String TAG = "TrackMeActivity";
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x99;
    private static final int STREET_VIEW_ZOOM = 15;
    private String mRecordId;
    private GoogleMap mGoogleMap;
    private Intent trackService;
    private TrackMeViewModel mTrackMe; //viewmodel
    private Disposable disposable;
    private boolean mBound;
    private long totalTime = 0;

    int polylineNo = -1;
    int routeState = RecordDetail.STATE_START;

    private LocationTrackService locationTrackService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connect");
            locationTrackService = ((LocationTrackService.LocalBinder) service).getService();
            locationTrackService.startTimer();
            disposable = locationTrackService.observePressure().observeOn(AndroidSchedulers.mainThread()).subscribe(time ->
            {
                Log.i(TAG, "DURATION :: " + time);
                mTrackMe.getMapDuration().setValue(totalTime + time);
                mTrackMe.getMapSpeed().setValue(Utils.avgSpeedKmH(Utils.avgSpeed(mTrackMe.getMapDistance().getValue(),mTrackMe.getMapDuration().getValue())));
            });
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnect");
            mBound = false;
            locationTrackService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (PermissionUtils.isLocationPermissionGrant(this)) {
            startService();
        } else {
            PermissionUtils.requestLocationPermission(this, REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        FileUtil.writeImage(getApplicationContext(), bitmap, mRecordId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startService();
            } else {
                finish();
            }
        }
    }

    private void init() {

        mRecordId = getIntent().getStringExtra(Constants.INTENT_KEY.SEASON_KEY);
        if (TextUtils.isEmpty(mRecordId)) {
            mRecordId = DateUtils.formatDateToRecordId(new Date());
            updateTrackControl(TrackMeViewModel.STATUS_START_RECORD);
        } else {
            updateTrackControl(TrackMeViewModel.STATUS_REVIEW);
        }
        mTrackMe = new TrackMeViewModel(getApplication(), mRecordId);
        mTrackMe.getListDetail().observe(this, this::updatePointList);
        mTrackMe.getMapDuration().observe(this, this::updateDuration);
        mTrackMe.getMapSpeed().observe(this, this::updateSpeed);
        mTrackMe.getMapDistance().observe(this, this::updateDistance);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackMap);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void startService() {
        routeState = RecordDetail.STATE_START;
        polylineNo++;

        mGoogleMap.setMyLocationEnabled(true);
        trackService = new Intent(getApplication(), LocationTrackService.class);
        trackService.putExtra(Constants.INTENT_KEY.SEASON_KEY, mRecordId);
        trackService.putExtra(Constants.INTENT_KEY.SEASON_SUB_KEY, polylineNo);
        startService(trackService);
        bindService(trackService, serviceConnection, BIND_AUTO_CREATE);
        mBound = true;
    }

    private void stopService() {
        routeState = RecordDetail.STATE_END;
        if (disposable != null) {
            disposable.dispose();
        }
        if (locationTrackService != null) {
            locationTrackService.stopTimer();

        }
        if (trackService != null) {
            stopService(trackService);
        }
        if (mBound) {
            unbindService(serviceConnection);
            mBound = false;
        }
        if (mTrackMe.getMapDuration().getValue() != null)
            totalTime = mTrackMe.getMapDuration().getValue();
    }

    private void updateTrackControl(int status) {

        ImageButton btnPause = findViewById(R.id.btnTrackPause);
        ImageButton btnResume = findViewById(R.id.btnTrackResume);
        ImageButton btnStop = findViewById(R.id.btnTrackStop);
        btnPause.setOnClickListener(v -> pauseRecord());
        btnResume.setOnClickListener(v -> resumeRecord());
        btnStop.setOnClickListener(v -> stopRecord());

        switch (status) {
            case TrackMeViewModel.STATUS_START_RECORD:
                btnPause.setVisibility(View.VISIBLE);
                btnResume.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                break;
            case TrackMeViewModel.STATUS_PAUSE:
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                break;
            case TrackMeViewModel.STATUS_REVIEW:
                btnPause.setVisibility(View.GONE);
                btnResume.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                break;
        }
    }

    private void updatePointList(List<RecordDetail> list) {
        Log.e("TRACK_ME", "LIST ::" + list.size());
        if (list.size() == 0 || mGoogleMap == null) {
            return;
        }
        int routeNo = list.get(0).getRouteNo();
        float totalDistance = 0;
        PolylineOptions options = new PolylineOptions();
        options.color(Color.BLUE);
        for (int i = 0; i < list.size(); i++) {
            RecordDetail detail = list.get(i);
            if (list.size() > 2 && i < list.size() - 1 && list.get(i).getRouteNo() == list.get(i + 1).getRouteNo()) {
                totalDistance = Utils.distanceBetweenTwoPoint(list.get(i).getRouteLat(), list.get(i).getRouteLat(), list.get(i + 1).getRouteLat(), list.get(i + 1).getRouteLat());
            }
            if (detail.getRouteState() == RecordDetail.STATE_START) {
                createMaker(mGoogleMap, detail.getRouteLat(), detail.getRouteLng());
            }
            if (detail.getRouteNo() != routeNo) {
                mGoogleMap.addPolyline(options);
                routeNo = detail.getRouteNo();
                options = new PolylineOptions();
                options.color(Color.BLUE);
            }
            options.add(new LatLng(detail.getRouteLat(), detail.getRouteLng()));
        }
        mTrackMe.getMapDistance().setValue(totalDistance);
        mGoogleMap.addPolyline(options);
        RecordDetail last = list.get(list.size() - 1);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(last.getRouteLat(), last.getRouteLng()), STREET_VIEW_ZOOM));
    }

    private void updateDuration(long time) {
        TextView tvTrackDuration = findViewById(R.id.tvTrackDuration);
        tvTrackDuration.setText(DateUtils.convertMinToHH(time));
    }

    private void updateSpeed(float speed) {
        TextView tvTrackSpeed = findViewById(R.id.tvTrackSpeed);
        tvTrackSpeed.setText(String.format(getString(R.string.speed_format), speed));
    }

    private void updateDistance(float distance) {
        TextView tvTrackDistance = findViewById(R.id.tvTrackDistance);
        tvTrackDistance.setText(String.format(getString(R.string.distane_format), distance / 1000));
    }

    private void stopRecord() {
        mGoogleMap.snapshot(this);
        stopService();
        float distance = mTrackMe.getMapDistance().getValue() == null ? 0 : mTrackMe.getMapDistance().getValue();
        long duration = mTrackMe.getMapDuration().getValue() == null ? 0 : mTrackMe.getMapDuration().getValue();
        Record record = new Record();
        record.setRecordId(mRecordId);
        record.setDistance(distance);
        record.setDuration(duration);
        record.setAvgSpeed(Utils.avgSpeed(distance,duration));
        record.setInsertTime(System.currentTimeMillis());
        record.setMapImageName(mRecordId);
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_KEY.RECORD_RESULT_KEY, record);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void pauseRecord() {
        updateTrackControl(TrackMeViewModel.STATUS_PAUSE);
        stopService();
    }

    private void resumeRecord() {
        updateTrackControl(TrackMeViewModel.STATUS_START_RECORD);
        startService();
    }

    private void createMaker(GoogleMap map, double lat, double ln) {
        MarkerOptions options = new MarkerOptions();
        LatLng latLng = new LatLng(lat, ln);
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(polylineNo));
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, STREET_VIEW_ZOOM));
    }
}
