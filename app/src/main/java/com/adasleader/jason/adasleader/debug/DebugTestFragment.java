package com.adasleader.jason.adasleader.debug;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.adasleader.jason.adasleader.R;
import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.MyApplication;
import com.adasleader.jason.adasleader.common.logger.Log;
import com.adasleader.jason.adasleader.net.Message.Factory.MsgFactory;
import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdCalibrateReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdSaveFrame;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdStaModeReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrKey;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugDVRCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugFPGACmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugMCUCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileReadReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileWriteReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.ParameterReadReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnClearStat;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVType;
import com.adasleader.jason.adasleader.net.TcpIntentService;
import com.adasleader.jason.adasleader.net.UdpHelper;

import java.util.Locale;


/**
 *
 */
public class DebugTestFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DebugTestFragment";
    private static final int OFFSET = 50;
    private MyApplication mApp;
    private NumberPicker picker;
    private byte[] mCarpara = null;

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

        initFoeYOffset(view);

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
            case R.id.btnFoeYOffset:
                dealSetFoeYOffset();
                break;
            case R.id.btnReadFoeOffset:
                dealReadFoeOffset();
                break;
            case R.id.btnWriteFoeOffset:
                dealWriteFoeOffset();
                break;
        }
    }

    private void saveFrame() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdSaveFrame msg = new CmdSaveFrame();

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

    private void downloadFrame() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            FileReadReq msg = new FileReadReq();
            msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue("" + String.valueOf(count));
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
        if (key == 4) {
            ParameterReadReq msg = (ParameterReadReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_SETTINGS,
                    MessageType.PARA_READ_REQ,
                    ResponseType.REQUEST);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(), Constants.DESC_READ_PARAM);
            }
            return;
        }


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
        byte id = (byte) (0x80);
        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void dealCali(int cmd) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            CmdCalibrateReq msg = new CmdCalibrateReq();
            msg.getBody().get(TLVType.TP_CALIBRATE_CMD_ID).setValue((byte) cmd);
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

    private void initFoeYOffset(View rootView) {
        Button btn = rootView.findViewById(R.id.btnFoeYOffset);
        btn.setOnClickListener(this);
        btn.setEnabled(false);

        btn = rootView.findViewById(R.id.btnReadFoeOffset);
        btn.setOnClickListener(this);

        btn = rootView.findViewById(R.id.btnWriteFoeOffset);
        btn.setOnClickListener(this);
        btn.setEnabled(false);

        String[] values = new String[2 * OFFSET + 1];
        for (int i = 0; i < values.length; i++) {
            values[i] = String.valueOf(i - OFFSET);
        }
        picker = rootView.findViewById(R.id.yOffsetPicker);
        picker.setDisplayedValues(values);
        picker.setMinValue(0);
        picker.setMaxValue(values.length - 1);
        picker.setValue(OFFSET);
    }

    private void dealSetFoeYOffset() {
        if (null == picker) return;

        int value = picker.getValue() - OFFSET;
        int key = ((value & 0xFF) << 8) + 3;
        String txt = String.format("SetFoeYOffset value %X   key %X", value & 0xff, key);

        Log.d(TAG, txt);

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
                Toast.makeText(getActivity().getApplicationContext(), txt, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void dealReadFoeOffset() {
        Log.d(TAG, "read CARPARA");

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            FileReadReq fileReadReq = (FileReadReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_FILE,
                    MessageType.FILE_READ_REQ,
                    ResponseType.REQUEST);
            fileReadReq.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(Constants.CAR_PARA_CONFIG_PREFIX);
            if (fileReadReq.encode()) {
                TcpIntentService.startActionFileService(getActivity(), fileReadReq.getData(), Constants.DESC_READ_CARPARA);
            }
        }
    }

    public void dealReadCARPARAResult(Intent intent) {
        byte[] buf = intent.getByteArrayExtra(Constants.EXTEND_CARPARA);
        if (null == buf || buf.length != Constants.CARPARA_LEN) return;

        mCarpara = buf;

        picker.setValue(mCarpara[Constants.CARPARA_FOE_Y_OFFSET_INDEX] + OFFSET);

        View view = getView();
        if (view != null) {
            getView().findViewById(R.id.btnFoeYOffset).setEnabled(true);
            getView().findViewById(R.id.btnWriteFoeOffset).setEnabled(true);
        }
    }

    private void dealWriteFoeOffset() {
        if (null == mCarpara || mCarpara.length != Constants.CARPARA_LEN) return;

        byte[] buf = new byte[Constants.CARPARA_LEN];
        int bodyLen = Constants.CARPARA_LEN - 4;
        System.arraycopy(mCarpara, 0, buf, 0, bodyLen);
        buf[Constants.CARPARA_FOE_Y_OFFSET_INDEX] = (byte) (picker.getValue() - OFFSET);

        int crc = MsgUtils.getCrc32(buf, bodyLen);
        byte[] temp = MsgUtils.int2Bytes(crc);
        System.arraycopy(temp, 0, buf, bodyLen, 4);

        Log.d(TAG, String.format(Locale.getDefault(), "src [39]= %d\n%s",
                mCarpara[Constants.CARPARA_FOE_Y_OFFSET_INDEX], MsgUtils.bytes2HexString(mCarpara)));
        Log.d(TAG, String.format(Locale.getDefault(), "out [39]= %d\n%s",
                buf[Constants.CARPARA_FOE_Y_OFFSET_INDEX], MsgUtils.bytes2HexString(buf)));

        writeCARPARA(buf);
    }

    private void writeCARPARA(byte[] buf) {
        Log.d(TAG, String.format(Locale.getDefault(), "Write file name : %s  ip : %s   port : %d",
                Constants.CAR_PARA_CONFIG_PREFIX, mApp.mIp, mApp.mPort));
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            FileWriteReq msg = (FileWriteReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_FILE,
                    MessageType.FILE_WRITE_REQ,
                    ResponseType.REQUEST);
            //add file name tlv value
            msg.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(
                    Constants.CAR_PARA_CONFIG_PREFIX);
            //add file content tlv value  length + crc + content
            msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue(buf);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(), Constants.DESC_WRITE_CARPARA);
            }
        }
    }

    public void dealWriteCARPARResult(Intent intent) {
        String fileName = intent.getStringExtra(Constants.EXTEND_FILE_NAME).toUpperCase();
        int len = intent.getIntExtra(Constants.EXTEND_FILE_LENGTH, 0);

        if (fileName.startsWith(Constants.CAR_PARA_CONFIG_PREFIX)) {
            String msg = String.format(Locale.getDefault(), "Write %s len=%d success !",
                    fileName, len);
            Toast toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        }
    }
}
