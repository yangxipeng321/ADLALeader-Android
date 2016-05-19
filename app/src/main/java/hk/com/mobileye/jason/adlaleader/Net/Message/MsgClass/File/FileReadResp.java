package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.File;

import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgBase;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/26.
 */
public class FileReadResp extends MsgBase {

    private int mFileCRC = 0x0;
    private int mFileLen = 0;

    @Override
    public void initMsg() {
        getHeader().MsgResponseType = ResponseType.RESPONSE;
        getHeader().MsgServiceType = ServiceType.SERVICE_FILE;
        getHeader().MsgType = MessageType.FILE_READ_RESP;
    }

    @Override
    public boolean encode() {
        return super.encode();
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            //Decode the message header.
            if(!super.decode()) {return  false;}
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
            //Get file content
            byte[] fileContent = new byte[mFileLen];
            System.arraycopy((getData()), index + 64 + 4 + 4, fileContent, 0, fileContent.length );
            //Add file content to tlv
            getBody().add(TLVType.TP_FILE_PARA_ID, fileContent.getClass()).setValue(fileContent);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public int getFileCRC() {return mFileCRC;}
    public int getFileLen() {return mFileLen;}
}
