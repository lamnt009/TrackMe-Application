package com.example.trackme.database;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.trackme.model.Record;

@Dao
public interface RecordDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Record record);

    @Query("SELECT * from record_table WHERE recordId = :recordId")
    LiveData<Record> getRecord(String recordId);

    @Query("SELECT * from record_table ORDER BY insertTime DESC")
    DataSource.Factory<Integer, Record> getAllRecordPage();

    @Update
    void updateRecord(Record record);

    @Query("SELECT * from record_table ORDER BY insertTime DESC LIMIT 1 ")
    Record getLastInsertRecord();
}
