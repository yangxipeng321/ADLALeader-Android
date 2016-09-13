package com.adasleader.jason.adasleader.preference;

/**
 * Created by Jason on 2016/9/6.
 *
 */
public class WarningConfigItem {
    private byte mDefaultKey;
    private byte mKey;
    private String[] mDescs;
    private byte[] mKeys;
    private String mTitle;

    //aDefaultKey必须是[key: description]中有的，不然就没法玩了
    //aKey则有可能不在其中
    public WarningConfigItem(String title, String[] descs, byte[] keys, byte aDefaultKey, byte aKey) {
        mTitle = title;
        mDescs = descs;
        mKeys = keys;
        mDefaultKey = aDefaultKey;
        mKey = aKey;
    }

    public String getDesc() {
        byte theKey = getKey();
        for (int i = 0; i < mKeys.length; i++) {
            if (mKeys[i] == theKey) {
                return mDescs[i];
            }
        }

        return "";
    }

    public void setDesc(String value) {
        for (int i = 0; i<mDescs.length; i++) {
            if (mDescs[i].equals(value)) {
                mKey = mKeys[i];
            }
        }
    }

    public byte getKey() {
        for (int i = 0; i < mKeys.length; i++) {
            if (mKey == mKeys[i])
                return mKey;
        }
        return mDefaultKey;
    }

    public String getTitle() {
        return mTitle;
    }
}
