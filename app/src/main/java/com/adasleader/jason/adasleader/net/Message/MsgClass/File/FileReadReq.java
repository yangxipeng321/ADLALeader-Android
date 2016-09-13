package com.adasleader.jason.adasleader.net.Message.MsgClass.File;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVClass;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/26.
 */
public class FileReadReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgServiceType = ServiceType.SERVICE_FILE;
        getHeader().MsgType = MessageType.FILE_READ_REQ;

        getBody().add(TLVType.TP_FILE_NAME_ID, String.class);
    }

    @Override
    public boolean encode() {
        //getBody().add(TLVType.TP_FILE_NAME_ID, );
        boolean result = true;
        try {
            byte[] bytesTLVs = new byte[64 + 4 + 4];
            int index = 0;

            TLVClass tlv = getBody().get(TLVType.TP_FILE_NAME_ID);
            if (null == tlv || null == tlv.getValueBytes()) {
                throw new Exception("The file name tlv is null or valuebytes is null");
            }
            System.arraycopy(tlv.getValueBytes(), 0, bytesTLVs, 0, tlv.getValueBytes().length);
            index += 64 + 4;

            int CRC = 0xBBBBBBBB;
            System.arraycopy(MsgUtils.int2Bytes(CRC), 0, bytesTLVs, index, 4);
            index += 4;

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
