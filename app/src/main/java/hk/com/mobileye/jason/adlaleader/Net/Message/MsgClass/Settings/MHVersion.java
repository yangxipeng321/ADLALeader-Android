package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings;

import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVValue;

/**
 * Created by Jason on 2015/1/28.
 */
public class MHVersion  implements TLVValue{
    private byte[] mBytes = new byte[MsgConst.MH_SN_LEN + MsgConst.MH_SW_VER_LEN
            + MsgConst.MH_VF_VER_LEN];
    private String mMHSn;
    private String mMHSwVer;
    private String mMHVfVer;

    public MHVersion(String aMHSn, String aMHSwVer, String aMHVfVer) {
        if (aMHSn.length()<= MsgConst.MH_SN_LEN)
            mMHSn = aMHSn;
        else
            mMHSn = aMHSn.substring(0, MsgConst.MH_SN_LEN);

        if (aMHSwVer.length()<=MsgConst.MH_SW_VER_LEN)
            mMHSwVer = aMHSwVer;
        else
            mMHSwVer = aMHSwVer.substring(0, MsgConst.MH_SW_VER_LEN);

        if (aMHVfVer.length() <= MsgConst.MH_VF_VER_LEN)
            mMHVfVer = aMHVfVer;
        else
            mMHVfVer = aMHVfVer.substring(0, MsgConst.MH_VF_VER_LEN);

//        int offset = 0;
//        System.arraycopy(mMHSn.getBytes(), 0, mBytes, offset, MsgConst.MH_SN_LEN);
//        offset += MsgConst.MH_SN_LEN;
//        System.arraycopy(mMHSwVer.getBytes(), 0, mBytes, offset, MsgConst.MH_SW_VER_LEN);
//        offset += MsgConst.MH_SW_VER_LEN;
//        System.arraycopy(mMHVfVer.getBytes(), 0, mBytes, offset, MsgConst.MH_VF_VER_LEN);
    }

    public MHVersion(byte[] buffer, int offset, int len) {
        System.arraycopy(buffer, offset, mBytes, 0, Math.min(len, mBytes.length));
        int index = 0;
        mMHSn = new String(mBytes, index, MsgConst.MH_SN_LEN).trim();
        index += MsgConst.MH_SN_LEN;
        mMHSwVer = new String(mBytes, index, MsgConst.MH_SW_VER_LEN).trim();
        index += MsgConst.MH_SW_VER_LEN;
        mMHVfVer = new String(mBytes, index, MsgConst.MH_VF_VER_LEN).trim();
    }

    public String getMHSn(){return  mMHSn;}
    public String getMHSwVer(){return  mMHSwVer;}
    public String getMHVfVer(){return  mMHVfVer;}

    public void clear() {
        mMHSn = "";
        mMHSwVer = "";
        mMHVfVer = "";
    }

    @Override
    public byte[] getBytes() {
        return mBytes;
    }
}
