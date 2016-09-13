package com.adasleader.jason.adasleader.common;

import android.text.TextUtils;

/**
 * Created by Jason on 2015/1/20.
 */
public class AM_AWS_SETUP {
    private static final String fileName = Constants.MH_CONFIG_FILE;
    private static final String SEPARATOR = " = ";
    private static final String strHW_Ticks = "HW_Ticks";
    private static final String strHMW_MaxValue = "HMW_MaxValue";
    private static final String strHMW_MinValue = "HMW_MinValue";
    private static final String strHMWModeLevel = "HMWModeLevel";
    private static final String strbuzzerMaxVolume = "buzzerMaxVolume";
    private static final String strbuzzerMinVolume = "buzzerMinVolume";
    private static final String strVolume = "Volume";
    private static final String strVB_Ticks = "VB_Ticks";
    private static final String strVB_MaxValue = "VB_MaxValue";
    private static final String strVB_MinValue = "VB_MinValue";
    private static final String strVBLevel = "virtualBumperLevel";
    private static final String strLDWTicks = "LDW_Ticks";
    private static final String strLDWModeLevel = "LDWModeLevel";
    private static final String strLDWSpeed = "LDW_Speed";
    private static final String strSpeedLDWMaxOn = "SpeedLDW_MaxOn";
    private static final String strSpeedLDWMaxOff = "SpeedLDW_MaxOff";
    private static final int[] JS_VOLUME_TICKS = {0, 1, 2, 3, 5};
    private static final String[] JS_VOLUME_TICKS_DESC = {"关", "低", "中", "高", "最大"};
    private static final String[] JS_HMW_TICKS = {"0.6", "0.8", "1.0"};
    private static final String[] JS_HMW_TICKS_DESC = {"近", "中", "远"};
    private static final String[] JS_VB_TICKS = {"1.0", "1.4", "2.0"};
    private static final String[] JS_VB_TICKS_DESC = {"低", "中", "高"};
    private static final String[] JS_LDW_SPEED_TICKS = {"55", "65", "75"};
    private static final String[] JS_LDW_SPEED_TICKS_DESC = {"55公里/小时", "65公里/小时", "75公里/小时"};

    private String[] mStrArray = null;

    private int idxHW_Ticks = -1;
    private int idxHMW_MaxValue = -1;
    private int idxHMW_MinValue = -1;
    private int idxHMWModeLevel = -1;
    private int idxbuzzerMaxVolume = -1;
    private int idxbuzzerMinVolume = -1;
    private int idxVolume = -1;
    private int idxVB_Ticks = -1;
    private int idxVB_MaxValue = -1;
    private int idxVB_MinValue = -1;
    private int idxVBLevel = -1;
    private int idxLDWTicks = -1;
    private int idxLDWModeLevel = -1;
    private int idxLDWSpeed = -1;
    private int idxSpeedLDWMaxOn = -1;
    private int idxSpeedLDWMaxOff = -1;


    public AM_AWS_SETUP(byte[] data) {
        this(data, 0, data.length);
    }

    public AM_AWS_SETUP(byte[] data, int offset, int len) {
        String s = new String(data, offset, len);
        mStrArray = s.split("\n");
        initIndex();
    }

