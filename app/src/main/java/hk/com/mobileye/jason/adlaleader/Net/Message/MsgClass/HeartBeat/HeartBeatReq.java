package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.HeartBeat;

import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;

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
