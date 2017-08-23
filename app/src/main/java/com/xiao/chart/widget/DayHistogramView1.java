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
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiaoyanger on 2017/8/17.
 * 柱状图(日期).
 */
public class DayHistogramView1 extends View {

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

    private String[] xValues = {"5", "10", "15", "20", "25", "30"};  // x轴刻度值

    private float[] yValues = {};            // y轴刻度值
    private Histogram[][] histograms = {};   // 柱
    private float animatedValue;             // 动画值

    private List<DayKwh> dayKwhList;


    public DayHistogramView1(Context context, @Nullable AttributeSet attrs) {
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

        xScaleDiff = width / 8.5f;          // x轴刻度差
        yScaleDiff = height / 7.5f;         // y轴刻度差

        originX = xScaleDiff;               // 原点的x坐标
        originY = height - yScaleDiff;      // 原点的y坐标

        xAxisLength = 6.5f * xScaleDiff;    // x轴长度
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
        // 绘制y轴标题："KW·H"
        String yLable = "KW·H";
        axisPaint.getTextBounds(yLable, 0, yLable.length(), bounds);
        canvas.drawText(yLable,
                originX - bounds.width() / 2f,
                originY - yAxisLength - (yScaleDiff / 3f - bounds.height() / 3f),
                axisPaint);
        // 绘制x轴
        canvas.drawLine(originX, originY, originX + xAxisLength, originY, axisPaint);
        // 绘制x轴刻度
        for (int i = 1; i < 7; i++) {
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
        // 绘制x轴标题："D"
        String xLable = "D";
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

    public void setDayKwhs(final List<DayKwh> dayKwhs) {
        this.post(new Runnable() {
            @Override
            public void run() {
                dayKwhList = dayKwhs;
                yValues = generateYValues(dayKwhs);
                histograms = generateHistograms(dayKwhs);
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
     * 根据数据值生成柱状。
     * <pre>
     *      数据值的刻度差与像素值刻度差yScaleDiff的比例关系可以确定柱状的像素高度。
     *      float dataValue = dayKwhs.get(i).kwh;
     *      float dataValueScaleDiff = calculateScaleDiff(dayKwhs);
     *      float pxScaleDiff = yScaleDiff;
     *      flaot pxHistogramHeight =  originY - top;
     *      存在比例关系： pxHistogramHeight : dataValue = pxScaleDiff : dataValueScaleDiff
     *      即：         (originY - top) * calculateScaleDiff(dayKwhs) = dayKwhs.get(i).kwh * yScaleDiff;
     *      所以：        float top = originY - dayKwhs.get(i).kwh * yScaleDiff / calculateScaleDiff(dayKwhs);
     * </pre>
     *
     * @param dayKwhs 数据
     * @return 柱状数组
     */
    private Histogram[][] generateHistograms(List<DayKwh> dayKwhs) {
        Histogram[][] histograms = new Histogram[dayKwhs.size()][2];
        for (int i = 0; i < histograms.length; i++) {
            float left = originX + (i + 1) * space + (0.5f + i) * histogramWidth;
            float right = originX + (i + 1) * space + (1.5f + i) * histogramWidth;
            float top0 = originY - dayKwhs.get(i).kwh * yScaleDiff / calculateScaleDiff(dayKwhs);
            float top1 = originY - yAxisLength;
            float bottom = originY;
            histograms[i][0] = new Histogram(left, top0, right, bottom, 0xFF74B5FD);
            histograms[i][1] = new Histogram(left, top1, right, bottom, 0x00000000);
        }
        return histograms;
    }

    /**
     * 根据dayKwhs来生成y坐标的刻度值
     *
     * @return y坐标刻度值
     */
    private float[] generateYValues(List<DayKwh> dayKwhs) {
        float[] yValues = new float[5];
        for (int i = 0; i < yValues.length; i++) {
            // 计算y轴刻度值并把整数格式化为2为小数
            yValues[i] = format2Bit((i + 1) * calculateScaleDiff(dayKwhs));
        }
        return yValues;
    }

    /**
     * 计算y轴的数据值刻度差
     *
     * @param dayKwhs 数据
     * @return y轴的数据刻度差值
     */
    private float calculateScaleDiff(List<DayKwh> dayKwhs) {
        float[] data = new float[dayKwhs.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = dayKwhs.get(i).kwh;
        }
        Arrays.sort(data);
        // 四舍五入取整
        return (float) Math.rint(data[data.length - 1] / 5f);
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
            if (event.getX() >= histograms[i][1].left - space / 2f
                    && event.getX() <= histograms[i][1].right + space / 2f
                    && event.getY() >= histograms[i][1].top
                    && event.getY() <= histograms[i][1].bottom) {
                histograms[i][1].color = 0x96B3B3B3;
                touchIndex = i;
            } else {
                histograms[i][1].color = 0x00000000;
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
                .inflate(R.layout.layout_detail_day, null);
        TextView tvDay = (TextView) view.findViewById(R.id.tv_popup_windwo_day);
        TextView tvKwh = (TextView) view.findViewById(R.id.tv_popup_windwo_kwh);
        tvDay.setText(dayKwhList.get(index).day);
        tvKwh.setText("电量：" + dayKwhList.get(index).kwh + "KW·H");

        int width = DensityUtil.dp2px(getContext(), 130f);
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
            xoff = (int) (histograms[index][1].right + space);
        } else {
            xoff = (int) (histograms[index][1].right - width - space - histogramWidth);
        }
        int yoff = (int) (-getMeasuredHeight() / 2f - yScaleDiff);
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
