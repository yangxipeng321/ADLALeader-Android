package com.adasleader.jason.adasleader.net.Message;

import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrFileList;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrPlay;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.DevVersion;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.MHVersion;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.WifiPassword;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.DayStat;

/**
 * Created by Jason on 2015/1/4.
 */
public class TLVClass {
    private int mType = 0x00;
    private Object mValue = null;
    private byte[]
            mValueBytes = new byte[0];
    private Class mValueType = null;

    public TLVClass(int aTLVType, Class aVauleType) {
        mType = aTLVType;
        mValueType = aVauleType;
    }

    public int getType() { return mType; }

    public int getLength() {
        return mValueBytes == null ?
                MsgConst.TLV_MIN_LEN : (MsgConst.TLV_MIN_LEN + mValueBytes.length);
    }

    public Class getValueType() {return mValueType;}

    public Object getValue(){return mValue;}

    public void setValue(Object aValue) {
        mValue = aValue;
        if (null != mValue) {
            String simpleName = mValueType.getSimpleName();
            switch (simpleName) {
                case "byte":
                    mValueBytes = new byte[]{(byte)mValue};
                    break;
                case "byte[]":
                    mValueBytes = (byte[]) mValue;
                    break;
                case "short":
                    mValueBytes = new byte[2];
                    short ashort = (short) mValue;
                    mValueBytes[0] = (byte) (ashort & 0xff);
                    mValueBytes[1] = (byte) ((ashort >> 8) & 0xff);
                    break;
                case "int":
                    mValueBytes = new byte[4];
                    int aint = (int)mValue;
                    mValueBytes[0] = (byte) (aint & 0xff);
                    mValueBytes[1] = (byte) ((aint >> 8) & 0xff);
                    mValueBytes[2] = (byte) ((aint >> 16) & 0xff);
                    mValueBytes[3] = (byte) ((aint >> 24) & 0xff);
                    break;
                case "long":
                    mValueBytes = new byte[8];
                    long along = (long)mValue;
                    for (int i = 0; i < mValueBytes.length; i++) {
                        mValueBytes[i] = (byte) (along & 0xff);
                        along = along >> 8;
                    }
                    break;
                case "String":
                    mValueBytes = ((String)mValue).getBytes();
                    break;
                case "WifiPassword":
                    mValueBytes = ((WifiPassword) mValue).getBytes();
                    break;
                case "MHVersion":
                    mValueBytes = ((MHVersion) mValue).getBytes();
                    break;
                case "DevVersion":
                    mValueBytes = ((DevVersion) mValue).getBytes();
                    break;
                case "DayStat":
                    mValueBytes = ((DayStat) mValue).getBytes();
                    break;
                case "DvrFileList":
                    mValueBytes = ((DvrFileList) mValue).getBytes();
                    break;
                case "DvrPlay":
                    mValueBytes = ((DvrPlay) mValue).getBytes();
                    break;
                default:
                    break;
            }
        }
    }

    //return the value in bytes array
    public byte[] getValueBytes() {return  mValueBytes;  }

    //return the whole tlv in bytes array
    public byte[] getTLVBytes() {
        //Create buffer
        byte[] buffer = new byte[getLength()];
        //Fill type of TLV
        buffer[0] = (byte) (getType() & 0xff);
        buffer[1] = (byte) ((getType() >> 8) & 0xff);
        //Fill length of TLV
        buffer[2] = (byte) (getLength() & 0xff);
        buffer[3] = (byte) ((getLength() >> 8) & 0xff);
        //Fill value of TLV
        if (getValueBytes().length>0)
        System.arraycopy(getValueBytes(), 0, buffer, 4, getValueBytes().length);
        return buffer;
    }

    @Override
    public String toString() {
        if (mValueType.getClass().toString().toString().equals("byte[]")) {
            if (mValueBytes != null && mValueBytes.length > 0) {
                char[] hexChars = new char[mValueBytes.length * 3 - 1];
                for (int i = 0; i < mValueBytes.length; i++) {
                    hexChars[i * 3] = hexArray[(mValueBytes[i] & 0xf0) >>> 4];
                    hexChars[i * 3 + 1] = hexArray[(mValueBytes[i] & 0x0f)];
                    hexChars[i * 3 + 2] = ' ';
                }
                return new String(hexChars);
            } else {
                return "";
            }
        }
        return super.toString();
    }

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
}
