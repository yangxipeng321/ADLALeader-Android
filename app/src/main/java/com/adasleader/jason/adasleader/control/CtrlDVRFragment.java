package com.adasleader.jason.adasleader.control;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.common.MyApplication;
import com.adasleader.jason.adasleader.common.logger.Log;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DVRRecord;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrFileListReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrPlay;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrPlayFileReq;
import com.adasleader.jason.adasleader.net.Message.TLVType;
import com.adasleader.jason.adasleader.net.TcpIntentService;
import com.adasleader.jason.adasleader.net.UdpHelper;

import java.util.ArrayList;

import hk.com.mobileye.jason.adasleader.R;

/**
  DVR Control Fragment.
  1. Show DVR screen
  2. List and play video files
  3. List and play emergency video files
  4. List and play picture files.
 */
public class CtrlDVRFragment extends Fragment {

    private static final String TAG = "CtrlDVRFragment";
    private MyApplication mApp;
    private int mFileType;

    private ListView listView;
    private FileAdapter mAdapter;
    private Button btnFormat;
    private byte curCtrl = 0;
    private float curAlpha = 0f;
    private int curButton = 0; //0 录像 1 视频 2 紧急视频 3 照片
    private ArrayList<ToggleButton> btns = new ArrayList<>();

    BtnClickListener listener;

    private ArrayList<String> mlist;

    public CtrlDVRFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "oncreateview");
        View view = inflater.inflate(R.layout.fragment_ctrl_dvr, container, false);

        mApp = (MyApplication) getActivity().getApplication();

        listener = new BtnClickListener();
        btnFormat = (Button) view.findViewById(R.id.btnDVRFormat);
        btnFormat.setOnClickListener(listener);
        view.findViewById(R.id.btnScrVideo).setOnClickListener(listener);

        btns.clear();
        addToggleButtonToList(view.findViewById(R.id.btnRecord));
        addToggleButtonToList(view.findViewById(R.id.btnDVRFileList));
        addToggleButtonToList(view.findViewById(R.id.btnDVRFCWFileList));
        addToggleButtonToList(view.findViewById(R.id.btnDVRPicList));

        checkToggleButton(curButton);

        if (null == mlist) {
            mlist = new ArrayList<>();
        }

        if (null == mAdapter) {
            mAdapter = new FileAdapter(mlist);
        }

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                mAdapter.notifyDataSetChanged();
                dealPlayFile((byte)1);
            }
        });
        listView.setAlpha(curAlpha);

        return view;
    }


    //    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        if (null != videoContainer && null != controlContainer) {
