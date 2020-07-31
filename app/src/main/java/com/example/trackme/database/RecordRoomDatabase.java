package com.example.trackme.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.trackme.model.Record;
import com.example.trackme.model.RecordDetail;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Record.class, RecordDetail.class}, version = 1, exportSchema = false)
public abstract class RecordRoomDatabase extends RoomDatabase {
    public abstract RecordDao recordDao();

    public abstract RecordDetailDao recordDetailDao();

    private static volatile RecordRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RecordRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RecordRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RecordRoomDatabase.class, "record_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
