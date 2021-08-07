package com.season.klinechart;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.google.android.material.tabs.TabLayout;
import com.season.klinechart.net.BeanPrice;
import com.season.klinechart.net.WebSocketService;
import com.season.klinechart.panel.TimePanel;
import com.season.klinechart.panel.TopPanel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WebSocketService.IMessageResponseListener {


    KLineChartView kLineChartView;
    KLineChartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        WebSocketService.getInstance().register(this);
        WebSocketService.getInstance().connect(Configure.socketUrl, "BTC_USDT");
        //WebSocketService.getInstance().connect("ws://api-new-test.sgpexchange.com/deep", "BTC_USDT");

        kLineChartView = findViewById(R.id.kLineChartView);
        adapter = new KLineChartAdapter();
        kLineChartView.setDateTimeFormatter(new DateFormatter());
        kLineChartView.setOverScrollRange(138);
        kLineChartView.setGridRows(5);
        kLineChartView.setGridColumns(5);
        kLineChartView.setAdapter(adapter);
        initView();

        kLineChartView.justShowLoading();
        if (false){
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

    }

    private TextView tv_coin_title;

    private TabLayout tab_layout;
    private AutofitHeightViewPager tab_View;

    private TimePanel timePanel;
    private TopPanel topPanel;

    private void initView() {
        tv_coin_title = findViewById(R.id.toolbarTitle);
        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        timePanel = new TimePanel(this, kLineChartView).act();
        topPanel = new TopPanel(this, kLineChartView).act();

        findViewById(R.id.btn_back).setOnClickListener(o -> {
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketService.getInstance().unRegister(this);
        WebSocketService.getInstance().close();
    }

    @Override
    public void onPriceChange(BeanPrice price) {
        topPanel.onPriceChange(price);
    }


    List<KLineEntity> currentList = new ArrayList<>();
    @Override
    public void onChartChange(List<KLineEntity> data) {
        DataHelper.calculate(data);
        currentList = data;
        runOnUiThread(() -> {
            adapter.addFooterData(data);
            adapter.notifyDataSetChanged();
            kLineChartView.startAnimation();
            kLineChartView.refreshEnd();
            WebSocketService.getInstance().subscribe(timePanel.currentTimeSwitch);
        });
    }

    @Override
    public synchronized void onSubscribe(boolean update, KLineEntity data) {
        if (update){
            currentList.set(currentList.size() - 1, data);
        }else{
            currentList.add(data);
        }
        DataHelper.calculate(currentList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addFooterData(currentList);
                adapter.notifyDataSetChanged();
                kLineChartView.refreshEnd();
                kLineChartView.setScrollEnable(true);
            }
        });
    }

    @Override
    public void onSocketConnected(int index) {
        if (index == 0){
            //订阅曲线
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timePanel.timeSwitch("15");
                }
            });
        }else{
            //订阅交易记录
            //WebSocketService.getInstance().subscribeTrade();
        }
    }
}