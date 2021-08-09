package com.season.klinechart.draw;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.season.klinechart.BaseKLineChartView;
import com.season.klinechart.base.IChartDraw;
import com.season.klinechart.base.IValueFormatter;
import com.season.klinechart.entity.IWR;
import com.season.klinechart.formatter.ValueFormatter;

/**
 * KDJ实现类
 * Created by tifezh on 2016/6/19.
 */
public class WRDraw implements IChartDraw<IWR> {

    private Paint mRPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public WRDraw(BaseKLineChartView view) {
    }

    @Override
    public void drawTranslated(IWR lastPoint, IWR curPoint, float lastX, float curX, Canvas canvas, BaseKLineChartView view, int position) {
        if (lastPoint.getR() != -10) {
            view.drawChildLine(canvas, mRPaint, lastX, lastPoint.getR(), curX, curPoint.getR());
        }
    }

    @Override
    public void drawText(Canvas canvas, BaseKLineChartView view, int position, float x, float y) {
        IWR point = (IWR) view.getItem(position);
        if (point.getR() != -10) {
            String text = "WR(14):";
            canvas.drawText(text, x, y, view.getTextPaint());
            x += view.getTextPaint().measureText(text);
            text = view.formatValue(point.getR()) + " ";
            canvas.drawText(text, x, y, mRPaint);
        }
    }

    @Override
    public float getMaxValue(IWR point) {
        return point.getR();
    }

    @Override
    public float getMinValue(IWR point) {
        return point.getR();
    }

    @Override
    public IValueFormatter getValueFormatter() {
        return new ValueFormatter();
    }

    /**
     * 设置%R颜色
     */
    public void setRColor(int color) {
        mRPaint.setColor(color);
    }

    /**
     * 设置曲线宽度
     */
    public void setLineWidth(float width) {
        mRPaint.setStrokeWidth(width);
    }

    /**
     * 设置文字大小
     */
    public void setTextSize(float textSize) {
        mRPaint.setTextSize(textSize);
    }
}
