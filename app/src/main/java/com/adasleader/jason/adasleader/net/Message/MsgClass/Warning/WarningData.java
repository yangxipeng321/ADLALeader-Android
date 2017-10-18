package com.adasleader.jason.adasleader.net.Message.MsgClass.Warning;

import com.adasleader.jason.adasleader.net.Message.MsgBase;
import com.adasleader.jason.adasleader.net.Message.MsgConst;

/**
 * Created by Jason on 2015/1/5.
 */
public class WarningData extends MsgBase{
    private byte[] mWarningData = null;
    public byte fcw = 0;
    public byte ufcw = 0;
    public byte ldwLeft = 0;
    public byte ldwRight = 0;
    public byte pcw = 0;
    public byte overSpeed =0;
    public int hmw = 0;
    public int speedLimit = 0;
    public int speed = 0;
    public byte carBreak = 0;
    public byte wiper = 0;
    public byte blinkLeft = 0;
    public byte blinkRight = 0;
    public byte highBeam = 0;

    public boolean decode() {
        boolean result = true;

        try {
            //decode header
            if (!super.decode()) {
                return  false;
            }

            byte[] mData = getData();
//            System.arraycopy(mData, 0, mData, 0, mData.length);
            int index = MsgConst.MSG_LEN_HEADER +4 ;
            if (mData.length < index + MsgConst.TP_WARNING_VALUE_LEN) {
                return false;
            }

            mWarningData = new byte[MsgConst.TP_WARNING_VALUE_LEN];
            System.arraycopy(mData, index, mWarningData, 0, mWarningData.length);

            fcw = (byte) ((mData[index] & 0x80) >>> 7);
            ufcw = (byte) (mData[index] & 0x40);
            ldwLeft = (byte) (mData[index] & 0x20);
            ldwRight = (byte) (mData[index] & 0x10);
            pcw = (byte) ((mData[index] & 0x0c) >> 2);
            overSpeed = (byte) (mData[index] & 0x02);

            hmw = mData[++index] & 0xff;
            speedLimit = mData[++index] & 0xff;
            speed = mData[++index] & 0xff;

            carBreak = (byte) (mData[++index] & 0x80);
            wiper = (byte) (mData[index] & 0x40);
            blinkLeft = (byte) (mData[index] & 0x20);
            blinkRight = (byte) (mData[index] & 0x10);
            highBeam = (byte) (mData[index] & 0x80);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public byte[] getWarnData() {
        return mWarningData;
    }
}
