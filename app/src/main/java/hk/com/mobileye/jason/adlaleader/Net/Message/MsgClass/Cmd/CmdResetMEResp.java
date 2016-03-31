package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Cmd;

import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;

/**
 * Created by Jason on 2015/5/15.
 */
public class CmdResetMEResp extends MsgBase{
    private int mDelay = 0;

    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.RESPONSE;
        getHeader().MsgServiceType = ServiceType.SERVICE_CMD;
        getHeader().MsgType = MessageType.CMD_RESET_ME_RESP;
    }

    @Override
    public boolean encode() {
        return super.encode();
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            //Decode the message header
            if (!super.decode()) {return false;}
            getBody().clear();
            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;
            if (getData().length < (index + 4)) { return  false; }

            tlvType = (getData()[index] & 0xff)
                    + ((getData()[index + 1] & 0xff) << 8);
            tlvLen = (getData()[index + 2] & 0xff)
                    + ((getData()[index + 3] & 0xff) << 8);
            if (tlvLen!=8) {return false;}
            int delay = MsgUtils.bytes2Int(getData(), index + 4);
            getBody().add(tlvType, int.class).setValue(delay);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return  result;
    }
}
