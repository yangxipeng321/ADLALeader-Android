package com.adasleader.jason.adasleader.common;

/**
 * Created by Jason on 2015/1/7.
 *
 */
public final class Constants {
    public static final boolean DEVELOPER_MODE = false;
    public static final boolean SHOW_DEBUG = true;

    public static final int MSG_WARNING = 1;
    public static final int MSG_SEND_HEARTBEAT = 2;
    public static final int MSG_REFRESH_WARNING = 3;
    public static final int MSG_SWITCH_SCREEN = 4;
    public static final int MSG_LOG_CONTENT = 5;
    public static final int MSG_DVR_PLAY_FILE = 6;

    public static final int HEARTBEAT_INTERVAL = 5000;  //ms
    public static final long REFRESH_WARNING_INTERVAL = 800; //ms

    public static final int SOCKET_READ_TIMEOUT = 20000;  //10s
    public static final int SOCKET_CONNECT_TIMEOUT = 1000; //1s

    public static final int HTTP_CONNECT_TIMEOUT = 5000;  //2s
    public static final int HTTP_READ_TIMEOUT = 10000;    //10s

    public static final int RESET_DELAY = 1000; //1s
    public static final int COUNTDOWN = 20000;  //20s

    public static final String SSID = "ADASLeader";
    //    public static final String SSID = "qddytt";
    public static final String TEST_SSID = "MBK-TPLink";
    public static final String IP = "192.168.168.1";
    //    public static final String IP = "192.168.2.1";
    public static final String TEST_IP = "192.168.0.16";
    public static final int UDP_PORT = 6666;
    public static final int TCP_PORT = 6667;
    public static final int TCP_SLEEP = 500;  //

    public static final String WIFI_PASSWORD = "88888888";

    //Constants for warning statistics
    public static final int HISTORY_DAYS = 15;

    //Switch Screen ID
    public static final byte SCREEN_CAR_CAR = 1;  //原车屏显示原内容
    public static final byte SCREEN_CAR_ME = 2;  //原车屏显示天眼
    public static final byte SCREEN_CAR_DVR = 3;  //原车屏显示DVR
    public static final byte SCREEN_APP_CAR = -127;  //0x81  App显示原车内容
    public static final byte SCREEN_APP_ME = -126;  //0x82  App显示天眼
    public static final byte SCREEN_APP_DVR = -125;  //0x83  App显示DVR

    //DVR File Tpye ID
    public static final byte DVR_FILE_TYPE_VIDEO = 1;
    public static final byte DVR_FILE_TYPE_PIC = 2;
    public static final byte DVR_FILE_TYPE_FCW = 3;


    //public static final String MH_CONFIG_FILE = "AM_AWS_SETUP.CONF";
    public static final String MH_CONFIG_FILE = "WARNING.CONF";
    public static final String UDHCPD_FILE = "UDHCPD.CONF";

    //public static final String HI3_FIRMWARE = "ADASGATE_.bin";
    //public static final String MH_CONFIG_FILE = "etc/AWS2Buzzer.conf";
    //public static final String MH_BUZZER_FILE = "AWS2Buzzer.conf";
    public static final String HI3_GATE_PREFIX = "ADASGATE_";
    public static final String HI3_DAEMON_PREFIX = "ADASD_";
    public static final String HI3_RTSP_PREFIX = "RTSP_";
    public static final String MCU_FILE_PREFIX = "MCU_";
    public static final String PIC_FILE_PREFIX = "PIC_";
    public static final String FPGA_FILE_PREFIX = "FPGA_";
    public static final String FIRMWARE_EXTENSION = ".BIN";
    public static final String WARNING_CONFIG_PREFIX = "WARNING";
    public static final String CAR_PARA_CONFIG_PREFIX = "CARPARA";
    public static final String CAR_DISPLAY_CONFIG_PREFIX = "CAR_DISPLAY";
    public static final String CONFIG_EXTENSION = ".CONF";

