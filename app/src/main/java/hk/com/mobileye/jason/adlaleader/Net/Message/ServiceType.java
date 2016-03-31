package hk.com.mobileye.jason.adlaleader.Net.Message;

/**
 * Created by Jason on 2015/1/4.
 */
public class ServiceType {
    public static final byte NO_SERVICE = 0x00;
    public static final byte SERVICE_FILE = 0x01;
    public static final byte SERVICE_WARNING = 0x02;
    public static final byte SERVICE_SETTINGS = 0x03;
    public static final byte SERVICE_HEARTBEAT = 0x05;
    public static final byte SERVICE_CMD = 0x06;
    public static final byte SERVICE_DVR = 0x07;
}
