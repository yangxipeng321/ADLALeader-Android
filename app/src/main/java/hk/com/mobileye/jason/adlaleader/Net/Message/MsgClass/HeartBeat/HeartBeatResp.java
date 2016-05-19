package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.HeartBeat;

import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;

/**
 * Created by Jason on 2015/1/4.
 */
public class HeartBeatResp extends HeartBeatReq {
    @Override
    public void initMsg() {
        super.initMsg();
        getHeader().MsgResponseType = ResponseType.RESPONSE;
    }
}
