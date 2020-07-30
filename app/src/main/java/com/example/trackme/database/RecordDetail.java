package com.example.trackme.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_detail_table")
public class RecordDetail {
    public static final int STATE_START = 0x0;
    public static final int STATE_ROUTE = 0x1;
    public static final int STATE_END = 0x2;

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String recordId;  // MMDDYYYYHHMMSS
    private double routeLat;
    private double routeLng;
    private int routeNo;
    private int routeState;
    private long timeInMili;

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

    public long getTimeInMili() {
        return timeInMili;
    }

    public void setTimeInMili(long timeInMili) {
        this.timeInMili = timeInMili;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(int routeNo) {
        this.routeNo = routeNo;
    }

    public int getRouteState() {
        return routeState;
    }

    public void setRouteState(int routeState) {
        this.routeState = routeState;
    }
}
