package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2016/9/12.
 *
 */
public class DebugMCUCmd extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_DEBUG;
        getHeader().MsgType = MessageType.DEBUG_MCU_COMMAND;
        getHeader().MsgResponseType = ResponseType.INFO;

        getBody().add(TLVType.TP_DEBUG_CMD_ID, int.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[8];
            int index = 0;

            TLVClass tlv = getBody().get(TLVType.TP_DEBUG_CMD_ID);
            if (null == tlv || null == tlv.getValueBytes()) {
                throw new Exception("The DEBUG CMD tlv is null or valuebytes is null");
            }
            System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, 0, tlv.getTLVBytes().length);
            index += tlv.getTLVBytes().length;

            this.setVar(MsgConst.MSG_LEN_HEADER + index);
            this.setCRC(MsgUtils.getCrc32(bytesTLVs, index));
            System.arraycopy(bytesTLVs, 0, this.getData(), MsgConst.MSG_LEN_HEADER, index);

            /**---------------WARNING---------------
             * Must encode body first, then can calculate the whole length.
             *
             */
            result = result && super.encode();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

}