    public static final String queryAppUpgradeUrlStr = "http://adasleader.com.cn:10010/upgrade/tt1";
    public static final String queryFirmwareUpgradeUrlStr = "http://adasleader.com.cn:10010/upgrade/tt2";
    public static final String NO_UPGRADE = "NO_UPGRADE";

    public static final String APP_DIR = "ADASLeader";


    //SharedPreference file that saves settings
    public static final String PREFS_FILE = "MyPrefsFile";
    public static final String PREFS_ITEM_FIRMWARE_URL = "firmwareUrl";
    public static final String PREFS_ITEM_FIRMWARE_FILE_PATH = "firmwareFilePath";
    public static final String PREFS_ITEM_APP_URL = "appUrl";
    public static final String PREFS_ITEM_DEV_SN = "devSn";
    public static final String PREFS_ITEM_DEV_SW_VER = "devSwVer";
    public static final String PREFS_ITEM_DEV_HW_VER = "devHwVer";
    public static final String PREFS_ITEM_MH_SN = "MHSn";
    public static final String PREFS_ITEM_MH_SW_VER = "MHSvVer";
    public static final String PREFS_ITEM_MH_VF_VER = "MHVfVer";
    public static final String PREFS_ITEM_FPGA_VER = "FPGAVer";
    public static final String PREFS_ITEM_GATE_VER = "GateVer";


    //saves device version info for firmware upgrade
    public static final String PREFS_ITEM_DEV_SN_FOR_FIRMWARE = "devSnForFirmware";
    public static final String PREFS_ITEM_DEV_SW_VER_FOR_FIRMWARE = "devSwVerForFirmware";
    public static final String PREFS_ITEM_DEV_HW_VER_FOR_FIRMWARE = "devHwVerForFirmware";

    public static final String NETWORK_CHANGE_ACTION =
            "hk.com.mobileye.jason.adlaLeader.NETWORK_CHANGE";

    /**
     * TcpIntentService work status changed , broadcast the status.
     * The EXTENDED_TCP_STATUS int extra is set to corresponding value.
     * The EXTENDED_OWNER extra is represent the context which start the TcpIntentService.
     * If the work completed, then the EXTENDED_TCP_RECEIVE_DATA byte[] extra is set to
     * received data.
     */
    public static final String TCP_WORK_STATUS_ACTION = "hk.com.mobileye.jason.adlaLeader.TCP_WORK_STATUS";

    // Defines the key for the status "extra" in the TcpIntentService
    public static final String EXTENDED_TCP_STATUS = "hk.com.mobileye.jason.adlaLeader.TCP_STATUS";
    // Defines the key for the Receive data "extra" in the TcpIntentService
    public static final String EXTENDED_TCP_RECEIVE_DATA = "hk.com.mobileye.jason.adlaLeader.TCP_RECEIVE_DATA";
    // Defines the key for the owner "extra" which represent the original starter of the work
    public static final String EXTENDED_OWNER = "hk.com.mobileye.jason.adlaLeader.ORIGINATOR";
    // Defines the key for the receiver "extra" which is the final ender of the work
    public static final String EXTENDED_RECEIVER = "hk.com.mobileye.jason.adlaLeader.RECEIVER";
    // Defines the key for the description "extra" which describes the work that this service has done.
    public static final String EXTENTED_DESCRIPTION = "hk.com.mobileye.jason.adlaLeader.DESCRIPTION";

