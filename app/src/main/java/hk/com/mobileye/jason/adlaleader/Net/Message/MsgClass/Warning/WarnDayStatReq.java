package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.Warning;

import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVType;

/**
 * Created by Jason on 2016/2/3.
 */
public class WarnDayStatReq extends MsgBase{
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgServiceType = ServiceType.SERVICE_WARNING;
        getHeader().MsgType = MessageType.WARN_DAY_STAT_REQ;

        getBody().add(TLVType.TP_DATE_INDEX_ID, int.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[8];
            int index = 0;
            TLVClass tlv = getBody().get(TLVType.TP_DATE_INDEX_ID);
            if (null == tlv && null == tlv.getValueBytes()) {
                throw new Exception("The delay tlv is null or valuebytes is null ");
            }
            System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, index, tlv.getLength());
            index += 8;

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
