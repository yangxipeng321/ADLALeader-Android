package com.adasleader.jason.adasleader.net.Message;


/**
 * Created by Jason on 2015/1/4.
 */
public class MsgHeader implements MsgInterface {
    private byte[] mData=null;

    public int MsgLength = 0x00;
    public int MsgResponseType = 0x00;
    public int MsgSeq = 0x00;
    public int MsgServiceType = 0x00;
    public int MsgType = 0x00;
    public int CRC = 0xBBBBBBBB;
    public int MsgReserve = 0x00;

    protected MsgHeader() {
        mData = new byte[MsgConst.MSG_LEN_HEADER];
    }

    public byte[] getData(){return  mData;}

    @Override
    public boolean encode() {
        mData = new byte[MsgConst.MSG_LEN_HEADER];

        try {
            int index = 0;
            byte[] temp = null;
            //start flag
            System.arraycopy(MsgUtils.int2Bytes(MsgConst.MSG_FLAG), 0, mData, index, 4);
            index += 4;

            //length
            mData[index++] = (byte) (MsgLength & 0xff);
            mData[index++] = (byte)((MsgLength>>8) & 0xff);

            //reserve byte
            if (MsgLength > 0xffff) {
                //if message length is more than 0xffff, then use reserve byte
                mData[index++] = (byte) ((MsgLength >> 16) & 0xff);
            }else {
                mData[index++] = (byte) (MsgReserve & 0xff);
            }

            //response type
            mData[index++] = (byte) (MsgResponseType & 0xff);

            //msg seq
            mData[index++] = (byte) (MsgSeq & 0xff);
            mData[index++] = (byte)((MsgSeq>>8) &0xff);

            //service type
            mData[index++] = (byte) (MsgServiceType & 0xff);

            //message type
            mData[index++] = (byte) (MsgType & 0xff);

            //CRC
            System.arraycopy(MsgUtils.int2Bytes(CRC), 0, mData, index, 4);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean decode() {
        if (mData.length<MsgConst.MSG_LEN_HEADER) {
            return false;
        }

        //check start flag
        for (int i = 0; i < 4; i++) {
            if ((mData[i] &0xff) != 0xaa) {
                return false;
            }
        }
        try {
            //msg length
            int index = 4;
            MsgLength = (mData[index] & 0xff) + ((mData[index + 1] & 0xff) << 8);
            index+=2;

            //reserve
            MsgReserve = mData[index++] & 0xff;

            //response type
            MsgResponseType = mData[index++] & 0xff;

            //msg seq
            MsgSeq = (mData[index] & 0xff) + ((mData[index + 1] & 0xff) << 8);
            index += 2;

            //service type
            MsgServiceType = mData[index++] & 0xff;
            //msg type
            MsgType = mData[index++] & 0xff;

            //CRC
            CRC = (mData[index] & 0xff) + ((mData[index + 1] & 0xff) << 8)
                    + ((mData[index + 2] & 0xff) << 16)
                    + ((mData[index + 3] & 0xff) << 24);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void initMsg() {

    }
}
