package hk.com.mobileye.jason.adlaleader.common;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;



/**
 * Created by Jason on 2014/12/31.
 */
public class ExitManager extends Application{
    private static final String TAG = "ExitManager";
    private List<Activity> activityList = new LinkedList<Activity>();

    private static ExitManager mInstance;
    private ExitManager() { }

    public static ExitManager getInstance() {
        if (mInstance == null) {
            mInstance = new ExitManager();
        }
        return mInstance;
    }

    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public void exit() {
        Log.d(TAG, "Finishs all activities");
        for (Activity activity : activityList) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
        int id = android.os.Process.myPid();
        if (id != 0) {
            android.os.Process.killProcess(id);
        }
    }

}
