package hk.com.mobileye.jason.adlaleader.debug;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.common.logger.LogFragment;
import hk.com.mobileye.jason.adlaleader.common.logger.LogWrapper;
import hk.com.mobileye.jason.adlaleader.common.logger.MessageOnlyLogFilter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugLogFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "DebugLogFragment";

    private LogFragment mLogFragment;
    MessageOnlyLogFilter msgFilter;

    public DebugLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debug_log, container, false);
        view.findViewById(R.id.btnClear).setOnClickListener(this);
        view.findViewById(R.id.cbAppLog).setOnClickListener(this);

        initLogging();

        return view;
    }

    private void initLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        mLogFragment = (LogFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.debug_log_fragment);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnClear:
                mLogFragment.getLogView().setText("");
                break;
            case R.id.cbAppLog:
                onCBShowLogClicked(v);
                break;
        }
    }

    private void onCBShowLogClicked(View view) {
        CheckBox cb = (CheckBox) view;
        Log.d(TAG, "checkbox is " + cb.isChecked());
        if (cb.isChecked()) {
            msgFilter.setNext(mLogFragment.getLogView());
        } else {
            msgFilter.setNext(null);
        }
    }

}
