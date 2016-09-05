package hk.com.mobileye.jason.adlaleader.control;


import android.animation.ValueAnimator;
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

import java.util.ArrayList;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DVRRecord;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrFileListReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrPlay;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrPlayFileReq;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.net.UdpHelper;

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
    private Button btnPlay;
    private View videoContainer;
    private View controlContainer;
    private float videoY = 0;
    private float controlY = 0;
    private byte curCtrl = 0;

    public CtrlDVRFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ctrl_dvr, container, false);

        mApp = (MyApplication) getActivity().getApplication();

        BtnClickListener listener = new BtnClickListener();
        view.findViewById(R.id.btnScrVideo).setOnClickListener(listener);
        view.findViewById(R.id.btnRecord).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRFileList).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRFCWFileList).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRPicList).setOnClickListener(listener);
        btnPlay = (Button) view.findViewById(R.id.btnDVRPlayFile);
        btnPlay.setOnClickListener(listener);
        videoContainer = view.findViewById(R.id.videoContainer);
        controlContainer = view.findViewById(R.id.controlContainer);


        mAdapter = new FileAdapter(new ArrayList<String>());
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
        return view;
    }

    private class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnScrVideo:
                    dealScreenVideo();
                    break;
                case R.id.btnRecord:
                    dealRecord();
                    break;
                case R.id.btnDVRFileList:
                    dealGetFileList(Constants.DVR_FILE_TYPE_VIDEO);
                    break;
                case R.id.btnDVRFCWFileList:
                    dealGetFileList(Constants.DVR_FILE_TYPE_FCW);
                    break;
                case R.id.btnDVRPicList:
                    dealGetFileList(Constants.DVR_FILE_TYPE_PIC);
                    break;
                case R.id.btnDVRPlayFile:
                    if (curCtrl == 1)
                        dealPlayFile((byte) 2);
                    else
                        dealPlayFile((byte) 1);
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
                        null, false);
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
        //videoContainer.animate().y(200f);
        if (controlY == 0) {
            controlY = controlContainer.getY();
        }
        //controlContainer.animate().y(controlY);
        ValueAnimator animator = new ValueAnimator();
        animator.setTarget(controlContainer);
        animator.start();

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
        //videoContainer.animate().y(-200f);
        //controlContainer.animate().y(videoY);

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

    public void SetListView(ArrayList<String> list) {
        Log.d(TAG, "SetListView");
        mAdapter.clear();
        mAdapter.addAll(list);
    }

    public void setPlayFile(byte fileType, byte playCtrl, String fileName) {
        mAdapter.setSelected(fileName);

        if (playCtrl != curCtrl && fileType == 1) {
            curCtrl = playCtrl;
            if (playCtrl == 1) {
                btnPlay.setText("暂停");
            } else {
                btnPlay.setText("播放");
            }
        }
    }

    private void dealVideoControl(String desc) {
        Button button = (Button) getView().findViewById(R.id.btnPlayVideo);

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


//    private void moveUI(View view) {
//        ValueAnimator animator = new ValueAnimator();
//        animator.setDuration(1000);
//
//        animator.setEvaluator();
//
//        animator.start();
//
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float[] xyPos = (float[]) animation.getAnimatedValue();
//                view.getLayoutParams().height = (int) xyPos[0];
//            }
//        });
//
//
//    }

}
