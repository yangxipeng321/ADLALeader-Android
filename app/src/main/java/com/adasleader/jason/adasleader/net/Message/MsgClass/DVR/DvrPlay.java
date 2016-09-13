package com.adasleader.jason.adasleader.net.Message.MsgClass.DVR;

import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.TLVValue;

/**
 * Created by Jason on 2016/7/8.
 *
 */
public class DvrPlay implements TLVValue {
    private byte mFileType;
    private byte mCtrl;
    private String mFileName;

    public DvrPlay(byte[] buffer, int offset, int len) {
        byte[] buf = new byte[len];
        System.arraycopy(buffer, offset, buf, 0, len);
        if (buf.length > 2) {
            mFileType = buf[0];
            mCtrl = buf[1];

            String fileExt;
            switch (mFileType) {
                case 1:
                    fileExt = ".MP4";
                    break;
                case 2:
                    fileExt = ".JPG";
                    break;
                default:
                    fileExt = "";
                    break;
            }

            StringBuilder str = new StringBuilder(MsgUtils.bytes2HexString(buf, 2, len - 2));
            str.insert(8, "_").append(fileExt);
            mFileName = str.toString();
        }
    }

    public DvrPlay(byte fileType, byte ctrl, String fileName) {
        mFileType = fileType;
        mCtrl = ctrl;
        mFileName = fileName;
    }

    @Override
    public byte[] getBytes() {
        byte[] strBuf = mFileName.getBytes();
        byte[] buf = new byte[2 + strBuf.length];
        buf[0] = mFileType;
        buf[1] = mCtrl;
        System.arraycopy(strBuf, 0, buf, 2, strBuf.length);
        return buf;
    }

    public byte getFileType() {
        return mFileType;
    }

    public byte getCtrl() {
        return mCtrl;
    }

    public String getFileName() {
        return mFileName;
    }
}
