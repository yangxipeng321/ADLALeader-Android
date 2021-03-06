package com.adasleader.jason.adasleader.net.Message.MsgClass.DVR;

import com.adasleader.jason.adasleader.common.Constants;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.TLVValue;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jason on 2016/7/6.
 *
 */
public class DvrFileList implements TLVValue {
    private byte[] mBytes = null;
    private int mFileType;
    private ArrayList<String> mFileList = new ArrayList<>();

    public DvrFileList(byte[] buffer, int offset, int len, int fileType) {
        mFileType = fileType;
        mBytes = new byte[len];
        System.arraycopy(buffer, offset, mBytes, 0, len);
        createList();
    }

    public DvrFileList(String[] fileList, int fileType) {
        mFileType = fileType;
        Collections.addAll(mFileList, fileList);
    }

    private void createList() {
        clear();
        String fileExt;
        switch (mFileType) {
            case Constants.DVR_FILE_TYPE_VIDEO:
            case Constants.DVR_FILE_TYPE_FCW:
                fileExt = ".MP4";
                break;
            case Constants.DVR_FILE_TYPE_PIC:
                fileExt = ".JPG";
                break;
            default:
                fileExt = "";
                break;
        }

        int step = 6;
        int count = mBytes.length/step;
        for (int i = 0; i < count; i++) {
            StringBuilder str = new StringBuilder(MsgUtils.bytes2HexString(mBytes, i * step, step));
            str.insert(8, "_").append(fileExt);
            mFileList.add(str.toString());
        }
    }


    public ArrayList<String> getFileList(){ return mFileList;}

    public void clear() {
        mFileList.clear();
    }

    @Override
    public byte[] getBytes() {
        return mBytes;
    }
}
