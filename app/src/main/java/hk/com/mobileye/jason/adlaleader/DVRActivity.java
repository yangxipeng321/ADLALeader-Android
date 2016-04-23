package hk.com.mobileye.jason.adlaleader;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import hk.com.mobileye.jason.adlaleader.Control.CtrlFragment;
import hk.com.mobileye.jason.adlaleader.Control.CtrlInteractionListener;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;


public class DVRActivity extends FragmentActivity
        implements CtrlInteractionListener {

    public static final String TAG = "DVRActivity";

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
            CtrlFragment ctrlFragment = new CtrlFragment();

            transaction.replace(R.id.controlContainer, ctrlFragment);
            transaction.commit();
        }
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

            String desc = view.getContentDescription().toString();
            textView.setText(view.getContentDescription());

            dealKey(desc);
            dealSwitchScreen(desc);
            dealVideoControl(desc);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void dealKey(String desc) {
        byte key;
        if (desc.equals(getString(R.string.dvr_up))) {
            key = 1;
        } else if (desc.equals(getString(R.string.dvr_down))) {
            key = 2;
        } else if (desc.equals(getString(R.string.dvr_confirm))) {
            key = 3;
        } else if (desc.equals(getString(R.string.dvr_cancel))) {
            key = 4;
        } else if (desc.equals(getString(R.string.dvr_home))) {
            key = 5;
        } else if (desc.equals(getString(R.string.dvr_photo))) {
            key = 6;
        } else {
            return;
        }

        Intent intent = new Intent(Constants.DVR_KEY_ACTION);
        intent.putExtra(Constants.EXTEND_DVR_KEY, key);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void dealSwitchScreen(String desc) {
        byte id;
        if (desc.equals(getString(R.string.screen_car))) {
            id = 1;
        } else if (desc.equals(getString(R.string.screen_mobileye))) {
            id = 2;
        } else if (desc.equals(getString(R.string.screen_dvr))) {
            id = 3;
        }else if (desc.equals(getString(R.string.screen_video))) {
            id = 4;
        } else {
            return;
        }

        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void dealVideoControl(String desc) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.controlContainer);
        if (fragment.getView() == null)
            return;

        Button button = (Button) fragment.getView().findViewById(R.id.btnPlayVideo);

        if (desc.equals(getString(R.string.play_video))) {
            button.setText(getString(R.string.stop_video));
            button.setContentDescription(getString(R.string.stop_video));
            try {
                //videoView.setVideoPath("rtsp://218.204.223.237:554/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");
                //videoView.setVideoPath("rtsp://192.168.168.102:6880/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");
//                videoView.setVideoPath("rtsp://192.168.168.1:6880/test:network-caching=1000");
//                MediaController mc = new MediaController(this);
//                videoView.setMediaController(mc);
//                videoView.start();
//                videoView.requestFocus();
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.getMessage());
            }
        } else if (desc.equals(getString(R.string.stop_video))) {
            button.setText(getString(R.string.play_video));
            button.setContentDescription(getString(R.string.play_video));
//            videoView.stopPlayback();
        }
    }




}
