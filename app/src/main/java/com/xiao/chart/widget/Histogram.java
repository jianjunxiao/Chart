package com.xiao.chart.widget;

import android.support.annotation.NonNull;

/**
 * Created by xiaoyanger on 2017/8/18.
 */
public class Histogram implements Comparable<Histogram> {
    public float width;
    public float height;
    public float left;
    public float top;
    public float right;
    public float bottom;
    public int color;

    public Histogram(float left, float top, float right, float bottom, int color) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.color = color;
        width = right - left;
        height = bottom - top;
    }

    @Override
    public int compareTo(@NonNull Histogram o) {
        if (this.height > o.height) {
            return 1;
        } else if (this.height < o.height) {
            return -1;
        } else {
            return 0;
        }
    }
}
