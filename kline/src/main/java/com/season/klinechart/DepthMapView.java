package com.season.klinechart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;

import com.season.mylibrary.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DepthMapView extends View {

    private int mWidth;
    //圆点半径
    private int mDotRadius = 2;
    //圆圈半径
    private int mCircleRadius = 8;
    private float mGridWidth;
    //底部价格区域高度
    private int mBottomPriceHeight;
    //右侧委托量绘制个数
    private int mLineCount;
    //背景颜色
    private int mBackgroundColor;

    private boolean mIsHave;
    //是否是长按
    private boolean mIsLongPress;

    //最大的委托量
    private float mMaxVolume;
    private float mMultiple;
    private int mLastPosition;
    private int mDrawWidth = 0;
    private int mDrawHeight;
    //触摸点的X轴值
    private int mEventX;

    //文案绘制画笔
    private Paint mTextPaint;
    //买入区域边线绘制画笔
    private Paint mBuyLinePaint;
    //卖出区域边线绘制画笔
    private Paint mSellLinePaint;
    //买入区域绘制画笔
    private Paint mBuyPathPaint;
    //卖出取悦绘制画笔
    private Paint mSellPathPaint;
    //选中时圆点绘制画笔
    private Paint mRadioPaint;
    //选中时中间文案背景画笔
    private Paint mSelectorBackgroundPaint;
    //选中时中间文案背景画笔
    private Paint mSelectorBackgroundLinePaint;

    private Path mBuyPath = new Path();
    private Path mSellPath = new Path();

    private List<DepthDataBean> mBuyData;
    private List<DepthDataBean> mSellData;

    //    价格显示精度限制
    public int mPriceLimit = 4;

    private HashMap<Integer, Integer> mMapPosition;
    private HashMap<Integer, DepthDataBean> mMapX;
    private HashMap<Integer, Float> mMapY;
    private Float[] mBottomPrice;
    private GestureDetector mGestureDetector;

    public DepthMapView(Context context) {
        this(context, null);
    }

    public DepthMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DepthMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @SuppressLint("UseSparseArrays")
    private void init(AttributeSet attrs) {
        mBottomPriceHeight = 40;
        mMapX = new HashMap<>();
        mMapPosition = new HashMap<>();
        mMapY = new HashMap<>();
        mBottomPrice = new Float[4];
        mBuyData = new ArrayList<>();
        mSellData = new ArrayList<>();
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                mIsLongPress = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                invalidate();
            }
        });

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.RIGHT);

        mBuyLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBuyLinePaint.setStyle(Paint.Style.STROKE);
        mBuyLinePaint.setTextAlign(Paint.Align.CENTER);
        mBuyPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSellLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSellLinePaint.setStyle(Paint.Style.STROKE);
        mSellLinePaint.setTextAlign(Paint.Align.CENTER);
        mSellPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mRadioPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRadioPaint.setTextAlign(Paint.Align.CENTER);
        mSelectorBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mSelectorBackgroundLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSelectorBackgroundLinePaint.setStyle(Paint.Style.STROKE);
        mSelectorBackgroundLinePaint.setStrokeWidth(dp2px(0.8f));

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DepthMapView);
        if (typedArray != null) {
            try {
                mLineCount = typedArray.getInt(R.styleable.DepthMapView_line_count, 4);
                mDotRadius = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_dot_radius, dp2px(mDotRadius));
                mCircleRadius = typedArray.getDimensionPixelSize(R.styleable.DepthMapView_circle_radius, dp2px(mCircleRadius));
                mBackgroundColor = typedArray.getColor(R.styleable.DepthMapView_background_color, getColor(R.color.chart_dark));
                mBuyLinePaint.setStrokeWidth(typedArray.getDimensionPixelSize(R.styleable.DepthMapView_line_width, dp2px(1.5f)));
                mSellLinePaint.setStrokeWidth(typedArray.getDimensionPixelSize(R.styleable.DepthMapView_line_width, dp2px(1.5f)));
                mTextPaint.setColor(typedArray.getColor(R.styleable.DepthMapView_text_color, getColor(R.color.chart_white)));
                mTextPaint.setTextSize(typedArray.getDimension(R.styleable.DepthMapView_text_size, getDimension(R.dimen.chart_text_size)));
                mBuyLinePaint.setColor(typedArray.getColor(R.styleable.DepthMapView_buy_line_color,
                        getColor(ColorStrategy.getStrategy().getRiseColor())));
                mSellLinePaint.setColor(typedArray.getColor(R.styleable.DepthMapView_sell_line_color,
                        getColor(ColorStrategy.getStrategy().getFallColor())));
                mBuyPathPaint.setColor(typedArray.getColor(R.styleable.DepthMapView_buy_path_color,
                        getColor(ColorStrategy.getStrategy().getRiseAlphaColor())));
                mSellPathPaint.setColor(typedArray.getColor(R.styleable.DepthMapView_sell_path_color,
                        getColor(ColorStrategy.getStrategy().getFallAlphaColor())));
                mSelectorBackgroundPaint.setColor(typedArray.getColor(R.styleable.DepthMapView_selector_background_color, getColor(R.color.chart_selector)));
                mSelectorBackgroundLinePaint.setColor(typedArray.getColor(R.styleable.DepthMapView_selector_line_color, getColor(R.color.chart_selector_line)));
            } finally {
                typedArray.recycle();
            }
        }
    }

    public int getColor(@ColorRes int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    public float getDimension(@DimenRes int resId) {
        return getContext().getResources().getDimension(resId);
    }

    public int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        mDrawWidth = mWidth / 2 - 1;
        mDrawHeight = h - mBottomPriceHeight;
    }

    public void setData(List<DepthDataBean> buyData, List<DepthDataBean> sellData) {
        if (buyData.size() > 0) {
            mBuyData.clear();
            mBuyData.addAll(buyData);
            //修改底部买入价格展示
            mBottomPrice[0] = mBuyData.get(0).getPrice();
            mBottomPrice[1] = mBuyData.get(mBuyData.size() > 1 ? mBuyData.size() - 1 : 0).getPrice();
            mMaxVolume = mBuyData.get(0).getVolume();
        }

        if (sellData.size() > 0) {
            mSellData.clear();
            mSellData.addAll(sellData);
            //修改底部卖出价格展示
            mBottomPrice[2] = mSellData.get(0).getPrice();
            mBottomPrice[3] = mSellData.get(mSellData.size() > 1 ? mSellData.size() - 1 : 0).getPrice();
            mMaxVolume = Math.max(mMaxVolume, mSellData.get(mSellData.size() - 1).getVolume());
        }
        mMaxVolume = mMaxVolume * 1.05f;
        mMultiple = mMaxVolume / mLineCount;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEventX = (int) event.getX();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsLongPress = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                invalidate();
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(mBackgroundColor);
        canvas.save();
        //绘制买入区域
        drawBuy(canvas);
        //绘制卖出区域
        drawSell(canvas);
        //绘制界面相关文案
        drawText(canvas);
        canvas.restore();
    }

    private void drawBuy(Canvas canvas) {
        mGridWidth = (mDrawWidth * 1.0f / (mBuyData.size() - 1 == 0 ? 1 : mBuyData.size() - 1));
        mBuyPath.reset();
        mMapX.clear();
        mMapY.clear();
        mMapPosition.clear();

        for (int i = 0; i < mBuyData.size(); i++) {
            float y = getY(mBuyData.get(i).getVolume());
            if (i == 0) {
                mBuyPath.moveTo(0, y);
            } else {
                mBuyPath.lineTo(mGridWidth * i, y);
            }

            int x = (int) (mGridWidth * i);
            mMapPosition.put(i, x);
            mMapX.put(x, mBuyData.get(i));
            mMapY.put(x, y);
        }
        mBuyPath.lineTo(mDrawWidth, mDrawHeight);
        canvas.drawPath(mBuyPath, mBuyLinePaint);
        mBuyPath.lineTo(0, mDrawHeight);
        mBuyPath.close();
        canvas.drawPath(mBuyPath, mBuyPathPaint);
    }

    private void drawSell(Canvas canvas) {
        mGridWidth = (mDrawWidth * 1.0f / (mSellData.size() - 1 == 0 ? 1 : mSellData.size() - 1));
        mSellPath.reset();

        mSellPath.moveTo(mDrawWidth, mDrawHeight);
        for (int i = 0; i < mSellData.size(); i++) {
            float y = getY(mSellData.get(i).getVolume());
            mSellPath.lineTo((mGridWidth * i) + mDrawWidth, y);
            int x = (int) ((mGridWidth * i) + mDrawWidth);
            mMapPosition.put(mBuyData.size() + i, x);
            mMapX.put(x, mSellData.get(i));
            mMapY.put(x, y);
        }
        canvas.drawPath(mSellPath, mSellLinePaint);
        mSellPath.lineTo(mWidth, mDrawHeight);
        mSellPath.close();
        canvas.drawPath(mSellPath, mSellPathPaint);
    }

    private void drawText(Canvas canvas) {
        float value;
        String str;
        for (int j = 0; j < mLineCount; j++) {
            value = mMaxVolume - mMultiple * j;
            str = getVolumeValue(value);
            canvas.drawText(str, mWidth, mDrawHeight / mLineCount * j + 30, mTextPaint);
        }
        int size = mBottomPrice.length;
        int height = mDrawHeight + mBottomPriceHeight / 2 + 10;
        if (size > 0 && mBottomPrice[0] != null) {
            String data = getValue(mBottomPrice[0]);
            canvas.drawText(data, mTextPaint.measureText(data), height, mTextPaint);
            data = getValue(mBottomPrice[1]);
            canvas.drawText(data, mDrawWidth - 10, height, mTextPaint);
            data = getValue(mBottomPrice[2]);
            canvas.drawText(data, mDrawWidth + mTextPaint.measureText(data) + 10, height, mTextPaint);
            data = getValue(mBottomPrice[3]);
            canvas.drawText(data, mWidth, height, mTextPaint);
        }
        if (mIsLongPress) {
            mIsHave = false;
            for (int key : mMapPosition.keySet()) {
                if (key == mEventX / (mWidth / mMapPosition.size())) {
                    mLastPosition = mEventX;
                    drawSelectorView(canvas, mMapPosition.get(key));
                    break;
                }
            }
            if (!mIsHave) {
                drawSelectorView(canvas, mLastPosition);
            }
        }
    }

    private float currentWidth = -1;
    private void drawSelectorView(Canvas canvas, int position) {
        mIsHave = true;
        Float y = mMapY.get(position);
        if (y == null) return;
        if (position < mDrawWidth) {
            canvas.drawCircle(position, y, mCircleRadius, mBuyLinePaint);
            mRadioPaint.setColor(getColor(ColorStrategy.getStrategy().getRiseColor()));
        } else {
            canvas.drawCircle(position, y, mCircleRadius, mSellLinePaint);
            mRadioPaint.setColor(getColor(ColorStrategy.getStrategy().getFallColor()));
        }
        canvas.drawCircle(position, y, mDotRadius, mRadioPaint);

        String volume = getContext().getString(R.string.number) + ": " + getVolumeValue(mMapX.get(position).getVolume());
        String price = getContext().getString(R.string.price) + ": " + getValue(mMapX.get(position).getPrice());
        float width = Math.max(mTextPaint.measureText(volume), mTextPaint.measureText(price));
        currentWidth = Math.max(currentWidth, width);
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        int padding = dp2px(5);
        canvas.drawRoundRect(new RectF(mDrawWidth - currentWidth / 2 - padding, 16,
                        mDrawWidth + currentWidth / 2 + padding * 2, textHeight * 2 + padding * 2 + 16),
                10, 10, mSelectorBackgroundPaint);
        canvas.drawRoundRect(new RectF(mDrawWidth - currentWidth / 2 - padding, 16,
                        mDrawWidth + currentWidth / 2 + padding * 2, textHeight * 2 + padding * 2 + 16),
                10, 10, mSelectorBackgroundLinePaint);
        canvas.drawText(getContext().getString(R.string.number) + ": ",
                mDrawWidth - currentWidth / 2 + padding + mTextPaint.measureText(getContext().getString(R.string.number)), textHeight + 2 + 16, mTextPaint);
        canvas.drawText(getVolumeValue(mMapX.get(position).getVolume()), mDrawWidth + currentWidth / 2 + padding, textHeight + 2 + 16, mTextPaint);
        canvas.drawText(getContext().getString(R.string.price) + ": ",
                mDrawWidth - currentWidth / 2 + padding + mTextPaint.measureText(getContext().getString(R.string.price)), textHeight * 2 + padding + 16, mTextPaint);
        canvas.drawText(getValue(mMapX.get(position).getPrice()), mDrawWidth + currentWidth / 2 + padding, textHeight * 2 + padding + 16, mTextPaint);
    }

    private float getY(float volume) {
        return mDrawHeight - (mDrawHeight) * volume / mMaxVolume;
    }

    private String getValue(float value) {
//        String value = new BigDecimal(data).toPlainString();
//        return subZeroAndDot(value);
        return String.format("%." + mPriceLimit + "f", value);
    }

    @SuppressLint("DefaultLocale")
    private String getVolumeValue(float value) {
        return String.format("%.4f", value);
    }

}