    private void initIndex() {
        if (mStrArray!=null && mStrArray.length > 0) {
            for (int i = 0; i < mStrArray.length; i++) {
                if (idxHW_Ticks == -1 && mStrArray[i].indexOf(strHW_Ticks)==0) {
                    idxHW_Ticks = i;
                }
                if (idxHMW_MaxValue == -1 && mStrArray[i].indexOf(strHMW_MaxValue) == 0) {
                    idxHMW_MaxValue = i;
                }
                if (idxHMW_MinValue == -1 && mStrArray[i].indexOf(strHMW_MinValue) == 0) {
                    idxHMW_MinValue = i;
                }
                if (idxHMWModeLevel == -1 && mStrArray[i].indexOf(strHMWModeLevel) == 0) {
                    idxHMWModeLevel = i;
                }
                if (idxbuzzerMaxVolume == -1 && mStrArray[i].indexOf(strbuzzerMaxVolume) == 0) {
                    idxbuzzerMaxVolume = i;
                }
                if (idxbuzzerMinVolume == -1 && mStrArray[i].indexOf(strbuzzerMinVolume) == 0) {
                    idxbuzzerMinVolume = i;
                }
                if (idxVolume == -1 && mStrArray[i].indexOf(strVolume) == 0) {
                    idxVolume = i;
                }
                if (idxVB_Ticks == -1 && mStrArray[i].indexOf(strVB_Ticks) == 0) {
                    idxVB_Ticks = i;
                }
                if (idxVB_MaxValue == -1 && mStrArray[i].indexOf(strVB_MaxValue) == 0) {
                    idxVB_MaxValue = i;
                }
                if (idxVB_MinValue == -1 && mStrArray[i].indexOf(strVB_MinValue) == 0) {
                    idxVB_MinValue = i;
                }
                if (idxVBLevel == -1 && mStrArray[i].indexOf(strVBLevel) == 0) {
                    idxVBLevel = i;
                }
                if (idxLDWTicks == -1 && mStrArray[i].indexOf(strLDWTicks) == 0) {
                    idxLDWTicks = i;
                }
                if (idxLDWModeLevel == -1 && mStrArray[i].indexOf(strLDWModeLevel) == 0) {
                    idxLDWModeLevel = i;
                }
                if (idxLDWSpeed == -1 && mStrArray[i].indexOf(strLDWSpeed) == 0) {
                    idxLDWSpeed = i;
                }
                if (idxSpeedLDWMaxOn == -1 && mStrArray[i].indexOf(strSpeedLDWMaxOn) == 0) {
                    idxSpeedLDWMaxOn = i;
                }
                if (idxSpeedLDWMaxOff == -1 && mStrArray[i].indexOf(strSpeedLDWMaxOff) == 0) {
                    idxSpeedLDWMaxOff = i;
                }
                if (idxHMW_MaxValue >= 0 && idxHMW_MinValue >= 0
                        && idxHW_Ticks >= 0 && idxHMWModeLevel >= 0
                        && idxbuzzerMaxVolume >= 0 && idxbuzzerMinVolume >= 0
                        && idxVolume >= 0 && idxVB_Ticks >= 0 & idxVBLevel >= 0
                        && idxVB_MaxValue >= 0 && idxVB_MinValue >= 0
                        && idxLDWTicks >= 0 && idxLDWModeLevel >= 0
                        && idxLDWSpeed >= 0 && idxSpeedLDWMaxOn >= 0 && idxSpeedLDWMaxOff >= 0) {
                    break;
                }
            }
        }
    }

    public String getFileName(){ return fileName; }

    private String getHW_Ticks() {
        return idxHW_Ticks >= 0 ? mStrArray[idxHW_Ticks].split(SEPARATOR)[1] : null;
    }

    private String getHMW_MaxValue() {
        if (idxHMW_MaxValue ==-1) { return null; }
        String valueStr = mStrArray[idxHMW_MaxValue].split(SEPARATOR)[1];
        //Check whether the value is greater than JS_HMW_TICKS's max value.
        //If not, set it to max value
        double value = Double.valueOf(valueStr);
        double jsMax = Double.valueOf(JS_HMW_TICKS[JS_HMW_TICKS.length - 1]);
        if (value < jsMax) {
            value = jsMax;
            String str = String.format("%.2f", value);
            mStrArray[idxHMW_MaxValue] = strHMW_MaxValue + SEPARATOR + str;
        }
        //convert 1.60 to 1.6
        return String.format("%.1f", value);
    }

