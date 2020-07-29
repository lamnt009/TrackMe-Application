package com.example.trackme.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "record_table")
public class Record implements Serializable {
    @PrimaryKey
    @NonNull
    private String recordId ;  // MMDDYYYYHHMMSS
    private String distance; //
    private long duration;
    private String avgSpeed;
    private String mapImageName;
    private long insertTime;

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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
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

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "Record{" +
                "recordId='" + recordId + '\'' +
                ", distance='" + distance + '\'' +
                ", duration=" + duration +
                ", avgSpeed='" + avgSpeed + '\'' +
                ", mapImageName='" + mapImageName + '\'' +
                ", insertTime=" + insertTime +
                '}';
    }
}
