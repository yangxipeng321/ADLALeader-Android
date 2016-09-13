package com.adasleader.jason.adasleader.net.Message.MsgClass.HeartBeat;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

/**
 * Created by Jason on 2015/1/4.
 */
public class HeartBeatReq extends MsgBase {
    public HeartBeatReq() {
        super();
    }

    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_HEARTBEAT;
        getHeader().MsgType = MessageType.HEART_BEAT_REQ;
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgReserve = (byte) (MsgConst.HEART_DEAT_INTERVAL & 0xff);
    }

    @Override
    public boolean encode() {
        setVar(MsgConst.MSG_LEN_HEADER);
        return super.encode();
    }

    @Override
    public boolean decode() {
        return super.decode();
    }

}
