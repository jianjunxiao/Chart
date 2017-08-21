package com.xiao.chart.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiao.chart.DensityUtil;
import com.xiao.chart.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by xiaoyanger on 2017/8/17.
 * 柱状图(年份).
 */
public class YearHistogramView extends View {

    private Paint axisPaint;           // 画坐标轴的画笔
    private Paint pillarPaint;         // 画柱状的画笔
    private float xScaleDiff;          // x轴单位刻度差(像素)
    private float yScaleDiff;          // y轴单位刻度差(像素)
    private float originX;             // 原点的X坐标(像素)
    private float originY;             // 原点的Y坐标(像素)
    private float xAxisLength;         // x轴长度(像素)
    private float yAxisLength;         // y轴长度(像素)
    private float scaleLineLength;     // 刻度线长度(像素)

    private float histogramWidth;      // 柱状的宽度
    private float space;               // 柱状之间的间隙

    private Rect bounds = new Rect();   // 测试字符串的长宽矩形

    private String[] xValues = {"5", "10", "15", "20", "25"};  // x轴刻度值

    private float[] yValues = {};        // y轴刻度值
    private Histogram[][] histograms = {}; // 柱
    private float animatedValue;        // 动画值

    private List<YearMillionYuan> yearMillionYuanList;


    public YearHistogramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 画笔
        axisPaint = new Paint();
        pillarPaint = new Paint();

