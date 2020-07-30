package com.example.trackme.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDetailDao {
    @Query("DELETE FROM record_detail_table")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecordDetail recordDetail);

    @Query("SELECT * from record_detail_table WHERE recordId = :recordId ORDER BY routeState ASC")
    LiveData<List<RecordDetail>> getRecordDetailByRecordId(String recordId);

    @Query("SELECT * from record_detail_table WHERE recordId = :recordId ORDER BY timeInMili DESC LIMIT 1 ")
    RecordDetail getLastInsertRecord(String recordId);
}
