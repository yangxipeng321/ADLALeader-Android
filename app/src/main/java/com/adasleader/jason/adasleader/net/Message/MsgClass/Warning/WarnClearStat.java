package com.adasleader.jason.adasleader.net.Message.MsgClass.Warning;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

/**
 * Created by jason on 2017/6/3.
 *
 */

public class WarnClearStat extends MsgBase {

    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_WARNING;
        getHeader().MsgType = MessageType.WARN_CLEAR_STAT_REQ;
        getHeader().MsgResponseType = ResponseType.INFO;
    }

    @Override
    public boolean encode() {
        setVar(MsgConst.MSG_LEN_HEADER);
        return super.encode();
    }
}
