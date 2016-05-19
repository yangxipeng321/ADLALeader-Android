package hk.com.mobileye.jason.adlaleader.net;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import hk.com.mobileye.jason.adlaleader.common.Constants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CheckUpgradeIntentService extends IntentService {
    private static final String TAG = "CheckUpgradeService";
    public static final String ACTION_QUERY_APP_UPGRADE = "hk.com.mobileye.jason.adlaleader.Net.action.QUERY_APP_UPGRADE";
    public static final String ACTION_QUERY_FIRMWARE_UPGRADE = "hk.com.mobileye.jason.adlaleader.Net.action.QUERY_FIRMWARE_UPGRADE";

    private static final String EXTRA_URL = "hk.com.mobileye.jason.adlaleader.Net.extra.URL";
    private static final String EXTRA_VERSION = "hk.com.mobileye.jason.adlaLeader.NET.extra.VERSION";
    private static final String EXTRA_SENDER = "hk.com.mobileye.jason.adlaleader.Net.extra.SENDER";

    public static boolean startActionQueryAppUpgrade(Context context, int version) {
        String verInfo = String.valueOf(version);
        byte[] encode = Base64.encode(verInfo.getBytes(), Base64.DEFAULT);
        String verInfoEncode = new String(encode);
        String urlStr = Constants.queryAppUpgradeUrlStr;
        Log.d(TAG, String.format("startActionQueryAppUpgrade \nurl : %s  version : %s  Base64 : %s",
                urlStr, verInfo, verInfoEncode));

        if (version <= 0) {
            Log.e(TAG, "App version code is " + version);
            return false;
        }
        Log.d(TAG, "check app upgrade : " + verInfo);

        Intent intent = new Intent(context, CheckUpgradeIntentService.class);
        intent.setAction(ACTION_QUERY_APP_UPGRADE);
        intent.putExtra(EXTRA_URL, urlStr);
        intent.putExtra(EXTRA_VERSION, verInfoEncode);
        intent.putExtra(EXTRA_SENDER, context.getClass().getSimpleName());
        context.startService(intent);
        return true;
    }

    public static boolean startActionQueryFirmwareUpgrade(Context context, String version) {
        String urlStr = Constants.queryFirmwareUpgradeUrlStr;
        Log.d(TAG, String.format("startActionQueryFirmwareUpgrade \nurl : %s  version : %s",
                urlStr, version));

        if (version == null) {
            Log.e(TAG, "Firmware version code is null");
            return false;
        }
        Log.d(TAG, "check firmware upgrade : " + version);

        Intent intent = new Intent(context, CheckUpgradeIntentService.class);
        intent.setAction(ACTION_QUERY_FIRMWARE_UPGRADE);
        intent.putExtra(EXTRA_URL, urlStr);
        intent.putExtra(EXTRA_VERSION, version);
        intent.putExtra(EXTRA_SENDER, context.getClass().getSimpleName());
        context.startService(intent);
        return true;
    }
    public CheckUpgradeIntentService() {
        super("CheckUpgradeIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent");

        if (intent != null) {
            final String action = intent.getAction();
            final String urlStr = intent.getStringExtra(EXTRA_URL);
            final String version = intent.getStringExtra(EXTRA_VERSION);
            final String sender = intent.getStringExtra(EXTRA_SENDER);

            InputStream inputStream = null;
            String result = Constants.NO_UPGRADE;
            try {
                URL url = new URL(urlStr + "?info=" + version);
                Log.d(TAG, url.toString());
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(Constants.HTTP_READ_TIMEOUT);
                conn.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                broadcastHttpStatus(sender, Constants.DOWNLOAD_STATUS_CONNECTING);
                conn.connect();
                int response = conn.getResponseCode();
                Log.d(TAG, "The response is : " + response);
                if (response!= HttpURLConnection.HTTP_OK)
                    return;
                int len = conn.getContentLength();
                inputStream = conn.getInputStream();

                broadcastHttpStatus(sender, Constants.DOWNLOAD_STATUS_START);
                byte[] buffer = new byte[len];
                int readLen = inputStream.read(buffer);
                if (readLen > 0) {
                    broadcastHttpStatus(sender, Constants.DOWNLOAD_STATUS_DOWNLOADED);
                    result = new String(buffer).trim();
                } else {
                    broadcastHttpStatus(sender, Constants.DOWNLOAD_STATUS_FAIL);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG,  e.getMessage());
            }finally {
                if (null != inputStream) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                    }
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }

                Log.d(TAG, String.format("Sender : %s. Result : %s", sender, result));
                if (action.equals(ACTION_QUERY_FIRMWARE_UPGRADE)) {
                    broadcastHttpResult(sender, Constants.FIRMWARE_UPGRADE_RESULT_ACTION, result);
                } else if (action.equals(ACTION_QUERY_APP_UPGRADE)) {
                    broadcastHttpResult(sender, Constants.APP_UPGRADE_RESULT_ACTION, result);
                }
            }
        }
    }

    private void broadcastHttpStatus(String sender, String aStatus) {
        Intent intent = new Intent(Constants.CHECK_UPGRADE_WORK_STATUS_ACTION);
        intent.putExtra(Constants.EXTEND_CHECK_UPGRADE_STATUS, aStatus);
        intent.putExtra(Constants.EXTENDED_OWNER, sender);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastHttpResult(String sender, String action, String aResult) {
        Intent intent = new Intent(action);
        intent.putExtra(Constants.EXTEND_CHECK_UPGRADE_RESULT, aResult);
        intent.putExtra(Constants.EXTENDED_OWNER, sender);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
