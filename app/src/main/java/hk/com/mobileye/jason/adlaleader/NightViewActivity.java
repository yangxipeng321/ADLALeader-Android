package hk.com.mobileye.jason.adlaleader;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.logger.Log;
import hk.com.mobileye.jason.adlaleader.common.logger.LogFragment;
import hk.com.mobileye.jason.adlaleader.common.logger.LogWrapper;
import hk.com.mobileye.jason.adlaleader.common.logger.MessageOnlyLogFilter;


public class NightViewActivity extends Activity {

    private static final String TAG = "NightViewActivity";
    private LogFragment mLogFragment;
    MessageOnlyLogFilter msgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_night_view);
        ExitManager.getInstance().addActivity(this);

        Log.d(TAG, "onCreate");
        initLogging();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_night_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void initLogging() {
        //Using Log, fornt-end to the logging chain, emulates
        //android.util.log method signatures.

        //Wraps Android's natvie log framework.
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        //A filter that strips out everything except the message text.
        msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        //On screen logging via a fragment with a TextView.
        mLogFragment = (LogFragment) getFragmentManager().findFragmentById(
                R.id.night_view_fragment);
        //msgFilter.setNext(mLogFragment.getLogView());
    }

    public void onBtnClearClicked(View view) {
        mLogFragment.getLogView().setText("");
    }
    public void onBtnShowLogClicked(View view) {
        CheckBox cb = (CheckBox)view;
        Log.d(TAG, "check box is " + cb.isChecked());
        if (cb.isChecked()) {
            msgFilter.setNext(mLogFragment.getLogView());
        } else {
            msgFilter.setNext(null);
        }
    }

}
