package hk.com.mobileye.jason.adlaleader;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;

import hk.com.mobileye.jason.adlaleader.common.Constants;
import hk.com.mobileye.jason.adlaleader.common.ExitManager;
import hk.com.mobileye.jason.adlaleader.common.MyApplication;
import hk.com.mobileye.jason.adlaleader.custom.MyYAxisValueFormatter;
import hk.com.mobileye.jason.adlaleader.net.Message.Factory.MsgFactory;
import hk.com.mobileye.jason.adlaleader.net.Message.MessageType;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Warning.DayStat;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Warning.WarnDayStatReq;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgClass.Warning.WarnRecord;
import hk.com.mobileye.jason.adlaleader.net.Message.MsgUtils;
import hk.com.mobileye.jason.adlaleader.net.Message.ResponseType;
import hk.com.mobileye.jason.adlaleader.net.Message.ServiceType;
import hk.com.mobileye.jason.adlaleader.net.Message.TLVType;
import hk.com.mobileye.jason.adlaleader.net.TcpIntentService;
import hk.com.mobileye.jason.adlaleader.statistics.StatisticsData;


public class ChartActivity extends Activity {

    public static final String TAG = "ChartActivity";

    private MyApplication mApp;

    private LineChart mChart;
    //private Typeface mTf;

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };

    protected String[] mHours = new String[]{
            "00", "1", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
    };

    private int[] mColors = new int[]{
            Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53), Color.WHITE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        mApp = (MyApplication)getApplication();
        initLocalReceiver();
        initChart();
        //initDaysData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        refreshDayStat();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestory");
        mApp = null;
        releaseLocalReceive();
        super.onDestroy();
    }

    private long mExitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, R.string.toast_twice_press_back_to_exit, Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                Log.d(TAG, "twice press back to exit");
                ExitManager.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private LocalBroadcastReceiver localReceiver;

    private void initLocalReceiver() {
        localReceiver = new LocalBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Constants.DAY_STAT_UPDATE_ACTION);
        //filter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter);
    }

    private void releaseLocalReceive() {
        if (localReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
            localReceiver = null;
        }
    }

    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                final String action = intent.getAction();
                final String sender = intent.getStringExtra(Constants.EXTENDED_OWNER);
                Log.d(TAG, String.format("Receive broadcast : %s. Sender : %s", action, sender));

                if (action.equals(Constants.DAY_STAT_UPDATE_ACTION)) {
                    dealDayStatUpdateResult(intent);
                    return;
                }
            }

        }

        private void dealDayStatUpdateResult(Intent intent) {
            final int date = intent.getIntExtra(Constants.EXTEND_DAY_STAT_DATE, -1);
            final int runTime = intent.getIntExtra(Constants.EXTEND_DAY_STAT_RUN_TIME, 0);
            final int index = intent.getIntExtra(Constants.EXTEND_DAY_STAT_INDEX, -1);

            Log.d(TAG, String.format("deal day statistics update date:%d run time: %d index: %d",
                    date, runTime, index));

            if (date < 160101 || runTime == 0 || index < 0)
                return;

            updateDaysChart();

            //try to get next from history
            getHistory();
        }
    }

    private void printDayStat(DayStat dayStat) {
        if (null == dayStat)
            return;

        WarnRecord[] records = dayStat.getDayStat();

        Log.d(TAG, String.format("%d %d %d", dayStat.getDate(), dayStat.getMileage(),
                dayStat.getRunTime()));
        for (int i = 0; i < records.length; i++) {
            Log.d(TAG, String.format("%d %s", i, Arrays.toString(records[i].getCounts())));
        }
    }


    private void getHistory() {
        int index = mApp.mDayStats.count();
        Log.e(TAG, String.format("getHistory index %d", index));

        if (index < Constants.HISTORY_DAYS) {
            queryDayStat(index);
        }
    }

    public void onBtnStatClicked(View view) {
        CheckBox cb = (CheckBox)view;
        LineData lineData = mChart.getData();

        LineDataSet set = (LineDataSet) lineData.getDataSetByLabel(
                view.getContentDescription().toString(), true);

        if (null != set) {
            set.setVisible(cb.isChecked());
            set.setDrawValues(cb.isChecked());
            set.setValueTextColor(Color.WHITE);
            mChart.invalidate();
        }
    }

    private void refreshDayStat() {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            WarnDayStatReq msg = (WarnDayStatReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_WARNING,
                    MessageType.WARN_DAY_STAT_REQ,
                    ResponseType.REQUEST);

            msg.getBody().get(TLVType.TP_DATE_INDEX_ID).setValue(0);

            if (msg.encode()) {
                Log.d(TAG, "refreshDayStat index=0");
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_WARN_DAY_STAT);
            }
        }
    }

    private void queryDayStat(int aIndex) {
        if (mApp.isOnCAN && null != mApp.mIp && mApp.mPort > 0) {
            WarnDayStatReq msg = (WarnDayStatReq) MsgFactory.getInstance().create(
                    ServiceType.SERVICE_WARNING,
                    MessageType.WARN_DAY_STAT_REQ,
                    ResponseType.REQUEST);

            int index = MsgUtils.int2bcd(aIndex);
            msg.getBody().get(TLVType.TP_DATE_INDEX_ID).setValue(index);

            if (msg.encode()) {
                Log.d(TAG, "startActionFileService");
                TcpIntentService.startActionFileService(this, msg.getData(),
                        Constants.DESC_WARN_DAY_STAT);
            }
        }
    }

    private void initChart() {
        mChart = new LineChart(this);
        FrameLayout fl = (FrameLayout) findViewById(R.id.chart);
        fl.addView(mChart);

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

//        mChart.setDrawBarShadow(false);
//        mChart.setDrawValueAboveBar(true);

        mChart.setDescription("");
        mChart.setDrawBorders(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(48);

        // mChart.setDrawYLabels(false);

        //mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
        xAxis.setTextColor(Color.WHITE);

        YAxisValueFormatter custom = new MyYAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(10f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGridColor(Color.GREEN);

        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setEnabled(false);
//        //rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setTextColor(Color.WHITE);

        Legend l = mChart.getLegend();
        //l.setEnabled(false);
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setTextColor(Color.WHITE);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);


    }

    private void initHoursData() {
        BarData data = new BarData(createDaysXVals());

        BarDataSet set = createDaysSet("FCW");
        data.addDataSet(set);

//        BarDataSet set2 = createDaysSet2("LDW");
//        data.addDataSet(set2);

        data.setDrawValues(false);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);
        //mChart.setData(data);

        //All methods modifying the viewport need to be called on the Char after setting data!!!
        mChart.setVisibleXRange(24, 96);
        mChart.moveViewToX((Constants.HISTORY_DAYS - 1) * 24);
        mChart.zoom(4f, 1f, (Constants.HISTORY_DAYS - 1) * 24, 0f);
    }

    private BarDataSet createDaysSet(String label) {
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < Constants.HISTORY_DAYS; i++) {
            for (int j = 0; j < 24; j++) {
                yVals.add(new BarEntry(j % 4 , j + i * 24));
            }
        }
        BarDataSet set = new BarDataSet(yVals, label);
        set.setBarSpacePercent(35f);
        set.setColor(Color.RED);
        return set;
    }

    private BarDataSet createDaysSet2(String label) {
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < Constants.HISTORY_DAYS; i++) {
            for (int j = 0; j < 24; j++) {
                yVals.add(new BarEntry(new float[]{j%4 ,3- (j % 4)} , j + i * 24));
            }
        }
        BarDataSet set = new BarDataSet(yVals, label);
        set.setBarSpacePercent(35f);
        set.setColors(new int[]{Color.RED, Color.GREEN});

        return set;
    }



    private ArrayList<String> createDaysXVals() {
        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < Constants.HISTORY_DAYS; i++) {
            for (int j = 0; j < 24; j++) {
                if (j == 0) {
                    int date = StatisticsData.getDateByIndex(Constants.HISTORY_DAYS - i - 1);
                    date = date % 10000;
                    xVals.add(String.format("%04d", date));
                } else {
                    xVals.add(mHours[j]);
                }
            }
        }
        return xVals;
    }


    private void updateDaysChart() {
        Log.d(TAG, "updateDaysChart");
        ArrayList<String> xVals = new ArrayList<>();
        ArrayList<Entry> valuesFCW = new ArrayList<>();
        ArrayList<Entry> valuesLDW = new ArrayList<>();
        ArrayList<Entry> valuesPCW = new ArrayList<>();
        ArrayList<Entry> valuesSpeeding = new ArrayList<>();
        ArrayList<Entry> valuesHMW = new ArrayList<>();
        ArrayList<Entry> valuesTotal = new ArrayList<>();

        StatisticsData data = mApp.mDayStats;

        int count = data.count();
        count = count == -1 ? Constants.HISTORY_DAYS : count;

        for (int i = count-1; i >= 0; i--) {
            DayStat dayStat = data.getDayStatByIndex(i);
            if (null == dayStat)
                continue;

            xVals.add(dayStat.date2String());

            valuesFCW.add(new Entry((float) dayStat.getFCW(), count-1-i));
            valuesLDW.add(new Entry((float) dayStat.getLDW(), count-1-i));
            valuesPCW.add(new Entry((float) dayStat.getPCW(), count-1-i));
            valuesSpeeding.add(new Entry((float) dayStat.getSpeeding(), count-1-i));
            //valuesHMW.add(new Entry((float) dayStat.getHMW(), i));

            valuesTotal.add(new Entry((float) dayStat.getFCW() + (float) dayStat.getLDW()
                    + (float) dayStat.getPCW() + (float) dayStat.getSpeeding(), count-1-i));

        }

        LineData lineData = new LineData(xVals);

        LineDataSet set = new LineDataSet(valuesFCW, getString(R.string.FCW));
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setColor(mColors[0]);
        set.setCircleColor(mColors[0]);
        set.setValueTextColor(Color.WHITE);
        lineData.addDataSet(set);

        set = new LineDataSet(valuesLDW, getString(R.string.LDW));
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setColor(mColors[1]);
        set.setCircleColor(mColors[1]);
        set.setValueTextColor(Color.WHITE);
        lineData.addDataSet(set);

        set = new LineDataSet(valuesPCW, getString(R.string.PCW));
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setColor(mColors[2]);
        set.setCircleColor(mColors[2]);
        set.setValueTextColor(Color.WHITE);
        lineData.addDataSet(set);

        set = new LineDataSet(valuesSpeeding, getString(R.string.Speeding));
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setColor(mColors[3]);
        set.setCircleColor(mColors[3]);
        set.setValueTextColor(Color.WHITE);
        lineData.addDataSet(set);

//        set = new LineDataSet(valuesHMW, getString(R.string.HMW));
//        set.setDrawCubic(true);
//        set.setCubicIntensity(0.2f);
//        set.setColor(mColors[4]);
//        set.setCircleColor(mColors[4]);
//        lineData.addDataSet(set);

        set = new LineDataSet(valuesTotal, getString(R.string.Total));
        set.setDrawCubic(true);
        set.setCubicIntensity(0.2f);
        set.setColor(mColors[5]);
        set.setCircleColor(mColors[5]);
        lineData.addDataSet(set);
        lineData.setValueTextColor(Color.WHITE);
        lineData.setValueTextSize(12f);

        mChart.setData(lineData);
//        mChart.setBackgroundColor(Color.GRAY);


        //All methods modifying the viewport need to be called on the Char after setting data!!!
        mChart.setVisibleXRange(4, 30);
        //mChart.moveViewToX((Constants.HISTORY_DAYS - 1));
        mChart.invalidate();
    }
}
