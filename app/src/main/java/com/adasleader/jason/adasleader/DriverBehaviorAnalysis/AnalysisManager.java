package com.adasleader.jason.adasleader.DriverBehaviorAnalysis;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarningData;
import com.adasleader.jason.adasleader.upgrade.UpgradeManager;

import java.io.FileOutputStream;

/**
 * Created by jason on 2017/11/14.
 *
 */

public class AnalysisManager {

    private static final String TAG = "AnalysisManager";

    private static final AnalysisManager sInstance;
    static {
        sInstance = new AnalysisManager();
    }
    public static AnalysisManager getInstance() {
        return sInstance;
    }

    private static final int MSG_TIMEOUT = 10;
    private static final int MSG_DATA = 20;

    private static final int INTERVAL = 1000;

    private Context mContext;
    private Handler mHandler = null;
    private AnalyzeHMW analyzeHMW;

    private AnalysisManager() {
        HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        analyzeHMW = new AnalyzeHMW(this, INTERVAL);

        mHandler = new Handler(thread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_DATA:
                        dealData(msg);
                        break;
                    case MSG_TIMEOUT:
                        dealTimeout();
                        break;
                }
                return true;
            }
        });

    }

    public void setApplicationContext(Context context) {
        mContext = context;
    }

    public void push(WarningData warningData) {
        mHandler.obtainMessage(MSG_DATA, warningData).sendToTarget();
    }

    private void dealData(Message msg) {
        if (msg.obj instanceof WarningData) {
            WarningData warningData = (WarningData) msg.obj;

            analyzeHMW.check(warningData.hmw);
        }
    }

    private void dealTimeout() {
        analyzeHMW.timeout();
    }

    //For HMW

    void saveHMW(HMWRecord record) {
        Log.d(TAG, record.toJSON().toString());
        String pathName = UpgradeManager.getExternalStorageFilePath("hmw.log");
        //SaveFileIntentService.saveAction(mContext, pathName,record.toJSON().toString());
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(pathName, true);
            outputStream.write(record.toJSON().toString().getBytes());
            Log.d(TAG, pathName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                //Dose nothing
            }
        }
    }


    void setTimeout(int timeout) {
        mHandler.removeMessages(MSG_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(MSG_TIMEOUT, timeout);
    }

    void clearTimeout() {
        mHandler.removeMessages(MSG_TIMEOUT);
    }


}
