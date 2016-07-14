package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;

/**
 * Created by Jason on 2016/7/12.
 */
public class LogMCUSwitch extends LogAdasgateSwitch {
    @Override
    public void initMsg() {
        super.initMsg();
        getHeader().MsgType = MessageType.LOG_MCU_SWITCH;
    }
}
