package com.adasleader.jason.adasleader.net.Message.MsgClass.Warning;

import com.adasleader.jason.adasleader.common.WarnType;
import com.adasleader.jason.adasleader.net.Message.MsgConst;
import com.adasleader.jason.adasleader.net.Message.MsgUtils;

/**
 * Created by Jason on 2016/2/3.
 */
public class WarnRecord {
    private int[] mCount = new int[MsgConst.WARN_ITEM_COUNT];

    public WarnRecord() {
        for (int i = 0; i < mCount.length; i++) {
            mCount[i] = 0;
        }
    }

    public WarnRecord(byte[] input, int offset, int len) {
        for (int i = 0; i < Math.min(mCount.length, len/MsgConst.WARN_ITEM_LEN); i++) {
            mCount[i] = MsgUtils.bytes2Int(input, offset + i * MsgConst.WARN_ITEM_LEN);
        }
    }

    public void clear() {
        for (int i = 0; i < mCount.length; i++) {
            mCount[i] = 0;
        }
    }

    public void setCount(int[] aCount) {
        for (int i = 0; i < Math.min(aCount.length, mCount.length); i++) {
            mCount[i] = aCount[i];
        }
    }

    public void setCount(byte[] input, int offset) {
        if (input != null && (input.length - offset) < mCount.length * 4)
            return;

        for (int i = 0; i < mCount.length; i++) {
            mCount[i] = MsgUtils.bytes2Int(input, offset + 4 * i);
        }
    }

    public int[] getCounts() {
        return mCount;
    }

    public int getCount(WarnType warnType) {
        int result = -1;
        switch (warnType) {
            case FCW:
                result = mCount[warnType.getIndex()] + mCount[warnType.getIndex()+1];
                break;
            case LDW:
                result = mCount[warnType.getIndex()] + mCount[warnType.getIndex()+1];
                break;
            case PCW:
            case SPEEDING:
            case HMW:
                result = mCount[warnType.getIndex()];
                break;
        }
        return result;
    }
}