    private String getHMW_MinValue() {
        if (idxHMW_MinValue ==-1) { return null;}
        String valueStr = mStrArray[idxHMW_MinValue].split(SEPARATOR)[1];
        //Check whether the value is less than JS_HMW_TICKS's min value
        //If not ,set it to min value
        double value = Double.valueOf(valueStr);
        double jsMin = Double.valueOf(JS_HMW_TICKS[0]);
        if (value > jsMin) {
            value = jsMin;
            String str = String.format("%.2f", value);
            mStrArray[idxHMW_MinValue] = strHMW_MinValue + SEPARATOR + str;
        }
        //convert 0.60 to 0.6
        return String.format("%.1f", value);
    }

    public String getHw_TicksAllowed() {
        String ticks = getHW_Ticks();
        String max = getHMW_MaxValue();
        String min = getHMW_MinValue();
        if (null == ticks || null == max || null == min) {
            return null;
        }

        double iMax = Double.parseDouble(max);
        double iMin = Double.parseDouble(min);
        String[] tickArray = ticks.split(" ");
        int idxMax = tickArray.length -1;
        int idxMin = 1;
        for (int i = 1; i < tickArray.length; i++) {
            double tick = Double.parseDouble(tickArray[i]);
            if (iMin > tick) idxMin = i + 1;
            if (iMax < tick ) idxMax = i - 1;
        }

        if (idxMax > idxMin) {
            idxMax = ticks.indexOf(tickArray[idxMax]);
            idxMin = ticks.indexOf(tickArray[idxMin]);
            String ticksAllowed = ticks.substring(idxMin, idxMax + max.length());
            if (isAContainB(ticksAllowed, JS_HMW_TICKS)) {
                return TextUtils.join(" ", JS_HMW_TICKS_DESC);
            }
        }
        return null;
    }

    public String getHMWModeLevel() {
        String temp = getHW_Ticks();
        if (null == temp || idxHMWModeLevel==-1) { return null; }

        String[] ticks = temp.split(" ");
        int level = Integer.parseInt(mStrArray[idxHMWModeLevel].split(SEPARATOR)[1]) + 1;
        if (level > 0 && level < ticks.length) {
            String value = ticks[level].trim();
            // It is possible for the old device that the hmwModeLevel isn't in the
            // array["0.6", "0.8", "1.0"]. So this function will just return the value.
            // If the hmwModeLevel is in the array, then find and return the
            // corresponding description
            for (int i = 0; i<JS_HMW_TICKS.length; i++) {
                if (value.equals(JS_HMW_TICKS[i])) {
                    return JS_HMW_TICKS_DESC[i];
                }
            }
            return value;
        }
        return null;
    }

    public boolean setHMWModeLevel(String value) {
        String temp = getHW_Ticks();
        if (null == temp || idxHMWModeLevel==-1) { return false;}

        String tick = null;
        for (int i = 0; i < JS_HMW_TICKS_DESC.length; i++) {
            if (value.equals(JS_HMW_TICKS_DESC[i])) {
                tick = JS_HMW_TICKS[i];
                break;
            }
        }
        if (tick == null) {
            return false;
        }

        String[] strs = temp.split(" ");
        for (int i = 1; i < strs.length; i++) {
            if (strs[i].equals(tick)) {
                mStrArray[idxHMWModeLevel] = strHMWModeLevel + SEPARATOR + Integer.toString(i - 1);
                return true;
            }
        }
        return false;
    }

    private int getbuzzerMaxVolume() {
        return idxbuzzerMaxVolume >= 0 ?
                Integer.parseInt(mStrArray[idxbuzzerMaxVolume].split(SEPARATOR)[1]) : -1;
    }

    private int getbuzzerMinVolume() {
        return idxbuzzerMinVolume >= 0 ?
                Integer.parseInt(mStrArray[idxbuzzerMinVolume].split(SEPARATOR)[1]) : -1;
    }

    public String getVolumeTicksAllowed() {
        int max = getbuzzerMaxVolume();
        int min = getbuzzerMinVolume();
        if (max < 0 || min < 0 || max < min
                || min>JS_VOLUME_TICKS[0] || max<JS_VOLUME_TICKS[JS_VOLUME_TICKS.length-1]) {
            return null;
        }

        return TextUtils.join(" ", JS_VOLUME_TICKS_DESC);
    }

