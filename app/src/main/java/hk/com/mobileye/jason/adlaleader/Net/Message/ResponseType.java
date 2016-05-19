package hk.com.mobileye.jason.adlaleader.net.Message;

/**
 * Created by Jason on 2015/1/4.
 *
 */
public class ResponseType {
    //A message need no response
    public static final byte INFO = 0x00;

    //A message need response
    public static final byte REQUEST = 0x01;

    //The response to a message
    public static final byte RESPONSE = 0x02;

    //More response to a message
    public static final byte MORE_RESPONSE = 0x03;
}
