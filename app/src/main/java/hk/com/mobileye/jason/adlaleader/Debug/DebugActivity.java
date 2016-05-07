package hk.com.mobileye.jason.adlaleader.Debug;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.view.SlidingTabLayout;

public class DebugActivity extends FragmentActivity {
    private static final String TAG = "DebugActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new DebugPagerAdapter(getSupportFragmentManager()));

        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
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
                    fragment = new DebugFirmwareFragment();
                    break;
                case 1:
                    fragment = new DebugLogFragment();
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

}
