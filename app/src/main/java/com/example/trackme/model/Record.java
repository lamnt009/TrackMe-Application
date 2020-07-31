package com.example.trackme.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "record_table")
public class Record implements Serializable {
    @PrimaryKey
    @NonNull
    private String recordId;  // MMDDYYYYHHMMSS
    private float distance; //meter
    private long duration; // second
    private float avgSpeed; // m/s
    private String mapImageName; // image name
    private long insertTime; // insert time

    @NonNull
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(@NonNull String recordId) {
        this.recordId = recordId;
    }


    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public float getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(float avgSpeed) {
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

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    @NonNull
    public String toString() {
        return "Record{" +
                "recordId='" + recordId + '\'' +
                ", distance=" + distance +
                ", duration=" + duration +
                ", avgSpeed=" + avgSpeed +
                ", mapImageName='" + mapImageName + '\'' +
                ", insertTime=" + insertTime +
                '}';
    }
}
