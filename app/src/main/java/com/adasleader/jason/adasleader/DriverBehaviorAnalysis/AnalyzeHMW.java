package com.adasleader.jason.adasleader.DriverBehaviorAnalysis;

import android.util.Log;

/**
 * Created by jason on 2017/11/16.
 * 记录HMW数据。
 * 如果有HMW数据，定时采样，在HMW结束后，保存到文件。
 */
class AnalyzeHMW {
    private static final String TAG = "AnalyzeHMW";

    private final AnalysisManager mManager;
    private final int interval;
    private final int timeout;
    private long lastUpdate;

    private HMWRecord record;

    /**
     *
     * @param interval 数据采样间隔
     */
    AnalyzeHMW(AnalysisManager manager, int interval) {
        super();
        mManager = manager;
        this.interval = interval;
        timeout = interval * 3;
    }

    void check(int data) {
        if (record == null) {
            if (data > 0) {
                start(data);
            }
        } else {
            if (data > 0) {
                if ((System.currentTimeMillis() - lastUpdate) > interval) {
                    update(data);
                }
            } else {
                stop();
            }
        }
    }

    private void start(int data) {
        Log.d(TAG, "start");
        record = new HMWRecord(interval);
        record.add(data);
        lastUpdate = System.currentTimeMillis();
        mManager.setTimeout(timeout);
    }

    private void update(int data) {
        Log.d(TAG, "update");
        record.add(data);
        lastUpdate = System.currentTimeMillis();
        if (record.isFull()) {
            stop();
        } else {
            mManager.setTimeout(timeout);
        }
    }

    private void stop() {
        Log.d(TAG, "stop");
        mManager.saveHMW(record);
        record = null;
        mManager.clearTimeout();
    }

    void timeout() {
        Log.d(TAG, "timeout");
        stop();
    }
}

