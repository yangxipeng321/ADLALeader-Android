package hk.com.mobileye.jason.adlaleader.Upgrade;

import android.content.Context;
import android.content.SharedPreferences;

import hk.com.mobileye.jason.adlaleader.common.Constants;

/**
 * Created by Jason on 2015/2/10.
 */
public class FirmwareInfo {

    private static final String TAG = "FirmwareInfo";
    private String mUrlOld;
    private String mUrl;
    private String mFileName;


    public FirmwareInfo(Context context) {
        SharedPreferences settings = context.getSharedPreferences(Constants.PREFS_FILE,
                Context.MODE_PRIVATE);

    }

    public boolean isFileExist() {
        return true;
    }



}
