package com.season.example.panel;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.season.klinechart.KLineChartView;
import com.season.example.net.BeanPrice;
import com.season.klinechart.ColorStrategy;
import com.season.mylibrary.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class TopPanel {

    private final Activity context;

    public TopPanel(Activity context, KLineChartView kLineChartView) {
        this.context = context;
    }


    private TextView priceView;
    private TextView priceCNView;
    private TextView percentView;
    private TextView max24View;
    private TextView min24View;
    private TextView vol24View;

    public TopPanel act() {
        priceView = findViewById(R.id.top_tv_price);
        priceCNView = findViewById(R.id.top_tv_price_cn);
        percentView = findViewById(R.id.top_tv_percent);
        max24View = findViewById(R.id.top_tv_24max);
        min24View = findViewById(R.id.top_tv_24min);
        vol24View = findViewById(R.id.top_tv_24vol);
        return this;
    }

    public <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }


    public void onPriceChange(BeanPrice price) {
        context.runOnUiThread(() -> {
            priceView.setText(getValueDot(price.currentExchangPrice));
            max24View.setText(getValueDot(price.maxPrice));
            min24View.setText(getValueDot(price.minPrice));
            vol24View.setText(price.transactionSum);
            priceCNView.setText("≈" + getValueDot(price.getPriceRMB()) + " " + price.getCoinSymbol());
            double percent = price.getPercent();
            if (percent >= 0){
                percentView.setText("+"+getValueDot(percent) + "%");
                priceView.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getRiseColor()));
                percentView.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getRiseColor()));
            }else{
                percentView.setText(getValueDot(percent) + "%");
                priceView.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getFallColor()));
                percentView.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getFallColor()));
            }
        });
    }

    static DecimalFormat dfRMB = new DecimalFormat("#,##0.00");

    public static String getValueDot(double value) {
        return dfRMB.format(value);
    }

    public static String getValueDot(String value) {
        return dfRMB.format(new BigDecimal((value)));
    }
}
