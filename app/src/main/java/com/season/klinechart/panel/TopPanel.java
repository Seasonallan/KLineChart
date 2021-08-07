package com.season.klinechart.panel;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.github.fujianlian.klinechart.KLineChartView;
import com.season.klinechart.R;
import com.season.klinechart.net.BeanPrice;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.logging.Handler;

public class TopPanel {

    private final KLineChartView kLineChartView;
    private final Activity context;
    public TopPanel(Activity context, KLineChartView kLineChartView) {
        this.context = context;
        this.kLineChartView = kLineChartView;
    }


    private TextView k_Price;
    private TextView approx;
    private TextView apprise;
    private TextView app_zuigao;
    private TextView app_zuidi;
    private TextView app_liang;
    private TextView text_zuigao;
    private TextView text_zuidi;
    private TextView text_liang;
    public TopPanel act(){
        k_Price = findViewById(R.id.k_Price);
        approx = findViewById(R.id.approx);
        apprise = findViewById(R.id.apprise);
        app_zuigao = findViewById(R.id.app_zuigao);
        app_zuidi = findViewById(R.id.app_zuidi);
        app_liang = findViewById(R.id.app_liang);
        text_zuigao = findViewById(R.id.text_zuigao);
        text_zuidi = findViewById(R.id.text_zuidi);
        text_liang = findViewById(R.id.text_liang);
        return this;
    }

    public <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }

    private static DecimalFormat df = new DecimalFormat("0.00");
    private static DecimalFormat dfRMB = new DecimalFormat("#,###");


    public void onPriceChange(BeanPrice price) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                k_Price.setText(getValueDot(price.currentExchangPrice));
                app_zuigao.setText(getValueDot(price.maxPrice));
                app_zuidi.setText(getValueDot(price.minPrice));
                app_liang.setText(price.transactionSum);
                approx.setText("≈"+ getValueDot(getValue(price.getPriceRMB()))+" CNY");
                apprise.setText(getValue(price.getPercent()) +"%");
            }
        });
    }



    public static String getValue(double value) {
        return df.format(value);
    }

    public static String getValueDot(String value) {
        try {
            String[] split = value.split("[.]");
            if (split.length == 2){
                String start = split[0];
                String end = split[1];
                StringBuffer stringBuffer = new StringBuffer();

                stringBuffer.append(dfRMB.format(new BigInteger(start)));
                stringBuffer.append(".");
                stringBuffer.append(end);
                return stringBuffer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
