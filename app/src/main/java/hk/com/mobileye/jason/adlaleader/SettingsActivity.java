package hk.com.mobileye.jason.adlaleader;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.net.CheckUpgradeIntentService;
import hk.com.mobileye.jason.adlaleader.net.DownloadIntentService;
import hk.com.mobileye.jason.adlaleader.net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Cmd.CmdResetMEReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Cmd.CmdResetReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileReadReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileWriteReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Settings.ParameterReadReq;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.preference.WarningConfig;
import hk.com.mobileye.jason.adlaleader.preference.WarningPrefsFragment;
import hk.com.mobileye.jason.adlaleader.upgrade.UpgradeManager;
import hk.com.mobileye.jason.adlaleader.upgrade.UploadDialogFragment;


public class SettingsActivity extends Activity implements UpgradeManager.TaskUpgradeMethods,
        UploadDialogFragment.NoticeDialogListener, WarningPrefsFragment.InteractionListener {

    public static final String TAG = "SettingsActivity";
    public static final int MSG_READ_CONFIG_FILE = 1;
    public static final int MSG_READ_CONFIG_FILE_RESULT = 2;
    public static final int MSG_WRITE_CONFIG_FILE = 3;
    public static final int MSG_WRITE_CONFIG_FILE_RESULT = 4;
    public static final int MSG_RESET_MOBILEYE = 5;
    public static final int MSG_WAITING_MOBILEYE_RESTART = 6;


    //    public static String mIp =null;
//    public static int mPort = -1;
//    public static AM_AWS_SETUP mConfigFile = null;
//    public static boolean isOnCAN =false;
    //public static final String mFileName = Constants.MH_CONFIG_FILE;

    private MyApplication mApp;


    //private LinearLayout layVirtualBumper;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RelativeLayout appLayout;
    private RelativeLayout firmwareLayout;
    private Button btnCheckUpgrade;
    private Button btnAppUpgrade;
    private Button btnFirmwareUpgrade;
    private TextView txtUpgradeInfo;
    private TextView txtAppUpgradeInfo;
    private TextView txtFirmwareUpgradeInfo;
    //private LinearLayout warningLayout;
    private RelativeLayout applyLayout;
    private TextView txtApplyWarning;
    private Button btnSettingsConfirm;
    private Button btnSettingsCancel;


    private MyHandler mHandler;
    ViewGroup container = null;

    enum SettingsApplyStatus {NONE, MODIFIED, WRITING_CONFIG, RESETTING, WAIT_RESTART}

    private SettingsApplyStatus asStatus = SettingsApplyStatus.NONE;
    private Boolean isWaitMobileyeRestart = false;
    private WarningPrefsFragment warningPrefsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_settings);
        ExitManager.getInstance().addActivity(this);

        mApp = (MyApplication) getApplication();
        initHandler();
        initControls();
        initLocalReceiver();

        container = (ViewGroup) findViewById(R.id.container);
        LayoutTransition mTransition = new LayoutTransition();
        mTransition.setDuration(500);
        container.setLayoutTransition(mTransition);

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
        refreshControls();
        refreshUpgradeNotify();
    }

    @Override
    protected void onPause() {
        //writeConfig();
        super.onPause();
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
                    case MSG_READ_CONFIG_FILE_RESULT:
//                        activity.onRefreshComplete();
//                        //loadingAnimation(false);
//                        if (msg.obj == null) {
//                            //this.sendEmptyMessageDelayed(MSG_READ_CONFIG_FILE, Constants.READ_CONFIG_INTERVAL);
//                        } else {
//                            activity.parseConfigFile((byte[]) msg.obj);
//                        }
                        break;
                    case MSG_READ_CONFIG_FILE:
                        //activity.readConfig();
                        break;
                    case MSG_WRITE_CONFIG_FILE_RESULT:
//                        activity.parseWriteResp((byte[]) msg.obj);
                        break;
                    case MSG_WRITE_CONFIG_FILE:
//                        activity.writeMHConfig();
                        break;
                    case MSG_RESET_MOBILEYE:
                        activity.resetMobileye();
                    case MSG_WAITING_MOBILEYE_RESTART:
                        activity.isWaitMobileyeRestart = true;
                }
            }
        }
    }

    private void releaseHandler() {
        if (null != mHandler) {
            mHandler.removeMessages(MSG_READ_CONFIG_FILE);
            mHandler.removeMessages(MSG_READ_CONFIG_FILE_RESULT);
            mHandler.removeMessages(MSG_WRITE_CONFIG_FILE);
            mHandler.removeMessages(MSG_WRITE_CONFIG_FILE_RESULT);
            mHandler.removeMessages(MSG_RESET_MOBILEYE);
            mHandler = null;
        }
    }

    public void onBtnExitOnClicked(View view) {
        ExitManager.getInstance().exit();
    }



    /**
     * When user changed the settings, show applying settings info.
     * If user clicks "confirm" button, the app reset device.
     * If user clicks "cancel" button, the app refresh the settings value
     */

    private void updateSettingsApplyInfo(SettingsApplyStatus status) {
        asStatus = status;

        switch (asStatus) {
            case NONE:
                applyLayout.setVisibility(View.GONE);
                break;
            case MODIFIED:
                applyLayout.setVisibility(View.VISIBLE);
                txtApplyWarning.setText(R.string.settings_apply);
                btnSettingsConfirm.setEnabled(true);
                btnSettingsCancel.setEnabled(true);
                break;
            case WRITING_CONFIG:
                applyLayout.setVisibility(View.VISIBLE);
                txtApplyWarning.setText(R.string.settings_writing_config);
                btnSettingsConfirm.setEnabled(false);
                btnSettingsCancel.setEnabled(false);
                break;
            case RESETTING:
                applyLayout.setVisibility(View.VISIBLE);
                txtApplyWarning.setText(R.string.settings_prepare_reset);
                btnSettingsConfirm.setEnabled(false);
                btnSettingsCancel.setEnabled(false);
                break;
            case WAIT_RESTART:
                applyLayout.setVisibility(View.VISIBLE);
                txtApplyWarning.setText(R.string.waiting_device_restart);
                btnSettingsConfirm.setEnabled(false);
                btnSettingsCancel.setEnabled(false);
        }
    }

    public void onBtnSettingsCancelClicked(View view) {
        //showApplyInfo(false);
        updateSettingsApplyInfo(SettingsApplyStatus.NONE);
        initiateRefresh();
    }

    public void onBtnSettingsConfirmClicked(View view) {
//        txtApplyWarning.setText(R.string.settings_writing_config);
//        btnSettingsConfirm.setEnabled(false);
//        btnSettingsCancel.setEnabled(false);
        updateSettingsApplyInfo(SettingsApplyStatus.WRITING_CONFIG);
        refreshMHConfig(mApp.mMHConfigFile);
        writeMHConfig();
    }

