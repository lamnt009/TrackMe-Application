package com.example.trackme.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.example.trackme.model.Record;
import com.example.trackme.repositories.RecordRepository;

public class HistoryViewModel extends AndroidViewModel {

    private RecordRepository mRepository;

    private LiveData<PagedList<Record>> mAllRecord;
    private MutableLiveData<Record> selectRecord;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordRepository(application);
        mAllRecord = mRepository.getAllRecord();
    }

    public LiveData<PagedList<Record>> getAllRecord() {
        return mAllRecord;
    }

    public MutableLiveData<Record> getSelectRecord() {
        if (selectRecord == null) {
            selectRecord = new MutableLiveData<>();
        }
        return selectRecord;
    }

    public void insert(Record record) {
        mRepository.insert(record);
    }

}
