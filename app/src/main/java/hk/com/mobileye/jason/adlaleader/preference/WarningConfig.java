package hk.com.mobileye.jason.adlaleader.preference;

import java.util.ArrayList;

/**
 * Created by Jason on 2016/9/5.
 */
public class WarningConfig {
    static final int FILE_SIZE = 64;

    static final String FILE_NAME = "WARNING.CONF";

    static final String TITLE = "预警设置";

    //预警设置各项的标题
    static final String volumeStr = "报警音量";
    static final String hmwStr = "跟车距离预警";
    static final String hmwSpeedStr = "跟车距离预警速度";
    static final String virtualBumperStr = "虚拟保险杠灵敏度";
    static final String ldwStr = "车道偏离预警";
    static final String speedingDisplayStr = "超速预警显示方式";
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
    static final String[] JS_SPEEDING_DISPLAY_DESC = {"不显示", "正常显示", "精简显示"};

    static final byte[] JS_SPEEDING = {30, 40, 50, 60, 80, 100};
    static final String[] JS_SPEEDING_DESC = {"30公里/小时", "40公里/小时", "50公里/小时",
            "60公里/小时", "80公里/小时", "100公里/小时"};

    static final byte[] JS_SPEEDING_PERCENT = {0, 5, 10};
    static final String[] JS_SPEEDING_PERCENT_DESC = {"超过限速", "超过限速5%", "超过限速10/%"};

    static final byte[] JS_FOLLOW_CAR = {3, 5, 10};
    static final String[] JS_FOLLOW_CAR_DESC = {"近", "中", "远"};

    static final String[] TITLES = {volumeStr, hmwStr, hmwSpeedStr, virtualBumperStr, ldwStr,
            speedingDisplayStr, speedingStr, speedingPercentStr};

    static final int[] INDEXS = {volumeIndex, hmwIndex, hmwSpeedIndex, virtualBumperIndex,
            ldwIndex, speedingDisplayIndex, speedingIndex, speedingPercentIndex};

    static final byte[] DEFAULT_KEYS = {volumeDefault, hmwDefault, hmwSpeedDefault,
            virtualBumperDefault, ldwDefault, speedingDisplayDefault, speedingDefault, speedingPercentDefault};

    static final String[][] DESCS = {JS_VOLUME_DESC, JS_HMW_DESC, JS_HMW_SPEED_DESC, JS_VB_DESC,
            JS_LDW_DESC, JS_SPEEDING_DISPLAY_DESC, JS_SPEEDING_DESC, JS_SPEEDING_PERCENT_DESC};
    static final byte[][] KEYS = {JS_VOLUME, JS_HMW, JS_HMW_SPEED, JS_VB,
            JS_LDW, JS_SPEEDING_DISPLAY, JS_SPEEDING, JS_SPEEDING_PERCENT};

    static final int SPEED_INDEX_MIN = 5;
    static final int SPEED_INDEX_MAX = 7;


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
}