//    private class HMTicksSpinnerListener implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            String str = (String) parent.getItemAtPosition(position);
//            Log.i(TAG, String.format("HMTicksSpinner Item select %s", str));
//            if (null != mApp.mMHConfigFile && !str.equals(mApp.mMHConfigFile.getHMWModeLevel())) {
//                mApp.mMHConfigFile.setHMWModeLevel(str);
////                mHandler.removeMessages(MSG_WRITE_CONFIG_FILE);
////                mHandler.sendEmptyMessageDelayed(MSG_WRITE_CONFIG_FILE,
////                        Constants.WRITE_CONFIG_DELAY);
//                //showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    }
//
//    private class VolumeSpinnerListener implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            String volume = (String) parent.getItemAtPosition(position);
//            Log.i(TAG, String.format("VolumnSpinner Item select %s", volume));
//            if (null != mApp.mMHConfigFile && !volume.equals(mApp.mMHConfigFile.getVolume())) {
//                mApp.mMHConfigFile.setVolume(volume);
////                mHandler.removeMessages(MSG_WRITE_CONFIG_FILE);
////                mHandler.sendEmptyMessageDelayed(MSG_WRITE_CONFIG_FILE,
////                        Constants.WRITE_CONFIG_DELAY);
////                showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//        }
//    }
//
//    private class VBSpinnerListener implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            String str = (String) parent.getItemAtPosition(position);
//            Log.i(TAG, String.format("VBSpinner Item select %s", str));
//            if (null != mApp.mMHConfigFile && !str.equals(mApp.mMHConfigFile.getVBLevel())) {
//                mApp.mMHConfigFile.setVBLevel(str);
////                showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    }
//
//    private class LDWSpeedSpinnerListener implements AdapterView.OnItemSelectedListener {
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            String str = (String) parent.getItemAtPosition(position);
//            Log.i(TAG, String.format("LDWSpeedSpinner Item select %s", str));
//            if (null != mApp.mMHConfigFile && !str.equals(mApp.mMHConfigFile.getLDWSpeed())) {
//                mApp.mMHConfigFile.setLDWSpeed(str);
////                showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
//        }
//
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//
//        }
//    }

