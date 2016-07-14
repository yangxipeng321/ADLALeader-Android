package hk.com.mobileye.jason.adlaleader.debug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.view.SlidingTabLayout;

public class DebugActivity extends FragmentActivity {
    private static final String TAG = "DebugActivity";

    private ViewPager mViewPager;
    private DebugPagerAdapter mPagerAdapter;
    private DebugFirmwareFragment mFirmwareFragment;
    private DebugLogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mPagerAdapter = new DebugPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.WHITE);

        initLocalReceiver();
    }

    private void initLocalReceiver() {
        //Register BroadcastReceiver to track local work status
        IntentFilter filter = new IntentFilter(Constants.FIRMWARE_UPLOAD_RESULT_ACTION);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastReceiver localReceiver = new LocalBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);

        filter = new IntentFilter(Constants.LOG_CONTENT_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
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

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            final String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);

            Log.d(TAG, String.format("Receive broadcast : %s. Sender : %s", action, sender));

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


}
