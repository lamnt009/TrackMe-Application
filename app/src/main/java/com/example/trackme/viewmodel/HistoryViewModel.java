package com.example.trackme.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.example.trackme.database.Record;
import com.example.trackme.repositories.RecordRepository;
import com.example.trackme.utils.DateUtils;

import java.util.Date;

public class HistoryViewModel extends AndroidViewModel {

    private RecordRepository mRepository;

    private LiveData<PagedList<Record>> mAllRecord;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new RecordRepository(application);
        mAllRecord = mRepository.getAllRecord();
    }

    public LiveData<PagedList<Record>> getAllRecord() {
        return mAllRecord;
    }

    public void insert(Record record) {
        mRepository.insert(record);
    }

    public void updateRecord(Record record){
        mRepository.updateRecord(record);
    }

    public Record getLastInsert(){
       return mRepository.getLastInsertRecord();
    }

    public String createNewSeason() {
        Record record = new Record();
        record.setRecordId(DateUtils.formatDateToRecordId(new Date()));
        record.setAvgSpeed("0");
        record.setDistance("0");
        record.setDuration(0);
        record.setMapImageName("");
        record.setInsertTime(System.currentTimeMillis());
        insert(record);
        return record.getRecordId();
    }

}