    //Describe what is this TcpIntentService doing
    public static final int DESC_UNKNOW = -1;
    public static final int DESC_READ_MH_CONFIG = 0;
    public static final int DESC_WRITE_MH_CONFIG = 1;
    public static final int DESC_READ_PARAM = 2;
    public static final int DESC_WRITE_PARAM = 3;
    public static final int DESC_WRITE_FIRMWARE = 4;
    public static final int DESC_RESET_DEVICE = 5;
    public static final int DESC_RESET_MOBILEYE = 6;
    public static final int DESC_TEST = 7;
    public static final int DESC_WARN_DAY_STAT = 8;
    public static final int DESC_WARN_MONTH_STAT = 9;
    public static final int DESC_CMD_SAVE_FRAME = 10;
    public static final int DESC_CMD_DOWNLOAD_FRAME = 11;
    public static final int DESC_DVR_FILE_LIST = 12;
    public static final int DESC_DVR_PLAY_FILE = 13;
    public static final int DESC_CMD_STA_MODE = 14;


    // Status values of TcpIntentService to broadcast to the Activity
    //The file service is starting
    public static final int STATE_ACTION_STARTED = 0;
    //The background thread is connecting to the CAN
    public static final int STATE_ACTION_CONNECTING = 1;
    //The background thread is sending request data
    public static final int STATE_ACTION_SEND = 2;
    //The background thread is receive response data
    public static final int STATE_ACTION_RECEIVE = 3;
    //The background thread is done
    public static final int STATE_ACTION_COMPLETE = 4;
    //The background thread is timeout
    public static final int STATE_ACTION_TIMEOUT = 5;
    //The background thread is send failed
    public static final int STATE_ACTION_SEND_FAILED = 6;

    /**
     * When receive the settings from the Device, broadcast the updating settings message.
     * The settings activity refresh the data from MyApplication
     */
    public static final String READ_MH_CONFIG_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.READ_MH_CONFIG_RESULT";

    public static final String WRITE_MH_CONFIG_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.WRITE_MH_CONFIG_RESULT";

    /**
     * DownloadIntentService work status changed, broadcast the current status and
     * the download progress.
     * The EXTEND_DOWNLOAD_STATUS string extra is set to correspond status.
     * The EXTEND_DOWNLOAD_PROGRESS int extra is set to progress when start downloading file.
     */
    public static final String DOWNLOAD_WORK_STATUS_ACTION =
            "hk.com.mobileye.jason.adlaLeader.DOWNLOAD_WORK_STATUS";
    //Define the key for the status "extra" in the DownloadIntentService
    public static final String EXTEND_DOWNLOAD_STATUS =
            "hk.com.mobileye.jason.adlaLeader.DOWNLOAD_STATUS";
    //Define the key for the progress "extra" int the DownlaodIntentService
    public static final String EXTEND_DOWNLOAD_PROGRESS =
            "hk.com.mobileye.jason.adlaLeader.DOWNLOAD_PROGRESS";

    //Status values of DownloadIntentService to broadcast to the Activity
    public static final String DOWNLOAD_STATUS_START = "Start";
    public static final String DOWNLOAD_STATUS_CONNECTING = "Connecting";
    public static final String DOWNLOAD_STATUS_DOWNLOADING = "Downloading";
    public static final String DOWNLOAD_STATUS_DOWNLOADED = "Download finish";
    public static final String DOWNLOAD_STATUS_FAIL = "Download fail";

    public static final String DOWNLOAD_WORK_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.DOWNLOAD_RESULT";

    public static final String EXTEND_DOWNLOAD_RESULT =
            "hk.com.mobileye.jason.adlaLeader.DOWNLOAD_RESULT";

    /**
     * CheckUpgradeIntentService work status changed, broadcast the current status.
     * The EXTEND_CHECK_UPGRADE_STATUS string extra is set to correspond status.
     */
    public static final String CHECK_UPGRADE_WORK_STATUS_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CHECK_UPGRADE_WORK_STATUS";
    //Define the key for the status "extra" in the DownloadIntentService
    public static final String EXTEND_CHECK_UPGRADE_STATUS =
            "hk.com.mobileye.jason.adlaLeader.CHECK_UPGREAD_STATUS";


    public static final String APP_UPGRADE_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.APP_UPGRADE_RESLUT";