//    public void onSwitchLDWClicked(View view) {
//        if (view instanceof Switch) {
//            if (null == mApp.mMHConfigFile || -1 == mApp.mMHConfigFile.getLDWModeLevel()) {
//                return;
//            }
//
//            Switch ldw = (Switch) view;
//
//            boolean isEnable = mApp.mMHConfigFile.getLDWModeLevel() != 0;
//            if (ldw.isChecked() != isEnable) {
//                mApp.mMHConfigFile.setLDWModeLevel(ldw.isChecked() ? 2 : 0);
//                //showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
//
//            if (ldw.isChecked() && null != mApp.mMHConfigFile.getLDWSpeed()) {
//                layoutLDWSpeed.setVisibility(View.VISIBLE);
//            } else {
//                layoutLDWSpeed.setVisibility(View.GONE);
//            }
//        }
//    }


//    class ReadFileRunnable implements Runnable {
//        @Override
//        public void run() {
//            Socket socket= null;
//            byte[]  configFile = null;
//            try {
//                Log.i(TAG, String.format("start read file thread. Socket connect to %s:%d",
//                        mApp.mIp,mApp.mPort));
//                socket = new Socket();
//                InetSocketAddress addr = new InetSocketAddress(mApp.mIp, mApp.mPort);
//                socket.connect(addr, Constants.SOCKET_CONNECT_TIMEOUT);
////                Log.d(TAG, "Send read file request.");
////                byte[] pack = getReadPack(mFileName);
////                sendMsg(socket, pack);
////                Log.d(TAG, "Waiting read file response");
////                configFile = recvMsg(socket);
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//                Log.e(TAG, e.getMessage());
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e(TAG, e.getMessage());
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e(TAG, e.getMessage());
//            }finally {
//                if (null != socket) {
//                    try {
//                        Log.d(TAG, "close read file socket");
//                        socket.close();
//                    } catch (IOException e) {
//
//                    }
//                }
//            }
//
//            //parseConfigFile(configFile);
//            Message msg = mHandler.obtainMessage(MSG_READ_CONFIG_FILE_RESULT);
//            msg.obj = configFile;
//            mHandler.sendMessage(msg);
//        }
//    }

//    /**
//     * Refreshs the settings when activity creates or network chanages.
//     */
//    private void readConfig() {
//        Log.d(TAG, "refreshConfig");
//        if (mApp.isOnCAN && null!= mApp.mIp && mApp.mPort>0) {
//            //loadingAnimation(true);
//            new Thread(new ReadFileRunnable()).start();
//        }
//    }


//    private void sendMsg(Socket socket, byte[] buffer) {
//        try {
//            if (null != buffer && buffer.length > 0) {
//                DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
//                writer.write(buffer);
//                writer.flush();
//                Log.d(TAG, String.format("Send data %d bytes", buffer.length));
//            } else {
//                Log.e(TAG, "Buffer is null or length = 0");
//            }
//        } catch (IOException e) {
//            Log.e(TAG, e.toString());
//        }
//    }

//    private void parseConfigFile(byte[] buffer) {
//        if (null==buffer)
//            return;
//
//        Log.i(TAG, String.format("Receive config file, size is %d bytes", buffer.length));
//        AM_AWS_SETUP configFile = new AM_AWS_SETUP(buffer, 0, buffer.length);
//        refreshMHConfig(configFile);
//    }

//    private boolean parseWriteResp(byte[] buffer) {
//        Log.d(TAG, "parseWriteResp");
//        if (null == buffer) {
//            return false;
//        }
//        boolean result = true;
//        MsgBase msg = MsgFactory.getInstance().create(buffer);
//        if (null == msg || !msg.decode()) {
//            return false;
//        }
//
//        if (msg.getHeader().MsgServiceType == ServiceType.SERVICE_FILE
//                && msg.getHeader().MsgType == MessageType.FILE_WRITE_RESP) {
//            TLVClass tlv = msg.getBody().get(TLVType.TP_FILE_NAME_ID);
//            if (null != tlv && null != tlv.getValue()) {
//                String fileName = (String)tlv.getValue();
//                Log.d(TAG, String.format("file name : %s", fileName));
//            }
//            tlv = msg.getBody().get(TLVType.TP_FILE_PARA_ID);
//            if (null != tlv && null != tlv.getValue()) {
//                int fileLen = (int) tlv.getValue();
//                Log.d(TAG, String.format("file length : %d", fileLen));
//            }
//        } else {
//            result = false;
//        }
//        return result;
//    }

