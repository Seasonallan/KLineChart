package com.season.klinechart;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.draw.Status;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.google.android.material.tabs.TabLayout;
import com.season.klinechart.panel.TimePanel;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    KLineChartView kLineChartView;
    KLineChartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        kLineChartView = findViewById(R.id.kLineChartView);
        adapter = new KLineChartAdapter();
        kLineChartView.setDateTimeFormatter(new DateFormatter());
        kLineChartView.setOverScrollRange(138);
        kLineChartView.setGridRows(5);
        kLineChartView.setGridColumns(5);
        kLineChartView.setAdapter(adapter);

        initView();
        kLineChartView.justShowLoading();
        new Thread(() -> {
            List<KLineEntity> data = DataRequest.getALL(MainActivity.this);
            DataHelper.calculate(data);
            runOnUiThread(() -> {
                adapter.addFooterData(data);
                adapter.notifyDataSetChanged();
                kLineChartView.startAnimation();
                kLineChartView.refreshEnd();
            });
        }).start();


    }

    private DecimalFormat df = new DecimalFormat("0.000");
    private DecimalFormat dfCoinPrice = new DecimalFormat("0.00");//币种价格小数位限制
    private DecimalFormat dfCoinNumber = new DecimalFormat("0.00");//币种数量小数位限制

    private TextView tv_coin_title;
    private TextView k_Price;
    private TextView approx;
    private TextView apprise;
    private TextView app_zuigao;
    private TextView app_zuidi;
    private TextView app_liang;
    private TextView text_zuigao;
    private TextView text_zuidi;
    private TextView text_liang;
    private TextView tv_text_fen0;
    private TextView tv_text_fen1;
    private TextView tv_text_fen5;
    private TextView tv_text_fen15;
    private TextView tv_text_fen30;
    private TabLayout tab_layout;
    private AutofitHeightViewPager tab_View;


    private TextView textViewSve;
    private TextView textViewNine;
    private TextView textViewSex;
    private TextView textViewFar;
    private void initView() {
        k_Price = findViewById(R.id.k_Price);
        approx = findViewById(R.id.approx);
        apprise = findViewById(R.id.apprise);
        app_zuigao = findViewById(R.id.app_zuigao);
        app_zuidi = findViewById(R.id.app_zuidi);
        app_liang = findViewById(R.id.app_liang);
        text_zuigao = findViewById(R.id.text_zuigao);
        text_zuidi = findViewById(R.id.text_zuidi);
        text_liang = findViewById(R.id.text_liang);


        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        new TimePanel(this, kLineChartView).act();

        findViewById(R.id.btn_back).setOnClickListener(o -> {
            finish();
        });


    }



}