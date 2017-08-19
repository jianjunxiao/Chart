package com.xiao.chart.widget;

/**
 * Created by xioayanger on 2017/8/18.
 */
public class YearMillionYuan {

    public String year;
    public float totalIncome;
    public float stateSubsidy;
    public float lacalSubsidy;
    public float save;
    public float sell;

    public YearMillionYuan(String year, float totalIncome, float stateSubsidy,
                           float lacalSubsidy, float save, float sell) {
        this.year = year;
        this.totalIncome = totalIncome;
        this.stateSubsidy = stateSubsidy;
        this.lacalSubsidy = lacalSubsidy;
        this.save = save;
        this.sell = sell;
    }
}