//            videoY = videoContainer.getTop();
//            videoContainer.measure(0, 0);
//            controlY = videoContainer.getMeasuredHeight();
//        }
//    }

    private class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnScrVideo:
                    dealScreenVideo();
                    break;
                case R.id.btnRecord:
                    checkToggleButton(0);
                    dealRecord();
                    break;
                case R.id.btnDVRFileList:
                    checkToggleButton(1);
                    dealGetFileList(Constants.DVR_FILE_TYPE_VIDEO);
                    break;
                case R.id.btnDVRFCWFileList:
                    checkToggleButton(2);
                    dealGetFileList(Constants.DVR_FILE_TYPE_FCW);
                    break;
                case R.id.btnDVRPicList:
                    checkToggleButton(3);
                    dealGetFileList(Constants.DVR_FILE_TYPE_PIC);
                    break;
                case R.id.btnDVRFormat:
                    dealFormat();
                    break;
            }
        }
    }

    private class FileAdapter extends BaseAdapter {

        private ArrayList<String> mFileList = null;
        private int mSelectedPosition = -1;

        public FileAdapter(ArrayList<String> fileList) {
            mFileList = fileList;
        }

        public void setSelectedPosition(int position) {
            mSelectedPosition = position;
        }

        public String getSelectedFile() {
            if (mSelectedPosition >= 0) {
                return mFileList.get(mSelectedPosition);
            } else {
                return null;
            }
        }

        @Override
        public int getCount() {
            return mFileList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view;
            if (null != convertView && convertView instanceof TextView) {
                view = (TextView) convertView;
            } else {
                view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.dvr_file_item,
                        parent, false);
            }

            if (position == mSelectedPosition) {
                view.setSelected(true);
                view.setPressed(true);
                view.setBackgroundColor(Color.RED);
            } else {
                view.setSelected(false);
                view.setPressed(false);
                view.setBackgroundColor(Color.TRANSPARENT);
            }
            view.setText(mFileList.get(position));
            return view;
        }

        public void clear() {
            mFileList.clear();
            notifyDataSetChanged();
        }

        public void addAll(ArrayList<String> list) {
            mFileList.addAll(list);
            notifyDataSetChanged();
        }

        public void setSelected(String fileName) {
            int index = mFileList.indexOf(fileName);
            if (index >= 0 && mSelectedPosition != index) {
                mSelectedPosition = index;
                notifyDataSetChanged();
                listView.setSelection(mSelectedPosition);
            }
        }
    }

    private void dealScreenVideo() {
        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        byte id =  Constants.SCREEN_APP_DVR;
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void dealRecord() {
        moveUI(true);

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DVRRecord msg = new DVRRecord();
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealGetFileList(int fileType) {
        mFileType = fileType;
        mAdapter.clear();
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DvrFileListReq msg= new DvrFileListReq();
            msg.getBody().get(TLVType.TP_DVR_FILE_TYPE_ID).setValue((short)fileType);
            if (msg.encode()) {
                Log.d(TAG, "Get DVR file list req");
                //Intent can't send data over 48K, it will report error
                //"FAILED BINDER TRANSACTION!!!". So we usa a global variable.
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_DVR_FILE_LIST);
            }
        }
    }

    // 1 play
    // 2 pause
    private void dealPlayFile(byte ctrl) {
        String fileName = mAdapter.getSelectedFile();
        if (fileName == null) {
            return;
        }

        Toast.makeText(getActivity(), fileName,Toast.LENGTH_LONG).show();

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DvrPlayFileReq msg = new DvrPlayFileReq();

            DvrPlay play = new DvrPlay((byte) mFileType, ctrl, fileName);
            msg.getBody().get(TLVType.TP_DVR_PLAY_FILE_ID).setValue(play);
            msg.setSeq(UdpHelper.getSeq());
            if (msg.encode()) {
                byte[] buffer = new byte[msg.getData().length];
                System.arraycopy(msg.getData(), 0, buffer, 0, buffer.length);
                Intent intent = new Intent(Constants.UDP_SEND_ACTION);
                intent.putExtra(Constants.EXTEND_UDP_SEND_BUFFER, buffer);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            }
        }
    }

    private void dealFormat() {
        FormatDialogFragment dialog = new FormatDialogFragment();
        dialog.show(getActivity().getFragmentManager(), "hello");
    }

    public void SetListView(ArrayList<String> list) {
        Log.d(TAG, "SetListView");
        mlist.clear();
        mlist.addAll(list);
        mAdapter.notifyDataSetChanged();
        moveUI(false);
    }

    public void setPlayFile(byte fileType, byte playCtrl, String fileName) {
        mAdapter.setSelected(fileName);
    }

    private void dealVideoControl(String desc) {
        if (null != getView()) {
            Button button = (Button) getView().findViewById(R.id.btnDVRFormat);

            if (desc.equals(getString(R.string.play_video))) {
                button.setText(getString(R.string.stop_video));
                button.setContentDescription(getString(R.string.stop_video));
                try {
                    //videoView.setVideoPath("rtsp://218.204.223.237:554/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");
                    //videoView.setVideoPath("rtsp://192.168.168.102:6880/live/1/67A7572844E51A64/f68g2mj7wjua3la7.sdp");
//                videoView.setVideoPath("rtsp://192.168.168.1:6880/test:network-caching=1000");
//                MediaController mc = new MediaController(this);
//                videoView.setMediaController(mc);
//                videoView.start();
//                videoView.requestFocus();
                } catch (Exception e) {
                    android.util.Log.e(TAG, "error: " + e.getMessage());
                }
            } else if (desc.equals(getString(R.string.stop_video))) {
                button.setText(getString(R.string.play_video));
                button.setContentDescription(getString(R.string.play_video));
//            videoView.stopPlayback();
            }
        }
    }

    private void moveUI(boolean isRecord) {
        if (isRecord) {
            curAlpha = 0f;
        } else {
            curAlpha = 1f;
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(listView, "Alpha", curAlpha);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animator);
        set.start();
    }

    private void checkToggleButton(int index) {
        curButton = index;
        for (int i = 0; i< btns.size(); i++) {
            btns.get(i).setChecked(i == index);
        }
    }

    private void addToggleButtonToList(View view) {
        if (view instanceof ToggleButton) {
            ToggleButton tb = (ToggleButton) view;
            tb.setOnClickListener(listener);
            btns.add(tb);
        }
    }
}
