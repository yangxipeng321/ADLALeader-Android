package com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

/**
 * Created by Jason on 2016/12/13.
 *
 */

public class CmdStaModeReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgServiceType = ServiceType.SERVICE_CMD;
        getHeader().MsgType = MessageType.MSG_CMD_STA_MODE_REQ;
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
