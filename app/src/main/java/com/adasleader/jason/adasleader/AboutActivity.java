package com.adasleader.jason.adasleader;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.adasleader.jason.adasleader.common.MyApplication;

import java.util.Locale;


public class AboutActivity extends Activity {

    private static final String TAG = "AboutActivity";
    private TextView txtVersion;
    private TextView txtGateVersion;
    private TextView txtDevVersion;
    private TextView txtMHVersion;
    private TextView txtFPGAVersion;
    private TextView txtDVRVersion;
    private MyApplication myApp;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
                actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        myApp = (MyApplication) getApplication();
        initControls();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshVersion();

        ActionBar bar = getActionBar();
        Log.d(TAG, "actionbar is " + bar);
    }

    @Override
    protected void onDestroy() {
        myApp = null;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_about, menu);
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

    private void initControls() {
        txtVersion = findViewById(R.id.txtVersion);
        txtGateVersion = findViewById(R.id.txtGateVersion);
        txtDevVersion = findViewById(R.id.txtDevVersion);
        txtMHVersion = findViewById(R.id.txtMHVersion);
        txtFPGAVersion = findViewById(R.id.txtFPGAVersion);
        txtDVRVersion = findViewById(R.id.txtDVRVersion);
    }

    private void refreshVersion() {
        if (myApp == null)
            return;

        txtVersion.setText(String.format(Locale.getDefault(), "%s(%d)", myApp.getAppVersionName(),
                myApp.getAppVersionCode()));

        if (myApp.mGateVer == 0)
            txtGateVersion.setText("\nGate : ");
        else
            txtGateVersion.setText(String.format("\nGate : %08X", myApp.mGateVer));

        if (myApp.mDevVersion != null) {
            //如果DevSn为0，说明还没有获取到设备的版本信息
            if (myApp.mDevVersion.getDevSn() == 0)
                txtDevVersion.setText("\nModel ID : \n\nDev SW : \nDev HW : ");
            else
                txtDevVersion.setText(String.format("\nModel ID : %08X\n\nDev SW : %08X\nDev HW : %08X",
                        myApp.mDevVersion.getDevSn(), myApp.mDevVersion.getDevSwVer(),
                        myApp.mDevVersion.getDevHwVer()));
        } else {
            txtDevVersion.setText("");
        }

        if (myApp.mMHVersion != null) {
            txtMHVersion.setText(String.format("\nMH SN : %s \nMH SW : %s\nMH VF : %s",
                    myApp.mMHVersion.getMHSn(), myApp.mMHVersion.getMHSwVer(),
                    myApp.mMHVersion.getMHVfVer()));
        } else {
            txtMHVersion.setText("");
        }

        if (myApp.mFPGAVer == 0) {
            txtFPGAVersion.setText("\nFPGA : ");
        } else {
            txtFPGAVersion.setText(String.format("\nFPGA : %08X", myApp.mFPGAVer));
        }

        if (myApp.mDVRVer == 0) {
            txtDVRVersion.setText("\nDVR : ");
        } else {
            txtDVRVersion.setText(String.format("\nDVR : %08X", myApp.mDVRVer));
        }
    }
}
