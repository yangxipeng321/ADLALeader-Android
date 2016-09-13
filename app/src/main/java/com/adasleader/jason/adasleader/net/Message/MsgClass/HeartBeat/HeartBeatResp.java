package com.adasleader.jason.adasleader.net.Message.MsgClass.HeartBeat;

import com.adasleader.jason.adasleader.net.Message.ResponseType;

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
