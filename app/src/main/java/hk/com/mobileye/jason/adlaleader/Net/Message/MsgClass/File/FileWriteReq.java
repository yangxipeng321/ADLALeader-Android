package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVClass;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/26.
 */
public class FileWriteReq extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.REQUEST;
        getHeader().MsgServiceType = ServiceType.SERVICE_FILE;
        getHeader().MsgType = MessageType.FILE_WRITE_REQ;

        getBody().add(TLVType.TP_FILE_NAME_ID, String.class);
        getBody().add(TLVType.TP_FILE_PARA_ID, byte[].class);
    }

    @Override
    public boolean encode() {
        boolean result = true;
        try {
            TLVClass tlv = getBody().get(TLVType.TP_FILE_PARA_ID);
            TLVClass tlvName = getBody().get(TLVType.TP_FILE_NAME_ID);

            if (null == tlv || null == tlv.getValueBytes()) {
                throw new Exception("The file content tlv is null or valuebytes is null");
            }
            if (null == tlvName || null == tlvName.getValueBytes()) {
                throw new Exception("The file name tlv is null or valuebytes is null");
            }

            int fileLen = tlv.getValueBytes().length;
            byte[] bytesTLVs = new byte[64 + 4 + 4 + fileLen];
            int index = 0;
            //Add the file name
            System.arraycopy(tlvName.getValueBytes(), 0, bytesTLVs, index, tlvName.getValueBytes().length);
            index += 64;
            //Add the file length
            byte[] temp = MsgUtils.int2Bytes(fileLen);
            System.arraycopy(temp, 0, bytesTLVs, index, 4);
            index += 4;
            //Add the file crc
            int crc = MsgUtils.getCrc32(tlv.getValueBytes(), fileLen);
            temp = MsgUtils.int2Bytes(crc);
            System.arraycopy(temp, 0, bytesTLVs, index, 4);
            index += 4;
            //Add the file content
            System.arraycopy(tlv.getValueBytes(), 0, bytesTLVs, index, fileLen);
            index += fileLen;

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
        return result;
    }

    @Override
    public boolean decode() {
        return super.decode();
    }
}
