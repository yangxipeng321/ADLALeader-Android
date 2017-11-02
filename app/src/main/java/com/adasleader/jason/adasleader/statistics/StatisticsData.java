package com.adasleader.jason.adasleader.statistics;

import com.adasleader.jason.adasleader.net.Message.MsgClass.Warning.DayStat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Jason on 2016/3/1.
 *
 */
public class StatisticsData {

    public static final String TAG = "StatisticsData";

    //private DayStat[] mData;
    private List<DayStat> mDataList;
    //private int todayIndex;

    public StatisticsData() {
        //mData = new DayStat[Constants.HISTORY_DAYS];
        mDataList = new ArrayList<>();
        //todayIndex = 0;
    }

    /**
     * 负责更新或者添加记录
     */
    public void update(DayStat dayStat) {
        //处理当前记录
        if (dayStat.getIndex() == 0) {
            if (mDataList.size() == 0) {
                mDataList.add(dayStat);
            } else {
                DayStat curStat = mDataList.get(0);
                if (curStat.getDate() == dayStat.getDate()) {    //当前日期没变，更新当前记录
                    mDataList.set(0, dayStat);
                } else if (curStat.getDate() < dayStat.getDate()) { //新的一天，将新的记录插入到最前面
                    mDataList.add(0, dayStat);
                } else {  //剩下情况，就是把记录都清空，然后更新当前记录
                    mDataList.clear();
                    mDataList.add(dayStat);
                }
            }
        } else {   //处理历史记录
            mDataList.add(dayStat);
        }
    }

    public int count() {
        return mDataList.size();
    }


    /**
     * 获取几天前的统计记录
     * @param index  0 表示今天的，1表示昨天，2表示两天前，
     * @return statics data
     */
    public DayStat getDayStatByIndex(int index) {
        //int i = (index + todayIndex) % Constants.HISTORY_DAYS;
        return mDataList.get(index);
    }

//    /**
//     * 计算指定日期date对应今天的索引值
//     * @param date   160221 表示 2016年2月21日
//     * @return
//     */
//    public static int getIndexByDate(int date) {
//        int year = date / 10000 + 2000;
//        int month = (date / 100) % 100 -1;
//        int day = date % 100;
//        Calendar calendar = new GregorianCalendar(year, month, day);
//        long thatDay = calendar.getTimeInMillis();
//
//        int offset = Calendar.getInstance().getTimeZone().getRawOffset();
//        long today = (System.currentTimeMillis() + offset) / 86400000;
//        thatDay = (thatDay + offset) /86400000;
//
//        Log.d(TAG, String.format("date %d index %d", date, today-thatDay));
//        return (int)(today-thatDay);
//    }


    public static int getDateByIndex(int todayIndex) {
        Calendar cale = Calendar.getInstance();
        cale.add(Calendar.DAY_OF_MONTH, 0 - todayIndex);
        return (cale.get(Calendar.YEAR) % 100) * 10000  + (cale.get(Calendar.MONTH) +1) * 100
                + cale.get(Calendar.DAY_OF_MONTH);
    }



}
