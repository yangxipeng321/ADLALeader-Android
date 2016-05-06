package hk.com.mobileye.jason.adlaleader.Net.Message.MsgClass.File;

import hk.com.mobileye.jason.adlaleader.Net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.Net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.Net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.Net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.Net.Message.TLVType;

/**
 * Created by Jason on 2015/1/26.
 */
public class FileWriteResp extends MsgBase{
    private int mFileCRC = 0xBBBBBBBB;
    private int mFileLen = 0;

    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.RESPONSE;
        getHeader().MsgServiceType = ServiceType.SERVICE_FILE;
        getHeader().MsgType = MessageType.FILE_WRITE_RESP;
    }

    @Override
    public boolean encode() {
        return super.encode();
    }



    @Override
    public boolean decode() {
        boolean result = true;
        try {
            if (!super.decode()) { return false;}

            getBody().clear();

            int index = MsgConst.MSG_LEN_HEADER;
            //Gets file name
            String fileName = new String(getData(), index, 64).trim();
            //Add file name to tlv
            getBody().add(TLVType.TP_FILE_NAME_ID, fileName.getClass()).setValue(fileName);
            //Gets file length
            mFileLen = MsgUtils.bytes2Int(getData(), index + 64);
            //Gets file CRC
            mFileCRC = MsgUtils.bytes2Int(getData(), index + 64 + 4);

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public int getFileCRC() {return mFileCRC;}
    public int getFileLength() {return  mFileLen;}
}