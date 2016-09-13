package com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVClass;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/9/17.
 */
public class CmdSwitchScreen extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgServiceType = ServiceType.SERVICE_CMD;
        getHeader().MsgType = MessageType.CMD_SWITCH_SCREEN_REQ;

        getBody().add(TLVType.TP_SWITCH_SCREEN, byte.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[5];
            int index = 0;
            TLVClass tlv = getBody().get(TLVType.TP_SWITCH_SCREEN);
            if (null == tlv && null == tlv.getValueBytes()) {
                throw new Exception("The DVR Key tlv is null or valuebytes is null");
            }
            System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, index, tlv.getLength());
            index += tlv.getLength();

            this.setVar(MsgConst.MSG_LEN_HEADER + index);
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
        return result;
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
            byte screenId = getData()[index + 4];
            getBody().add(TLVType.TP_SWITCH_SCREEN, byte.class).setValue(screenId);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return  result;
    }
}
