package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;

/**
 * Created by Jason on 2016/9/12.
 */
public class DebugDVRCmd extends DebugMCUCmd{
    @Override
    public void initMsg() {
        super.initMsg();
        getHeader().MsgType = MessageType.DEBUG_DVR_COMMAND;
    }
}
