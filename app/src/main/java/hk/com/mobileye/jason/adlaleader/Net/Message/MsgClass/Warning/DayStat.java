package hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Warning;

import android.util.Log;

import hk.com.mobileye.jason.adlaleader.common.WarnType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgConst;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVValue;


/**
 * Created by Jason on 2016/2/3.
 */
public class DayStat implements TLVValue {
    private byte[] mBytes = new byte[MsgConst.WARN_DAY_STAT_OFFSET + MsgConst.WARN_ITEM_LEN * MsgConst.WARN_ITEM_COUNT
            * MsgConst.WARN_DAY_RECORDS_COUNT];
    private WarnRecord[] mStat = new WarnRecord[MsgConst.WARN_DAY_RECORDS_COUNT];
    private int mDate;
    private int mMileage;
    private int mRunTime;
    private int mIndex;
    private int mFCW;
    private int mLDW;
    private int mPCW;
    private int mSpeeding;
    private int mHMW;

    public DayStat() {
        mDate = -1;
        mMileage = 0;
        mRunTime = 0;
        mIndex = 0;
        for (int i = 0; i < mStat.length; i++) {
            mStat[i] = new WarnRecord();
        }
    }

    public DayStat(byte[] buffer, int offset, int len) {
        System.arraycopy(buffer, offset, mBytes, 0, Math.min(len, mBytes.length));
        mDate = MsgUtils.bcd2int(MsgUtils.bytes2Int(mBytes, 0));
        mMileage = mBytes[4] + (mBytes[5] << 8);
        mRunTime = mBytes[6] + (mBytes[7] << 8);
        mIndex = mBytes[8] + (mBytes[9] << 8);

        int index = MsgConst.WARN_DAY_STAT_OFFSET;
        int recordLen = MsgConst.WARN_ITEM_LEN * MsgConst.WARN_ITEM_COUNT;
        for (int i = 0; i < mStat.length; i++) {
            if ((index + recordLen) > mBytes.length) {
                Log.e("DayStat", String.format("Read DayStat TLV error index + recordLen > buffer.length %d %d",
                        index + recordLen, mBytes.length));
                break;
            }

            mStat[i] = new WarnRecord(mBytes, index, recordLen);
            mFCW += mStat[i].getCount(WarnType.FCW);
            mLDW += mStat[i].getCount(WarnType.LDW);
            mPCW += mStat[i].getCount(WarnType.PCW);
            mSpeeding += mStat[i].getCount(WarnType.SPEEDING);
            mHMW += mStat[i].getCount(WarnType.HMW);

            index += recordLen;
        }
        Log.e("Statistics",  String.format("%d:  %d %d %d %d %d", mDate, mFCW, mLDW, mPCW, mSpeeding, mHMW));
    }

    public void clear() {
        mDate = 0;
        for (int i = 0; i < mStat.length; i++) {
            mStat[i].clear();
        }
    }

    public int getDate() {
        return mDate;
    }

    public int getMileage() {
        return mMileage;
    }

    public  int getRunTime() {
        return mRunTime;
    }

    public int getIndex() {
        return mIndex;
    }

    public WarnRecord[] getDayStat() {
        return mStat;
    }

    public String date2String() {
        int month = (mDate/100)%100;
        int day = mDate %100;
        return String.format("%d-%d", month, day);
    }

    public int getFCW() {
        return mFCW;
    }

    public int getLDW() {
        return mLDW;
    }

    public int getPCW() {
        return mPCW;
    }

    public int getSpeeding() {
        return mSpeeding;
    }

    public int getHMW() {
        return mHMW;
    }

    @Override
    public byte[] getBytes() {
        return new byte[0];
    }
}
