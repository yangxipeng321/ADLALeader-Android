package com.adasleader.jason.adasleader.common;

/**
 * Created by Jason on 2016/2/29.
 *
 */

public enum WarnType {
    FCW("FCW", 0), LDW("LDW", 2), PCW("PCW", 4), SPEEDING("超速",5), HMW("HMW",6);

    private String name;
    private int index;

    WarnType(String aName, int aIndex) {
        name = aName;
        index = aIndex;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }
}