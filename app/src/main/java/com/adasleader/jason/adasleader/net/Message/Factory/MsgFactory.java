package com.adasleader.jason.adasleader.net.Message.Factory;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdCalibrateReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdCalibrateResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetMEReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetMEResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdResetResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdSwitchScreen;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdTestReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Cmd.CmdTestResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrFileListResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrFormat;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrKey;
import com.adasleader.jason.adasleader.net.Message.MsgClass.DVR.DvrPlayFileResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugDVRCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugFPGACmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.DebugMCUCmd;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.LogAdasgateContent;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Debug.LogMCUContent;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileReadReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileReadResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileWriteReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.File.FileWriteResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.HeartBeat.HeartBeatReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.HeartBeat.HeartBeatResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.ParameterReadReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.ParameterReadResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.ParameterSetReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Settings.ParameterSetResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnDayStatReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnDayStatResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnMonthStatReq;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarnMonthStatResp;
import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.WarningData;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;

/**
 * Created by Jason on 2015/1/4.
 *
 */
public class MsgFactory {
    //Use the singleton pattern
    private static final Object mLock = new Object();
    private static MsgFactory mInstance = null;
    private MsgFactory(){}
    public static MsgFactory getInstance() {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new MsgFactory();
            }
            return mInstance;
        }
    }

    public MsgBase create(byte[] buffer) {
        if (buffer.length < MsgConst.MSG_LEN_HEADER) {return  null;}

        int serviceType = buffer[10] & 0xff;
        int msgType = buffer[11] & 0xff;
        int responseType = buffer[7] & 0xff;
        int length = (buffer[4] & 0xff) + ((buffer[5] & 0xff) << 8);
        if (length<MsgConst.MSG_LEN_HEADER) {return null;}
        MsgBase obj = MsgFactory.getInstance().create(serviceType, msgType, responseType);
        if (obj != null) {
            //Must call setVar before arraycopy!!! The bytes array is created in the setVar method
            // which will be used in the arraycopy.
            obj.setVar(length);
            System.arraycopy(buffer, 0, obj.getData(), 0, length);
        }
        return  obj;
    }

    public MsgBase create(int aServiceType, int aMsgType, int aResponseType) {
        MsgBase obj;
        switch (aServiceType) {
            case ServiceType.SERVICE_FILE:
                obj = createFile(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_HEARTBEAT:
                obj = createHeartbeat(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_SETTINGS:
                obj = createSetting(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_WARNING:
                obj = createWarning(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_CMD:
                obj = createCmd(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_DVR:
                obj = createDVR(aMsgType, aResponseType);
                break;
            case ServiceType.SERVICE_DEBUG:
                obj = createDebug(aMsgType, aResponseType);
                break;
            default:
                obj = null;
                break;
        }
        return obj;
    }

    private MsgBase createFile(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.REQUEST:
                switch (aMsgType) {
                    case MessageType.FILE_READ_REQ:
                        obj = new FileReadReq();
                        break;
                    case MessageType.FILE_WRITE_REQ:
                        obj = new FileWriteReq();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case MessageType.FILE_READ_RESP:
                        obj = new FileReadResp();
                        break;
                    case MessageType.FILE_WRITE_RESP:
                        obj = new FileWriteResp();
                        break;
                }
                break;
        }
        return obj;
    }

    private MsgBase createHeartbeat(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.REQUEST:
                switch (aMsgType) {
                    case MessageType.HEART_BEAT_REQ:
                        obj = new HeartBeatReq();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case MessageType.HEART_BEAT_RESP:
                        obj = new HeartBeatResp();
                        break;
                }
                break;
        }
        return obj;
    }

    private MsgBase createWarning(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.INFO:
                switch (aMsgType) {
                    case MessageType.WARNING_REQ:
                        obj = new WarningData();
                        break;
                }
                break;
            case ResponseType.REQUEST:
                switch (aMsgType) {
                    case MessageType.WARN_DAY_STAT_REQ:
                        obj = new WarnDayStatReq();
                        break;
                    case MessageType.WARN_MONTH_STAT_REQ:
                        obj = new WarnMonthStatReq();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case MessageType.WARN_DAY_STAT_RESP:
                        obj = new WarnDayStatResp();
                        break;
                    case MessageType.WARN_MONTH_STAT_RESP:
                        obj = new WarnMonthStatResp();
                        break;
                }
                break;
            default:
                break;
        }
        return obj;
    }

    private MsgBase createSetting(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.REQUEST:
                switch (aMsgType) {
                    case MessageType.PARA_READ_REQ:
                        obj = new ParameterReadReq();
                        break;
                    case MessageType.PARA_SET_REQ:
                        obj = new ParameterSetReq();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case MessageType.PARA_READ_RESP:
                        obj = new ParameterReadResp();
                        break;
                    case MessageType.PARA_SET_RESP:
                        obj = new ParameterSetResp();
                        break;
                }
                break;
        }
        return obj;
    }

    private MsgBase createCmd(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.REQUEST:
                switch (aMsgType) {
                    case MessageType.CMD_RESET_REQ:
                        obj = new CmdResetReq();
                        break;
                    case MessageType.CMD_RESET_ME_REQ:
                        obj = new CmdResetMEReq();
                        break;
                    case MessageType.CMD_TEST_REQ:
                        obj = new CmdTestReq();
                        break;
                    case MessageType.CMD_SWITCH_SCREEN_REQ:
                        obj = new CmdSwitchScreen();
                        break;
                    case MessageType.MSG_CMD_CALI_REQ:
                        obj = new CmdCalibrateReq();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case MessageType.CMD_RESET_RESP:
                        obj = new CmdResetResp();
                        break;
                    case MessageType.CMD_RESET_ME_RESP:
                        obj = new CmdResetMEResp();
                        break;
                    case MessageType.CMD_TEST_RESP:
                        obj = new CmdTestResp();
                        break;
                    case MessageType.CMD_SWITCH_SCREEN_RESP:
                        obj = new CmdSwitchScreen();
                        break;
                    case MessageType.MSG_CMD_CALI_RESP:
                        obj = new CmdCalibrateResp();
                        break;
                }
                break;
        }
        return  obj;
    }

    private MsgBase createDVR(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.INFO:
                switch (aMsgType) {
                    case MessageType.DVR_KEY_INFO:
                        obj = new DvrKey();
                        break;
                    case MessageType.DVR_FORMAT:
                        obj = new DvrFormat();
                        break;
                }
                break;
            case ResponseType.RESPONSE:
                switch (aMsgType) {
                    case  MessageType.DVR_FILE_LIST_RESP:
                        obj = new DvrFileListResp();
                        break;
                    case MessageType.DVR_PLAY_FILE_RESP:
                        obj = new DvrPlayFileResp();
                        break;
                }
                break;
        }
        return obj;
    }

    private MsgBase createDebug(int aMsgType, int aResponseType) {
        MsgBase obj = null;
        switch (aResponseType) {
            case ResponseType.INFO:
                switch (aMsgType) {
                    case MessageType.LOG_ADASGATE_CONTENT:
                        obj = new LogAdasgateContent();
                        break;
                    case MessageType.LOG_MCU_CONTENT:
                        obj = new LogMCUContent();
                        break;
                    case MessageType.DEBUG_MCU_COMMAND:
                        obj = new DebugMCUCmd();
                        break;
                    case MessageType.DEBUG_FPGA_COMMAND:
                        obj = new DebugFPGACmd();
                        break;
                    case MessageType.DEBUG_DVR_COMMAND:
                        obj = new DebugDVRCmd();
                        break;
                }
                break;
        }
        return  obj;
    }

}
