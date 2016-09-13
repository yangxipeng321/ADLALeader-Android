package com.adasleader.jason.adasleader.net.Message.MsgClass.Settings;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVClass;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/27.
 */
public class ParameterReadReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_SETTINGS;
        getHeader().MsgType = MessageType.PARA_READ_REQ;
        getHeader().MsgResponseType = ResponseType.REQUEST;

        getBody().add(TLVType.TP_WIFI_PASSWORD_ID, WifiPassword.class);
        getBody().add(TLVType.TP_TIME_ID, long.class);
        getBody().add(TLVType.TP_DEV_VER_ID, DevVersion.class);
        getBody().add(TLVType.TP_MH_VER_ID, MHVersion.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[1024];
            int tlvLen;
            int index = 0;
            for (TLVClass tlv : getBody().getTLVs()) {
                System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, index, tlv.getLength());
                index += tlv.getLength();
            }
            this.setVar(index + MsgConst.MSG_LEN_HEADER);
            this.setCRC(MsgUtils.getCrc32(bytesTLVs, index));
            System.arraycopy(bytesTLVs, 0, this.getData(), MsgConst.MSG_LEN_HEADER, index);
            /**---------------WARNING---------------
             * Must encode body first, then can calculate the whole length.
             *
             */
            result = result && super.encode();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return  result;
    }

    @Override
    public boolean decode() {
        return super.decode();
    }
}
