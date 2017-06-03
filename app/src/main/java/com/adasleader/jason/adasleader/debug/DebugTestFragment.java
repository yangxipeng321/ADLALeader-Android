package com.adasleader.jason.adasleader.debug;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.MyApplication;
import com.adasleader.jason.adasleader.common.logger.Log;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdCalibrateReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdSaveFrame;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdStaModeReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrKey;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugDVRCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugFPGACmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugMCUCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileReadReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnClearStat;
import com.adasleader.jason.adasleader.net.Message.TLVType;
import com.adasleader.jason.adasleader.net.TcpIntentService;
import com.adasleader.jason.adasleader.net.UdpHelper;

import hk.com.mobileye.jason.adasleader.R;

/**
 *
 */
public class DebugTestFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DebugTestFragment";
    private MyApplication mApp;

    private byte count = 10;

    public DebugTestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debug_test, container, false);
        mApp = (MyApplication) getActivity().getApplication();
        view.findViewById(R.id.btnReset).setOnClickListener(this);
        view.findViewById(R.id.btnStaMode).setOnClickListener(this);
        view.findViewById(R.id.btnOutputVideo).setOnClickListener(this);

        view.findViewById(R.id.btnClearStat).setOnClickListener(this);
        view.findViewById(R.id.btnStartCali).setOnClickListener(this);
        view.findViewById(R.id.btnStopCali).setOnClickListener(this);

        //DVR
        view.findViewById(R.id.btnDVRUp).setOnClickListener(this);
        view.findViewById(R.id.btnDVRDown).setOnClickListener(this);
        view.findViewById(R.id.btnDVRConfirm).setOnClickListener(this);
        view.findViewById(R.id.btnDVRCancel).setOnClickListener(this);
        view.findViewById(R.id.btnDVRHome).setOnClickListener(this);
        view.findViewById(R.id.btnDebugMcu1).setOnClickListener(this);
        view.findViewById(R.id.btnDebugMcu2).setOnClickListener(this);
        view.findViewById(R.id.btnDebugMcu3).setOnClickListener(this);
        view.findViewById(R.id.btnDebugMcu4).setOnClickListener(this);
        view.findViewById(R.id.btnDebugFpga1).setOnClickListener(this);
        view.findViewById(R.id.btnDebugFpga2).setOnClickListener(this);
        view.findViewById(R.id.btnDebugFpga3).setOnClickListener(this);
        view.findViewById(R.id.btnDebugFpga4).setOnClickListener(this);
        view.findViewById(R.id.btnDebugDvr1).setOnClickListener(this);
        view.findViewById(R.id.btnDebugDvr2).setOnClickListener(this);
        view.findViewById(R.id.btnDebugDvr3).setOnClickListener(this);
        view.findViewById(R.id.btnDebugDvr4).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        byte key;
        switch (v.getId()) {
            case R.id.btnReset:
                reset();
                //saveFrame();
                break;
            case R.id.btnStaMode:
                staMode();
                //downloadFrame();
                break;
            case R.id.btnOutputVideo:
                dealOutputVideo();
                break;
            case R.id.btnClearStat:
                dealClearStat();
                break;
            case R.id.btnStartCali:
                dealCali(1);
                break;
            case R.id.btnStopCali:
                dealCali(2);
                break;
            case R.id.btnDVRUp:
                key = 1;
                dealDVRKey(key);
                break;
            case R.id.btnDVRDown:
                key = 2;
                dealDVRKey(key);
                break;
            case R.id.btnDVRConfirm:
                key = 3;
                dealDVRKey(key);
                break;
            case R.id.btnDVRCancel:
                key = 4;
                dealDVRKey(key);
                break;
            case R.id.btnDVRHome:
                key = 5;
                dealDVRKey(key);
                break;
            case R.id.btnDebugMcu1:
                key = 1;
                dealDebugMcu(key);
                break;
            case R.id.btnDebugMcu2:
                key = 2;
                dealDebugMcu(key);
                break;
            case R.id.btnDebugMcu3:
                key = 3;
                dealDebugMcu(key);
                break;
            case R.id.btnDebugMcu4:
                key = 4;
                dealDebugMcu(key);
                break;
            case R.id.btnDebugFpga1:
                key = 1;
                dealDebugFpga(key);
                break;
            case R.id.btnDebugFpga2:
                key = 2;
                dealDebugFpga(key);
                break;
            case R.id.btnDebugFpga3:
                key = 3;
                dealDebugFpga(key);
                break;
            case R.id.btnDebugFpga4:
                key = 4;
                dealDebugFpga(key);
                break;
            case R.id.btnDebugDvr1:
                key = 1;
                dealDebugDvr(key);
                break;
            case R.id.btnDebugDvr2:
                key = 2;
                dealDebugDvr(key);
                break;
            case R.id.btnDebugDvr3:
                key = 3;
                dealDebugDvr(key);
                break;
            case R.id.btnDebugDvr4:
                key = 4;
                dealDebugDvr(key);
                break;
        }
    }

    private void saveFrame(){
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
             CmdSaveFrame msg= new CmdSaveFrame();

            msg.getBody().get(TLVType.TP_FRAME_COUNT_ID).setValue(count);
            if (msg.encode()) {
                Log.d(TAG, "saveFrame");
                //Intent can't send data over 48K, it will report error
                //"FAILED BINDER TRANSACTION!!!". So we usa a global variable.
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_CMD_SAVE_FRAME);
            }
        }

    }

    private void downloadFrame(){
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            FileReadReq msg = new FileReadReq();
            msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue(""+String.valueOf(count));
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_CMD_DOWNLOAD_FRAME);
            }
        }
    }

    // send reset command to 3518
    private void reset() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdResetReq msg = new CmdResetReq();
            int delay = 2000;  //2 seconds
            msg.getBody().get(TLVType.TP_CMD_DELAY).setValue(delay);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_RESET_DEVICE);
            }
        }

    }

    // change 3518's wifi to sta mode
    private void staMode() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdStaModeReq msg = new CmdStaModeReq();
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_CMD_STA_MODE);
            }
        }
    }

    private void dealDVRKey(byte key) {
        Log.e(TAG, "dealDVRKey");

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DvrKey msg = new DvrKey();
            msg.getBody().get(TLVType.TP_DVR_KEY).setValue(key);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealDebugMcu(int key) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DebugMCUCmd msg = new DebugMCUCmd();
            msg.getBody().get(TLVType.TP_DEBUG_CMD_ID).setValue(key);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealDebugFpga(int key) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DebugFPGACmd msg = new DebugFPGACmd();
            msg.getBody().get(TLVType.TP_DEBUG_CMD_ID).setValue(key);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buf = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buf, 0, buf.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buf);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealDebugDvr(int key) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DebugDVRCmd msg = new DebugDVRCmd();
            msg.getBody().get(TLVType.TP_DEBUG_CMD_ID).setValue(key);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buf = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buf, 0, buf.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buf);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealOutputVideo() {
        byte id = (byte)(0x80);
        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void dealCali(int cmd) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdCalibrateReq msg = new CmdCalibrateReq();
            msg.getBody().get(TLVType.TP_CALIBRATE_CMD_ID).setValue((byte)cmd);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buf = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buf, 0, buf.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buf);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealClearStat() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            WarnClearStat msg = new WarnClearStat();
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buf = new byte[msg.getMsgLength()];
                System.arraycopy(msg.getData(), 0, buf, 0, buf.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buf);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }
}
