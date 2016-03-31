package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Settings;

import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVType;

/**
 * Created by Jason on 2015/1/27.
 */
public class ParameterSetReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_SETTINGS;
        getHeader().MsgType = MessageType.PARA_SET_REQ;
        getHeader().MsgResponseType = ResponseType.REQUEST;

        getBody().add(TLVType.TP_WIFI_PASSWORD_ID, WifiPassword.class);
        getBody().add(TLVType.TP_TIME_ID, long.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[1024];
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
