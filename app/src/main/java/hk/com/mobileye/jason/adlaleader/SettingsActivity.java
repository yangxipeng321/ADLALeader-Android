package hk.com.mobileye.jason.adlaleader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileWriteReq;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.preference.WarningConfig;
import hk.com.mobileye.jason.adlaleader.preference.WarningPrefsFragment;

public class SettingsActivity extends Activity implements  WarningPrefsFragment.InteractionListener {

    public static final String TAG = "SettingsActivity";
    private MyApplication mApp;
    private MyHandler mHandler;
    private WarningPrefsFragment warningPrefsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_settings);
        ExitManager.getInstance().addActivity(this);

        mApp = (MyApplication) getApplication();
        initHandler();
        initLocalReceiver();

        warningPrefsFragment = new WarningPrefsFragment();
        getFragmentManager().beginTransaction().replace(R.id.warningPrefs, warningPrefsFragment).commit();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestory");
        releaseLocalReceiver();
        releaseHandler();
        mApp = null;

        //Must always call the super method at the end.
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG, "onResume");

        refreshMHConfig(mApp.mMHConfigFile);
    }

    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.toast_twice_press_back_to_exit, Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Log.d(TAG, "twice press back to exit");
                ExitManager.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHandler() {
        mHandler = new MyHandler(this);
    }

    private void releaseHandler() {
        if (null != mHandler) {
//            mHandler.removeMessages(MSG_READ_CONFIG_FILE_RESULT);
            mHandler = null;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SettingsActivity> mActivity;

        public MyHandler(SettingsActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SettingsActivity activity = mActivity.get();
            if (activity != null) {
                //super.handleMessage(msg);
                switch (msg.what) {
                    default:
                        break;
                }
            }
        }
    }

    public void onBtnExitOnClicked(View view) {
        ExitManager.getInstance().exit();
    }

    private void writeMHConfig() {
        Log.d(TAG, String.format("Write file name : %s  ip : %s   port : %d", Constants.MH_CONFIG_FILE, mApp.mIp, mApp.mPort));
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            //new Thread(new WriteFileRunnable()).start();

            if (mApp.mMHConfigFile != null) {
                FileWriteReq msg = (FileWriteReq) MsgFactory.getInstance().create(
                        ServiceType.SERVICE_FILE,
                        MessageType.FILE_WRITE_REQ,
                        ResponseType.REQUEST);
                //add file name tlv value
                msg.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(
                        mApp.mMHConfigFile.getFileName());
                //add file content tlv value  length + crc + content
                byte[] buffer = mApp.mMHConfigFile.getData();
                msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue(buffer);
                if (msg.encode()) {
                    TcpIntentService.startActionFileService(this, msg.getData(), Constants.DESC_WRITE_MH_CONFIG);
                }
            }
        }
    }

    private void refreshMHConfig(WarningConfig aMHConfig) {
        Log.d(TAG, "refreshMHConfig");
        if (null != warningPrefsFragment) {
            warningPrefsFragment.refresh(aMHConfig);
        }
    }

    //Register BroadcastReceiver to track local work status
    private LocalBroadcastReceiver localReceiver;

    private void initLocalReceiver() {
        //Register BroadcastReceiver to track local work status
        localReceiver = new LocalBroadcastReceiver();

        IntentFilter filter = new IntentFilter(Constants.NETWORK_CHANGE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.READ_MH_CONFIG_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.WRITE_MH_CONFIG_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

//        filter = new IntentFilter(Constants.VERSION_INFO_UPDATE_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
//
//        filter = new IntentFilter(Constants.DOWNLOAD_WORK_STATUS_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
//
//        filter = new IntentFilter(Constants.APP_UPGRADE_RESULT_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
//
//        filter = new IntentFilter(Constants.CMD_WRITE_FIRMWARE_REQ_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
    }

    private void releaseLocalReceiver() {
        //Unregister BroadcastReceiver when app is destroyed.
        if (localReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
            localReceiver = null;
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        private LocalBroadcastReceiver() {
            //Prevents instantiation by other packages.
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                final String action = intent.getAction();
                Log.e(TAG, action);
                // Both this activity and Mainactivity2 can start a TcpIntentService. But Only
                // Mainactivity2 receive and process the result which TcpIntentService
                // broadcasts. So this just need to wait the settings update which Mainactivity2
                // broadcasts.
                if (action.equals(Constants.NETWORK_CHANGE_ACTION)) {
                    dealNetworkChange();
                    return;
                }

                if (action.equals(Constants.READ_MH_CONFIG_RESULT_ACTION)) {
                    dealReadMHConfigResult();
                    return;
                }

                if (action.equals(Constants.WRITE_MH_CONFIG_RESULT_ACTION)) {
                    dealWriteMHConfigResult(intent);
                    return;
                }
            }
        }

        private void dealReadMHConfigResult() {
            if (mApp.mMHConfigFile != null) {
                refreshMHConfig(mApp.mMHConfigFile);
            }
        }

        private void dealWriteMHConfigResult(Intent intent) {
            int length = intent.getIntExtra(Constants.EXTEND_FILE_LENGTH, -1);
            Log.d(TAG, "Deal write MH config reslut. file lenght = " + length);
            String str;
            if (length > 0) {
                str = "Write Warning Config File OK";
            } else {
                str = "Write Warning Config File Failed";
            }
            Toast toast = Toast.makeText(SettingsActivity.this, str, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();
        }

        private void dealNetworkChange() {
            refreshMHConfig(mApp.mMHConfigFile);
        }
    }

    public void onBtnAboutClicked(View view) {
        Log.d(TAG, "open about activity");
        startActivity(new Intent(this, AboutActivity.class));
    }

    @Override
    public void warningPreferencesChanged(WarningPrefsFragment fragment, WarningConfig config) {
        writeMHConfig();
    }
}