        // 抗锯齿
        axisPaint.setAntiAlias(true);
        pillarPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        calculateParams();
    }

    private void calculateParams() {
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        xScaleDiff = width / 7.5f;          // x轴刻度差
        yScaleDiff = height / 7.5f;         // y轴刻度差

        originX = xScaleDiff;               // 原点的x坐标
        originY = height - yScaleDiff;      // 原点的y坐标

        xAxisLength = 5.5f * xScaleDiff;    // x轴长度
        yAxisLength = 5.5f * yScaleDiff;    // y轴长度

        histogramWidth = xScaleDiff * 0.15f;   //柱宽度
        space = xScaleDiff * 0.05f;            // 柱间距

        scaleLineLength = 0.08f * xScaleDiff;   // 刻度线长度

        float axisStrokeWidth = xScaleDiff / 40f;     // 坐标轴的线宽
        float textSize = xScaleDiff / 4f;             // 文本大小

        // 设置画笔属性
        axisPaint.setStrokeWidth(axisStrokeWidth);
        axisPaint.setTextSize(textSize);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);
        axisPaint.setColor(0xFF202428);

        pillarPaint.setColor(0x9674B5FD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinate(canvas);    // 绘制坐标系
        drawXScaleValues(canvas);  // 绘制x轴的刻度值
        drawYScaleValues(canvas);  // 绘制y轴的刻度值
        drawHistograms(canvas);    // 绘制数据
    }

    private void drawCoordinate(Canvas canvas) {
        // 绘制y轴
        canvas.drawLine(originX, originY, originX, originY - yAxisLength, axisPaint);
        // 绘制y轴刻度
        for (int i = 1; i < 6; i++) {
            canvas.drawLine(originX,
                    originY - i * yScaleDiff,
                    originX - scaleLineLength,
                    originY - i * yScaleDiff,
                    axisPaint);
        }
        // 绘制y轴箭头
        canvas.drawLine(originX,
                originY - yAxisLength,
                originX - scaleLineLength,
                originY - yAxisLength + scaleLineLength,
                axisPaint);
        canvas.drawLine(originX,
                originY - yAxisLength,
                originX + scaleLineLength,
                originY - yAxisLength + scaleLineLength,
                axisPaint);
        // 绘制y轴标题："万元"
        String yLable = "万元";
        axisPaint.getTextBounds(yLable, 0, yLable.length(), bounds);
        canvas.drawText(yLable,
                originX - bounds.width() / 2f,
                originY - yAxisLength - (yScaleDiff / 3f - bounds.height() / 3f),
                axisPaint);
        // 绘制x轴
        canvas.drawLine(originX, originY, originX + xAxisLength, originY, axisPaint);
        // 绘制x轴刻度
        for (int i = 1; i < 6; i++) {
            canvas.drawLine(originX + i * xScaleDiff,
                    originY,
                    originX + i * xScaleDiff,
                    originY + scaleLineLength,
                    axisPaint);
        }
        // 绘制x轴箭头
        canvas.drawLine(originX + xAxisLength,
                originY,
                originX + xAxisLength - scaleLineLength,
                originY - scaleLineLength,
                axisPaint);
        canvas.drawLine(originX + xAxisLength,
                originY,
                originX + xAxisLength - scaleLineLength,
                originY + scaleLineLength,
                axisPaint);
        // 绘制x轴标题："年"
        String xLable = "年";
        axisPaint.getTextBounds(xLable, 0, xLable.length(), bounds);
        canvas.drawText(xLable,
                originX + xAxisLength + (xScaleDiff / 4f - bounds.width() / 4f),
                originY + bounds.height() / 2f,
                axisPaint);
    }

    private void drawXScaleValues(Canvas canvas) {
        for (int i = 0; i < xValues.length; i++) {
            axisPaint.getTextBounds(xValues[i], 0, xValues[i].length(), bounds);
            canvas.drawText(xValues[i],
                    originX + xScaleDiff * (i + 1) - bounds.width() / 2f,
                    originY + scaleLineLength + bounds.height()
                            + (yScaleDiff - scaleLineLength - bounds.height()) / 5f,
                    axisPaint);
        }
    }

    private void drawYScaleValues(Canvas canvas) {
        for (int i = 0; i < yValues.length; i++) {
            String yValue = format2Bit(String.valueOf(yValues[i]));
            axisPaint.getTextBounds(yValue, 0, yValue.length(), bounds);
            canvas.drawText(yValue,
                    originX - scaleLineLength - bounds.width()
                            - (xScaleDiff - scaleLineLength - bounds.width()) / 5f,
                    originY - (i + 1) * yScaleDiff + bounds.height() / 2f,
                    axisPaint);
        }
    }

    private void drawHistograms(Canvas canvas) {
        for (Histogram[] hgs : histograms) {
            for (Histogram hg : hgs) {
                if (hg.color != 0x00000000) {
                    pillarPaint.setColor(hg.color);
                    canvas.drawRect(hg.left,
                            hg.bottom - animatedValue * hg.height,
                            hg.right,
                            hg.bottom,
                            pillarPaint);
                }
            }
        }
    }

    public void setYearMillionYuans(final List<YearMillionYuan> yearMillionYuans) {
        this.post(new Runnable() {
            @Override
            public void run() {
                yearMillionYuanList = yearMillionYuans;
                yValues = generateYValues(yearMillionYuans);
                histograms = generateHistograms(yearMillionYuans);
                startAnimation();
                hideDetail();
            }
        });
    }

    /**
     * 动画
     */
    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(800);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatedValue = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.start();
    }

    /**
     * 根据数据值生成柱状
     *
     * @param ymys 数据
     * @return 柱状数组
     */
    private Histogram[][] generateHistograms(List<YearMillionYuan> ymys) {
        Histogram[][] histograms = new Histogram[ymys.size()][6];
        for (int i = 0; i < histograms.length; i++) {
            float left = originX + (i + 1) * space + (0.5f + i) * histogramWidth;
            float right = originX + (i + 1) * space + (1.5f + i) * histogramWidth;

            float top0 = originY - yAxisLength;
            float top1 = originY - ymys.get(i).totalIncome * yScaleDiff / calculateScaleDiff(ymys);
            float top2 = originY - ymys.get(i).stateSubsidy * yScaleDiff / calculateScaleDiff(ymys);
            float top3 = originY - ymys.get(i).lacalSubsidy * yScaleDiff / calculateScaleDiff(ymys);
            float top4 = originY - ymys.get(i).save * yScaleDiff / calculateScaleDiff(ymys);
            float top5 = originY - ymys.get(i).sell * yScaleDiff / calculateScaleDiff(ymys);

            float bottom = originY;

            Histogram h = new Histogram(left, top0, right, bottom, 0x00000000);
            // 颜色可修改
            ArrayList<Histogram> arrayList = new ArrayList<>();
            arrayList.add(new Histogram(left, top1, right, bottom, 0xFF74B5FD));
            arrayList.add(new Histogram(left, top2, right, bottom, 0xFFD58481));
            arrayList.add(new Histogram(left, top3, right, bottom, 0xFFD5C081));
            arrayList.add(new Histogram(left, top4, right, bottom, 0xFF89D581));
            arrayList.add(new Histogram(left, top5, right, bottom, 0xFFAA81D5));
            // 按照高度排序，高的需要先绘制
            Collections.sort(arrayList);
            Collections.reverse(arrayList);
            for (int j = 0; j < histograms[i].length; j++) {
                if (j < arrayList.size()) {
                    histograms[i][j] = arrayList.get(j);
                } else {
                    histograms[i][j] = h; // 最后一个放点击显示的灰色层
                }
            }
        }
        return histograms;
    }

    /**
     * 根据ymys来生成y坐标的刻度值
     *
     * @return y坐标刻度值
     */
    private float[] generateYValues(List<YearMillionYuan> ymys) {
        float[] yValues = new float[5];
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = format2Bit(((i + 1) * calculateScaleDiff(ymys)) * 0.0001f);
        }
        return yValues;
    }

    /**
     * 计算y轴的数据值刻度差
     *
     * @param ymys 数据
     * @return y轴的数据刻度差值
     */
    private float calculateScaleDiff(List<YearMillionYuan> ymys) {
        float[] data = new float[ymys.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = ymys.get(i).totalIncome;
        }
        Arrays.sort(data);
        return format2Bit(data[data.length - 1] / 5f);
    }

    /**
     * 格式化2为小数###.00
     *
     * @return ###.00
     */
    private float format2Bit(float number) {
        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        String target = decimalFormat.format(number);
        if (target.startsWith(".")) {
            target = "0" + target;
        }
        return Float.parseFloat(target);
    }

    /**
     * 格式化2为小数###.00
     *
     * @return string ###.00
     */
    private String format2Bit(String numberStr) {
        if (TextUtils.isEmpty(numberStr)) {
            return "0.00";
        }
        float numberFloat = Float.parseFloat(numberStr);
        DecimalFormat decimalFormat = new DecimalFormat("###.00");
        String target = decimalFormat.format(numberFloat);
        if (target.startsWith(".")) {
            target = "0" + target;
        }
        return target;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchIndex = -1;
        for (int i = 0; i < histograms.length; i++) {
            if (event.getX() >= histograms[i][5].left - space / 2f
                    && event.getX() <= histograms[i][5].right + space / 2f
                    && event.getY() >= histograms[i][5].top
                    && event.getY() <= histograms[i][5].bottom) {
                histograms[i][5].color = 0x96B3B3B3;
                touchIndex = i;
            } else {
                histograms[i][5].color = 0x00000000;
            }
        }
        invalidate();
        if (touchIndex != -1) {
            showDetail(touchIndex);
        } else {
            hideDetail();
        }
        return super.onTouchEvent(event);
    }

    private PopupWindow popupWindow;

    /**
     * 弹窗展示详细信息
     *
     * @param index 点击的索引
     */
    public void showDetail(int index) {
        hideDetail();

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_detail_year, null);
        TextView tvTotalIncome = (TextView) view.findViewById(R.id.tv_popup_windwo_total_income);
        TextView tvStateSubsidy = (TextView) view.findViewById(R.id.tv_popup_windwo_state_subsidy);
        TextView tvLocalSubsidy = (TextView) view.findViewById(R.id.tv_popup_windwo_lacal_subsidy);
        TextView tvSave = (TextView) view.findViewById(R.id.tv_popup_windwo_save);
        TextView tvSell = (TextView) view.findViewById(R.id.tv_popup_windwo_sell);
        tvTotalIncome.setText("年收益总计：" + format2Bit(yearMillionYuanList.get(index).totalIncome + ""));
        tvStateSubsidy.setText("国家补贴：" + format2Bit(yearMillionYuanList.get(index).stateSubsidy + ""));
        tvLocalSubsidy.setText("地方补贴：" + format2Bit(yearMillionYuanList.get(index).lacalSubsidy + ""));
        tvSave.setText("节省电费：" + format2Bit(yearMillionYuanList.get(index).save + ""));
        tvSell.setText("出售电费：" + format2Bit(yearMillionYuanList.get(index).sell + ""));

        int width = DensityUtil.dp2px(getContext(), 160f);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(false);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setOutsideTouchable(true);
        // 计算弹窗偏移量
        int xoff;
        if (index < histograms.length / 2) {
            xoff = (int) (histograms[index][0].right + space);
        } else {
            xoff = (int) (histograms[index][0].right - width - space - histogramWidth);
        }
        int yoff = (int) (-getMeasuredHeight() / 2f - 1.5f * yScaleDiff);
        popupWindow.showAsDropDown(this, xoff, yoff);
    }

    /**
     * 关闭弹窗
     */
    private void hideDetail() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
}
