package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Warning;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2016/2/3.
 */
public class WarnDayStatResp extends MsgBase{

    private int mDate = -1;

    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.RESPONSE;
        getHeader().MsgServiceType = ServiceType.SERVICE_WARNING;
        getHeader().MsgType = MessageType.WARN_DAY_STAT_REQ;

        getBody().add(TLVType.TP_WARN_DAT_STAT, DayStat.class);
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            //Decode the message header.
            if(!super.decode()) {return false;}
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
                        case TLVType.TP_WARN_DAT_STAT:
                            DayStat stat = new DayStat(getData(), index + 4, tlvLen - 4);
                            getBody().add(tlvType, stat.getClass()).setValue(stat);
                            break;
                        default:
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
