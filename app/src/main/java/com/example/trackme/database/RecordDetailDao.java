package com.example.trackme.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.trackme.model.RecordDetail;

import java.util.List;

@Dao
public interface RecordDetailDao {
    @Query("DELETE FROM record_detail_table WHERE recordId = :recordId")
    void deleteAllByRecordId(String recordId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecordDetail recordDetail);

    @Query("SELECT * from record_detail_table WHERE recordId = :recordId ORDER BY routeNo ASC")
    LiveData<List<RecordDetail>> getRecordDetailByRecordId(String recordId);
}
