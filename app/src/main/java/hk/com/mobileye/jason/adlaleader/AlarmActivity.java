package hk.com.mobileye.jason.adlaleader;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketAddress;
import java.util.Date;

import hk.com.mobileye.jason.adlaleader.Net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Cmd.CmdSwitchScreen;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Cmd.CmdTestReq;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.DVR.DvrKey;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Warning.WarningData;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.Net.NetManager;
import hk.com.mobileye.jason.adlaleader.Net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.Net.UdpHelper;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;


public class AlarmActivity extends Activity {

    ImageView ivA2B1;
    ImageView ivA2B2;
    ImageView ivA2B3;
    ImageView ivA2B4Animation;   //red car
    ImageView ivA2B5;
    ImageView ivA2B5Animation;  //LDW Left Animate
    ImageView ivA2B6;
    ImageView ivA2B6Animation;   //LDW right Animate
    ImageView ivA2B9Animation;  //green car
    ImageView ivA2B10Animation;  //Pedestrian red
    ImageView ivA2B11Animation;  //Perdestrian yellow
    TextView txtSpeedLimit;
    TextView txtSpeed;
    TextView txtHMW;
    TextView txtInfo;

    AnimationDrawable ldwLeftAnimation;
    AnimationDrawable ldwRightAnimation;
    AnimationDrawable carRedAnimation;
    AnimationDrawable carGreenAnimation;
    AnimationDrawable pedestrianAnimation;
    AnimationDrawable pedestrianYellowAnimation;
    AnimatorSet speedSet;
    AnimatorSet hmwSet;

    private MyApplication mApp;
    static final String TAG = "AlarmActivity";

    //receive alarm message
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Constants.DEVELOPER_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        super.onCreate(savedInstanceState);

        if (getActionBar()!=null)
            getActionBar().hide();

        Log.d(TAG, TAG + "onCreate");
        setContentView(R.layout.activity_alarm);
        ExitManager.getInstance().addActivity(this);

