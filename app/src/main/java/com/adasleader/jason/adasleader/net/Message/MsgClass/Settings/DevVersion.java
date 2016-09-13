package com.adasleader.jason.adasleader.net.Message.MsgClass.Settings;

import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.TLVValue;

/**
 * Created by Jason on 2015/1/28.
 */
public class DevVersion implements TLVValue {
    private byte[] mBytes = new byte[12];
    private int mDevSn;
    private int mDevSwVer;
    private int mDevHwVer;

    public DevVersion(int aDevSn, int aDevSwVer, int aDevHwVer) {
        mDevSn = aDevSn;
        mDevSwVer = aDevSwVer;
        mDevHwVer = aDevHwVer;
        System.arraycopy(MsgUtils.int2Bytes(mDevSn), 0, mBytes, 0, 4);
        System.arraycopy(MsgUtils.int2Bytes(mDevSwVer), 0, mBytes, 4, 4);
        System.arraycopy(MsgUtils.int2Bytes(mDevHwVer), 0, mBytes, 8, 4);
    }

    public DevVersion(byte[] buffer, int offset, int len) {
        System.arraycopy(buffer, offset, mBytes, 0, Math.min(len, mBytes.length));
        mDevSn = MsgUtils.bytes2Int(mBytes, 0);
        mDevSwVer = MsgUtils.bytes2Int(mBytes, 4);
        mDevHwVer = MsgUtils.bytes2Int(mBytes, 8);
    }

    public int getDevSn() {return  mDevSn;}
    public int getDevSwVer() { return  mDevSwVer;}
    //return device hardware version
    public int getDevHwVer(){ return mDevHwVer;}

    public void clear() {
        mDevSn = 0;
        mDevSwVer = 0;
        mDevHwVer = 0;
    }

    @Override
    public byte[] getBytes() {
        return mBytes;
    }
}
