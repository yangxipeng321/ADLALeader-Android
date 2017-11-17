package com.adasleader.jason.adasleader.DriverBehaviorAnalysis;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * 记录 HMW 数据，用于驾驶行为分析。
 *
 */
public class SaveFileIntentService extends IntentService {
    private static final String TAG = "SaveFileIntentService";

    private static final String ACTION_SAVE = "com.adasleader.jason.adasleader.DriverBehaviorAnalysis.action.save";

    private static final String EXTRA_PATH_NAME = "com.adasleader.jason.adasleader.DriverBehaviorAnalysis.extra.path_name";
    private static final String EXTRA_CONTENT = "com.adasleader.jason.adasleader.DriverBehaviorAnalysis.extra.content";

    private SaveFileIntentService() {
        super("SaveFileIntentService");
    }

    public static void saveAction(Context context, String path, String content) {
        Intent intent = new Intent(context, SaveFileIntentService.class);
        intent.putExtra(EXTRA_PATH_NAME, path);
        intent.putExtra(EXTRA_CONTENT, content);
        Log.d(TAG, "1");
        context.startService(intent);
        Log.d(TAG, "2");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String pathName = intent.getStringExtra(EXTRA_PATH_NAME);
            final String content = intent.getStringExtra(EXTRA_CONTENT);
            handleActionSave(pathName, content);
        }
    }


    private void handleActionSave(String pathName, String content) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(pathName);
            outputStream.write(content.getBytes());
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

}
