package hk.com.mobileye.jason.adlaleader.debug;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import hk.com.mobileye.jason.adlaleader.R;
import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File.FileWriteReq;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebugFirmwareFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "DebugFirmwareFragment";

    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mUpdatingFileName;
    private UpdateState mState = UpdateState.Wait;

    private final List<FirmwareEntry> mFilesList = new ArrayList<>();

    public DebugFirmwareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debug_firmware, container, false);
        mRecycleView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        if (getFiles() > 0) {
            mAdapter = new FirmwareAdapter(mFilesList);
            mRecycleView.setAdapter(mAdapter);
        }

        return view;
    }


    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        UpdateState curState = UpdateState.Wait;

        switch (mState) {
            case Wait:
                curState = UpdateState.Updating;
                break;
            case Updating:
                curState = UpdateState.Wait;
                break;
        }

        refreshUpdatingUI(curState, mFilesList.get(position).getName());

        //send updating command
        if (curState == UpdateState.Updating) {
            //Start update
            FirmwareEntry firmwareEntry = mFilesList.get(position);
            File appDir = Environment.getExternalStoragePublicDirectory(Constants.APP_DIR);
            File file = new File(appDir, firmwareEntry.getName());
            mUpdatingFileName = firmwareEntry.getName();

            if (file.exists()) {
                updateFirmware(file);
            } else {
                Log.e(TAG, file.toString() + "is not exist. Write firmware failed");
                Toast toast = Toast.makeText(getActivity(), getString(R.string.firmware_not_exist),
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:

        }
    }

    public void dealUpdateResult(Intent intent) {
        Log.e(TAG, "dealUpdateResult");
        String fileName = intent.getStringExtra(Constants.EXTEND_FILE_NAME);
        int len = intent.getIntExtra(Constants.EXTEND_FILE_LENGTH, 0);

        Log.d(TAG, String.format(Locale.getDefault(), "Receive UploadFirmwareResult broadcast. " +
                "FileName : %s  File Lenght : %d ", fileName, len));

        refreshUpdatingUI(UpdateState.Wait, fileName);

        if (len > 0 ) {
            Toast.makeText(getActivity(), fileName + " 更新成功", Toast.LENGTH_LONG).show();
            Log.d(TAG, String.valueOf(mFilesList.size()));
        } else {
            Toast.makeText(getActivity(), R.string.upload_firmware_fail,
                    Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Search the firmware file in the ADASLeader dir.
     * @return
     */
    private int getFiles() {
        int result = 0;
        mFilesList.clear();


        File appDir = Environment.getExternalStoragePublicDirectory(Constants.APP_DIR);
        Log.d(TAG, appDir.getAbsolutePath());

        if (appDir.exists()) {
            File[] files = appDir.listFiles(new FirmwareFilter());
            if (null != files) {
                for (File file : appDir.listFiles(new FirmwareFilter())) {
                    mFilesList.add(new FirmwareEntry(file.getName(), file.length()));
                }
            }

            if (mFilesList.size() > 1) {
                SortComparator sort = new SortComparator();
                Collections.sort(mFilesList, sort);
            }

            result = mFilesList.size();
        }
        return result;
    }

    private void updateFirmware(File file) {
        MyApplication mApp = (MyApplication) getActivity().getApplication();
        Log.d(TAG, String.format(Locale.SIMPLIFIED_CHINESE, "Write file name : %s  ip : %s   " +
                "port : %d", file.getName(), mApp.mIp, mApp.mPort));

        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            FileWriteReq msg = (FileWriteReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_FILE,
                    MessageType.FILE_WRITE_REQ,
                    ResponseType.REQUEST);

            //add file name tlv value
            msg.getBody().get(TLVType.TP_FILE_NAME_ID).setValue(file.getName());
            //add file content
            try {
                InputStream inputStream = new FileInputStream(file.getAbsoluteFile());
                byte[] temp = new byte[2 * 1024 * 1024];
                int readlen = inputStream.read(temp);
                byte[] buffer = new byte[readlen];
                Log.d(TAG, "file size : " + readlen);
                System.arraycopy(temp, 0, buffer, 0, readlen);
                msg.getBody().get(TLVType.TP_FILE_PARA_ID).setValue(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            if (msg.encode()) {
                Log.d(TAG, "startActionFileService");
                //Intent can't send data over 48K, it will report error
                //"FAILED BINDER TRANSACTION!!!". So we usa a global variable.
                if (msg.getMsgLength() > (48 * 1024)) {
                    mApp.setSendBuf(msg.getData());
                    TcpIntentService.startActionFileService(getActivity(), null,
                            Constants.DESC_WRITE_FIRMWARE);
                } else {
                    TcpIntentService.startActionFileService(getActivity(), msg.getData(),
                            Constants.DESC_WRITE_FIRMWARE);
                }
            }
        }
    }

    private void refreshUpdatingUI(UpdateState state, String fileName) {
        int position = -1;
        if (fileName != null)
            position = indexOfFileList(fileName);

        switch (mState) {
            case Wait:
                switch (state) {
                    case Wait:
                        break;
                    case Updating:
                        for (int i = 0; i < mFilesList.size(); i++) {
                            FirmwareAdapter.ViewHolder vh = (FirmwareAdapter.ViewHolder)
                                    mRecycleView.findViewHolderForAdapterPosition(i);
                            if (null == vh) continue;
                            vh.setEnabled(false);
                            if (i == position) {
                                vh.setUpdating(true);
                            } else {
                                vh.setUpdating(false);
                            }
                            vh.setUpdated(false);
                        }
                        break;
                }
                break;
            case Updating:
                switch (state) {
                    case Wait:
                        for (int i = 0; i < mFilesList.size(); i++) {
                            FirmwareAdapter.ViewHolder vh = (FirmwareAdapter.ViewHolder)
                                    mRecycleView.findViewHolderForAdapterPosition(i);
                            if (null == vh) continue;
                            vh.setEnabled(true);
                            vh.setUpdating(false);
                            if (i == position) {
                                vh.setUpdated(true);
                            } else {
                                vh.setUpdated(false);
                            }
                        }
                        break;
                    case Updating:
                        break;
                }
                break;
        }
        mState = state;
        mUpdatingFileName = fileName;
    }

    private int indexOfFileList(String fileName) {
        for (int i = 0; i < mFilesList.size(); i++) {
            if (fileName.equalsIgnoreCase(mFilesList.get(i).getName())) {
                return i;
            }
        }
        return -1;
    }


    class FirmwareFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String filename) {
            if (! new File(dir, filename).isFile())
                return false;

            if (filename.toUpperCase().startsWith("MCU_"))
                return true;
            else if (filename.toUpperCase().startsWith("PIC_"))
                return true;
            else if (filename.toUpperCase().startsWith("ADASGATE_"))
                return true;
            else if (filename.toUpperCase().startsWith("ADASD_"))
                return true;
            else if (filename.toUpperCase().startsWith("RTSP_"))
                return true;
            else
                return false;
        }
    }

    class SortComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            FirmwareEntry l = (FirmwareEntry) lhs;
            FirmwareEntry r = (FirmwareEntry) rhs;

            return l.getName().compareToIgnoreCase(r.getName());
        }
    }

    class FirmwareAdapter extends RecyclerView.Adapter<FirmwareAdapter.ViewHolder> {
        private List<FirmwareEntry> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView mCardView;
            private Button mBtn;
            private ProgressBar mPb;

            public ViewHolder(View itemView) {
                super(itemView);
                mCardView = (CardView) itemView;
                mBtn = (Button) mCardView.findViewById(R.id.btnUpdate);
                mPb = (ProgressBar) mCardView.findViewById(R.id.progressBar);
            }

            public void setEnabled(boolean enabled) {
                mBtn.setEnabled(enabled);
            }

            public void setUpdating(boolean updating) {
                mPb.setVisibility(updating ? View.VISIBLE : View.INVISIBLE);
                mPb.setIndeterminate(updating);

                mBtn.setText(updating? "CANCEL" : "UPDATE");
            }

            public void setUpdated(boolean updated) {
                mCardView.findViewById(R.id.updated).setVisibility(updated ?
                        View.VISIBLE : View.INVISIBLE);
            }

            public boolean isUpdating() {
                return mPb.isIndeterminate();
            }
        }


        public FirmwareAdapter(List<FirmwareEntry> aDataset) {
            mDataset = aDataset;
        }

        @Override
        public FirmwareAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_firmware_file, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
//
        @Override
        public void onBindViewHolder(FirmwareAdapter.ViewHolder holder, int position) {
            holder.mCardView.setTag(mDataset.get(position));
            //File name
            TextView tv = (TextView) holder.mCardView.findViewById(R.id.file_name);
            String fileName = mDataset.get(position).getName();
            tv.setText(fileName);
            //File size
            tv = (TextView) holder.mCardView.findViewById(R.id.file_size);
            tv.setText(String.format(Locale.SIMPLIFIED_CHINESE, "%.1f KB", mDataset.get(position).getLenght()/1024.0));
            //Update button
            Button btn = (Button) holder.mCardView.findViewById(R.id.btnUpdate);
            btn.setOnClickListener(DebugFirmwareFragment.this);
            btn.setTag(position);

            Log.w(TAG, String.format(Locale.getDefault(), "onBindViewHolder position: %d " +
                    "fileName: %s  mUpdatingFileName: %s", position, fileName, mUpdatingFileName));

            switch (mState) {
                case Wait:
                    holder.setEnabled(true);
                    holder.setUpdating(false);
                    if (null != mUpdatingFileName && mUpdatingFileName.equals(fileName)) {
                        holder.setUpdated(true);
                    } else {
                        holder.setUpdated(false);
                    }
                    break;
                case Updating:
                    holder.setEnabled(false);
                    holder.setUpdated(false);
                    if (null!= mUpdatingFileName && mUpdatingFileName.equals(fileName)) {
                        holder.setUpdating(true);
                    } else {
                        holder.setUpdating(false);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    class FirmwareEntry {
        private final String mName;
        private final long mLength;

        public FirmwareEntry(String name, long length) {
            mName = name;
            mLength = length;
        }

        public String getName() {
            return mName;
        }

        public long getLenght() {
            return mLength;
        }
    }

    enum UpdateState {
        Wait, Updating
    }
}
