package com.example.trackme.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.example.trackme.model.Record;
import com.example.trackme.database.RecordDao;
import com.example.trackme.database.RecordRoomDatabase;

public class RecordRepository {
    private RecordDao mRecordDao;
    private LiveData<PagedList<Record>> mAllRecord;

    public RecordRepository(Application application) {
        RecordRoomDatabase db = RecordRoomDatabase.getDatabase(application);
        mRecordDao = db.recordDao();
        DataSource.Factory<Integer, Record> source = mRecordDao.getAllRecordPage();
        LivePagedListBuilder<Integer, Record> pagedListBuilder = new LivePagedListBuilder<>(source, 50);
        mAllRecord = pagedListBuilder.build();
    }
    public LiveData<PagedList<Record>> getAllRecord() {
        return mAllRecord;
    }

    public void insert(Record record) {
        RecordRoomDatabase.databaseWriteExecutor.execute(() -> mRecordDao.insert(record));
    }
}
