package hk.com.mobileye.jason.adlaleader.net.Message;

/**
 * Created by Jason on 2015/1/4.
 *
 */
public class MsgConst {
    public static final int MSG_MAX_BUFFER = 2048;
    public static final byte MSG_LEN_HEADER = 0x10;
    public static final int MSG_FLAG = 0xAAAAAAAA;
    public static final int HEART_DEAT_INTERVAL = 50;  //unit 100ms


    //TLV constants
    public static final int TLV_MIN_LEN = 4;

    public static final int TP_WARNING_VALUE_LEN = 5;
    public static final int TP_WIFI_PASSWORD_VALUE_LEN = 9;
    public static final int MH_SN_LEN = 20;
    public static final int MH_SW_VER_LEN = 20;
    public static final int MH_VF_VER_LEN = 20;

    // Warning statistics constans
    // 报警项为最小单位，记录了某项报警在某段时间内发生的次数或者持续时间
    // 若干个报警项组成一个单位时间统计记录，记录了某段时间内各类报警的发生次数和持续时间
    // 每天报警统计由24×n个单位时间统计记录组成，n=1...60
    public static final int WARN_DAY_STAT_OFFSET         = 12;  //在tlv结构TP_WARN_DAT_STAT的value中报警记录开始的偏移量

    public static final int WARN_ITEM_LEN           = 4;    //每个报警项的长度4 bytes
    public static final int WARN_ITEM_COUNT    = 8;         //每个统计记录包含的报警项个数
    public static final int WARN_DAY_RECORDS_COUNT     = 24;        //每天统计包含的记录的个数，必须是24的倍数

    public static final String[] WARN_DESCS = {"FCW", "LDW", "PCW", "SPEEDING", "HMW"};
    public static final int[] WARN_COLOR = {0xFFFF0000, 0xFFFFFFFF, 0xFFFF0000, 0xFFFF0000, 0xFF00FF00};
}
