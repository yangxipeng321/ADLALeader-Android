package hk.com.mobileye.jason.adlaleader.debug;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Cmd.CmdSaveFrame;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileReadReq;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;

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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveFrame:
                saveFrame();
                break;
            case R.id.btnDownloadFrame:
                downloadFrame();
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
}
