package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;

/**
 * Created by Jason on 2016/9/12.
 *
 */
public class DvrFormat extends MsgBase{
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.INFO;
        getHeader().MsgServiceType = ServiceType.SERVICE_DVR;
        getHeader().MsgType = MessageType.DVR_FORMAT;
    }

    @Override
    public boolean encode() {
        setVar(MsgConst.MSG_LEN_HEADER);
        return super.encode();
    }

}
