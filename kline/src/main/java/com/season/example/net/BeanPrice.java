package com.season.example.net;

import android.text.TextUtils;

public class BeanPrice {
    public String maxPrice;
    public String minPrice;
    public String currentExchangPrice;

    public String transactionSum;
    public String openPrice;
    public double usdttormb;

    public String currencyToRmb;
    public String coinSymbol;

    public String getCoinSymbol() {
        if (TextUtils.isEmpty(coinSymbol)) {
            return "CNY";
        }
        return coinSymbol;
    }


    public double getPriceRMB() {
        try {
            double currentPrice = Double.parseDouble(currentExchangPrice);
            if (!TextUtils.isEmpty(currencyToRmb)) {
                return currentPrice * usdttormb / Double.parseDouble(currencyToRmb);
            } else {
                return currentPrice * usdttormb;
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public double getPercent() {
        try {
            double currentPrice = Double.parseDouble(currentExchangPrice);
            double startPrice = Double.parseDouble(openPrice);
            return (currentPrice - startPrice) * 100 / startPrice;
        } catch (Exception e) {
        }
        return 0.00;
    }
}
