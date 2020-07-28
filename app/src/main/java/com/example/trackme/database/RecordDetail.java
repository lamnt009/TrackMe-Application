package com.example.trackme.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_detail_table")
public class RecordDetail {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String recordId;  // MMDDYYYYHHMMSS
    private double routeLat;
    private double routeLng;
    private int timeInMili;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public double getRouteLat() {
        return routeLat;
    }

    public void setRouteLat(double routeLat) {
        this.routeLat = routeLat;
    }

    public double getRouteLng() {
        return routeLng;
    }

    public void setRouteLng(double routeLng) {
        this.routeLng = routeLng;
    }

    public int getTimeInMili() {
        return timeInMili;
    }

    public void setTimeInMili(int timeInMili) {
        this.timeInMili = timeInMili;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