    public String getVolume() {
        if (idxVolume < 0) {
            return null;
        }

        int volume = Integer.parseInt(mStrArray[idxVolume].split(SEPARATOR)[1]);
        String desc = String.valueOf(volume);
        for (int i = 0; i < JS_VOLUME_TICKS.length; i++) {
            if (volume == JS_VOLUME_TICKS[i]) {
                return JS_VOLUME_TICKS_DESC[i];
            }
        }
        return desc;
    }

    public boolean setVolume(String value) {
        if (getbuzzerMaxVolume() == -1 || getbuzzerMinVolume() == -1 || getVolume() == null) {
            return false;
        }

        int volume = -1;
        for (int i = 0; i < JS_VOLUME_TICKS_DESC.length; i++) {
            if (value.equals(JS_VOLUME_TICKS_DESC[i])) {
                volume = JS_VOLUME_TICKS[i];
                break;
            }
        }
        if (volume == -1) {
            return false;
        }

        mStrArray[idxVolume] = strVolume + SEPARATOR + String.valueOf(volume);
        return true;
    }

    private String getVB_Ticks() {
        return idxVB_Ticks >= 0 ? mStrArray[idxVB_Ticks].split(SEPARATOR)[1] : null;
    }

    private String getVB_MaxValue() {
        if (idxVB_MaxValue ==-1) { return null; }
        String valueStr = mStrArray[idxVB_MaxValue].split(SEPARATOR)[1];
        //Check wheterh value is greater than JS_VB_TICKS's max value
        //If not, set to max value
        double value = Double.valueOf(valueStr);
        double jsMax = Double.valueOf(JS_VB_TICKS[JS_VB_TICKS.length - 1]);
        if (value < jsMax) {
            value = jsMax;
            String str = String.format("%.2f", value);
            mStrArray[idxVB_MaxValue] = strVB_MaxValue + SEPARATOR + str;
        }
        return String.format("%.1f", value);
    }

    private String getVB_MinValue() {
        if (idxVB_MinValue ==-1) { return null; }
        String valueStr =  mStrArray[idxVB_MinValue].split(SEPARATOR)[1];
        //Check whether value is less than JS_VB_TICKS's min value
        //If not, set to min value
        double value = Double.valueOf(valueStr);
        double jsMin = Double.valueOf(JS_VB_TICKS[0]);
        if (value > jsMin) {
            value = jsMin;
            String str = String.format("%.2f", value);
            mStrArray[idxVB_MinValue] = strVB_MinValue + SEPARATOR + str;
        }
        return String.format("%.1f", value);
    }

    public String getVB_TicksAllowed() {
        String ticks = getVB_Ticks();
        String max = getVB_MaxValue();
        String min = getVB_MinValue();
        if (null == ticks || null == max || null == min) {
            return null;
        }

        //VB_Ticks = 11 0.5 0.6 0.7 0.8 0.9 1.0 1.2 1.4 1.6 1.8 2.0
        //VB_MaxValue = 2.5
        //idxMax should be indexOf("2.0")

        int idxMax = ticks.indexOf(max);
        if (idxMax < 1) {
            idxMax = ticks.length();
            max = "";
        }
        int idxMin = ticks.indexOf(min);
        if ((idxMax > idxMin) && (idxMin >= 0)) {
            String ticksAllowed = ticks.substring(idxMin, idxMax + max.length());
            if (isAContainB(ticksAllowed, JS_VB_TICKS)) {
                return TextUtils.join(" ", JS_VB_TICKS_DESC);
            }
        }
        return null;
    }

