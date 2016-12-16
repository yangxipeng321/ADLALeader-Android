package com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd;

import com.adasleader.jason.adasleader.net.Message.ResponseType;

/**
 * Created by Jason on 2016/12/13.
 */

public class CmdStaModeResp extends CmdStaModeReq {
    @Override
    public void initMsg() {
        super.initMsg();
        getHeader().MsgResponseType = ResponseType.REQUEST;
    }
}
