package com.adasleader.jason.adasleader.net.Message.MsgClass.DVR;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2016/7/6.
 *
 */
public class DvrPlayFileResp extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_DVR;
        getHeader().MsgType = MessageType.DVR_PLAY_FILE_RESP;
        getHeader().MsgResponseType = ResponseType.RESPONSE;
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            //Decode the message header.
            if(!super.decode()) {return  false;}
            getBody().clear();
            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;
            while (getMsgLength() > index + 4) {
                tlvType = MsgUtils.bytes2Short(getData(), index);
                tlvLen = MsgUtils.bytes2Short(getData(), index + 2);
                if (tlvLen <= 4) {
                    result = false;
                    break;
                }

                switch (tlvType) {
                    case TLVType.TP_DVR_PLAY_FILE_ID:
                        DvrPlay play = new DvrPlay(getData(), index + 4, tlvLen - 4);
                        getBody().add(tlvType, play.getClass()).setValue(play);
                        break;
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
