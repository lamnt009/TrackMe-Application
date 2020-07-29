package com.example.trackme.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.trackme.R;
import com.example.trackme.database.Record;
import com.example.trackme.database.RecordDetail;
import com.example.trackme.services.TrackMeService;
import com.example.trackme.utils.Constants;
import com.example.trackme.utils.DateUtils;
import com.example.trackme.utils.FileUtil;
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
import java.util.Random;

public class TrackMeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.SnapshotReadyCallback {
    private static final int STREET_VIEW_ZOOM = 15;
    private String mRecordId;
    private GoogleMap mGoogleMap;
    private Intent trackService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        mRecordId = getIntent().getStringExtra(Constants.INTENT_KEY.SEASON_KEY);
        if (TextUtils.isEmpty(mRecordId)) {
            mRecordId = DateUtils.formatDateToRecordId(new Date());
            updateTrackControl(TrackMeViewModel.STATUS_START_RECORD);
            startService();
        } else {
            updateTrackControl(TrackMeViewModel.STATUS_REVIEW);
        }
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void init() {
        TrackMeViewModel mTrackMe = new TrackMeViewModel(getApplication(), mRecordId);
        mTrackMe.getListDetail().observe(this, this::handlePointList);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.trackMap);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    private void createMaker(GoogleMap map, double lat, double ln) {
        MarkerOptions options = new MarkerOptions();
        LatLng latLng = new LatLng(lat, ln);
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360)));
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, STREET_VIEW_ZOOM));
    }

    private void startService() {
        trackService = new Intent(getApplication(), TrackMeService.class);
        trackService.putExtra(Constants.INTENT_KEY.SEASON_KEY, mRecordId);
        startService(trackService);
    }

    private void stopService() {
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
        if (list.size() == 1) {
            createMaker(mGoogleMap, list.get(0).getRouteLat(), list.get(0).getRouteLng());
            return;
        }
        PolylineOptions options = new PolylineOptions();
        for (RecordDetail detail : list) {
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {
        FileUtil.writeImage(getApplicationContext(), bitmap, mRecordId);
    }
}
