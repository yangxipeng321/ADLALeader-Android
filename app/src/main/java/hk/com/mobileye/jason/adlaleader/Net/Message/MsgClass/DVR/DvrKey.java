package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/9/9.
 */
public class DvrKey extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.INFO;
        getHeader().MsgServiceType = ServiceType.SERVICE_DVR;
        getHeader().MsgType = MessageType.DVR_KEY_INFO;

        getBody().add(TLVType.TP_DVR_KEY, byte.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[5];
            int index = 0;
            TLVClass tlv = getBody().get(TLVType.TP_DVR_KEY);
            if (null == tlv && null == tlv.getValueBytes()) {
                throw new Exception("The DVR Key tlv is null or valuebytes is null");
            }
            System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, index, tlv.getLength());
            index += tlv.getLength();

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

    @Override
    public boolean decode() {
        return super.decode();
    }
}
