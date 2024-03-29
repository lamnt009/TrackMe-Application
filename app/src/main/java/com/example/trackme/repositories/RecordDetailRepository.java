package com.example.trackme.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.trackme.model.RecordDetail;
import com.example.trackme.database.RecordDetailDao;
import com.example.trackme.database.RecordRoomDatabase;

import java.util.List;

public class RecordDetailRepository {

    private RecordDetailDao mRecordDetailDao;

    public RecordDetailRepository(Application application) {
        RecordRoomDatabase db = RecordRoomDatabase.getDatabase(application);
        mRecordDetailDao = db.recordDetailDao();
    }

    public LiveData<List<RecordDetail>> getRecordDetailById(String recordId) {
        return mRecordDetailDao.getRecordDetailByRecordId(recordId);
    }

    public void insert(RecordDetail detail) {
        RecordRoomDatabase.databaseWriteExecutor.execute(() -> mRecordDetailDao.insert(detail));
    }
}
