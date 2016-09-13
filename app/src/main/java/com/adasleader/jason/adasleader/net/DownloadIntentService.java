package com.adasleader.jason.adasleader.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.adasleader.jason.adasleader.common.Constants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DownloadIntentService extends IntentService {
    private static final String TAG = "DownloadIntentService";

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_DOWNLOAD = "hk.com.mobileye.jason.adlaleader.Net.action.DOWNLOAD";

    // TODO: Rename parameters
    public static final String EXTRA_URL = "hk.com.mobileye.jason.adlaleader.Net.extra.URL";
    public static final String EXTRA_FILE = "hk.com.mobileye.jason.adlaleader.Net.extra.FILE";
    private static final String EXTRA_SENDER = "hk.com.mobileye.jason.adlaleader.Net.extra.SENDER";


    public static void startActionDownload(Context context, String aUrlStr, String aFileStr) {
        Intent intent = new Intent(context, DownloadIntentService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, aUrlStr);
        intent.putExtra(EXTRA_FILE, aFileStr);
        intent.putExtra(EXTRA_SENDER, context.getClass().getSimpleName());
        context.startService(intent);
    }

    public DownloadIntentService() {
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (intent != null && isExternalStorageWritable()) {
            final String urlStr = intent.getStringExtra(EXTRA_URL);
            final String fileStr = intent.getStringExtra(EXTRA_FILE);
            final String sender = intent.getStringExtra(EXTRA_SENDER);

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(Constants.HTTP_CONNECT_TIMEOUT);
                conn.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_CONNECTING);
                conn.connect();
                int response = conn.getResponseCode();
                int fileLength = conn.getContentLength();
                Log.d(TAG, "The response is : " + response);
                inputStream = conn.getInputStream();

                //outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
                outputStream = new FileOutputStream(fileStr);

                broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_START);
                int readCount;
                int progress ;
                int totalRead = 0;
                byte buf[] = new byte[1024*2];

                while (totalRead<fileLength) {
                    readCount = inputStream.read(buf);
                    if (readCount <= 0) { break; }
                    totalRead += readCount;
                    progress = (int) (((double) totalRead / fileLength) * 100);
                    outputStream.write(buf, 0, readCount);
                    broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_DOWNLOADING, progress);
                }

                Log.d(TAG, String.format("File Length : %d   Total read : %d", fileLength, totalRead));

                int time = 500;
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                }
                if (totalRead == fileLength)
                    broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_DOWNLOADED);
                else
                    broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_FAIL);
            } catch (IOException e) {
                e.printStackTrace();
                broadcastDownloadStatus(sender, Constants.DOWNLOAD_STATUS_FAIL);
            } finally {
                try {
                    if (null != inputStream) {
                        inputStream.close();
                    }
                    if (null != outputStream) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    //Does nothing
                }
            }
        }
    }


    private void broadcastDownloadStatus(String aSender, String aStatus) {
        broadcastDownloadStatus(aSender, aStatus, 0);
    }

    private void broadcastDownloadStatus(String aSender, String aStatus,int aProgress) {
        Intent intent = new Intent(Constants.DOWNLOAD_WORK_STATUS_ACTION);
        intent.putExtra(Constants.EXTEND_DOWNLOAD_STATUS, aStatus);
        intent.putExtra(Constants.EXTEND_DOWNLOAD_PROGRESS, aProgress);
        intent.putExtra(Constants.EXTENDED_OWNER, aSender);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastResult(String aSender, String aResult) {
        Intent intent = new Intent(Constants.DOWNLOAD_WORK_RESULT_ACTION);
        intent.putExtra(Constants.EXTEND_DOWNLOAD_RESULT, aResult);
        intent.putExtra(Constants.EXTENDED_OWNER, aSender);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private boolean isExternalStorageWritable() {
        boolean result;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            result = true;
        } else {
            result = false;
        }
        Log.d(TAG, result ? "External Storage is writable !" : " External Storage is not writable !");
        return  result;
    }
}
