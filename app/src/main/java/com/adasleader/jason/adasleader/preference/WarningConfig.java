package com.adasleader.jason.adasleader.preference;

import com.adasleader.jason.adasleader.common.Constants;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/9/5.
 *
 */
public class WarningConfig {
    private static final int FILE_SIZE = 64;

    private static final String FILE_NAME = Constants.MH_CONFIG_FILE;

    static final String CATEGORY_TITLE_WARN = "预警设置";
    static final String CATEGORY_TITLE_DISPLAY = "显示设置";
    static final String CATEGORY_TITLE_DVR = "紧急视频";

    static final String stateTitle = "特别提示";
    //static final String stateSummary = "开关打开后，将关闭原车屏上的特别提示。";
    static final String stateSummaryOn = "原车屏的特别提示已打开。";
    static final String stateSummaryOff = "原车屏上的特别提示已关闭。";

    static final String displayLogo = "显示ADASLeader标志";
    static final String displayLogo0 = "在所有界面显示";
    static final String displayLogo1 = "原车界面不显示";
    static final String displayLogo3 = "所有界面都不显示";

    static final String autoReturnADAS = "自动切换界面";
    static final String autoReturnADASOn = "倒车完成后自动切换回原视频界面";
    static final String autoReturnADASOff = "倒车完成后保持在原车界面";

    static final String dvrVirtualBumper = "虚拟保险杠";
    static final String dvrVirtualBumperOn = "虚拟保险杠预警时自动保存紧急视频";
    static final String dvrVirtualBumperOff = "虚拟保险杠预警时不保存紧急视频";


    //预警设置各项的标题
    static final String volumeStr = "报警音量";
    static final String hmwStr = "跟车距离预警";
    static final String hmwSpeedStr = "跟车距离预警速度";
    static final String virtualBumperStr = "虚拟保险杠灵敏度";
    static final String ldwStr = "车道偏离预警";
    static final String speedingDisplayStr = "超速预警模式";
    static final String speedingStr = "超速设置：限速";
    static final String speedingPercentStr = "超速设置：比例";
    static final String followCarStr = "堵车跟车预警";

    //各设置项在配置文件中的位置
    static final int volumeIndex = 16;
    static final int hmwIndex = 17;
    static final int hmwSpeedIndex = 18;
    static final int virtualBumperIndex = 19;
    static final int ldwIndex = 20;
    static final int speedingIndex = 21;
    static final int speedingPercentIndex = 22;
    static final int speedingDisplayIndex = 23;
    static final int followCarIndex = 24;

    //各项的默认值
    static final byte volumeDefault = 3;
    static final byte hmwDefault = 10;
    static final byte hmwSpeedDefault = 30;
    static final byte virtualBumperDefault = 20;
    static final byte ldwDefault = 55;
    static final byte speedingDefault = 30;
    static final byte speedingPercentDefault = 0;
    static final byte speedingDisplayDefault = 1;
    static final byte followCarDefault = 5;

    //各项的选择项（值与描述）
    static final byte[] JS_VOLUME = {0, 1, 2, 3, 5};
    static final String[] JS_VOLUME_DESC = {"关", "低", "中", "高", "最大"};

    static final byte[] JS_HMW = {6, 8, 10};
    static final String[] JS_HMW_DESC = {"近", "中", "远"};

    static final byte[] JS_HMW_SPEED = {30, 40, 50, 80};
    static final String[] JS_HMW_SPEED_DESC = {"30公里/小时", "40公里/小时", "50公里/小时",
            "80公里/小时"};

    static final byte[] JS_VB = {10, 14, 20};
    static final String[] JS_VB_DESC = {"低", "中", "高"};

    static final byte[] JS_LDW = {0, 45, 55, 65, 80};
    static final String[] JS_LDW_DESC = {"关", "45公里/小时", "55公里/小时", "65公里/小时",
            "80公里/小时"};

    static final byte[] JS_SPEEDING_DISPLAY = {0, 1, 2};
    static final String[] JS_SPEEDING_DISPLAY_DESC = {"静音", "精简", "正常"};

    static final byte[] JS_SPEEDING = {30, 40, 50, 60, 80, 100};
    static final String[] JS_SPEEDING_DESC = {"30公里/小时", "40公里/小时", "50公里/小时",
            "60公里/小时", "80公里/小时", "100公里/小时"};

    static final byte[] JS_SPEEDING_PERCENT = {0, 5, 10};
    static final String[] JS_SPEEDING_PERCENT_DESC = {"超过限速立即提醒", "超过限速5％提醒", "超过限速10％提醒"};

    static final byte[] JS_FOLLOW_CAR = {3, 5, 10};
    static final String[] JS_FOLLOW_CAR_DESC = {"近", "中", "远"};

    static final byte[] JS_DISPLAY_LOGO = {0, 2, 6};
    static final String[] JS_DISPLAY_LOG_DESC = {displayLogo0, displayLogo1, displayLogo3};