    public static final String FIRMWARE_UPGRADE_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.FIRMWARE_UPGRADE_RESULT";
    //Define the key for the progress "extra" int the DownlaodIntentService
    public static final String EXTEND_CHECK_UPGRADE_RESULT =
            "hk.com.mobileye.jason.adlaLeader.CHECK_UPGRADE_RESULT";


    public static final String FIRMWARE_UPLOAD_RESULT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.FIRMWARE_UPLOAD_RESULT";
    public static final String EXTEND_FILE_NAME =
            "hk.com.mobileye.jason.adlaLeader.FILE_NAME";
    public static final String EXTEND_FILE_LENGTH =
            "hk.com.mobileye.jason.adlaLeader.FILE_LENGTH";


    public static final String VERSION_INFO_UPDATE_ACTION =
            "hk.com.mobileye.jason.adlaLeader.VERSION_INFO_UPDATE";

    public static final String CMD_RESET_RESP_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CMD_RESET_RESP";
    public static final String EXTEND_DELAY =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DELAY";

    public static final String CMD_RESET_ME_RESP_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CMD_RESET_ME_RESP";

    public static final String CMD_TEST_RESP_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CMD_TEST_RESP";
    public static final String EXTEND_WORK_TIME =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_WORK_TIME";

    public static final String CMD_SWITCH_SCREEN_REQ_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CMD_SWITCH_SCREEN_REQ";
    public static final String CMD_SWITHCH_SCREEN_NOTIFY_ACTION =
            "hk.com.mobileye.jason.adlaLeader.CMD_SWITCH_SCREEN_NOTIFY";
    public static final String EXTEND_SCREEN_ID =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_SCREEN_ID";

    public static final String SPEED_CHANGED_ACTION =
            "hk.com.mobileye.jason.adlaLeader.SPEED_CHANGED";
    public static final String EXTEND_SPEED =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_SPEED";

    public static final String CMD_WRITE_FIRMWARE_REQ_ACTION =
            "hk.com.mobileye.jason.adlaLeader.WRITE_FIRMWARE";
    public static final String EXTNED_FIRMWARE_ID =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_FIRMWARE_ID";


    public static final String DAY_STAT_UPDATE_ACTION =
            "hk.com.mobileye.jason.adlaLeader.DAY_STAT_UPDATE";
    public static final String EXTEND_DAY_STAT_DATE =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DAY_STAT_DATE_ID";
    public static final String EXTEND_DAY_STAT_INDEX =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DAY_STAT_INDEX_ID";
    public static final String EXTEND_DAY_STAT_RUN_TIME =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DAY_STAT_RUN_TIME_ID";


    public static final String MONTH_STAT_UPDATE_ACTION =
            "hk.com.mobileye.jason.adlaLeader.MONTH_STAT_UPDATE";

    public static final String DVR_FILE_LIST_ACTION =
            "hk.com.mobileye.jason.adlaLeader.DVR_FILE_LIST";
    public static final String EXTEND_DVR_FILE_LIST =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DVR_FILE_LIST";

    public static final String DVR_PLAY_FILE_ACTION =
            "hk.com.mobileye.jason.adlaLeader.DVR_PLAY_FILE";
    public static final String EXTEND_DVR_PLAY_FILE_TYPE =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DVR_PLAY_FILE_TYPE";
    public static final String EXTEND_DVR_PLAY_CTRL =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DVR_PLAY_CTRL";
    public static final String EXTEND_DVR_PLAY_FILE_NAME =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_DVR_FILE_NAME";

    public static final String UDP_SEND_ACTION =
            "hk.com.mobileye.jason.adlaLeader.UDP_SEND";
    public static final String EXTEND_UDP_SEND_BUFFER =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_UDP_SEND_BUFFER";

    public static final String LOG_CONTENT_ACTION =
            "hk.com.mobileye.jason.adlaLeader.LOG_CONTENT";
    public static final String EXTEND_LOG_CONTENT =
            "hk.com.mobileye.jason.adlaLeader.EXTEND_LOG_CONTENT";
}

