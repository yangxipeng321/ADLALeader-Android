package hk.com.mobileye.jason.adlaleader.common;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Settings.DevVersion;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Settings.MHVersion;
import hk.com.mobileye.jason.adlaleader.statistics.StatisticsData;
import hk.com.mobileye.jason.adlaleader.upgrade.FirmwareInfo;

/**
 * Created by Jason on 2015/1/30.
 */
public class MyApplication extends Application {
    public StatisticsData mDayStats = null;


    @Override
    public void onCreate() {
        super.onCreate();

        mDevVersion = new DevVersion(
                getSettings().getInt(Constants.PREFS_ITEM_DEV_SN, 0),
                getSettings().getInt(Constants.PREFS_ITEM_DEV_SW_VER, 0),
                getSettings().getInt(Constants.PREFS_ITEM_DEV_HW_VER, 0));
        mMHVersion = new MHVersion(
                getSettings().getString(Constants.PREFS_ITEM_MH_SN, ""),
                getSettings().getString(Constants.PREFS_ITEM_MH_SW_VER, ""),
                getSettings().getString(Constants.PREFS_ITEM_MH_VF_VER, ""));

        mDevInfoForFirmware = new DevVersion(
                getSettings().getInt(Constants.PREFS_ITEM_DEV_SN_FOR_FIRMWARE, 0),
                getSettings().getInt(Constants.PREFS_ITEM_DEV_SW_VER_FOR_FIRMWARE, 0),
                getSettings().getInt(Constants.PREFS_ITEM_DEV_HW_VER_FOR_FIRMWARE, 0));

        mGateVer = getSettings().getInt(Constants.PREFS_ITEM_GATE_VER, 0);

        mDayStats = new StatisticsData();
    }

    //Whether there is a Wifi internet connection.
    public boolean isOnline = false;
    //Whether there is a CAN connection.
    public boolean isOnCAN = false;

    public String mIp =null;
    public int mPort = -1;
    public AM_AWS_SETUP mMHConfigFile = null;
    public DevVersion mDevVersion = null;
    public MHVersion mMHVersion = null;
    public int mGateVer = 0;

    public FirmwareInfo canInfo = null;

    private String mAppUpgradeUrl = null;
    private String mFirmwareUpgradeUrl = null;
    private String mFirmwareFilePath = null;
    public DevVersion mDevInfoForFirmware = null;

    public int speed = -1;

    public byte curScreen = 1;

    public int getAppVersionCode() {
        int code = -1;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return code;
    }

    public String getAppVersionName() {
        String verName = "";
        try {
            verName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public String getFirmwareVersion() {
        if (mDevVersion.getDevHwVer()!=0 && mDevVersion.getDevSwVer()!=0)
            return String.format("%08X,%08X", mDevVersion.getDevHwVer(), mDevVersion.getDevSwVer());
        else
            return null;
    }

    public String getAppUpgradeUrl() {
        if (mAppUpgradeUrl == null) {
            mAppUpgradeUrl = getSettings().getString(Constants.PREFS_ITEM_APP_URL, null);
        }
        return mAppUpgradeUrl;
    }

    public void setAppUpgradeUrl(String value) {
        if (Constants.NO_UPGRADE.equals(value.toUpperCase())) {
            mAppUpgradeUrl = null;
        } else {
            mAppUpgradeUrl = value;
        }
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(Constants.PREFS_ITEM_APP_URL, mAppUpgradeUrl);
        editor.commit();
    }

    public String getFirmwareUpgradeUrl() {
        if (mFirmwareUpgradeUrl == null) {
            mFirmwareUpgradeUrl = getSettings().getString(Constants.PREFS_ITEM_FIRMWARE_URL, null);
        }
        return mFirmwareUpgradeUrl;
    }
    public void setFirmwareUpgradeUrl(String value) {
        if (Constants.NO_UPGRADE.equals(value.toUpperCase())) {
            mFirmwareUpgradeUrl = null;
        } else {
            mFirmwareUpgradeUrl = value;
        }
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(Constants.PREFS_ITEM_FIRMWARE_URL, mFirmwareUpgradeUrl);
        editor.commit();
    }

    public String getFirmwareFilePath() {
        if (mFirmwareFilePath == null) {
            mFirmwareFilePath = getSettings().getString(Constants.PREFS_ITEM_FIRMWARE_FILE_PATH,
                    null);
        }
        return mFirmwareFilePath;
    }

    public void setFirmwareFilePath(String value) {
        mFirmwareFilePath = value;
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(Constants.PREFS_ITEM_FIRMWARE_FILE_PATH, mFirmwareFilePath);
        editor.commit();
    }


    public void saveDevInfoForFirmware(DevVersion aVer) {
        mDevInfoForFirmware = aVer;
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(Constants.PREFS_ITEM_DEV_SN_FOR_FIRMWARE,
                mDevInfoForFirmware.getDevSn());
        editor.putInt(Constants.PREFS_ITEM_DEV_SW_VER_FOR_FIRMWARE,
                mDevInfoForFirmware.getDevSwVer());
        editor.putInt(Constants.PREFS_ITEM_DEV_HW_VER_FOR_FIRMWARE,
                mDevInfoForFirmware.getDevHwVer());
        editor.commit();
    }


    public void saveDevVersion() {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(Constants.PREFS_ITEM_DEV_SN, mDevVersion.getDevSn());
        editor.putInt(Constants.PREFS_ITEM_DEV_SW_VER, mDevVersion.getDevSwVer());
        editor.putInt(Constants.PREFS_ITEM_DEV_HW_VER, mDevVersion.getDevHwVer());
        editor.commit();
    }

    public void saveMHVersion() {
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(Constants.PREFS_ITEM_MH_SN, mMHVersion.getMHSn());
        editor.putString(Constants.PREFS_ITEM_MH_SW_VER, mMHVersion.getMHSwVer());
        editor.putString(Constants.PREFS_ITEM_MH_VF_VER, mMHVersion.getMHVfVer());
        editor.commit();
    }

    public void saveGateVersion(){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putInt(Constants.PREFS_ITEM_GATE_VER, mGateVer);
        editor.commit();
    }



    private SharedPreferences getSettings() {
        return getSharedPreferences(Constants.PREFS_FILE, MODE_PRIVATE);
    }

    byte[] sendbuf = null;

    public void setSendBuf(byte[] buf) {
        sendbuf = new byte[buf.length];
        System.arraycopy(buf, 0, sendbuf, 0, buf.length);
    }

    public byte[] getSendBuf() {
        return sendbuf;
    }




}
