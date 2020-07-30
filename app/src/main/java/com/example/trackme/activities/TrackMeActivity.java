package com.example.trackme.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.trackme.R;
import com.example.trackme.database.Record;
import com.example.trackme.database.RecordDetail;
import com.example.trackme.services.LocationTrackService;
import com.example.trackme.services.TrackMeService;
import com.example.trackme.utils.Constants;
import com.example.trackme.utils.DateUtils;
import com.example.trackme.utils.FileUtil;
import com.example.trackme.utils.PermissionUtils;
import com.example.trackme.viewmodel.TrackMeViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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

public class TrackMeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {
    private static final int REQUEST_LOCATION_PERMISSION_CODE = 0x99;
    private static final int STREET_VIEW_ZOOM = 15;
    private final static int INTERVAL_TIME = 10 * 1000; //10 seconds
    private final static int FAST_INTERVAL_TIME = 5 * 1000;// 5 seconds
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;

    private String mRecordId;

    private GoogleMap mGoogleMap;

    private Intent trackService;
    private TrackMeViewModel mTrackMe; //viewmodel

    int polylineNo = -1;
    int routeState = RecordDetail.STATE_START;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        init();
        initLocation();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (PermissionUtils.isLocationPermissionGrant(this)) {
            startService();
        } else {
            PermissionUtils.prequestLocationPermission(this, REQUEST_LOCATION_PERMISSION_CODE);
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
        mTrackMe.getListDetail().observe(this, this::handlePointList);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackMap);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    private void startService() {
        routeState = RecordDetail.STATE_START;
        polylineNo++;
        getCurrentLocation();
        mGoogleMap.setMyLocationEnabled(true);
        trackService = new Intent(getApplication(), TrackMeService.class);
        trackService.putExtra(Constants.INTENT_KEY.SEASON_KEY, mRecordId);
        trackService.putExtra(Constants.INTENT_KEY.SEASON_SUB_KEY, polylineNo);

            startService(trackService);
//        LocationTrackService service = new LocationTrackService(this);
//        service.startTrackLocation(mRecordId,polylineNo);

    }

    private void stopService() {
        routeState = RecordDetail.STATE_END;
        getCurrentLocation();
        if (trackService != null) {
            stopService(trackService);
        }
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

    private void handlePointList(List<RecordDetail> list) {
        Log.e("TRACK_ME", "LIST ::" + list.size());
        if (list.size() == 0) {
            return;
        }
        int routeNo = list.get(0).getRouteNo();
        PolylineOptions options = new PolylineOptions();
        options.color(Color.BLUE);
        for (int i = 0; i < list.size(); i++) {
            RecordDetail detail = list.get(i);
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

        mGoogleMap.addPolyline(options);
        RecordDetail last = list.get(list.size() - 1);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(last.getRouteLat(), last.getRouteLng()), STREET_VIEW_ZOOM));
    }

    private void stopRecord() {
        mGoogleMap.snapshot(this);
        stopService();
        Record record = new Record();
        record.setRecordId(mRecordId);
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


    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL_TIME);
        locationRequest.setFastestInterval(FAST_INTERVAL_TIME);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mTrackMe.insert(mRecordId, locationResult.getLastLocation(), routeState, polylineNo);
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (PermissionUtils.isLocationPermissionGrant(this))
            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    mTrackMe.insert(mRecordId, location, routeState, polylineNo);
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });
        else {
            PermissionUtils.prequestLocationPermission(this, REQUEST_LOCATION_PERMISSION_CODE);
        }
    }

}