    //报警设置中显示的内容
    static final String[] TITLES = {volumeStr, hmwStr, hmwSpeedStr, virtualBumperStr,
            ldwStr, speedingDisplayStr, speedingStr, speedingPercentStr};
    static final int[] INDEXS = {volumeIndex, hmwIndex, hmwSpeedIndex, virtualBumperIndex,
            ldwIndex, speedingDisplayIndex, speedingIndex, speedingPercentIndex};
    static final byte[] DEFAULT_KEYS = {volumeDefault, hmwDefault, hmwSpeedDefault, virtualBumperDefault,
            ldwDefault, speedingDisplayDefault, speedingDefault, speedingPercentDefault};
    static final String[][] DESCS = {JS_VOLUME_DESC, JS_HMW_DESC, JS_HMW_SPEED_DESC, JS_VB_DESC,
            JS_LDW_DESC, JS_SPEEDING_DISPLAY_DESC, JS_SPEEDING_DESC, JS_SPEEDING_PERCENT_DESC};
    static final byte[][] KEYS = {JS_VOLUME, JS_HMW, JS_HMW_SPEED, JS_VB,
            JS_LDW, JS_SPEEDING_DISPLAY, JS_SPEEDING, JS_SPEEDING_PERCENT};
    //作为例外的速度设置项
    static final int SPEED_INDEX_MIN = 5;
    static final int SPEED_INDEX_MAX = 7;


    //显示设置中显示的内容
    static final String[][]DISPLAY_DESC = {};
    static final byte[][]DISPLAY_KEYS = {};



    private byte[] mData;
    public ArrayList<WarningConfigItem> items = new ArrayList<>();

    public WarningConfig(byte[] data, int offset, int len) {
        mData = new byte[WarningConfig.FILE_SIZE];
        int size = Math.min(len, WarningConfig.FILE_SIZE);
        System.arraycopy(data, offset, mData, 0, size);
        initWarningConfigItems();
    }

    private void initWarningConfigItems() {
        for (int i = 0; i<WarningConfig.DESCS.length; i++) {
            WarningConfigItem item = new WarningConfigItem(WarningConfig.TITLES[i],
                    WarningConfig.DESCS[i], WarningConfig.KEYS[i], WarningConfig.DEFAULT_KEYS[i],
                    mData[WarningConfig.INDEXS[i]]);
            items.add(item);
        }

        displayLogoItem = new WarningConfigItem(displayLogo, JS_DISPLAY_LOG_DESC, JS_DISPLAY_LOGO,
                (byte)0, (byte)(mData[25] & 0x06));
    }

    public byte[] getData() {
        for (int i = 0; i < items.size(); i++) {
            mData[WarningConfig.INDEXS[i]] = items.get(i).getKey();
        }
        return mData;
    }

    public String getFileName() {
        return WarningConfig.FILE_NAME;
    }

    public WarningConfigItem findItemByTitle(String title) {
        for (WarningConfigItem item : items) {
            if (title.equals(item.getTitle())) {
                return item;
            }
        }
        return null;
    }

    public boolean isDataHas40FF() {
        return mData[15] > 0;
    }

    public void clearData()  {
        mData[0] = 65;
        mData[1] = 68;
        mData[2] = 65;
        mData[3] = 83;
        mData[4] = 76;
        mData[5] = 101;
        mData[6] = 97;
        mData[7] = 100;
        mData[8] = 101;
        mData[9] = 114;

        mData[10] = 0;
        mData[11] = 0;
        mData[12] = 0;
        mData[13] = 0;

        mData[15] = 0;

        mData[24] = 0;
        mData[25] = (byte)(mData[25] & 0x0f);
        for (int i=26; i <=59; i++) {
            mData[i] = 0;
        }
    }

    public WarningConfigItem displayLogoItem;

    public boolean getStatementSwitch() {
        return (mData[25] & 0x01) == 0 ;
    }

    public void setStatementSwitch(boolean state) {
        if (state)
            mData[25] = (byte) (mData[25] & 0xFE);
        else
            mData[25] = (byte) (mData[25] | 0x01);
    }

    public boolean getAutoReturnADASSwitch() {
        return (mData[25] & 0x08) == 0;
    }

    public void setAutoReturnADASwitch(boolean isAuto){
        if (isAuto)
            mData[25] = (byte) (mData[25] & 0xF7);
        else
            mData[25] = (byte) (mData[25] | 0x08);
    }

    public void setDisplayLogo() {
        mData[25] = (byte)(mData[25] & 0xF9 | displayLogoItem.getKey());
    }

    public boolean getDVRVirtualBumperSwitch() {
        return (mData[26] & 0x01) == 0;
    }

    public void setDVRVirtualBumperSwitch(boolean isOn) {
        if (isOn)
            mData[26] = (byte) (mData[26] & 0xFE);
        else
            mData[26] = (byte) (mData[26] | 0x01);
    }
}
