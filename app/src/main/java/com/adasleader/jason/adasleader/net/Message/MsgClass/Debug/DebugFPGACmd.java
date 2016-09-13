package com.adasleader.jason.adasleader.net.Message.MsgClass.Debug;

import com.adasleader.jason.adasleader.net.Message.MessageType;

/**
 * Created by Jason on 2016/9/12.
 *
 */
public class DebugFPGACmd extends DebugMCUCmd {
    @Override
    public void initMsg() {
        super.initMsg();
        getHeader().MsgType = MessageType.DEBUG_FPGA_COMMAND;
    }
}
