package com.adasleader.jason.adasleader.custom;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Jason on 2016/2/27.
 *
 */
public class MyYAxisValueFormatter implements YAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        mFormat = new DecimalFormat("#0.#");
    }

    @Override
    public String getFormattedValue(float value, YAxis yAxis) {
        return mFormat.format(value) ;
//        Log.e("AAAAA", String.valueOf((value * 100) % 100));
//        return (value * 100) % 100 > 1 ? "" : mFormat.format(value);
    }
}
