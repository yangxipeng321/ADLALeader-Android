package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.HeartBeat;

import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;

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
