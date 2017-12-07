package com.adasleader.jason.adasleader.DriverBehaviorAnalysis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by jason on 2017/11/16.
 *
 */

class HMWRecord {
    private static final int HMW_SIZE = 1024;
    private final int interval;
    private final long startTime;
    private long stopTime;
    private int count;
    private int minValue;
    private final byte[] values = new byte[HMW_SIZE];

    HMWRecord(int interval) {
        this.interval = interval;
        startTime = System.currentTimeMillis();
    }

    //不能用byte，因为byte是有符号类型，-127～127
    public void add(int value) {
        if (value >= 100)
            value = value - 100;

        values[count] = (byte) (value & 0xff);
        count++;

        stopTime = System.currentTimeMillis();

        if (minValue == 0) {
            minValue = value;
        } else if (minValue > 0 && value < minValue) {
                minValue = value;
        }
    }

    boolean isFull() {
        return count >= values.length;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("HMWRecord");
        buffer.append("\nStartTime: ").append(startTime);
        buffer.append("\tStopTime: ").append(stopTime);
        buffer.append("\tInterval: ").append(interval);
        buffer.append("\nMinValue: ").append(minValue);
        buffer.append("\nCount: ").append(count);

        byte[] array = Arrays.copyOf(values, count);
        buffer.append("\nValues: ").append(Arrays.toString(array));
        return buffer.toString();
    }


    JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("StartTime", startTime);
            json.put("StopTime", stopTime);
            json.put("Interval", interval);
            json.put("MinValue", minValue);
            json.put("Count", count);

            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i<count; i++) {
                jsonArray.put(values[i]);
            }
            json.put("Values", jsonArray);
            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
