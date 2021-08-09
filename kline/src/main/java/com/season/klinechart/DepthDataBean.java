package com.season.klinechart;

public class DepthDataBean {

    private float mPrice = -1;
    private float mVolume = -1;

    public float getVolume() {
        return mVolume;
    }

    public void setVolume(float volume) {
        this.mVolume = volume;
    }

    public float getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        this.mPrice = price;
    }
}
