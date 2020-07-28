package com.example.trackme.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecordDetailDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(RecordDetail recordDetail);

    @Query("SELECT * from record_detail_table WHERE recordId = :recordId")
    List<RecordDetail> getRecordDetailByRecordId(String recordId);
}
