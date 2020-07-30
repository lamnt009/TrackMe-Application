package com.example.trackme.viewmodel;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.trackme.database.Record;
import com.example.trackme.database.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;

import java.util.List;

public class TrackMeViewModel extends AndroidViewModel {

    public static final int STATUS_REVIEW = 0x0;
    public static final int STATUS_START_RECORD = 0x1;
    public static final int STATUS_PAUSE = 0x2;

    private RecordDetailRepository mRepository;
    private LiveData<List<RecordDetail>> mListDetail;
    private LiveData<Record> record;


    public TrackMeViewModel(@NonNull Application application, String recordId) {
        super(application);
        mRepository = new RecordDetailRepository(application);
        mListDetail = mRepository.getRecordDetailById(recordId);
    }

    public LiveData<List<RecordDetail>> getListDetail() {
        return mListDetail;
    }

    public void insert(RecordDetail record) {
        mRepository.insert(record);
    }

    /**
     *
     * @param mRecordId Record id in table record
     * @param location  Location
     * @param routeState State of route
     * @param routeNo  Route number
     */
    public void insert(String mRecordId, Location location, int routeState, int routeNo) {
        RecordDetail recordDetail = new RecordDetail();
        recordDetail.setRecordId(mRecordId);
        recordDetail.setRouteLat(location.getLatitude());
        recordDetail.setRouteLng(location.getLongitude());
        recordDetail.setTimeInMili(System.currentTimeMillis());
        recordDetail.setRouteNo(routeNo);
        recordDetail.setRouteState(routeState);
        insert(recordDetail);
    }
}
