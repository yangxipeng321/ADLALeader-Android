package com.adasleader.jason.adasleader.net.Message.MsgClass.Settings;

import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.TLVValue;

/**
 * Created by Jason on 2015/1/28.
 */
public class WifiPassword implements TLVValue {
    private String password;
    private byte[] mBytes = new byte[MsgConst.TP_WIFI_PASSWORD_VALUE_LEN];

    public WifiPassword(String aPassword) {
        if (aPassword.length()<MsgConst.TP_WIFI_PASSWORD_VALUE_LEN)
            password = aPassword;
        else
            password = aPassword.substring(0, MsgConst.TP_WIFI_PASSWORD_VALUE_LEN - 2);
        System.arraycopy(password.getBytes(), 0, mBytes, 0, password.length());
    }

    public WifiPassword(byte[] buffer, int offfet, int len) {
        System.arraycopy(buffer, offfet, mBytes, 0,
                Math.min(len, MsgConst.TP_WIFI_PASSWORD_VALUE_LEN));
        password = new String(mBytes).trim();
    }

    public String getPassword() { return password; }

    @Override
    public byte[] getBytes() {
        return mBytes;
    }
}
