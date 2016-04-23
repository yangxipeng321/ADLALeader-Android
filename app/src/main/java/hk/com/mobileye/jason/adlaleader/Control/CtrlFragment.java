package hk.com.mobileye.jason.adlaleader.Control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.view.SlidingTabLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CtrlInteractionListener} interface
 * to handle interaction events.
 */
public class CtrlFragment extends Fragment {

    private CtrlInteractionListener mListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CtrlInteractionListener) {
            mListener = (CtrlInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new ContorlPagerAdapter(getActivity().getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mSlidingTabLayout.setOnPageChangeListener(new CtrlPageChangeListener());
    }

    class ContorlPagerAdapter extends FragmentPagerAdapter {

        public ContorlPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 0:
                    fragment = CtrlCarFragment.newInstance(Integer.toString(position),
                            Integer.toString(position));
                    break;
                case 1:
                    fragment = new CtrlMeFragment();
                    break;
                case  2:
                    fragment = new CtrlDVRFragment();
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
                    title = getString(R.string.screen_car);
                    break;
                case 1:
                    title = getString(R.string.screen_mobileye);
                    break;
                case 2:
                    title = getString(R.string.screen_dvr);
                    break;
            }
            return title;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    private class CtrlPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position >= 0 && position < 3) {
                byte id = (byte)(position + 1);
                Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
                intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }



}
