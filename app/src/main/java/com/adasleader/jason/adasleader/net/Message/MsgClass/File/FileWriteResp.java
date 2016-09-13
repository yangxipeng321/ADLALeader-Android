package com.adasleader.jason.adasleader.net.Message.MsgClass.File;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/26.
 * File Write response
 */
public class FileWriteResp extends MsgBase {
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
