package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Debug;

import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2016/7/12.
 *
 */
public class LogAdasgateContent extends MsgBase {

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            if (!super.decode()) { return false;}

            getBody().clear();

            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;

            while (getMsgLength() > index + 4) {
                tlvType = MsgUtils.bytes2Short(getData(), index);
                tlvLen = MsgUtils.bytes2Short(getData(), index + 2);
                if (tlvLen <= 4) {
                    result = false;
                    break;
                } else {
                    switch (tlvType) {
                        case TLVType.TP_LOG_CONTENT_ID:
                            byte[] buffer = new byte[tlvLen - 4];
                            System.arraycopy(getData(), index + 4, buffer, 0, tlvLen - 4);
                            getBody().add(tlvType, byte[].class).setValue(buffer);
                            break;
                    }
                }
                index += tlvLen;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;

    }
}
