package hk.com.mobileye.jason.adlaleader.debug;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Cmd.CmdSaveFrame;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrKey;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileReadReq;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.net.UdpHelper;

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
        view.findViewById(R.id.btnSaveFrame).setOnClickListener(this);
        view.findViewById(R.id.btnDownloadFrame).setOnClickListener(this);

        //DVR
        view.findViewById(R.id.btnDVRUp).setOnClickListener(this);
        view.findViewById(R.id.btnDVRDown).setOnClickListener(this);
        view.findViewById(R.id.btnDVRConfirm).setOnClickListener(this);
        view.findViewById(R.id.btnDVRCancel).setOnClickListener(this);
        view.findViewById(R.id.btnDVRHome).setOnClickListener(this);
        view.findViewById(R.id.btnKey10).setOnClickListener(this);
        view.findViewById(R.id.btnKey11).setOnClickListener(this);
        view.findViewById(R.id.btnKey12).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        byte key;
        switch (v.getId()) {
            case R.id.btnSaveFrame:
                saveFrame();
                break;
            case R.id.btnDownloadFrame:
                downloadFrame();
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
            case R.id.btnKey10:
                key = 10;
                dealDVRKey(key);
                break;
            case R.id.btnKey11:
                key = 11;
                dealDVRKey(key);
                break;
            case R.id.btnKey12:
                key = 12;
                dealDVRKey(key);
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
}
