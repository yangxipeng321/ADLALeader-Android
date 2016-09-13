package com.adasleader.jason.adasleader.control;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.MyApplication;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrFormat;

import hk.com.mobileye.jason.adasleader.R;

/**
 * Created by Jason on 2016/9/13.
 *
 */
public class FormatDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dvr_format_alert).setTitle(R.string.dvr_format_title);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication myApp = (MyApplication) getActivity().getApplication();
                if (myApp.isOnCAN && null != myApp.mIp && myApp.mPort > 0) {
                    DvrFormat msg = new DvrFormat();
                    if (msg.encode()) {
                        byte[] buf = new byte[msg.getMsgLength()];
                        System.arraycopy(msg.getData(), 0, buf, 0, buf.length);
                        Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                        intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buf);
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return builder.create();
    }

}
