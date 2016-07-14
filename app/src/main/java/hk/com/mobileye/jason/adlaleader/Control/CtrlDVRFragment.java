package hk.com.mobileye.jason.adlaleader.control;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Gravity;
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
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrFileListReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrPlay;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR.DvrPlayFileReq;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;

/**
 * A simple {@link Fragment} subclass.
 */
public class CtrlDVRFragment extends Fragment {

    private static final String TAG = "CtrlDVRFragment";
    private MyApplication mApp;
    private int mFileType;

    private ListView listView;
    private FileAdapter mAdapter;

    public CtrlDVRFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ctrl_dvr, container, false);

        Log.d(TAG, "onCreateView");

        mApp = (MyApplication) getActivity().getApplication();

        BtnClickListener listener = new BtnClickListener();
        view.findViewById(R.id.btnDVRUp).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRDown).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRConfirm).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRCancel).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRHome).setOnClickListener(listener);
        view.findViewById(R.id.btnScrVideo).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRFileList).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRPicList).setOnClickListener(listener);
        view.findViewById(R.id.btnDVRPlayFile).setOnClickListener(listener);
        view.findViewById(R.id.btnKey10).setOnClickListener(listener);
        view.findViewById(R.id.btnKey11).setOnClickListener(listener);
        view.findViewById(R.id.btnKey12).setOnClickListener(listener);
        view.findViewById(R.id.btnKey13).setOnClickListener(listener);

        mAdapter = new FileAdapter(new ArrayList<String>());
        listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.setSelectedPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        ArrayList<String> temp = new ArrayList<>();
        temp.add("111");
        temp.add("222");
        temp.add("333");
        SetListView(temp);

        return view;
    }

    private class BtnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            byte key;
            switch (v.getId()) {
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
                case R.id.btnDVRPhoto:
                    key = 6;
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
                case R.id.btnKey13:
                    key = 13;
                    dealDVRKey(key);
                    break;
                case R.id.btnScrVideo:
                    dealScreenVideo();
                    break;
                case R.id.btnDVRFileList:
                    dealGetFileList(Constants.DVR_FILE_TYPE_VIDEO);
                    break;
                case R.id.btnDVRPicList:
                    dealGetFileList(Constants.DVR_FILE_TYPE_PIC);
                    break;
                case R.id.btnDVRPlayFile:
                    if (mAdapter.getCount() > 0) {
                        dealPlayFile();
                    }
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
    }

    private void dealDVRKey(byte key) {
        Log.e(TAG, "dealDVRKey");
        Intent intent = new Intent(Constants.DVR_KEY_ACTION);
        intent.putExtra(Constants.EXTEND_DVR_KEY, key);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    private void dealScreenVideo() {
        Intent intent = new Intent(Constants.CMD_SWITCH_SCREEN_REQ_ACTION);
        byte id =  4;//Constants.SCREEN_APP_DVR;
        intent.putExtra(Constants.EXTEND_SCREEN_ID, id);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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

    public void SetListView(ArrayList<String> list) {
        Log.d(TAG, "SETListView");
        mAdapter.clear();
        mAdapter.addAll(list);
        Toast toast = Toast.makeText(getActivity(), "成功获取文件列表", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void dealPlayFile() {
        String fileName = mAdapter.getSelectedFile();
        if (fileName == null) {
            return;
        }

        Toast.makeText(getActivity(), fileName,Toast.LENGTH_LONG).show();

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            DvrPlayFileReq msg = new DvrPlayFileReq();
            DvrPlay play = new DvrPlay((byte) mFileType, (byte) 1, fileName);
            msg.getBody().get(TLVType.TP_DVR_PLAY_FILE_ID).setValue(play);
            if (msg.encode()) {
                TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                        Constants.DESC_DVR_PLAY_FILE);
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




}
