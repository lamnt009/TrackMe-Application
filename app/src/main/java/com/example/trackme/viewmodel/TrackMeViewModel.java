package com.example.trackme.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.trackme.database.RecordDetail;
import com.example.trackme.repositories.RecordDetailRepository;

import java.util.List;

public class TrackMeViewModel extends AndroidViewModel {

    public static final int STATUS_REVIEW = 0x0;
    public static final int STATUS_START_RECORD = 0x1;
    public static final int STATUS_PAUSE = 0x2;

    private RecordDetailRepository mRepository;
    LiveData<List<RecordDetail>> mListDetail;

    public TrackMeViewModel(@NonNull Application application, String recordId) {
        super(application);
        mRepository = new RecordDetailRepository(application);
        mListDetail = mRepository.getRecordDetailById(recordId);
    }

    public LiveData<List<RecordDetail>> getListDetail() {
        return mListDetail;
    }
}
