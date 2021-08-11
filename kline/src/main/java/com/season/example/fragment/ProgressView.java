package com.season.example.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.season.klinechart.ColorStrategy;

public class ProgressView extends View {
    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(buyRect, buyPaint);
        canvas.drawRect(sellRect, sellPaint);
    }

    Paint buyPaint, sellPaint;
    private RectF buyRect = new RectF(), sellRect = new RectF();

    public void setProgress(float percentBuy, float percentSell) {
        if (buyPaint == null) {
            buyPaint = new Paint();
        }
        if (sellPaint == null) {
            sellPaint = new Paint();
        }
        buyPaint.setColor(getContext().getResources().getColor(ColorStrategy.getStrategy().getRiseAlphaColor()));
        sellPaint.setColor(getContext().getResources().getColor(ColorStrategy.getStrategy().getFallAlphaColor()));
        buyRect.left = getWidth() / 2 - getWidth() * percentBuy / 2;
        buyRect.top = 0;
        buyRect.right = getWidth() / 2;
        buyRect.bottom = getHeight();

        sellRect.left = getWidth() / 2;
        sellRect.top = 0;
        sellRect.right = getWidth() / 2 + getWidth() * percentSell / 2;
        sellRect.bottom = getHeight();

        invalidate();
    }
}
