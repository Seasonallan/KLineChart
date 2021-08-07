package com.season.klinechart.net;

public class BeanPrice {
    public String maxPrice;
    public String minPrice;
    public String currentExchangPrice;

    public String transactionSum;
    public String openPrice;
    public double usdttormb;

    public double getPriceRMB() {
        try {
            double currentPrice = Double.parseDouble(currentExchangPrice);
            return currentPrice * usdttormb;
        } catch (Exception e) {
        }
        return 0;
    }

    public double getPercent(){
        try {
            double currentPrice = Double.parseDouble(currentExchangPrice);
            double startPrice = Double.parseDouble(openPrice);
            return (currentPrice - startPrice) * 100/ startPrice;
        } catch (Exception e) {
        }
        return 0.00;
    }
}
