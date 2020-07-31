package com.example.trackme.utils;

import android.location.Location;

public class Utils {
    /**
     * Distance between two point
     *
     * @param lat1 lat1
     * @param lng1 lng1
     * @param lat2 lat2
     * @param lng2 lng2
     * @return Distance in meters
     */
    public static float distanceBetweenTwoPoint(double lat1, double lng1, double lat2, double lng2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lng1, lat2, lng2, result);
        return result[0];
    }

    /**
     * Calculate Avg Speed
     *
     * @param distance Distance in meters
     * @param time     Time in second
     * @return avg Speed in m/s
     */
    public static float avgSpeed(float distance, long time) {
        if (distance <= 0 || time <= 0) {
            return 0;
        }
        return distance / time;
    }

    public static float avgSpeed(Float distance, Long time) {
        if (distance == null || distance <= 0 || time == null || time <= 0) {
            return 0;
        }
        return distance / time;
    }

    /**
     * Calculate Avg Speed
     *
     * @return avg Speed in km/h
     */
    public static float avgSpeedKmH(float speed) {
        if (speed <= 0) {
            return 0;
        }
        return (float) (speed * 3.6);
    }
}
