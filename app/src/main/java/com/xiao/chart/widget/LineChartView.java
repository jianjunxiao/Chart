package com.xiao.chart.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiao.chart.DensityUtil;
import com.xiao.chart.R;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by xiaoyanger on 2017/8/17.
 * 折线图
 */
public class LineChartView extends View {

    private Paint axisPaint;           // 画坐标轴的画笔
    private Paint linePaint;           // 线条画笔
    private Paint gradientPaint;       // 渐变画笔

    private float xScaleDiff;          // x轴单位刻度差(像素)
    private float yScaleDiff;          // y轴单位刻度差(像素)
    private float originX;             // 原点的X坐标(像素)
    private float originY;             // 原点的Y坐标(像素)
    private float xAxisLength;         // x轴长度(像素)
    private float yAxisLength;         // y轴长度(像素)
    private float scaleLineLength;     // 刻度线长度(像素)
    private float circleRadius;        // 圆圈半径大小

    private Rect bounds = new Rect();   // 测试字符串的长宽矩形

    private String[] xValues = {"03:00", "07:00", "11:00", "15:00", "19:00", "23:00"};  // x轴刻度值

    private float[] yValues = {};           // y轴刻度值
    private PointF[] pointFs;              // 数据点

    private List<HourKwh> hourKwhList;      // 数据

    private int[] shadeColors = {0x9674B5FD, 0x1474B5FD}; // 渐变色

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // 画笔
        axisPaint = new Paint();
        linePaint = new Paint();
        gradientPaint = new Paint();

        // 抗锯齿
        axisPaint.setAntiAlias(true);
        linePaint.setAntiAlias(true);
        gradientPaint.setAntiAlias(true);
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

        scaleLineLength = 0.08f * xScaleDiff;   // 刻度线长度

        float axisStrokeWidth = xScaleDiff / 40f;     // 坐标轴的线宽
        float lineScrokeWidth = xScaleDiff / 25f;     // 数据线条线宽
        float textSize = xScaleDiff / 4f;             // 文本大小

        circleRadius = 2 * lineScrokeWidth;   // 选中的圆圈大小

        // 设置画笔属性
        axisPaint.setStrokeWidth(axisStrokeWidth);
        axisPaint.setTextSize(textSize);
        axisPaint.setStrokeCap(Paint.Cap.ROUND);
        axisPaint.setColor(0xFF202428);

