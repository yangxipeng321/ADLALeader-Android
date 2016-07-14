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
 * Created by Jason on 2016/7/6.
 *
 */
public class DvrFileListReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_DVR;
        getHeader().MsgType = MessageType.DVR_FILE_LIST_REQ;
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getBody().add(TLVType.TP_DVR_FILE_TYPE_ID, short.class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[6];
            int index = 0;
            TLVClass tlv = getBody().get(TLVType.TP_DVR_FILE_TYPE_ID);
            if (null == tlv || null == tlv.getValueBytes()) {
                throw new Exception("The DVR File Type tlv is null or valuebytes is null");
            }
            System.arraycopy(tlv.getTLVBytes(), 0, bytesTLVs, index, tlv.getLength());
            index += tlv.getLength();

            this.setVar(index + MsgConst.MSG_LEN_HEADER);
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
        return  result;
    }
}
