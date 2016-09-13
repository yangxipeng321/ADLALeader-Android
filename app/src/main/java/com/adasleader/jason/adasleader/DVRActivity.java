package com.adasleader.jason.adasleader;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.ExitManager;
import com.adasleader.jason.adasleader.control.CtrlFragment;
import com.adasleader.jason.adasleader.control.CtrlInteractionListener;

import java.util.ArrayList;

import hk.com.mobileye.jason.adasleader.R;


public class DVRActivity extends FragmentActivity
        implements CtrlInteractionListener {

    public static final String TAG = "DVRActivity";

    private CtrlFragment ctrlFragment;

    //private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dvr);

//        Fragment fragment = getFragmentManager().findFragmentById(R.id.videoContainer);
//        if (fragment.getView() != null)
//            videoView = (VideoView) fragment.getView().findViewById(R.id.videoView);


        if (savedInstanceState == null) {
//            android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
//            DVRControlFragment ctrlFragment = new DVRControlFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            ctrlFragment = new CtrlFragment();
            transaction.replace(R.id.controlContainer, ctrlFragment);
            transaction.commit();
        }

        initLocalReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");

        byte id = (byte)(0);
        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        releaseLocalReceiver();
        super.onDestroy();
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
        getMenuInflater().inflate(R.menu.menu_dvr, menu);
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

    public void onFragmentInteraction(View view) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.videoContainer);
        if (fragment.getView() != null) {
            TextView textView = (TextView) fragment.getView().findViewById(R.id.textView);
            textView.setText(view.getContentDescription());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private LocalBroadCastReceiver localReceiver;

    private void initLocalReceiver() {
        localReceiver = new LocalBroadCastReceiver();
        IntentFilter filter = new IntentFilter(Constants.CMD_SWITHCH_SCREEN_NOTIFY_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.DVR_FILE_LIST_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.DVR_PLAY_FILE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
    }

    private void releaseLocalReceiver() {
        if (null != localReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
            localReceiver = null;
        }
    }

    private class LocalBroadCastReceiver extends BroadcastReceiver {
        private LocalBroadCastReceiver(){
            //Prevents instantiation by other packages
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                final String action = intent.getAction();

                switch (action) {
                    case Constants.CMD_SWITHCH_SCREEN_NOTIFY_ACTION:
                        dealSwitchScreenNotify(intent);
                        break;
                    case Constants.DVR_FILE_LIST_ACTION:
                        dealFileList(intent);
                        break;
                    case Constants.DVR_PLAY_FILE_ACTION:
                        dealPlayFile(intent);
                        break;
                }
            }
        }

        private void dealSwitchScreenNotify(Intent intent) {
            byte id = 0;
            id = intent.getByteExtra(Constants.EXTEND_SCREEN_ID, id);
            switch (id) {
                case Constants.SCREEN_CAR_CAR:
                case Constants.SCREEN_APP_CAR:
                    ctrlFragment.setCurrentPage(0);
                    break;
                case Constants.SCREEN_CAR_ME:
                case Constants.SCREEN_APP_ME:
                    ctrlFragment.setCurrentPage(1);
                    break;
                case Constants.SCREEN_CAR_DVR:
                case Constants.SCREEN_APP_DVR:
                    ctrlFragment.setCurrentPage(2);
            }
        }

        private void dealFileList(Intent intent) {
            ArrayList<String> fileList = intent.getStringArrayListExtra(Constants.EXTEND_DVR_FILE_LIST);
            if (null != fileList && fileList.size() > 0) {
                if (null != CtrlFragment.dvrFragment) {
                    CtrlFragment.dvrFragment.SetListView(fileList);
                }
            }
        }

        private void dealPlayFile(Intent intent) {
            byte fileType = intent.getByteExtra(Constants.EXTEND_DVR_PLAY_FILE_TYPE, (byte)0);
            byte playCtrl = intent.getByteExtra(Constants.EXTEND_DVR_PLAY_CTRL, (byte)0);
            String fileName = intent.getStringExtra(Constants.EXTEND_DVR_PLAY_FILE_NAME);

            if (fileType > 0 && null != fileName && null != CtrlFragment.dvrFragment) {
                CtrlFragment.dvrFragment.setPlayFile(fileType, playCtrl, fileName);
            }
        }
    }
}