        mApp = (MyApplication) getApplication();
        initControls();
        initNet();
        initNetworReceiver();
        initLocalReceiver();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, TAG + "onDestory");

        //Unregisters BroadcastReceiver when activity is destroied.
        releaseNetReceiver();
        releaseNet();
        releaseLocalReceiver();

        //Must always call the super method at the end.
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
        txtInfo.setText(String.format("%tT OnResume()", new Date()));
        //calcPosition();
        //Registers a listener whenever network connection changes.
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Unregisters the listener set in onResume()
        //It's best practice to unregister listeners when your app isn't
        //using them to cut down on unnecessary system overhead.
        //You do this in onPause().
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
//        getMenuInflater().inflate(R.menu.menu_alarm, menu);
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

    void initControls() {
        ivA2B1 = (ImageView) findViewById(R.id.imgA2B1);
        ivA2B2 = (ImageView) findViewById(R.id.imgA2B2);
        ivA2B3 = (ImageView) findViewById(R.id.imgA2B3);
        ivA2B4Animation = (ImageView) findViewById(R.id.imgA2B4);  //red car
        ivA2B5 = (ImageView) findViewById(R.id.imgA2B5);
        ivA2B5Animation = (ImageView) findViewById(R.id.imgA2B5Animate);//LDW Left
        ivA2B6 = (ImageView) findViewById(R.id.imgA2B6);
        ivA2B6Animation = (ImageView) findViewById(R.id.imgA2B6Animate);
        ivA2B9Animation = (ImageView) findViewById(R.id.imgA2B9);
        ivA2B10Animation = (ImageView) findViewById(R.id.imgA2B10);
        ivA2B11Animation = (ImageView) findViewById(R.id.imgA2B11);

        txtSpeedLimit = (TextView) findViewById(R.id.txtSpeedLimit);
        txtSpeed = (TextView) findViewById(R.id.txtSpeed);
        txtHMW = (TextView) findViewById(R.id.txtHMW);
        txtInfo = (TextView) findViewById(R.id.txtInfo);

        //Waring Animate
        ldwLeftAnimation = (AnimationDrawable) ivA2B5Animation.getDrawable();
        ldwRightAnimation = (AnimationDrawable) ivA2B6Animation.getDrawable();
        carRedAnimation = (AnimationDrawable)ivA2B4Animation.getDrawable();
        carGreenAnimation = (AnimationDrawable) ivA2B9Animation.getDrawable();
        pedestrianAnimation = (AnimationDrawable) ivA2B10Animation.getDrawable();
        pedestrianYellowAnimation = (AnimationDrawable)ivA2B11Animation.getDrawable();

        hmwSet = (AnimatorSet) AnimatorInflater.loadAnimator(AlarmActivity.this, R.animator.speeding);
        hmwSet.setTarget(txtHMW);

        speedSet = (AnimatorSet) AnimatorInflater.loadAnimator(AlarmActivity.this, R.animator.speeding);
        speedSet.setTarget(txtSpeed);

    }

    void calcPosition() {
        int width = ivA2B6.getWidth();
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        //txtInfo.append("A2B6 Width " + width);
        Log.v(TAG, "A2B6 Width = " + width);
    }

    public boolean onTouchEvent(MotionEvent event){
        if (event.getAction()== MotionEvent.ACTION_DOWN){
        }
        return super.onTouchEvent(event);
    }

    Thread recvThread;
    UdpHelper udpHelper = null;
    SocketAddress serverAddr = null;

    //Initialize the udp socket and receive thread
    //create handler processing receive data
    private void initNet() {
        Log.d(TAG, TAG + "initNet");
        handler = new AlarmHandler(this);

        udpHelper = new UdpHelper(handler);
        recvThread = new Thread(udpHelper);
        recvThread.start();

        handler.sendEmptyMessage(Constants.MSG_SEND_HEARTBEAT);
        handler.sendEmptyMessage(Constants.MSG_REFRESH_WARNING);

        //if connect ADASLeader wifi, open udp and send heartbeat
        NetManager netMgr = new NetManager(this);
        isSendHeartBeat = netMgr.isOnCAN();
        serverAddr = netMgr.getUdpServerAddr();
    }

    private static class AlarmHandler extends Handler {
        private final WeakReference<AlarmActivity> mActivity;

        public AlarmHandler(AlarmActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AlarmActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case Constants.MSG_WARNING:
                        activity.dealWarningData((byte[]) msg.obj);
                        break;
                    case Constants.MSG_SEND_HEARTBEAT:
                        //Log.d(TAG, " recv MSG_SEND_HEARTBEAT");
                        activity.dealSendHeartBeat();
                        break;
                    case Constants.MSG_REFRESH_WARNING:
                        //Log.d(TAG, " recv MSG_REFRESH_WARNING");
                        activity.dealRefreshWarning();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void releaseNet() {
        if (handler != null) {
            handler.removeMessages(Constants.MSG_WARNING);
            handler.removeMessages(Constants.MSG_SEND_HEARTBEAT);
            handler.removeMessages(Constants.MSG_REFRESH_WARNING);
            handler = null;
        }
        if (udpHelper != null) {
            udpHelper.isThreadDisable = true;
            udpHelper = null;
        }

        if (recvThread != null) {
            recvThread.interrupt();
            recvThread=null;
        }
    }

    private void dealWarningData(byte[] buf) {
//        Log.d(TAG, String.format("deal warning data \n%s", MsgUtils.bytes2HexString(buf)));
        WarningData data = (WarningData) (MsgFactory.getInstance().create(buf));

        if (data != null && data.decode()) {
            alarmLaneLeft(data.ldwLeft > 0);
            alarmLaneRight(data.ldwRight > 0);
            alarmPedestrian(data.pcw == 2);
            alarmPedestrianYellow(data.pcw == 1);

            //Speeding
            alarmSpeeding(data.overSpeed, data.speedLimit, data.speed);

            //First show FCW or UFCW
            if (data.fcw > 0 || data.ufcw > 0) {
                alarmRedCar(true);
                showRedCar(true);
                showGreenCar(false);
                alarmHMW(0);
            //Second show hmw
            } else if (data.hmw >= 100) {
                alarmHMW(data.hmw);
                alarmRedCar(false);
                showRedCar(true);
                showGreenCar(false);
            } else if (data.hmw > 0) {
                alarmHMW(data.hmw);
                alarmRedCar(false);
                showRedCar(false);
                alarmGreenCar(false);
                showGreenCar(true);
            } else {
                alarmHMW(0);
                alarmRedCar(false);
                showRedCar(false);
                showGreenCar(false);
            }

            lastWarningTime = System.currentTimeMillis();
        } else {
            Log.e(TAG, "data is null");
        }
    }

    long lastWarningTime;

    private void dealRefreshWarning() {
        if ((System.currentTimeMillis() - lastWarningTime) > Constants.REFRESH_WARNING_INTERVAL) {
            alarmGreenCar(false);
            alarmHMW(0);
            alarmLaneLeft(false);
            alarmLaneRight(false);
            alarmPedestrian(false);
            alarmPedestrianYellow(false);
            alarmRedCar(false);
            alarmGreenCar(false);
            showGreenCar(false);
            showRedCar(false);
            alarmSpeeding(0, -1, -1);
        }
        handler.removeMessages(Constants.MSG_REFRESH_WARNING);
        handler.sendEmptyMessageDelayed(Constants.MSG_REFRESH_WARNING, Constants.REFRESH_WARNING_INTERVAL);
    }

    //Monitor wifi state,if connect ADAL wifi , send heart beat.
    private static boolean isSendHeartBeat=false;

    private void dealSendHeartBeat() {
        if (isSendHeartBeat){
            sendHeartBeat();
        }
        handler.removeMessages(Constants.MSG_SEND_HEARTBEAT);
        handler.sendEmptyMessageDelayed(Constants.MSG_SEND_HEARTBEAT, Constants.HEAERBEAT_INTERVAL);
    }
    Runnable HeartBeatRunable = new Runnable() {
        @Override
        public void run() {
            if (isSendHeartBeat){ sendHeartBeat();}
            handler.removeMessages(Constants.MSG_SEND_HEARTBEAT);
            handler.postDelayed(this, Constants.HEAERBEAT_INTERVAL);
//            Log.d(TAG, "isSendHeartBeat = " + isSendHeartBeat);
        }
    };

    private void sendHeartBeat() {
        MsgBase msg = MsgFactory.getInstance().create(ServiceType.SERVICE_HEARTBEAT,
                MessageType.HEART_BEAT_REQ, ResponseType.REQUEST);
        msg.setSeq((UdpHelper.getSeq()));
        msg.encode();
        byte[] buffer = new byte[msg.getData().length];
        System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
        new SendUdpTask().execute(buffer);
    }

    private class SendUdpTask extends AsyncTask<byte[], Void, String> {
        @Override
        protected String doInBackground(byte[]... params) {
            try {
                return sendByUdp(params[0]);
            } catch (IOException e) {
                return getString(R.string.send_error);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            //Log.d(TAG, "SendUdpTask result : " + s);
        }
    }

    private String sendByUdp(byte[] buffer) throws IOException {
//        Log.d(TAG, String.format("Server : %s \nData : %s", serverAddr.toString(),
//                MsgUtils.bytes2HexString(buffer)));
        if (serverAddr!=null) {
            udpHelper.send(buffer, serverAddr);
            return getString(R.string.send_ok);
        }else {
            return getString(R.string.socket_null);
        }
    }

    public void alarmHMW(int hmw) {
        if (hmw > 0) {
            if (!hmwSet.isRunning())
                hmwSet.start();

            if (hmw >= 100) {
                hmw -= 100;
                txtHMW.setTextColor(getResources().getColor(R.color.warning_red));
            } else {
                txtHMW.setTextColor(getResources().getColor(R.color.warning_green));
            }

            txtHMW.setText(String.valueOf(hmw / 10.0));
            if (txtHMW.getVisibility() != View.VISIBLE) {
                txtHMW.setVisibility(View.VISIBLE);
            }
        }
        else {
            if (txtHMW.getVisibility() == View.VISIBLE) {
                txtHMW.setVisibility(View.INVISIBLE);
            }
            hmwSet.end();
        }
    }

    public void alarmSpeeding(int overSpeed, int speedLimit, int speed) {
        broadcastSpeedChanged(speed);

        //Sets speed
        if (speed >= 0) {
            txtSpeed.setText(String.valueOf(speed));
        } else {
            txtSpeed.setTextColor(getResources().getColor(R.color.waring_white));
            txtSpeed.setText("");
        }

        //Sets speed limit
        if (speedLimit > 0) {
            txtSpeedLimit.setText(String.valueOf(speedLimit));
            //Show speeding red circle if it is not visible.
            if (ivA2B1.getVisibility() != View.VISIBLE) {
                ivA2B1.setVisibility(View.VISIBLE);
            }
        } else {
            txtSpeedLimit.setText("");
            //Hide speeding red circle if it is not invisible.
            if (ivA2B1.getVisibility() != View.INVISIBLE) {
                ivA2B1.setVisibility(View.INVISIBLE);
            }
        }

        //Speeding
        if (overSpeed > 0 && speedLimit > 0) {
            if (!speedSet.isRunning())
                speedSet.start();

            double overRate = (speed - speedLimit) * 100.0 / speedLimit;
            if (overRate > +10.0) {
                txtSpeed.setTextColor(getResources().getColor(R.color.warning_red));
            } else if (overRate > +5.0) {
                txtSpeed.setTextColor(getResources().getColor(R.color.warning_yellow));
            } else {
                txtSpeed.setTextColor(getResources().getColor(R.color.waring_white));
            }
            //Not speeding
        } else {
            if (speedSet.isRunning()) {
                speedSet.end();
                txtSpeed.setTextSize(TypedValue.COMPLEX_UNIT_SP, 56);
                txtSpeed.setTextColor(getResources().getColor(R.color.waring_white));
            }
        }
    }

    public void alarmPedestrian(boolean isEnable) {
        if (isEnable) {
            ivA2B10Animation.setVisibility(View.VISIBLE);
            pedestrianAnimation.start();
        } else {
            ivA2B10Animation.setVisibility(View.INVISIBLE);
            pedestrianAnimation.stop();
        }
    }

    public void alarmPedestrianYellow(boolean isEnable) {
        if (isEnable) {
            ivA2B11Animation.setVisibility(View.VISIBLE);
            pedestrianYellowAnimation.start();
        } else {
            ivA2B11Animation.setVisibility(View.INVISIBLE);
            pedestrianYellowAnimation.stop();
        }
    }

    public void alarmRedCar(boolean isEnable) {
        if (isEnable) {
            ivA2B4Animation.setVisibility(View.VISIBLE);
            carRedAnimation.start();
        } else {
            ivA2B4Animation.setVisibility(View.INVISIBLE);
            carRedAnimation.stop();
        }
    }

    public void showRedCar(boolean isShow) {
        if (isShow) {
            ivA2B4Animation.setVisibility(View.VISIBLE);
        } else {
            ivA2B4Animation.setVisibility(View.INVISIBLE);
        }
    }

    public void alarmGreenCar(boolean isEnable) {
        if (isEnable) {
            ivA2B9Animation.setVisibility(View.VISIBLE);
            carGreenAnimation.start();
        } else {
            ivA2B9Animation.setVisibility(View.INVISIBLE);
            carGreenAnimation.stop();
        }
    }

    public void showGreenCar(boolean isShow) {
        if (isShow) {
            ivA2B9Animation.setVisibility(View.VISIBLE);
        } else {
            ivA2B9Animation.setVisibility(View.INVISIBLE);
        }
    }

    public void alarmLaneLeft(boolean isEnable) {
        if (isEnable) {
            ivA2B5Animation.setVisibility(View.VISIBLE);
            ldwLeftAnimation.start();
        } else {
            ivA2B5Animation.setVisibility(View.INVISIBLE);
            ldwLeftAnimation.stop();
        }
    }

    public void alarmLaneRight(boolean isEnable) {
        if (isEnable) {
            ivA2B6Animation.setVisibility(View.VISIBLE);
            ldwRightAnimation.start();
        } else {
            ivA2B6Animation.setVisibility(View.INVISIBLE);
            ldwRightAnimation.stop();
        }
    }

    private BroadcastReceiver receiver;

    //Register BroadcastReceiver to track network connectivity changes.
    private void initNetworReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);
    }

    private void releaseNetReceiver() {
        if (receiver != null) {
            this.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetManager netMgr = new NetManager(context);
            isSendHeartBeat = netMgr.isOnCAN();
            serverAddr = netMgr.getUdpServerAddr();
        }
    }

    private int speed = -1;
    private void broadcastSpeedChanged(int newSpeed) {
        mApp.speed = newSpeed;
        if (speed == newSpeed) return;
        speed = newSpeed;
        Log.d(TAG, "broadcast speed changed " + speed);
        Intent intent = new Intent(Constants.SPEED_CHANGED_ACTION);
        intent.putExtra(Constants.EXTEND_SPEED, speed);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private long mLastClickTime = 0;
    private int mClickCount = 0;
    public void onTestClicked(View view) {
        String msg;
        if ((System.currentTimeMillis() - mLastClickTime) > 2000) {
            mClickCount = 1;
        } else {
            mClickCount++;
            if (mClickCount > 5) {
                mClickCount = 0;
                if (!enterTestMode()) {
                    Toast toast = Toast.makeText(AlarmActivity.this, R.string.cant_open_test, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
        mLastClickTime = System.currentTimeMillis();
    }

    private boolean enterTestMode(){
        Log.d(TAG, "Test Mode");
        boolean result = true;

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdTestReq msg = (CmdTestReq)MsgFactory.getInstance().create(
                    ServiceType.SERVICE_CMD,
                    MessageType.CMD_TEST_REQ,
                    ResponseType.REQUEST);
            msg.getBody().get(TLVType.TP_WORK_TIME).setValue(1000*130);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_TEST);
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
        IntentFilter filter = new IntentFilter(Constants.CMD_TEST_RESP_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.DVR_KEY_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
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
            //Prevents instantiation by other packages
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                final String action = intent.getAction();

                if (action.equals(Constants.CMD_TEST_RESP_ACTION)) {
                    dealCmdTestResp(intent);
                    return;
                }

                if (action.equals(Constants.DVR_KEY_ACTION)) {
                    dealDVRKey(intent);
                    return;
                }

                if (action.equals(Constants.CMD_SWITCH_SCREEN_REQ_ACTION)) {
                    dealCmdSwitchScreen(intent);
                    return;
                }
            }
        }

        private void dealCmdTestResp(Intent intent) {
            int workTime = intent.getIntExtra(Constants.EXTEND_WORK_TIME, -1);
            Log.d(TAG, String.format("Receive CmdTestResp. WorkTime is %d", workTime));
            int strID;
            if (workTime >= 0) {
                strID = R.string.test_mode;
            } else {
                strID = R.string.cant_open_test;
            }
            Toast toast = Toast.makeText(AlarmActivity.this, strID, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        private void dealDVRKey(Intent intent) {
            byte key = 0;
            key = intent.getByteExtra(Constants.EXTEND_DVR_KEY, key);
            if (key == 0)
                return;

            DvrKey msg = (DvrKey) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_DVR,
                    MessageType.DVR_KEY_INFO,
                    ResponseType.INFO);
            msg.getBody().get(TLVType.TP_DVR_KEY).setValue(key);
            msg.setSeq((UdpHelper.getSeq()));
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                new SendUdpTask().execute(buffer);
            }
        }

        private void dealCmdSwitchScreen(Intent intent) {
            byte id = 0;
            id = intent.getByteExtra(Constants.EXTEND_SCREEN_ID, id);

            CmdSwitchScreen msg = (CmdSwitchScreen) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_CMD,
                    MessageType.CMD_SWITCH_SCREEN_REQ,
                    ResponseType.REQUEST);
            msg.getBody().get(TLVType.TP_SWITCH_SCREEN).setValue(id);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                new SendUdpTask().execute(buffer);
            }
        }
    }

}