package hk.com.mobileye.jason.adlaleader.Net.Message;

/**
 * Created by Jason on 2015/1/4.
 */
public class MessageType {
    public static final int FILE_READ_REQ = 0x10;
    public static final int FILE_READ_RESP = 0x10;

    public static final int FILE_READ_DATA_RESP = 0x11;

    public static final int FILE_WRITE_REQ = 0x20;
    public static final int FILE_WRITE_RESP = 0x20;

    public static final int FILE_WRITE_DATA_RESP = 0x21;


    public static final int WARNING_REQ = 0x01;
    public static final int WARN_DAY_STAT_REQ = 0x02;
    public static final int WARN_DAY_STAT_RESP = 0x02;
    public static final int WARN_MONTH_STAT_REQ = 0x03;
    public static final int WARN_MONTH_STAT_RESP = 0x03;

    public static final int PARA_READ_REQ = 0x01;
    public static final int PARA_READ_RESP = 0x01;
    public static final int PARA_SET_REQ = 0x02;
    public static final int PARA_SET_RESP = 0x02;

    public static final int HEART_BEAT_REQ = 0x01;
    public static final int HEART_BEAT_RESP = 0x01;

    public static final int CAR_STATUS_RESP = 0x01;

    public static final int CMD_RESET_REQ = 0x01;
    public static final int CMD_RESET_RESP = 0x01;

    public static final int CMD_RESET_ME_REQ = 0x02;
    public static final int CMD_RESET_ME_RESP = 0x02;

    public static final int CMD_TEST_REQ = 0x08;
    public static final int CMD_TEST_RESP = 0x08;

    public static final int CMD_SWITCH_SCREEN_REQ = 0x09;
    public static final int CMD_SWITCH_SCREEN_RESP = 0x09;

    public static final int DVR_KEY_INFO = 0x01;
}
