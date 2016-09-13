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

import hk.com.mobileye.jason.adasleader.R;

public class AboutActivity extends Activity {

    private static final String TAG = "AboutActivity";
    private TextView txtVersion;
    private TextView txtGateVersion;
    private TextView txtDevVersion;
    private TextView txtMHVersion;
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
        myApp = (MyApplication)getApplication();
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
        txtVersion = (TextView) findViewById(R.id.txtVersion);
        txtGateVersion = (TextView) findViewById(R.id.txtGateVersion);
        txtDevVersion = (TextView) findViewById(R.id.txtDevVersion);
        txtMHVersion = (TextView) findViewById(R.id.txtMHVersion);
    }

    private void refreshVersion() {
        if (myApp == null)
            return;

        txtVersion.setText(String.format(Locale.getDefault(), "%s(%d)", myApp.getAppVersionName(),
                myApp.getAppVersionCode()));

        if (myApp.mGateVer == 0)
            txtGateVersion.setText("");
        else
            txtGateVersion.setText(String.format("%08X", myApp.mGateVer));

        if (myApp.mDevVersion != null) {
            txtDevVersion.setText(String.format("\nDev SN : %08X\nDev SW : %08X\nDev HW : %08X",
                    myApp.mDevVersion.getDevSn(), myApp.mDevVersion.getDevSwVer(),
                    myApp.mDevVersion.getDevHwVer()));
        } else {
            txtDevVersion.setText("");
        }

        if (myApp.mMHVersion != null) {
            txtMHVersion.setText(String.format("\nMH SN : %s \nMH SW : %s\nMH VF : %s \n",
                    myApp.mMHVersion.getMHSn(), myApp.mMHVersion.getMHSwVer(),
                    myApp.mMHVersion.getMHVfVer()));
        } else {
            txtMHVersion.setText("");
        }
    }
}
