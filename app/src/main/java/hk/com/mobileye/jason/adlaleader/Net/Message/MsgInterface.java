package hk.com.mobileye.jason.adlaleader.Net.Message;

/**
 * Created by Jason on 2015/1/4.
 */
public interface MsgInterface {
    boolean encode();
    boolean decode();
    void initMsg();
}