package hk.com.mobileye.jason.adlaleader.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.view.SlidingTabLayout;

public class DebugActivity extends FragmentActivity {
    private static final String TAG = "DebugActivity";

    private DebugFirmwareFragment mFirmwareFragment;
    private DebugLogFragment mLogFragment;
    private LocalBroadcastReceiver localReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        DebugPagerAdapter mPagerAdapter = new DebugPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.WHITE);

        initLocalReceiver();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        releaseLocalReceiver();
        super.onDestroy();
        Log.d(TAG, "onDestory");
    }



    private void initLocalReceiver() {
        localReceiver = new LocalBroadcastReceiver();

        IntentFilter filter = new IntentFilter(Constants.FIRMWARE_UPLOAD_RESULT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.LOG_CONTENT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
    }

    private void releaseLocalReceiver() {
        if (null != localReceiver) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
            localReceiver = null;
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            //final String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);
            switch (action) {
                case Constants.FIRMWARE_UPLOAD_RESULT_ACTION:
                    dealFirmwareUploadResult(intent);
                    break;
                case Constants.LOG_CONTENT_ACTION:
                    dealLogContent(intent);
                    break;
            }
        }

        private void dealFirmwareUploadResult(Intent intent) {
            if (mFirmwareFragment != null)
                mFirmwareFragment.dealUpdateResult(intent);
        }

        private void dealLogContent(Intent intent) {
            if (null != mLogFragment) {
                mLogFragment.dealLogContent(intent);
            }
        }
    }

    class DebugPagerAdapter extends FragmentPagerAdapter {

        public DebugPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    Log.e(TAG, "Fragment getItem");
                    if (mFirmwareFragment == null)
                        mFirmwareFragment = new DebugFirmwareFragment();
                    fragment = mFirmwareFragment;
                    break;
                case 1:
                    if (null == mLogFragment)
                        mLogFragment = new DebugLogFragment();
                    fragment = mLogFragment;
                    break;
                case 2:
                    fragment = new DebugTestFragment();
                    break;
                default:
                    fragment = null;
            }
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            switch (position) {
                case 0:
                    title = "Firmware";
                    break;
                case 1:
                    title = "Log";
                    break;
                case 2:
                    title = "Test";
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //show an expanation
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[0] + " grant", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[0] + " deny", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
