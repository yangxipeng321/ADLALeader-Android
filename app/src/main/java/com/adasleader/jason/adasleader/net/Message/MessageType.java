package com.adasleader.jason.adasleader.net.Message;

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

    public static final int MSG_CMD_POWER_OFF_REQ = 0x0A;
    public static final int MSG_CMD_POWER_OFF_RESP = 0x0A;

    public static final int MSG_CMD_SAVE_FRAME_REQ = 0x0B;
    public static final int MSG_CMD_SAVE_FRAME_RESP = 0x0B;

    public static final int MSG_CMD_STA_MODE_REQ = 0x0C;
    public static final int MSG_CMD_STA_MODE_RESP = 0x0C;

    public static final int MSG_CMD_CALI_REQ = 0x0D;
    public static final int MSG_CMD_CALI_RESP = 0x0D;


    public static final int DVR_KEY_INFO = 0x01;
    public static final int DVR_FILE_LIST_REQ = 0x02;
    public static final int DVR_FILE_LIST_RESP = 0x02;
    public static final int DVR_PLAY_FILE_REQ = 0x03;
    public static final int DVR_PLAY_FILE_RESP = 0x03;
    public static final int DVR_RECORD = 0x04;
    public static final int DVR_FORMAT = 0x05;


    public static final int LOG_ADASGATE_SWITCH = 0x01;
    public static final int LOG_ADASGATE_CONTENT = 0x02;
    public static final int LOG_MCU_SWITCH = 0x03;
    public static final int LOG_MCU_CONTENT = 0x04;
    public static final int DEBUG_MCU_COMMAND = 0x05;
    public static final int DEBUG_FPGA_COMMAND = 0x06;
    public static final int DEBUG_DVR_COMMAND = 0x07;

}
