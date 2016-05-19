package hk.com.mobileye.jason.adlaleader.net.Message;

/**
 * Created by Jason on 2015/1/4.
 */
public class MsgBase implements MsgInterface {
    //Source data
    private byte[] mData = null;
    private MsgHeader mHeader = null;
    private MsgBody mBody = null;

    protected MsgBase(){
        mHeader = new MsgHeader();
        mBody = new MsgBody();
        initMsg();
    }

    public boolean encode() {
        boolean result = true;

        if (getMsgLength()<MsgConst.MSG_LEN_HEADER){ return false;}

        result = getHeader().encode();
        if (result) {
            System.arraycopy(getHeader().getData(), 0, getData(), 0, MsgConst.MSG_LEN_HEADER);
        }
        return result;
    }

    public boolean decode() {
        boolean result = true;

        if (getMsgLength() < MsgConst.MSG_LEN_HEADER) {
            result = false;
            return result;
        }

        System.arraycopy(getData(), 0, getHeader().getData(), 0, MsgConst.MSG_LEN_HEADER);

        result = getHeader().decode();

        //decode message body in sub class

        return result;
    }

    public void initMsg() {
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public String toString() {
        if (mData != null && mData.length > 0) {
            char[] hexChars = new char[mData.length * 2];
            for (int i = 0; i < mData.length; i++) {
                int v = mData[i] & 0xff;
                hexChars[i * 2] = hexArray[v >>> 4];
                hexChars[i * 2 + 1] = hexArray[v & 0x0f];
            }
            return new String(hexChars);
        } else {
            return "";
        }
    }

    public byte[] getData() { return mData; }
    public int getMsgLength() {
        if (mData == null) {
            return 0;
        }
        return mData.length;
    }

    public MsgHeader getHeader() { return mHeader; }

    public MsgBody getBody() {return mBody;}

    public void setVar(int length) {
        mData = new byte[length];
        getHeader().MsgLength = getMsgLength(); //confirm message length
    }
    public void setSeq(int aSeq) { getHeader().MsgSeq = aSeq; }

    public void setCRC(int aCRC ) {getHeader().CRC = aCRC;}
}

