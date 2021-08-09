package com.season.klinechart.panel;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.github.fujianlian.klinechart.KLineChartView;
import com.season.klinechart.R;
import com.season.klinechart.net.BeanPrice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class TopPanel {

    private final Activity context;

    public TopPanel(Activity context, KLineChartView kLineChartView) {
        this.context = context;
    }


    private TextView k_Price;
    private TextView approx;
    private TextView apprise;
    private TextView app_zuigao;
    private TextView app_zuidi;
    private TextView app_liang;

    public TopPanel act() {
        k_Price = findViewById(R.id.k_Price);
        approx = findViewById(R.id.approx);
        apprise = findViewById(R.id.apprise);
        app_zuigao = findViewById(R.id.app_zuigao);
        app_zuidi = findViewById(R.id.app_zuidi);
        app_liang = findViewById(R.id.app_liang);
        return this;
    }

    public <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }


    public void onPriceChange(BeanPrice price) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                k_Price.setText(getValueDot(price.currentExchangPrice));
                app_zuigao.setText(getValueDot(price.maxPrice));
                app_zuidi.setText(getValueDot(price.minPrice));
                app_liang.setText(price.transactionSum);
                approx.setText("≈" + getValueDot(price.getPriceRMB()) + " CNY");
                apprise.setText(getValueDot(price.getPercent()) + "%");
            }
        });
    }

    static DecimalFormat dfRMB = new DecimalFormat("#,#0.00");

    public static String getValueDot(double value) {
        return dfRMB.format(value);
    }

    public static String getValueDot(String value) {
        return dfRMB.format(new BigDecimal((value)));
    }
}
