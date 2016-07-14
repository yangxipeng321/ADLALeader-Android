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
 * Created by Jason on 2016/7/12.
 */
public class LogAdasgateSwitch extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_DEBUG;
        getHeader().MsgType = MessageType.LOG_ADASGATE_SWITCH;
        getHeader().MsgResponseType = ResponseType.INFO;

        getBody().add(TLVType.TP_LOG_SWITCH_ID, int.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[8];
            int index = 0;

            TLVClass tlv = getBody().get(TLVType.TP_LOG_SWITCH_ID);
            if (null == tlv || null == tlv.getValueBytes()) {
                throw new Exception("The LOG SWITHC tlv is null or valuebytes is null");
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
