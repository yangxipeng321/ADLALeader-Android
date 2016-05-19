package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Settings;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/27.
 */
public class ParameterSetResp extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_SETTINGS;
        getHeader().MsgType = MessageType.PARA_SET_RESP;
        getHeader().MsgResponseType = ResponseType.RESPONSE;

        getBody().add(TLVType.TP_WIFI_PASSWORD_ID, WifiPassword.class);
        getBody().add(TLVType.TP_TIME_ID, long.class);
    }

    @Override
    public boolean encode() {
        return super.encode();
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            if (!super.decode()) { return false;}

            getBody().clear();

            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;

            while (getMsgLength() > index + 4) {
                tlvType = (getData()[index] & 0xff)
                        + ((getData()[index + 1] & 0xff) << 8);
                tlvLen = (getData()[index + 2] & 0xff)
                        + ((getData()[index + 3] & 0xff) << 8);
                if (tlvLen < 4) {
                    result = false;
                } else if (tlvLen == 4) {
                    result = false;
                } else {
                    switch (tlvType) {
                        case TLVType.TP_WIFI_PASSWORD_ID:
                            WifiPassword password  = new WifiPassword(new String(getData(),
                                    index + 4, tlvLen - 4).trim());
                            getBody().add(tlvType, password.getClass());
                            getBody().get(tlvType).setValue(password);
                            break;
                        case TLVType.TP_TIME_ID:
                            long lTime = MsgUtils.bytes2Long(getData(), index + 4);
                            getBody().add(tlvType, long.class);
                            getBody().get(tlvType).setValue(lTime);
                            break;
                        default:
                            result = false;
                            break;
                    }
                }
                index += tlvLen;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
