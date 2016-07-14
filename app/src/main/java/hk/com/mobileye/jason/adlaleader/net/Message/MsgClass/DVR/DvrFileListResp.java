package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.DVR;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2016/7/6.
 *
 */
public class DvrFileListResp extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_SETTINGS;
        getHeader().MsgType = MessageType.DVR_FILE_LIST_RESP;
        getHeader().MsgResponseType = ResponseType.RESPONSE;
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            //Decode the message header.
            if(!super.decode()) {return  false;}
            getBody().clear();
            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;
            int fileType = 0;
            while (getMsgLength() > index + 4) {
                tlvType = MsgUtils.bytes2Short(getData(), index);
                tlvLen = MsgUtils.bytes2Short(getData(), index + 2);
                if (tlvLen <= 4) {
                    result =false;
                    break;
                }
                switch (tlvType) {
                    case TLVType.TP_DVR_FILE_TYPE_ID:
                        fileType = MsgUtils.bytes2Short(getData(), index + 4);
                        break;
                    case TLVType.TP_DVR_FILE_LIST_ID:
                        if (fileType > 0 && fileType < 3) {
                            DvrFileList list = new DvrFileList(getData(), index + 4, tlvLen - 4, fileType);
                            getBody().add(tlvType, list.getClass()).setValue(list);
                        } else {
                            result = false;
                        }
                        break;
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
