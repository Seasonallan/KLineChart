package com.season.klinechart;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class AutofitHeightViewPager extends ViewPager {

    private int position;

    private HashMap<Integer, Integer> maps = new LinkedHashMap<Integer, Integer>();

    public AutofitHeightViewPager(@NonNull Context context) {
        super(context);
    }

    public AutofitHeightViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            maps.put(i, h);
        }
        View childView = getChildAt(position);
        if (getChildCount() > 0 && childView != null) {
            height = childView.getMeasuredHeight();
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 在切换tab的时候，重置viewPager的高度
     */
    public void resetHeight(int position) {
        this.position = position;
        if (maps.size() > position) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maps.get(position));
            } else {
                layoutParams.height = maps.get(position);
            }
            setLayoutParams(layoutParams);
        }
    }
}