    public String getVBLevel() {
        String temp = getVB_Ticks();
        if (null == temp || idxVBLevel==-1) { return null; }

        String[] ticks = temp.split(" ");
        int level = Integer.parseInt(mStrArray[idxVBLevel].split(SEPARATOR)[1]) + 1;
        if (level > 0 && level < ticks.length) {
            String value = ticks[level].trim();
            // It is possible for the old device that the VBLevel isn't in the
            // array["1.2", "1.6", "2.0"]. So this function will just return the value.
            // If the VBLevel is in the array, then find and return the
            // corresponding description
            for (int i = 0; i < JS_VB_TICKS.length; i++) {
                if (value.equals(JS_VB_TICKS[i])) {
                    return JS_VB_TICKS_DESC[i];
                }
            }
            return value;
        }
        return null;
    }

    public boolean setVBLevel(String value) {
        String temp = getVB_Ticks();
        if (null == temp || idxVBLevel==-1) { return false;}

        String tick = null;
        for (int i = 0; i < JS_VB_TICKS_DESC.length; i++) {
            if (value.equals(JS_VB_TICKS_DESC[i])) {
                tick = JS_VB_TICKS[i];
                break;
            }
        }
        if (null == tick) {
            return false;
        }

        String[] strs = temp.split(" ");
        for (int i = 1; i < strs.length; i++) {
            if (strs[i].equals(tick)) {
                mStrArray[idxVBLevel] = strVBLevel + SEPARATOR + Integer.toString(i - 1);
                return true;
            }
        }
        return false;
    }

    /**
     * @return   -1 : not find   0 LDW disable  1 warning after touch line
     * 2 warning before touch line
     */
    public int getLDWModeLevel() {
        if (idxLDWModeLevel < 0 )
            return -1;

        String str = mStrArray[idxLDWModeLevel].split(SEPARATOR)[1].trim();
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * @param value can only be 0 or 2
     * @return
     */
    public boolean setLDWModeLevel(int value) {
        if (idxLDWModeLevel < 0)
            return false;

        if (value != 0 && value != 2)
            return false;

        mStrArray[idxLDWModeLevel] = strLDWModeLevel + SEPARATOR + Integer.toString(value);
        return true;
    }

    public String getLDWSpeedTicks() {
        return TextUtils.join(" ", JS_LDW_SPEED_TICKS_DESC);
    }

    public String getLDWSpeed() {
        if (idxLDWSpeed < 0 || idxSpeedLDWMaxOn < 0 || idxSpeedLDWMaxOff < 0)
            return null;

        String value = mStrArray[idxSpeedLDWMaxOff].split(SEPARATOR)[1].trim();
        for (int i = 0; i < JS_LDW_SPEED_TICKS.length; i++) {
            if (value.equals(JS_LDW_SPEED_TICKS[i])) {
                return JS_LDW_SPEED_TICKS_DESC[i];
            }
        }
        return value;
    }

    public boolean setLDWSpeed(String value) {
        if (idxLDWSpeed < 0 || idxSpeedLDWMaxOn < 0 || idxSpeedLDWMaxOff < 0)
            return false;

        for (int i = 0; i < JS_LDW_SPEED_TICKS_DESC.length; i++) {
            if (value.equals(JS_LDW_SPEED_TICKS_DESC[i])) {
                String speed = JS_LDW_SPEED_TICKS[i];
                int maxOff = Integer.parseInt(speed);
                int maxOn = maxOff - 5;
                mStrArray[idxLDWSpeed] = strLDWSpeed + SEPARATOR + Integer.toString(maxOn)
                        + " " + Integer.toString(maxOff);
                mStrArray[idxSpeedLDWMaxOn] = strSpeedLDWMaxOn + SEPARATOR + Integer.toString(maxOn);
                mStrArray[idxSpeedLDWMaxOff] = strSpeedLDWMaxOff + SEPARATOR + Integer.toString(maxOff);
                return true;
            }
        }
        return false;
    }

    public byte[] getArray() {
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < mStrArray.length; i++) {
            temp.append(mStrArray[i]);
            temp.append('\n');
        }
        return temp.toString().getBytes();
    }

    private boolean isAContainB(String a, String[] b) {
        for (int i = 0; i < b.length; i++) {
            if (a.indexOf(b[i]) < 0) {
                return false;
            }
        }
        return true;
    }

}

