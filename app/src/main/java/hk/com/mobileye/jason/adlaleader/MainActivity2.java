package hk.com.mobileye.jason.adlaleader;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import java.io.FileOutputStream;

import hk.com.mobileye.jason.adlaleader.Net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.File.FileReadReq;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.File.FileReadResp;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.File.FileWriteResp;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings.DevVersion;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings.MHVersion;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings.ParameterReadReq;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings.ParameterSetReq;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings.WifiPassword;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Warning.DayStat;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.Net.NetManager;
import hk.com.mobileye.jason.adlaleader.Net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.Upgrade.UpgradeManager;
import hk.com.mobileye.jason.adlaleader.common.AM_AWS_SETUP;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;


public class MainActivity2 extends TabActivity {

    TabHost tabHost;

    private static final String TAG = "MainActivity";
    //The BroadcastReceiver that tracks network connectivity changes.
    private NetworkReceiver networkReceiver;
    private LocalBroadcastReceiver receiver;

    private MyApplication mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getActionBar()!=null)
            getActionBar().hide();
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
//        setContentView(R.layout.activity_main_activity2);
        setContentView(R.layout.tabhost);
        ExitManager.getInstance().addActivity(this);

        tabHost = this.getTabHost();
        TabSpec alarmSpec = tabHost.newTabSpec("Alarm").setIndicator("Alarm").setContent(
                new Intent(this, AlarmActivity.class));
        TabSpec chartSpec = tabHost.newTabSpec("Chart").setIndicator("Chart").setContent(
                new Intent(this, ChartActivity.class));
        TabSpec dvrSpec = tabHost.newTabSpec("DVR").setIndicator("DVR").setContent(
                new Intent(this, DVRActivity.class));
        TabSpec nightViewSpce = tabHost.newTabSpec("Debug").setIndicator("Debug").setContent(
                new Intent(this, NightViewActivity.class));
        TabSpec settingSpec = tabHost.newTabSpec("Settings").setIndicator("Settings").setContent(
                new Intent(this, SettingsActivity.class));
        tabHost.addTab(alarmSpec);
        tabHost.addTab(chartSpec);
        tabHost.addTab(dvrSpec);
        tabHost.addTab(nightViewSpce);
        tabHost.addTab(settingSpec);

        RadioButton debugViewBtn = (RadioButton) findViewById(R.id.rbtnDebugView);
        if (Constants.DEVELOPER_MODE) {
            debugViewBtn.setVisibility(View.VISIBLE);
        } else {
            debugViewBtn.setVisibility(View.GONE);
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChanged());

        mApp = (MyApplication)getApplication();

        initConnectnionInfoBar();

        initNetworkReceiver();
        initLocalReceiver();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestory");
        releaseNetworkReceiver();
        releaseLocalReceiver();
        mApp = null;
        //Must always call the super method at the end.
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
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

    class OnCheckedChanged implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedID) {
            switch (checkedID) {
                case R.id.rbtnAlarm:
                    tabHost.setCurrentTabByTag("Alarm");
                    break;
                case R.id.rbtnChart:
                    tabHost.setCurrentTabByTag("Chart");
                    break;
                case R.id.rbtnDVR:
                    tabHost.setCurrentTabByTag("DVR");
                    break;
                case R.id.rbtnDebugView:
                    tabHost.setCurrentTabByTag("Debug");
                    break;
                case R.id.rbtnSettings:
                    tabHost.setCurrentTabByTag("Settings");
                    break;
                default:
                    break;
            }
        }
    }

    //


    public void onConnectionInfoClicked(View view) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    LinearLayout connectionInfoBar;
    TextView connectionInfoText;

    private void initConnectnionInfoBar() {
        connectionInfoBar = (LinearLayout) findViewById(R.id.connectionInfoBar);
        connectionInfoText = (TextView) findViewById(R.id.connectionInfoText);
        connectionInfoBar.setVisibility(View.INVISIBLE);
    }

    private void showConnectionInfo(String info) {
        if (info != null && !info.equals("")) {
            connectionInfoText.setText(info);
            connectionInfoBar.setVisibility(View.VISIBLE);
        } else {
            connectionInfoBar.setVisibility(View.GONE);
        }
    }

    private void initNetworkReceiver() {
        //Registers BroadcastReceiver to track network connectivity changes.
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        this.registerReceiver(networkReceiver, intentFilter);
    }

    private void releaseNetworkReceiver() {
        //Unregisters BroadcastReceiver when app is destroyed.
        if (networkReceiver != null) {
            this.unregisterReceiver(networkReceiver);
            networkReceiver = null;
        }
    }

    /**
     * This BroadcastReceiver intercept the android.net.ConnectivityManager.CONNECTIVITY_ACTION,
     * which indicates a connection change. It checks whether the type is TYPE_WIFI.
     * If it is, it checks whether wifi is connected and SSID is ADASLeader. If it is, it sets
     * the online flag.
     */
    public class NetworkReceiver extends BroadcastReceiver {
        private NetworkReceiver() {
            // prevents instantiation by other packages.
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "network receiver");
            NetManager netManager = new NetManager(context);
            mApp.isOnCAN = netManager.isOnCAN();
            mApp.isOnline = netManager.isOnline();
            mApp.mMHConfigFile = null;

            if (mApp.isOnCAN) {
                mApp.mDevVersion.clear();
                mApp.mMHVersion.clear();
                mApp.mIp = netManager.getTcpServerAddr().getAddress().getHostAddress();
                mApp.mPort = netManager.getTcpServerAddr().getPort();
                showConnectionInfo(null);

                readParameter();
                readMHConfig();
            } else if (mApp.isOnline) {
                String info = context.getString(R.string.connection_wifi);
                showConnectionInfo(info);

//                CheckUpgradeIntentService.startActionQueryAppUpgrade(
//                        context, mApp.getAppVersionCode());
//
//                CheckUpgradeIntentService.startActionQueryFirmwareUpgrade(
//                        context, mApp.getFirmwareVersion());
                Log.d(TAG, info);
            } else {
                String info = context.getString(R.string.connection_no);
                showConnectionInfo(info);
                Log.d(TAG, info);
            }
            broadcastNetworkChange();
        }
    }

    private void broadcastNetworkChange() {
        Log.d(TAG, "Broadcast network change");
        Intent intent = new Intent(Constants.NETWORK_CHANGE_ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void initLocalReceiver() {
        //Registers BroadcastReceiver to track TcpIntentService work status
        IntentFilter intentFilter = new IntentFilter(Constants.TCP_WORK_STATUS_ACTION);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new LocalBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, intentFilter);

        intentFilter = new IntentFilter(Constants.APP_UPGRADE_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, intentFilter);
        intentFilter = new IntentFilter(Constants.FIRMWARE_UPGRADE_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                receiver, intentFilter);
    }

    private void releaseLocalReceiver() {
        //Unregisters BroadcastReceiver when app is destroyed.
        if (receiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        private LocalBroadcastReceiver() {
            // prevents instantiation by other packages.
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);
                Log.d(TAG, String.format("Receive broadcast : %s. Sender : %s", action, sender));

                // Both this activity and SettingActivity can start a TcpIntentService.
                // But only this receives and processes the status which the service broadcast.
                if (action.equals(Constants.TCP_WORK_STATUS_ACTION)) {
                    dealTcpWork(intent);
                    return;
                }

                if (sender.equals(getLocalClassName())) {
                    switch (action) {
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

        private void dealTcpWork(Intent intent) {
            int status = intent.getIntExtra(Constants.EXTENDED_TCP_STATUS,
                    Constants.STATE_ACTION_COMPLETE);
            String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);
            int description = intent.getIntExtra(Constants.EXTENTED_DESCRIPTION, Constants.DESC_UNKNOW);

            Log.d(TAG, String.format("Receive TcpIntentService broadcast. Sender : %s " +
                    " Status : %d Desc : %d", sender, status, description));
            switch (status) {
                case Constants.STATE_ACTION_STARTED:
                    break;
                case Constants.STATE_ACTION_CONNECTING:
                    break;
                case Constants.STATE_ACTION_SEND:
                    break;
                case Constants.STATE_ACTION_RECEIVE:
                    break;
                case Constants.STATE_ACTION_COMPLETE:
                    byte[] buffer = intent.getByteArrayExtra(Constants.EXTENDED_TCP_RECEIVE_DATA);
                    if (buffer != null) {
                        decodeMessage(buffer);
                    } else {
                        if (description==Constants.DESC_WRITE_FIRMWARE) {
                            broadcastUploadFirmwareReslut(null, 0);
                        } else if (description == Constants.DESC_RESET_DEVICE) {
                            broadcastCmdResetResp(-1);
                        } else if (description == Constants.DESC_RESET_MOBILEYE) {
                            broadcastCmdRestMEResp(-1);
                        } else if (description == Constants.DESC_TEST) {
                            broadcastCmdTestResp(-1);
                        } else if (description == Constants.DESC_WRITE_MH_CONFIG) {
                            broadcastWriteMHConfigResult(0);
                        }
                    }
                    if (description ==Constants.DESC_READ_MH_CONFIG)
                        broadcastReadMHConfigResult();
                    break;
                default:
                    break;
            }
        }

        private void dealAppUpgradeResult(Intent intent) {
            String result = intent.getStringExtra(Constants.EXTEND_CHECK_UPGRADE_RESULT);
            Log.d(TAG, "dealAppUpgradeResult : " + result);
            mApp.setAppUpgradeUrl(result);
        }

        private void dealFirmwareUpgradeResult(Intent intent) {
            String result = intent.getStringExtra(Constants.EXTEND_CHECK_UPGRADE_RESULT);
            Log.d(TAG, "dealFirmwareUpgradeResult : " + result);
            mApp.setFirmwareUpgradeUrl(result);
        }
    }

    private void broadcastReadMHConfigResult() {
        Log.d(TAG, "Broadcast read MH Config result");
        Intent intent = new Intent(Constants.READ_MH_CONFIG_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastUploadFirmwareReslut(String fileName, int length) {
        Log.d(TAG, "broadcast upload firmware result");
        Intent intent = new Intent(Constants.FIRMWARE_UPLOAD_RESULT_ACTION);
        if (fileName != null)
            intent.putExtra(Constants.EXTEND_FILE_NAME, fileName);
        intent.putExtra(Constants.EXTEND_FILE_LENGTH, length);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastCmdResetResp(int delay) {
        Log.d(TAG, "Boradcast reset command response");
        Intent intent = new Intent(Constants.CMD_RESET_RESP_ACTION);
        intent.putExtra(Constants.EXTEND_DELAY, delay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastCmdRestMEResp(int delay) {
        Log.d(TAG, "Broadcast reset mobileye command response");
        Intent intent = new Intent(Constants.CMD_RESET_ME_RESP_ACTION);
        intent.putExtra(Constants.EXTEND_DELAY, delay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastCmdTestResp(int workTime) {
        Log.d(TAG, "Broadcast test command response");
        Intent intent = new Intent(Constants.CMD_TEST_RESP_ACTION);
        intent.putExtra(Constants.EXTEND_WORK_TIME, workTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastWriteMHConfigResult(int length) {
        Log.d(TAG, "broadcast write MH Config result");
        Intent intent = new Intent(Constants.WRITE_MH_CONFIG_RESULT_ACTION);
        intent.putExtra(Constants.EXTEND_FILE_LENGTH, length);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void readMHConfig() {
        Log.d(TAG, "readMHConfig");

        FileReadReq fileReadReq = (FileReadReq) MsgFactory.getInstance().create(
                ServiceType.SERVICE_FILE,
                MessageType.FILE_READ_REQ,
                ResponseType.REQUEST);
        fileReadReq.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(Constants.MH_CONFIG_FILE);
        if (fileReadReq.encode()) {
            TcpIntentService.startActionFileService(this, fileReadReq.getData(), Constants.DESC_READ_MH_CONFIG);
        }
    }

    private void readParameter() {
        ParameterReadReq msg = (ParameterReadReq) MsgFactory.getInstance().create(
                ServiceType.SERVICE_SETTINGS,
                MessageType.PARA_READ_REQ,
                ResponseType.REQUEST);
        if (msg.encode()) {
            TcpIntentService.startActionFileService(this, msg.getData(), Constants.DESC_READ_PARAM);
        }
    }

    private void WriteParameter() {
        ParameterSetReq msg = (ParameterSetReq) MsgFactory.getInstance().create(
                ServiceType.SERVICE_SETTINGS,
                MessageType.PARA_SET_REQ,
                ResponseType.REQUEST);
        WifiPassword password = new WifiPassword(Constants.WIFI_PASSWORD);
        msg.getBody().get(TLVType.TP_WIFI_PASSWORD_ID).setValue(password);
        msg.getBody().get(TLVType.TP_TIME_ID).setValue(System.currentTimeMillis());
        if (msg.encode()) {
            TcpIntentService.startActionFileService(this, msg.getData(), Constants.DESC_WRITE_PARAM);
        }
    }

    private void decodeMessage(byte[] buffer) {
        Log.d(TAG, "decodeMessage");
        try {
            if (null == buffer) {
                throw new Exception("The buffer is null, can't decode!");
            }
            MsgBase msg = MsgFactory.getInstance().create(buffer);
            if (null == msg || !msg.decode()) {
                throw new Exception("MsgFactory create obj failed or obj decode failed!");
            }
            processMsg(msg);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    private void processMsg(MsgBase msg) {
        switch (msg.getHeader().MsgServiceType) {
            case ServiceType.SERVICE_FILE:
                switch (msg.getHeader().MsgType) {
                    case MessageType.FILE_WRITE_RESP:
                        processFileWriteResp(msg);
                        break;
                    case MessageType.FILE_READ_RESP:
                        processFileReadResp(msg);
                        break;
                }
                break;
            case ServiceType.SERVICE_WARNING:
                switch (msg.getHeader().MsgType) {
                    case MessageType.WARN_DAY_STAT_RESP:
                        processWarnDayStatResp(msg);
                        break;
                    case MessageType.WARN_MONTH_STAT_RESP:
                        processWarnMonthStatResp(msg);
                        break;
                }
                break;
            case ServiceType.SERVICE_SETTINGS:
                switch (msg.getHeader().MsgType) {
                    case MessageType.PARA_SET_RESP:
                        processParaWriteResp(msg);
                        break;
                    case MessageType.PARA_READ_RESP:
                        processParaReadResp(msg);
                        break;
                }
                break;
            case ServiceType.SERVICE_CMD:
                switch (msg.getHeader().MsgType) {
                    case MessageType.CMD_RESET_RESP:
                        processResetResp(msg);
                        break;
                    case MessageType.CMD_RESET_ME_RESP:
                        processResetMEResp(msg);
                        break;
                    case MessageType.CMD_TEST_RESP:
                        processTestResp(msg);
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void processFileWriteResp(MsgBase msg) {
        Log.d(TAG, "Process FileWrite response.");

        TLVClass tlv = msg.getBody().get(TLVType.TP_FILE_NAME_ID);
        String fileName = null;
        if (null != tlv && null != tlv.getValue()) {
            fileName = (String) tlv.getValue();
            Log.d(TAG, String.format("file name : %s", fileName));
        } else {
            Log.e(TAG, "File name is null !");
        }
        int fileLen = ((FileWriteResp)msg).getFileLength();
        if (fileLen>0){
            Log.d(TAG, String.format("file length : %d", fileLen));
        } else {
            Log.e(TAG, String.format("file length : %d", fileLen));
        }

        if (fileName != null && fileName.endsWith(Constants.FIRMWARE_EXTENSION)) {
            broadcastUploadFirmwareReslut(fileName, fileLen);
        }
        if (fileName != null && fileName.equals(Constants.MH_CONFIG_FILE)) {
            broadcastWriteMHConfigResult(fileLen);
        }

    }

    private void processFileReadResp(MsgBase msg) {
        Log.d(TAG, "Process FileRead response");

        TLVClass tlv = msg.getBody().get(TLVType.TP_FILE_NAME_ID);
        String fileName;
        if (tlv != null && tlv.getValue() != null) {
            fileName = (String) tlv.getValue();
            Log.d(TAG, "Receive file name : " + fileName);
        } else {
            Log.e(TAG, "Receive file name is null !");
        }

        if (((FileReadResp) msg).getFileLen() > 0) {
            tlv = msg.getBody().get(TLVType.TP_FILE_PARA_ID);
            if (null != tlv && null != tlv.getValueBytes()) {
                byte[] buffer = tlv.getValueBytes();

                Log.i(TAG, String.format("Receive file size is %d bytes", buffer.length));

                //Calculate the file crc
                int crc = MsgUtils.getCrc32(buffer, buffer.length);
                Log.d(TAG, String.format("%d -- CRC calculated      %d -- CRC in message",
                        crc, ((FileReadResp) msg).getFileCRC()));

                mApp.mMHConfigFile = new AM_AWS_SETUP(buffer, 0, buffer.length);
                //writeMHConfigToPhone(buffer);
            } else {
                Log.e(TAG, "Receive file para is null !");
            }
        } else {
            Log.e(TAG, "Receive file length is 0 !");
            //Todo: add refresh function
        }
    }

    private void writeMHConfigToPhone(byte[] buffer) {
        String fileName = UpgradeManager.getExternalStorageFilePath(Constants.MH_CONFIG_FILE);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(fileName);
            outputStream.write(buffer, 0, buffer.length);
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

    private void processParaWriteResp(MsgBase msg) {
        Log.d(TAG, "Process ParaWrite response.");

        TLVClass tlv = msg.getBody().get(TLVType.TP_WIFI_PASSWORD_ID);
        if (null != tlv && null != tlv.getValue()) {
            String password = (String) tlv.getValue();
            Log.d(TAG, String.format("password : %s", password));
        } else {
            Log.e(TAG, "Wifi Password is null !");
        }

        tlv = msg.getBody().get(TLVType.TP_TIME_ID);
        if (null != tlv && null != tlv.getValue()) {
            long t = (long) tlv.getValue();
            Log.d(TAG, String.format("Time is %tF %tT", t, t));
        } else {
            Log.e(TAG, "Time is null !");
        }
    }

    private void processParaReadResp(MsgBase msg) {
        Log.d(TAG, "Parse ParaRead response");
        boolean isBroadcast = false;

        TLVClass tlv = msg.getBody().get(TLVType.TP_DEV_VER_ID);
        if (tlv != null && tlv.getValue() != null) {
            mApp.mDevVersion = (DevVersion) tlv.getValue();
            Log.d(TAG, String.format("Receive DevVersion SN[%d]  SW[%d]  HW[%d]",
                    mApp.mDevVersion.getDevSn(), mApp.mDevVersion.getDevSwVer(),
                    mApp.mDevVersion.getDevHwVer()));
            mApp.saveDevVersion();
            isBroadcast = true;
        } else {
            Log.e(TAG, "Device Version is null !");
        }

        tlv = msg.getBody().get(TLVType.TP_MH_VER_ID);
        if (null != tlv && null != tlv.getValue()) {
            mApp.mMHVersion = (MHVersion) tlv.getValue();
            Log.i(TAG, String.format("Receive MHVersion SN[%s]  SW[%s]  VF[%s]",
                    mApp.mMHVersion.getMHSn(), mApp.mMHVersion.getMHSwVer(),
                    mApp.mMHVersion.getMHVfVer()));
            mApp.saveMHVersion();
        } else {
            Log.e(TAG, "MH Version is null");
        }

        tlv = msg.getBody().get(TLVType.TP_GATE_VER_ID);
        if (null != tlv && null != tlv.getValue()) {
            mApp.mGateVer = (int)tlv.getValue();
            Log.i(TAG, String.format("Receive GATEVersion [%08X]", mApp.mGateVer));
            mApp.saveGateVersion();
        } else {
            Log.e(TAG, "Gate Version is null");
        }

        tlv = msg.getBody().get(TLVType.TP_WIFI_PASSWORD_ID);
        if (null != tlv && null != tlv.getValue()) {
            WifiPassword password = (WifiPassword) (tlv.getValue());
            Log.d(TAG, String.format("password : %s\n%s", password.getPassword(),
                    MsgUtils.bytes2HexString(tlv.getValueBytes())));
        } else {
            Log.e(TAG, "Wifi Password is null !");
        }

        tlv = msg.getBody().get(TLVType.TP_TIME_ID);
        if (null != tlv && null != tlv.getValue()) {
            long t = (long) (tlv.getValue());
            Log.d(TAG, String.format("Time is %tF %tT\n%s", t, t,
                    MsgUtils.bytes2HexString(tlv.getValueBytes())));
        } else {
            Log.e(TAG, "Time is null !");
        }

        if (isBroadcast) {
            Log.d(TAG, "Broadcast version info update");
            Intent intent = new Intent(Constants.VERSION_INFO_UPDATE_ACTION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void processResetResp(MsgBase msg) {
        Log.d(TAG, "Parse Reset response");

        TLVClass tlv = msg.getBody().get(TLVType.TP_CMD_DELAY);
        int delay = -1;
        if (tlv != null && tlv.getValue() != null) {
            delay = (int) tlv.getValue();
            Log.d(TAG, String.format("Receive CmdResetResp delay = %d", delay));
        } else {
            Log.e(TAG, "delay is null !");
        }
        broadcastCmdResetResp(delay);
    }

    private void processResetMEResp(MsgBase msg) {
        Log.d(TAG, "Parse Reset Mobileye response");

        TLVClass tlv = msg.getBody().get(TLVType.TP_CMD_DELAY);
        int delay = -1;
        if (tlv != null && tlv.getValue() != null) {
            delay = (int) tlv.getValue();
            Log.d(TAG, String.format("Receive CmdResetMEResp delay = %d", delay));
        } else {
            Log.e(TAG, "delay is null !");
        }
        broadcastCmdRestMEResp(delay);
    }

    private void processTestResp(MsgBase msg) {
        Log.d(TAG, "Parse Test response");

        TLVClass tlv = msg.getBody().get(TLVType.TP_WORK_TIME);
        int workTime = -1;
        if (tlv != null && tlv.getValue() != null) {
            workTime = (int) tlv.getValue();
            Log.d(TAG, String.format("Receive CmdTestResp workTime = %d", workTime));
        } else {
            Log.e(TAG, "workTime is null !");
        }
        broadcastCmdTestResp(workTime);
    }

    private void processWarnDayStatResp(MsgBase msg) {
        Log.d(TAG, "Warn day statistic response");

        TLVClass tlv = msg.getBody().get(TLVType.TP_WARN_DAT_STAT);
        if (tlv != null && tlv.getValue() != null) {
            DayStat stat = (DayStat)tlv.getValue();
            int statDate = stat.getDate();
            int runTime = stat.getRunTime();
            double mileage = stat.getMileage() / 10.0;
            int statIndex = stat.getIndex();
            Log.d(TAG, String.format("Receive day statistics index: %d date: %d run time: %d " +
                  "mileage: %f",  statIndex, statDate, runTime, mileage));
            //mApp.mDayStats[0] = stat;
            if (runTime > 0)
                mApp.mDayStats.update(stat);
            Log.d(TAG, "Broadcast day stat update");
            Intent intent = new Intent(Constants.DAY_STAT_UPDATE_ACTION);
            intent.putExtra(Constants.EXTEND_DAY_STAT_DATE, statDate);
            intent.putExtra(Constants.EXTEND_DAY_STAT_INDEX, statIndex);
            intent.putExtra(Constants.EXTEND_DAY_STAT_RUN_TIME, runTime);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Log.e(TAG, "day stat is null !");
        }
    }

    private void processWarnMonthStatResp(MsgBase msg) {
        Log.d(TAG, "Warn month statistic response");

    }
}
