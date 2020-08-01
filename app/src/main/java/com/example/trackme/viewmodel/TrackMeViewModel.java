package com.example.trackme.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.trackme.model.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;

import java.util.List;

public class TrackMeViewModel extends AndroidViewModel {

    public static final int STATUS_REVIEW = 0x0;
    public static final int STATUS_START_RECORD = 0x1;
    public static final int STATUS_PAUSE = 0x2;

    private RecordDetailRepository mDetailRepository;
    private LiveData<List<RecordDetail>> mListDetail;
    private MutableLiveData<Float> mapDistance;
    private MutableLiveData<Long> mapDuration;
    private MutableLiveData<Float> mapSpeed;
    private MutableLiveData<Integer> viewMode;


    public TrackMeViewModel(@NonNull Application application) {
        super(application);
        mDetailRepository = new RecordDetailRepository(application);
    }

    // Record Detail table
    public LiveData<List<RecordDetail>> getListDetail(String recordId) {
        if (mListDetail == null) {
            mListDetail = mDetailRepository.getRecordDetailById(recordId);
        }
        return mListDetail;
    }

    public MutableLiveData<Float> getMapDistance() {
        if (mapDistance == null) {
            mapDistance = new MutableLiveData<>();
        }
        return mapDistance;
    }

    public MutableLiveData<Long> getMapDuration() {
        if (mapDuration == null) {
            mapDuration = new MutableLiveData<>();
        }
        return mapDuration;
    }

    public MutableLiveData<Float> getMapSpeed() {
        if (mapSpeed == null) {
            mapSpeed = new MutableLiveData<>();
        }
        return mapSpeed;
    }

    public MutableLiveData<Integer> getViewMode() {
        if (viewMode == null) {
            viewMode = new MutableLiveData<>();
        }
        return viewMode;
    }
}
