package com.season.klinechart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.season.klinechart.BaseKLineChartView;
import com.season.klinechart.ColorStrategy;
import com.season.klinechart.base.IChartDraw;
import com.season.klinechart.base.IValueFormatter;
import com.season.klinechart.entity.IVolume;
import com.season.klinechart.formatter.BigValueFormatter;
import com.season.klinechart.utils.ViewUtil;

/**
 * Created by hjm on 2017/11/14 17:49.
 */
public class VolumeDraw implements IChartDraw<IVolume> {

    private Paint mRedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int pillarWidth = 0;

    public VolumeDraw(BaseKLineChartView view) {
        Context context = view.getContext();
        mRedPaint.setColor(ContextCompat.getColor(context, ColorStrategy.getStrategy().getFallColor()));
        mGreenPaint.setColor(ContextCompat.getColor(context, ColorStrategy.getStrategy().getRiseColor()));
        pillarWidth = ViewUtil.Dp2Px(context, 4);
    }

    @Override
    public void drawTranslated(
            IVolume lastPoint, IVolume curPoint, float lastX, float curX,
            Canvas canvas, BaseKLineChartView view, int position) {

        drawHistogram(canvas, curPoint, lastPoint, curX, view, position);
        if (lastPoint.getMA5Volume() != 0f) {
            view.drawVolLine(canvas, ma5Paint, lastX, lastPoint.getMA5Volume(), curX, curPoint.getMA5Volume());
        }
        if (lastPoint.getMA10Volume() != 0f) {
            view.drawVolLine(canvas, ma10Paint, lastX, lastPoint.getMA10Volume(), curX, curPoint.getMA10Volume());
        }
    }

    private void drawHistogram(
            Canvas canvas, IVolume curPoint, IVolume lastPoint, float curX,
            BaseKLineChartView view, int position) {

        float r = pillarWidth / 2;
        float top = view.getVolY(curPoint.getVolume());
        int bottom = view.getVolRect().bottom;
        if (curPoint.getClosePrice() >= curPoint.getOpenPrice()) {//???
            canvas.drawRect(curX - r, top, curX + r, bottom, mGreenPaint);
        } else {
            canvas.drawRect(curX - r, top, curX + r, bottom, mRedPaint);
        }

    }

    @Override
    public void drawText(
            Canvas canvas, BaseKLineChartView view, int position, float x, float y) {
        IVolume point = (IVolume) view.getItem(position);
        String text = "VOL:" + getValueFormatter().format(point.getVolume()) + "  ";
        canvas.drawText(text, x, y, view.getTextPaint());
        x += view.getTextPaint().measureText(text);
        text = "MA5:" + getValueFormatter().format(point.getMA5Volume()) + "  ";
        canvas.drawText(text, x, y, ma5Paint);
        x += ma5Paint.measureText(text);
        text = "MA10:" + getValueFormatter().format(point.getMA10Volume());
        canvas.drawText(text, x, y, ma10Paint);
    }

    @Override
    public float getMaxValue(IVolume point) {
        return Math.max(point.getVolume(), Math.max(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public float getMinValue(IVolume point) {
        return Math.min(point.getVolume(), Math.min(point.getMA5Volume(), point.getMA10Volume()));
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new BigValueFormatter();
    }

    /**
     * ?????? MA5 ????????????
     *
     * @param color
     */
    public void setMa5Color(int color) {
        this.ma5Paint.setColor(color);
    }

    /**
     * ?????? MA10 ????????????
     *
     * @param color
     */
    public void setMa10Color(int color) {
        this.ma10Paint.setColor(color);
    }

    public void setLineWidth(float width) {
        this.ma5Paint.setStrokeWidth(width);
        this.ma10Paint.setStrokeWidth(width);
    }

    /**
     * ??????????????????
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.ma5Paint.setTextSize(textSize);
        this.ma10Paint.setTextSize(textSize);
    }

}