        linePaint.setStrokeCap(Paint.Cap.ROUND);
        linePaint.setColor(0xFF74B5FD);
        linePaint.setStrokeWidth(lineScrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinate(canvas);    // 绘制坐标系
        drawXScaleValues(canvas);  // 绘制x轴的刻度值
        drawYScaleValues(canvas);  // 绘制y轴的刻度值
        drawPointAndLines(canvas); // 绘制点和线
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
        // 绘制x轴标题："H"
        String xLable = "H";
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

    private void drawPointAndLines(Canvas canvas) {
        // 绘制渐变
        LinearGradient linearGradient = new LinearGradient(0, originY - yAxisLength, 0, originY,
                shadeColors, null, Shader.TileMode.CLAMP);
        gradientPaint.setShader(linearGradient);
        Path gradientPath = new Path();
        for (int i = 0; i < pointFs.length; i++) {
            if (i == 0) {
                gradientPath.moveTo(pointFs[i].x, originY);
            }
            gradientPath.lineTo(pointFs[i].x, pointFs[i].y);
            if (i == pointFs.length - 1) {
                gradientPath.lineTo(pointFs[i].x, originY);
                gradientPath.close();
            }
        }
        canvas.drawPath(gradientPath, gradientPaint);

        // 画线条
        for (int i = 0; i < pointFs.length - 1; i++) {
            canvas.drawLine(pointFs[i].x, pointFs[i].y, pointFs[i + 1].x, pointFs[i + 1].y, linePaint);
        }

        // 画圆圈
        for (int i = 0; i < pointFs.length; i++) {
            if ((i + 1) % 4 == 0 && i != touchedIndex) {
                // 绘制小圆圈
                canvas.drawCircle(pointFs[i].x, pointFs[i].y, circleRadius, linePaint);
                linePaint.setColor(Color.WHITE);
                canvas.drawCircle(pointFs[i].x, pointFs[i].y, circleRadius / 2f, linePaint);
                linePaint.setColor(0xFF74B5FD);
            } else if (i == touchedIndex) {
                // 绘制竖直线条
                linePaint.setColor(0xFFE48944);
                canvas.drawLine(pointFs[i].x, originY, pointFs[i].x, originY - yAxisLength, linePaint);
                // 绘制点击后的大圆圈
                linePaint.setColor(0xFF74B5FD);
                canvas.drawCircle(pointFs[i].x, pointFs[i].y, 1.25f * circleRadius, linePaint);
                linePaint.setColor(Color.WHITE);
                canvas.drawCircle(pointFs[i].x, pointFs[i].y, 0.625f * circleRadius, linePaint);
                linePaint.setColor(0xFF74B5FD);
            }
        }
    }

    public void setHourKwhs(final List<HourKwh> hourKwhs) {
        this.post(new Runnable() {
            @Override
            public void run() {
                hourKwhList = hourKwhs;
                yValues = generateYValues(hourKwhs);
                pointFs = generatePointFs(hourKwhs);
                touchedIndex = -1;
                hideDetail();
                invalidate();
            }
        });
    }

    private PointF[] generatePointFs(List<HourKwh> hourKwhs) {
        PointF[] pointFs = new PointF[hourKwhs.size()];
        for (int i = 0; i < pointFs.length; i++) {
            float x = originX + (i + 1) * xScaleDiff / 4f;
            float y = originY - hourKwhs.get(i).kwh * yScaleDiff / calculateScaleDiff(hourKwhs);
            pointFs[i] = new PointF(x, y);
        }
        return pointFs;
    }

    private float[] generateYValues(List<HourKwh> hourKwhs) {
        float[] yValues = new float[5];
        for (int i = 0; i < yValues.length; i++) {
            yValues[i] = format2Bit((i + 1) * calculateScaleDiff(hourKwhs));
        }
        return yValues;
    }

    private float calculateScaleDiff(List<HourKwh> hourKwhs) {
        float[] data = new float[hourKwhs.size()];
        for (int i = 0; i < data.length; i++) {
            data[i] = hourKwhs.get(i).kwh;
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

    private int touchedIndex = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touchedIndex = -1;
        for (int i = 0; i < pointFs.length; i++) {
            if (event.getX() >= pointFs[i].x - xScaleDiff / 8f
                    && event.getX() <= pointFs[i].x + xScaleDiff / 8f
                    && event.getY() >= originY - yAxisLength
                    && event.getY() <= originY) {
                touchedIndex = i;
            }
        }
        invalidate();
        if (touchedIndex != -1) {
            showDeatail();
        } else {
            hideDetail();
        }
        return super.onTouchEvent(event);
    }

    private PopupWindow popupWindow;

    private void showDeatail() {
        hideDetail();
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.layout_detail_hour, null);
        TextView tvHour = (TextView) view.findViewById(R.id.tv_popup_windwo_hour);
        TextView tvKwh = (TextView) view.findViewById(R.id.tv_popup_windwo_kwh);
        tvHour.setText(hourKwhList.get(touchedIndex).time);
        tvKwh.setText("电量：" + format2Bit(hourKwhList.get(touchedIndex).kwh + ""));

        int width = DensityUtil.dp2px(getContext(), 100f);
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
        if (touchedIndex < pointFs.length / 2) {
            xoff = (int) (pointFs[touchedIndex].x);
        } else {
            xoff = (int) (pointFs[touchedIndex].x - width);
        }
        int yoff = (int) (-getMeasuredHeight() / 2f - yScaleDiff);
        popupWindow.showAsDropDown(this, xoff, yoff);
    }

    private void hideDetail() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }
}
