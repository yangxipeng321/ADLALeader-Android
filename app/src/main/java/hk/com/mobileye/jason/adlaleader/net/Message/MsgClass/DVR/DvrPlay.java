package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR;

import hk.com.mobileye.jason.adlaleader.net.Message.TLVValue;

/**
 * Created by Jason on 2016/7/8.
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
            mFileName = new String(buf, 2, buf.length - 2);
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
