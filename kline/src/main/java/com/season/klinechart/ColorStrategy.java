package com.season.klinechart;

import com.season.mylibrary.R;

public class ColorStrategy {

    private static ColorStrategy instance;

    public static ColorStrategy getStrategy() {
        if (null == instance) {
            synchronized (ColorStrategy.class) {
                if (null == instance) {
                    instance = new ColorStrategy();
                }
            }
        }
        return instance;
    }

    /**
     * 红涨绿跌
     */
    public void setRedRiseGreenFall(){
        isRiseGreen = false;
    }
    /**
     * 绿涨红跌
     */
    public void setGreenRiseRedFall(){
        isRiseGreen = true;
    }

    boolean isRiseGreen = true;
    public boolean isRiseGreen(){
        return isRiseGreen;
    }

    public int getRiseColor() {
        return isRiseGreen ? R.color.chart_green : R.color.chart_red;
    }

    public int getFallColor() {
        return isRiseGreen ? R.color.chart_red : R.color.chart_green;
    }

    public int getRiseAlphaColor() {
        return isRiseGreen ? R.color.chart_green_alpha : R.color.chart_red_alpha;
    }

    public int getFallAlphaColor() {
        return isRiseGreen ? R.color.chart_red_alpha : R.color.chart_green_alpha;
    }

}
