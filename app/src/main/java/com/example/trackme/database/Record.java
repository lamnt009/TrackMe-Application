package com.example.trackme.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_table")
public class Record {
    @PrimaryKey
    @NonNull
    private String recordId;  // MMDDYYYYHHMMSS
    private String distance; //
    private String duration;
    private String avgSpeed;
    private String mapImageName;
    private String insertTime;

    @NonNull
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(@NonNull String recordId) {
        this.recordId = recordId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getMapImageName() {
        return mapImageName;
    }

    public void setMapImageName(String mapImageName) {
        this.mapImageName = mapImageName;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}
