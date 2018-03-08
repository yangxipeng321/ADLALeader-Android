package com.adasleader.jason.adasleader.net.Message.MsgClass.Settings;

import com.adasleader.jason.adasleader.net.Message.MessageType;
import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;
import com.adasleader.jason.adasleader.net.Message.ResponseType;
import com.adasleader.jason.adasleader.net.Message.ServiceType;
import com.adasleader.jason.adasleader.net.Message.TLVType;

/**
 * Created by Jason on 2015/1/27.
 *
 */
public class ParameterReadResp extends MsgBase {
    @Override
    public void initMsg() {
        getHeader().MsgServiceType = ServiceType.SERVICE_SETTINGS;
        getHeader().MsgType = MessageType.PARA_READ_RESP;
        getHeader().MsgResponseType = ResponseType.RESPONSE;

//        getBody().add(TLVType.TP_WIFI_PASSWORD_ID, WifiPassword.class);
//        getBody().add(TLVType.TP_TIME_ID, long.class);
//        getBody().add(TLVType.TP_DEV_VER_ID, DevVersion.class);
//        getBody().add(TLVType.TP_MH_VER_ID, MHVersion.class);
//        getBody().add(TLVType.TP_FPGA_VER_ID, int.class);
    }

    @Override
    public boolean encode() {
        return super.encode();
    }

    @Override
    public boolean decode() {
        boolean result = true;
        try {
            if (!super.decode()) { return false;}

            getBody().clear();

            int index = MsgConst.MSG_LEN_HEADER;
            int tlvType, tlvLen;

            while (getMsgLength() > index + 4) {
                tlvType = (getData()[index] & 0xff)
                        + ((getData()[index + 1] & 0xff) << 8);
                tlvLen = (getData()[index + 2] & 0xff)
                        + ((getData()[index + 3] & 0xff) << 8);
                if (tlvLen < 4) {
                    result = false;
                } else if (tlvLen == 4) {
                    result = false;
                } else {
                    switch (tlvType) {
                        case TLVType.TP_WIFI_PASSWORD_ID:
                            WifiPassword password = new WifiPassword(getData(), index + 4,
                                    tlvLen - 4);
                            getBody().add(tlvType, password.getClass()).setValue(password);
                            break;
                        case TLVType.TP_TIME_ID:
                            long lTime = MsgUtils.bytes2Long(getData(), index + 4);
                            getBody().add(tlvType, long.class).setValue(lTime);
                            break;
                        case TLVType.TP_DEV_VER_ID:
                            DevVersion dev = new DevVersion(getData(), index + 4, tlvLen - 4);
                            getBody().add(tlvType, dev.getClass()).setValue(dev);
                            break;
                        case TLVType.TP_FPGA_VER_ID:
                            int fpgaVer = MsgUtils.bytes2Int(getData(), index + 4);
                            getBody().add(tlvType, int.class).setValue(fpgaVer);
                            break;
                        case TLVType.TP_MH_VER_ID:
                            MHVersion mh = new MHVersion(getData(), index + 4, tlvLen - 4);
                            getBody().add(tlvType, mh.getClass()).setValue(mh);
                            break;
                        case TLVType.TP_GATE_VER_ID:
                            int gateVer = MsgUtils.bytes2Int(getData(), index + 4);
                            getBody().add(tlvType, int.class).setValue(gateVer);
                            break;
                        case TLVType.TP_DVR_VER_ID:
                            int dvrVer = MsgUtils.bytes2Int(getData(), index + 4);
                            getBody().add(tlvType, int.class).setValue(dvrVer);
                            break;
                        default:
                            //result = false;
                            break;
                    }
                }
                index += tlvLen;
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