//    private void loadingAnimation(boolean isEnabled) {
//        if (isEnabled) {
//            ivLoading.setVisibility(View.VISIBLE);
//            mLoadingAnimation.start();
//        } else {
//            ivLoading.setVisibility(View.INVISIBLE);
//            mLoadingAnimation.stop();
//        }
//    }

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

    private void initControls() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.warning_green));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh called from SwipeRefreshLayout");
                Log.d(TAG, "isRefreshing = " + mSwipeRefreshLayout.isRefreshing());
                initiateRefresh();
            }
        });

        appLayout = (RelativeLayout) findViewById(R.id.appUpgrade);
        firmwareLayout = (RelativeLayout) findViewById(R.id.firmwareUpgrade);
        btnCheckUpgrade = (Button) findViewById(R.id.btnCheckUpgrade);
        btnAppUpgrade = (Button) findViewById(R.id.btnAppUpgrade);
        btnFirmwareUpgrade = (Button) findViewById(R.id.btnFirmareUpgrade);

        txtAppUpgradeInfo = (TextView) findViewById(R.id.txtAppUpgradeInfo);
        txtFirmwareUpgradeInfo = (TextView) findViewById(R.id.txtFirmwareUpgradeInfo);
        txtUpgradeInfo = (TextView) findViewById(R.id.txtUpgradeInfo);

        //warningLayout = (LinearLayout) findViewById(R.id.SettingsWarning);

        applyLayout = (RelativeLayout) findViewById(R.id.settingsApply);
        applyLayout.setVisibility(View.GONE);
        txtApplyWarning = (TextView) findViewById(R.id.settingsApplyWarning);
        btnSettingsConfirm = (Button) findViewById(R.id.btnSettingsConfirm);
        btnSettingsCancel = (Button) findViewById(R.id.btnSettingsCancel);
    }

    private void hideSpinnerDropDown(Spinner spinner) {
        try {
            Method method = Spinner.class.getDeclaredMethod("onDetachedFromWindow");
            method.setAccessible(true);
            method.invoke(spinner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //The settings can be set when app is connecting to CAN device and the speed is zero.
    //onresume, network changed , speed changed, this method will be called
    private void refreshControls() {
        Log.d(TAG, "refreshControls");
        boolean enabled = mApp.isOnCAN && (mApp.speed == 0);

        if (enabled) {
            updateSettingsApplyInfo(asStatus);
        } else {
            if (!mApp.isOnCAN) {
                updateSettingsApplyInfo(SettingsApplyStatus.NONE);
            } else {
                btnSettingsConfirm.setEnabled(false);
                btnSettingsCancel.setEnabled(false);
            }
        }

        btnCheckUpgrade.setEnabled(!mApp.isOnCAN);
        btnAppUpgrade.setEnabled(!mApp.isOnCAN);
        btnFirmwareUpgrade.setEnabled(mApp.isOnline || mApp.isOnCAN);

        mSwipeRefreshLayout.setEnabled(mApp.isOnCAN && (mApp.speed >= 0));

        Log.d(TAG, "speed = " + mApp.speed);

        //warningLayout.setVisibility((mApp.isOnCAN && mApp.speed != 0) ? View.VISIBLE : View.GONE);
    }


    /**
     * By abstracting the refresh settings process to a single method, the
     * app allow both the SwipeGestureLayout onRefresh() method and the
     * Refresh action item to refresh the content.
     */
    private void initiateRefresh() {
        if (mApp.isOnCAN) {
            FileReadReq fileReadReq = (FileReadReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_FILE,
                    MessageType.FILE_READ_REQ,
                    ResponseType.REQUEST);
            fileReadReq.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(Constants.MH_CONFIG_FILE);
            if (fileReadReq.encode()) {
                TcpIntentService.startActionFileService(this, fileReadReq.getData(), Constants.DESC_READ_MH_CONFIG);
            }

            ParameterReadReq msg = (ParameterReadReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_SETTINGS,
                    MessageType.PARA_READ_REQ,
                    ResponseType.REQUEST);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(this, msg.getData(), Constants.DESC_READ_PARAM);
            }
        }
    }

    /**
     * When the refresh settings thread finishes, it calls onRefreshComplete(),
     * which updates the data of settings and turns off the progress bar.
     */
    private void onRefreshComplete() {
        Log.d(TAG, "onRefreshComplete");
        //Stop the refreshing indicator.
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void refreshMHConfig(WarningConfig aMHConfig) {
        Log.d(TAG, "refreshMHConfig");
        if (null != warningPrefsFragment) {
            warningPrefsFragment.refresh(aMHConfig);
        }
    }

    private boolean isUserInteractionEnabled() {
        return mApp.isOnCAN && mApp.speed == 0 &&
                (asStatus == SettingsApplyStatus.NONE || asStatus == SettingsApplyStatus.MODIFIED);
    }

    private void writeFirmware() {
        String filePath = mApp.getFirmwareFilePath();
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, filePath + "is not exist. Write firmware failed");
            Toast.makeText(this, getString(R.string.firmware_not_exist), Toast.LENGTH_LONG).show();
            return;
        }
        String fileName = file.getName();

        Log.d(TAG, String.format("Write file name : %s  ip : %s   port : %d", fileName,
                mApp.mIp, mApp.mPort));

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            //new Thread(new WriteFileRunnable()).start();
            FileWriteReq msg = (FileWriteReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_FILE,
                    MessageType.FILE_WRITE_REQ,
                    ResponseType.REQUEST);

            //add file name tlv value
            msg.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(fileName);
            //add file content
            try {
                InputStream inputStream = new FileInputStream(filePath);
                byte[] temp = new byte[64 * 1024];
                int readlen = inputStream.read(temp);
                byte[] buffer = new byte[readlen];
                Log.d(TAG, "file size : " + readlen);
                System.arraycopy(temp, 0, buffer, 0, readlen);
                msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (msg.encode()) {
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_WRITE_FIRMWARE);
                upgradeManager.showUploadDialog();
            }
        }
    }

    private boolean resetDevice() {
        Log.d(TAG, "resetDevice");
        boolean result = true;

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdResetReq msg = (CmdResetReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_CMD,
                    MessageType.CMD_RESET_REQ,
                    ResponseType.REQUEST);
            msg.getBody().get(TLVType.TP_CMD_DELAY).setValue(Constants.RESET_DELAY);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_RESET_DEVICE);
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    private boolean resetMobileye() {
        Log.d(TAG, "resetMobileye");
        boolean result = true;

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdResetMEReq msg = (CmdResetMEReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_CMD,
                    MessageType.CMD_RESET_ME_REQ,
                    ResponseType.REQUEST);
            msg.getBody().get(TLVType.TP_CMD_DELAY).setValue(Constants.RESET_DELAY);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_RESET_MOBILEYE);
            } else {
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }


    //Register BroadcastReceiver to track local work status
    private LocalBroadcastReceiver localReceiver;

    private void initLocalReceiver() {
        //Register BroadcastReceiver to track local work status
        localReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constants.READ_MH_CONFIG_RESULT_ACTION);

        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.WRITE_MH_CONFIG_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        //receive the result of uploading firmware upgrade file to canbox
        filter = new IntentFilter(Constants.FIRMWARE_UPLOAD_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.DOWNLOAD_WORK_STATUS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.APP_UPGRADE_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.FIRMWARE_UPGRADE_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.NETWORK_CHANGE_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.VERSION_INFO_UPDATE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.CMD_RESET_RESP_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.CMD_RESET_ME_RESP_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

//        filter = new IntentFilter(Constants.CMD_TEST_RESP_ACTION);
//        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.SPEED_CHANGED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.CMD_WRITE_FIRMWARE_REQ_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
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
                final String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);
                // Both this activity and Mainactivity2 can start a TcpIntentService. But Only
                // Mainactivity2 receive and process the result which TcpIntentService
                // broadcasts. So this just need to wait the settings update which Mainactivity2
                // broadcasts.
                if (action.equals(Constants.READ_MH_CONFIG_RESULT_ACTION)) {
                    dealReadMHConfigResult();
                    return;
                }

                if (action.equals(Constants.WRITE_MH_CONFIG_RESULT_ACTION)) {
                    dealWriteMHConfigResult(intent);
                    return;
                }

                if (action.equals(Constants.NETWORK_CHANGE_ACTION)) {
                    dealNetworkChange();
                    return;
                }

                if (action.equals(Constants.FIRMWARE_UPLOAD_RESULT_ACTION)) {
                    dealUploadFirmwareResult(intent);
                    return;
                }

                if (action.equals(Constants.VERSION_INFO_UPDATE_ACTION)) {
                    refreshUpgradeNotify();
                    return;
                }

                if (action.equals(Constants.CMD_RESET_RESP_ACTION)) {
                    dealCmdResetResp(intent);
                    return;
                }

                if (action.equals(Constants.CMD_RESET_ME_RESP_ACTION)) {
                    dealCmdResetMEResp(intent);
                    return;
                }

                if (action.equals(Constants.SPEED_CHANGED_ACTION)) {
                    dealSpeedChanged(intent);
                    return;
                }

                //Only process the broadcast which is started by self
                if (sender.equals(getLocalClassName())) {
                    switch (action) {
                        case Constants.DOWNLOAD_WORK_STATUS_ACTION:
                            dealDownloadWork(intent);
                            break;
                        case Constants.APP_UPGRADE_RESULT_ACTION:
                            dealAppUpgradeResult(intent);
                            break;
                        case Constants.FIRMWARE_UPGRADE_RESULT_ACTION:
                            dealFirmwareUpgradeResult(intent);
                            break;
                    }
                }
            }
        }

        private void dealReadMHConfigResult() {
            onRefreshComplete();
            if (mApp.mMHConfigFile != null) {
                updateSettingsApplyInfo(SettingsApplyStatus.NONE);
                refreshMHConfig(mApp.mMHConfigFile);
                refreshControls();
            }
        }

        private void dealWriteMHConfigResult(Intent intent) {
            int length = intent.getIntExtra(Constants.EXTEND_FILE_LENGTH, -1);
            Log.d(TAG, "Deal write MH config reslut. file lenght = " + length);
            Toast toast = Toast.makeText(SettingsActivity.this, "Write MH config. file lenght = " + length, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 0, 20);
            toast.show();

//            if (length > 0) {
////                Toast toast = Toast.makeText(SettingsActivity.this, getString(R.string.settings_next_acc_on),
////                        Toast.LENGTH_LONG);
////                toast.setGravity(Gravity.CENTER, 0, 0);
////                toast.show();
//                //txtApplyWarning.setText(R.string.settings_prepare_reset);
//                updateSettingsApplyInfo(SettingsApplyStatus.RESETTING);
//                //resetMobileye();
//                mHandler.removeMessages(MSG_RESET_MOBILEYE);
//                mHandler.sendEmptyMessageDelayed(MSG_RESET_MOBILEYE, 1000);
//            } else {
////                showApplyInfo(true);
//                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
//            }
        }

        private void dealNetworkChange() {
            refreshMHConfig(mApp.mMHConfigFile);
            refreshControls();
            refreshUpgradeNotify();
        }

        private void dealDownloadWork(Intent intent) {
            String status = intent.getStringExtra(Constants.EXTEND_DOWNLOAD_STATUS);
            int progress = intent.getIntExtra(Constants.EXTEND_DOWNLOAD_PROGRESS, 100);
            Log.d(TAG, String.format("dealDownloadWork status : %s  progress : %d",
                    status, progress));
            switch (status) {
                case Constants.DOWNLOAD_STATUS_START:
                    break;
                case Constants.DOWNLOAD_STATUS_CONNECTING:
                    break;
                case Constants.DOWNLOAD_STATUS_DOWNLOADING:
                    Log.d(TAG, "progress : " + progress);
                    if (upgradeManager != null)
                        upgradeManager.downloadProgress(progress);
                    break;
                case Constants.DOWNLOAD_STATUS_DOWNLOADED:
                    if (upgradeManager != null) {
                        upgradeManager.downloadFinish();
                        saveFirmwareFilePath(upgradeManager.getFileName());
                    }
                    refreshUpgradeNotify();
                    //Toast.makeText(this, getString(R.string.download_ok),Toast.LENGTH_LONG).show();
                    break;
                case Constants.DOWNLOAD_STATUS_FAIL:
                    if (upgradeManager != null) {
                        upgradeManager.downloadFail();
                    }
//                    Toast.makeText(this, getString(R.string.download_fail), Toast.LENGTH_LONG).show();
                    break;
            }
        }

        private void saveFirmwareFilePath(String filePath) {
            Log.d(TAG, "saveFirmwareFilePath filePath : " + filePath);
            if (filePath != null && filePath.toUpperCase().endsWith(Constants.FIRMWARE_EXTENSION)) {
                File file = new File(filePath);
                if (file.exists()) {
                    mApp.setFirmwareFilePath(filePath);
                    mApp.saveDevInfoForFirmware(mApp.mDevVersion);
                }
            }
        }

        private void dealAppUpgradeResult(Intent intent) {
            upgradeManager.closeCheckUpgradeDialog();
            String result = intent.getStringExtra(Constants.EXTEND_CHECK_UPGRADE_RESULT);
            mApp.setAppUpgradeUrl(result);
            refreshUpgradeNotify();
        }

        private void dealFirmwareUpgradeResult(Intent intent) {
            upgradeManager.closeCheckUpgradeDialog();
            String result = intent.getStringExtra(Constants.EXTEND_CHECK_UPGRADE_RESULT);
            mApp.setFirmwareUpgradeUrl(result);
            refreshUpgradeNotify();
        }

        private void dealUploadFirmwareResult(Intent intent) {
            String fileName = intent.getStringExtra(Constants.EXTEND_FILE_NAME);
            int len = intent.getIntExtra(Constants.EXTEND_FILE_LENGTH, 0);
            Log.d(TAG, String.format("Receive UploadFirmwareResult broadcast. FileName : %s " +
                    " File Lenght : %d ", fileName, len));
            //upgradeManager.closeUploadDialog();ps
            if (len > 0) {
                //if it is adasgate file, don't reset device
                if (fileName != null && fileName.toUpperCase().startsWith(Constants.HI3_GATE_PREFIX)) {
                    upgradeManager.closeUploadDialog();
                }else {
                    upgradeManager.setUploadDialogContent(getString(
                            R.string.upload_firmware_complete));
                    //upgradeManager.startCountDown();
                    if (!upgradeManager.isCancel) {
                        resetDevice();
                    }
                    //firmwareLayout.setVisibility(View.GONE);
                }
            } else {
                upgradeManager.setUploadDialogContent(getString(
                        R.string.upload_firmware_fail));
                upgradeManager.closeUploadDialog();
                Toast.makeText(SettingsActivity.this, R.string.upload_firmware_fail,
                        Toast.LENGTH_LONG).show();
            }
        }

        private void dealCmdResetResp(Intent intent) {
            int delay = intent.getIntExtra(Constants.EXTEND_DELAY, -1);
            Log.d(TAG, String.format("Receive CmdResetResp broadcast. Delay is %d", delay));
            if (delay >= 0) {
                upgradeManager.setUploadDialogContent(getString(
                        R.string.waiting_device_restart));
                upgradeManager.startCountDown();
            } else {
                upgradeManager.closeUploadDialog();
            }
        }

        private void dealCmdResetMEResp(Intent intent) {
            int delay = intent.getIntExtra(Constants.EXTEND_DELAY, -1);
            Log.d(TAG, String.format("Receive ComResetMEResp broadcast.Delay is %d", delay));
            if (delay >= 0) {
//                updateSettingsApplyInfo(SettingsApplyStatus.NONE);
//                Toast toast = Toast.makeText(SettingsActivity.this, R.string.waiting_device_restart,
//                        Toast.LENGTH_LONG);
//                toast.setGravity(Gravity.TOP, 0, 20);
//                toast.show();
                updateSettingsApplyInfo(SettingsApplyStatus.WAIT_RESTART);
                mHandler.removeMessages(MSG_WAITING_MOBILEYE_RESTART);
                mHandler.sendEmptyMessageDelayed(MSG_WAITING_MOBILEYE_RESTART, 5000);
            } else {
//                showApplyInfo(true);
                updateSettingsApplyInfo(SettingsApplyStatus.MODIFIED);
            }
        }

        private void dealSpeedChanged(Intent intent) {
            int speed = intent.getIntExtra(Constants.EXTEND_SPEED, -1);
            Log.d(TAG, String.format("Receive Speed changed broadcast. Speed is %d", speed));

            //close the apply dialog after mobileye has restarted
            if (speed >= 0 && applyLayout.getVisibility() == View.VISIBLE
                    && isWaitMobileyeRestart) {
                updateSettingsApplyInfo(SettingsApplyStatus.NONE);
                isWaitMobileyeRestart = false;
            }

            refreshControls();
        }
    }

    public void onBtnAboutClicked(View view) {
        Log.d(TAG, "open about activity");
        startActivity(new Intent(this, AboutActivity.class));
    }


    //-----------------------------------------------------------------------------------
    //|                           Online upgrade function                               |
    //-----------------------------------------------------------------------------------
    UpgradeManager upgradeManager = new UpgradeManager(this, this);

    /**
     * Implements UpgradeManager.downloadFile(). Download the upgrade file and save it to a
     * named file.
     */
    @Override
    public void downloadFile(String aUrlStr, String aFileName) {
        Log.d(TAG, String.format("downloadFile  url: %s   file : %s", aUrlStr, aFileName));
        DownloadIntentService.startActionDownload(this, aUrlStr, aFileName);
    }


    private void refreshUpgradeNotify() {
        Log.d(TAG, "refreshUpgradeNotify");
        //Refresh app upgrade info
        if (null != mApp.getAppUpgradeUrl()) {
            appLayout.setVisibility(View.VISIBLE);
            txtAppUpgradeInfo.setText(R.string.click_to_download);
        } else {
            appLayout.setVisibility(View.GONE);
        }

        /**
         * Refresh firmware upgrade info. First check the upgrade file whether exists.
         */
        String fileName = null;
        if (null != mApp.getFirmwareFilePath()) {
            File file = new File(mApp.getFirmwareFilePath());
            if (file.exists())
                fileName = file.getName();
        }
        Log.d(TAG, String.format("Firmware File Path : %s   name : %s",
                mApp.getFirmwareFilePath(), fileName));

        String urlName = mApp.getFirmwareUpgradeUrl();
        if (null != urlName) {
            urlName = urlName.substring(urlName.lastIndexOf("/") + 1);
        }
        Log.d(TAG, String.format("Firmware Upgrade Url : %s   name : %s",
                mApp.getFirmwareUpgradeUrl(), urlName));

        Log.d(TAG, String.format("isOnline = %b isOnCan = %b", mApp.isOnline, mApp.isOnCAN));

        if (mApp.isOnline) {
            //Check whether the firmware upgrade file has been downloaded.
            if (null != fileName && fileName.equalsIgnoreCase(urlName)
                    && mApp.mDevVersion.getDevHwVer() == mApp.mDevInfoForFirmware.getDevHwVer()
                    && mApp.mDevVersion.getDevSwVer() == mApp.mDevInfoForFirmware.getDevSwVer()) {
                firmwareLayout.setVisibility(View.VISIBLE);
                txtFirmwareUpgradeInfo.setText(R.string.connect_can_to_upgrade);
            } else if (urlName != null) {
                firmwareLayout.setVisibility(View.VISIBLE);
                txtFirmwareUpgradeInfo.setText(R.string.click_to_download);
            } else {
                firmwareLayout.setVisibility(View.GONE);
            }
        } else if (mApp.isOnCAN) {
            if (null != fileName && fileName.equalsIgnoreCase(urlName)) {
                if (mApp.mDevVersion.getDevHwVer() == mApp.mDevInfoForFirmware.getDevHwVer()
                        && mApp.mDevVersion.getDevSwVer() == mApp.mDevInfoForFirmware.getDevSwVer()) {
                    firmwareLayout.setVisibility(View.VISIBLE);
                    txtFirmwareUpgradeInfo.setText(R.string.click_to_upgrade);
                } else {
                    firmwareLayout.setVisibility(View.GONE);
                }
            } else {
                firmwareLayout.setVisibility(View.GONE);
            }
        } else {
            firmwareLayout.setVisibility(View.GONE);
        }

        //Refresh check upgrade info
        if (mApp.getAppUpgradeUrl() != null || mApp.getFirmwareUpgradeUrl() != null) {
            txtUpgradeInfo.setText("");
        } else {
            txtUpgradeInfo.setText(R.string.no_upgrade);
        }
    }

    public void onCheckUpgradeClick(View view) {
        Log.d(TAG, "onCheckUpgradeClick");

        int appVer = mApp.getAppVersionCode();
        if (CheckUpgradeIntentService.startActionQueryAppUpgrade(this, appVer)) {
            upgradeManager.showCheckUpgradeDialog();
        }
        String fwVersion = mApp.getFirmwareVersion();
        if (CheckUpgradeIntentService.startActionQueryFirmwareUpgrade(this, fwVersion)) {
            upgradeManager.showCheckUpgradeDialog();
        }
    }

    public void onDownloadAppClick(View view) {
        upgradeManager.setUrl(mApp.getAppUpgradeUrl());
        upgradeManager.showDownloadDialog();
    }

    public void onDownloadFirmwareClick(View view) {
        if (txtFirmwareUpgradeInfo.getText().equals(getString(R.string.click_to_download))) {
            upgradeManager.setUrl(mApp.getFirmwareUpgradeUrl());
            upgradeManager.showDownloadDialog();
        } else if (txtFirmwareUpgradeInfo.getText().equals(getString(R.string.click_to_upgrade))) {
            writeFirmware();
        } else if (txtFirmwareUpgradeInfo.getText().equals(getString(R.string.connect_can_to_upgrade))) {
            //Does nothing
        }
    }

    /**
     * The dialog fragment receives a reference to this Activity through the
     * Fragment.onAttach() callback, which it uses to call the following methods
     * defined by the UploadDialogFragment.NoticeDialogListener interface.
     */
    //Implement NoticeDialogListener method
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        //User touched the dialog's positive button
    }

    //Implement NoticeDialogListener method
    @Override
    public void onDialognegativeClick(DialogFragment dialog) {
        //User touched the dialog's negative button

    }

    //Implement NoticeDialogListener method
    @Override
    public void onDialogCancel(DialogFragment dialogFragment) {
        //User touched the BACK key
        upgradeManager.isCancel = true;
    }

    public void onBtnGetConfigClicked(View view) {
    }

    public void onBtnSetConfigClicked(View view) {
    }

    public void onBtnReadFileClicked(View view) {
    }

    public void onBtnWriteFileClick(View view) {
    }

    @Override
    public void warningPreferencesChanged(WarningPrefsFragment fragment, WarningConfig config) {
        writeMHConfig();
    }
}
