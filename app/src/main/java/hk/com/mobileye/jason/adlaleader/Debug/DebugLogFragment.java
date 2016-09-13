package hk.com.mobileye.jason.adlaleader.debug;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.common.logger.LogFragment;
import hk.com.mobileye.jason.adlaleader.common.logger.LogWrapper;
import hk.com.mobileye.jason.adlaleader.common.logger.MessageOnlyLogFilter;
import hk.com.mobileye.jason.adlaleader.net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug.LogAdasgateSwitch;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug.LogMCUSwitch;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugLogFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "DebugLogFragment";

    private MyApplication mApp;

    private LogFragment mLogFragment;
    MessageOnlyLogFilter msgFilter;

    public DebugLogFragment() {
        // Required empty public constructor
    }

    ToggleButton cbHI3Log;
    ToggleButton cbHI3MCU;
    ToggleButton cbHI3DVR;
    ToggleButton cbHI3Stat;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debug_log, container, false);
        view.findViewById(R.id.btnClear).setOnClickListener(this);
        view.findViewById(R.id.cbAppLog).setOnClickListener(this);
        view.findViewById(R.id.cbMCU).setOnClickListener(this);

        cbHI3Log = (ToggleButton) view.findViewById(R.id.cbHI3Log);
        cbHI3Log.setOnClickListener(this);
        cbHI3MCU = (ToggleButton) view.findViewById(R.id.cbHI3MCU);
        cbHI3MCU.setOnClickListener(this);
        cbHI3DVR = (ToggleButton) view.findViewById(R.id.cbHI3DVR);
        cbHI3DVR.setOnClickListener(this);
        cbHI3Stat = (ToggleButton) view.findViewById(R.id.cbHI3Stat);
        cbHI3Stat.setOnClickListener(this);

        initLogging();

        mApp = (MyApplication) getActivity().getApplication();

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
            case R.id.cbHI3Log:
            case R.id.cbHI3MCU:
            case R.id.cbHI3DVR:
            case R.id.cbHI3Stat:
                onCBHI3LogClicked(v);
                break;
            case R.id.cbMCU:
                onCBMcuLogClicked(v);
                break;
        }
    }

    private void onCBShowLogClicked(View view) {
        ToggleButton cb = (ToggleButton) view;
        Log.d(TAG, "checkbox is " + cb.isChecked());
        if (cb.isChecked()) {
            msgFilter.setNext(mLogFragment.getLogView());
        } else {
            msgFilter.setNext(null);
        }
    }

    private void onCBHI3LogClicked(View view) {
        ToggleButton cb = (ToggleButton) view;

        if (mApp.isOnCAN && mApp.mIp != null && mApp.mPort > 0) {
            int mask = 0;
            mask = cbHI3Log.isChecked() ? (mask | 0x1) : (mask & 0xfffffffe);
            mask = cbHI3MCU.isChecked() ? (mask | 0x2) : (mask & 0xfffffffd);
            mask = cbHI3DVR.isChecked() ? (mask | 0x4) : (mask & 0xfffffffb);
            mask = cbHI3Stat.isChecked() ? (mask | 0x8) : (mask & 0xfffffff7);

            LogAdasgateSwitch msg = new LogAdasgateSwitch();
            msg.getBody().get(TLVType.TP_LOG_SWITCH_ID).setValue(mask);
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void onCBMcuLogClicked(View view) {
        ToggleButton cb = (ToggleButton) view;
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            int mask = 0;
            if (cb.isChecked()) mask = 0xFFFFFFFF;
            LogMCUSwitch msg = new LogMCUSwitch();
            msg.getBody().get(TLVType.TP_LOG_SWITCH_ID).setValue(mask);
            if (msg.encode()) {
                Log.e(TAG, "onCBMcuLogClicked");
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    public void dealLogContent(Intent intent) {
        Log.e(TAG, "dealUpdateResult");

        byte[] buf = intent.getByteArrayExtra(Constants.EXTEND_LOG_CONTENT);
        if (null == buf || buf.length < MsgConst.MSG_LEN_HEADER) {
            return;
        }

        MsgBase msg = MsgFactory.getInstance().create(buf);
        if (null != msg && msg.decode() ) {
            TLVClass tlv = msg.getBody().get(TLVType.TP_LOG_CONTENT_ID);
            if (null != tlv) {
                String head = "";
                if (msg.getHeader().MsgType == MessageType.LOG_ADASGATE_CONTENT)
                    head = "[ADASGATE]";
                else if (msg.getHeader().MsgType == MessageType.LOG_MCU_CONTENT)
                    head = "[MCU]";

                String log = new String(tlv.getValueBytes());
                mLogFragment.getLogView().appendToLog(head + log +"\n");
            }
        }
    }


}
