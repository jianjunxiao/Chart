package com.xiao.chart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xiao.chart.widget.DayHistogramView;
import com.xiao.chart.widget.DayHistogramView1;
import com.xiao.chart.widget.DayKwh;
import com.xiao.chart.widget.HourKwh;
import com.xiao.chart.widget.LineChartView;
import com.xiao.chart.widget.YearHistogramView;
import com.xiao.chart.widget.YearHistogramView1;
import com.xiao.chart.widget.YearMillionYuan;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChartView mLineChartView;
    private DayHistogramView mDayHistogramView;
    private YearHistogramView mYearHistogramView;

    private DayHistogramView1 mDayHistogramView1;
    private YearHistogramView1 mYearHistogramView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        mLineChartView = (LineChartView) findViewById(R.id.line_chart_view);
        mDayHistogramView = (DayHistogramView) findViewById(R.id.day_histogram_view);
        mYearHistogramView = (YearHistogramView) findViewById(R.id.year_histogram_view);

        mDayHistogramView1 = (DayHistogramView1) findViewById(R.id.day_histogram_view1);
        mYearHistogramView1 = (YearHistogramView1) findViewById(R.id.year_histogram_view1);

        refresh();
    }

    private void refresh() {
        mDayHistogramView.setDayKwhs(generateDayKwhs());
        mYearHistogramView.setYearMillionYuans(ganerateYearMillionYuan());
        mLineChartView.setHourKwhs(generateHourKwhs());

        mDayHistogramView1.setDayKwhs(generateDayKwhs1());
        mYearHistogramView1.setYearMillionYuans(ganerateYearMillionYuan1());
    }

    private List<HourKwh> generateHourKwhs() {
        List<HourKwh> hks = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            if (i < 5) {
                hks.add(new HourKwh("0" + i + ":00", (i + 1) * 2f));
            } else if (i < 10) {
                hks.add(new HourKwh("0" + i + ":00", (i + 1) * 3f));
            } else if (i < 15) {
                hks.add(new HourKwh(i + ":00", (i + 1) * 4f));
            } else if (i < 20) {
                hks.add(new HourKwh(i + ":00", (i + 1) * 3f));
            } else {
                hks.add(new HourKwh(i + ":00", (i + 1) * 2f));
            }
        }
        return hks;
    }

    private List<YearMillionYuan> ganerateYearMillionYuan() {
        List<YearMillionYuan> ymys = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i < 6) {
                ymys.add(new YearMillionYuan(i + "", 24000.00f, 20000.00f, 16000.00f, 12000.00f, 8000.00f));
            } else if (i < 11) {
                ymys.add(new YearMillionYuan(i + "", 23000.00f, 19000.00f, 15000.00f, 11000.00f, 7000.00f));
            } else if (i < 16) {
                ymys.add(new YearMillionYuan(i + "", 22000.00f, 18000.00f, 14000.00f, 10000.00f, 6000.00f));
            } else if (i < 21) {
                ymys.add(new YearMillionYuan(i + "", 21000.00f, 17000.00f, 13000.00f, 9000.00f, 5000.00f));
            } else {
                ymys.add(new YearMillionYuan(i + "", 20000.00f, 16000.00f, 12000.00f, 8000.00f, 4000.00f));
            }
        }
        return ymys;
    }

    private List<DayKwh> generateDayKwhs() {
        List<DayKwh> dayKwhs = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (i < 10) {
                dayKwhs.add(new DayKwh("2017-08-0" + i, (i % 8) * 10f + 10f));
            } else {
                dayKwhs.add(new DayKwh("2017-08-" + i, (i % 8) * 10 + 10));
            }
        }
        return dayKwhs;
    }

    private List<DayKwh> generateDayKwhs1() {
        List<DayKwh> dayKwhs = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            if (i < 10) {
                dayKwhs.add(new DayKwh("2017-08-0" + i, (i % 8) * 10f + 10.43f));
            } else {
                dayKwhs.add(new DayKwh("2017-08-" + i, (i % 8) * 10f + 10.03f));
            }
        }
        return dayKwhs;
    }

    private List<YearMillionYuan> ganerateYearMillionYuan1() {
        List<YearMillionYuan> ymys = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            if (i < 6) {
                ymys.add(new YearMillionYuan(i + "", 24000.00f, 20000.00f, 16000.00f, 12000.00f, 8000.00f));
            } else if (i < 11) {
                ymys.add(new YearMillionYuan(i + "", 23000.00f, 19000.00f, 15000.00f, 11000.00f, 7000.00f));
            } else if (i < 16) {
                ymys.add(new YearMillionYuan(i + "", 22000.00f, 18000.00f, 14000.00f, 10000.00f, 6000.00f));
            } else if (i < 21) {
                ymys.add(new YearMillionYuan(i + "", 21000.00f, 17000.00f, 13000.00f, 9000.00f, 5000.00f));
            } else {
                ymys.add(new YearMillionYuan(i + "", 20000.00f, 16000.00f, 12000.00f, 8000.00f, 4000.00f));
            }
        }
        return ymys;
    }
}
