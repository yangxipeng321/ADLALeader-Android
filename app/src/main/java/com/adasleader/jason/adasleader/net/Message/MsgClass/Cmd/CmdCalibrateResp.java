package com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

/**
 * Created by Jason on 2016/12/15.
 *
 */

public class CmdCalibrateResp extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.RESPONSE;
        getHeader().MsgServiceType = ServiceType.SERVICE_CMD;
        getHeader().MsgType = MessageType.MSG_CMD_CALI_RESP;
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
            if (tlvLen!=5) {return false;}
            byte cmd = getData()[index + 4];
            getBody().add(tlvType, byte.class).setValue(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return  result;
    }
}
