package com.season.klinechart;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.google.android.material.tabs.TabLayout;
import com.season.klinechart.fragment.BriefFragment;
import com.season.klinechart.fragment.DealFragment;
import com.season.klinechart.fragment.DealRecord;
import com.season.klinechart.net.BeanPrice;
import com.season.klinechart.net.LocalTestData;
import com.season.klinechart.net.WebSocketService;
import com.season.klinechart.panel.TimePanel;
import com.season.klinechart.panel.TopPanel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WebSocketService.IMessageResponseListener {


    KLineChartView kLineChartView;
    KLineChartAdapter adapter;

    DealFragment dealFragment;
    BriefFragment briefFragment;
    String coinCode = "BTC_USDT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        WebSocketService.getInstance().register(this);
        WebSocketService.getInstance().connect(Configure.socketUrl, coinCode);

        kLineChartView = findViewById(R.id.kLineChartView);
        adapter = new KLineChartAdapter();
        kLineChartView.setDateTimeFormatter(new DateFormatter());
        kLineChartView.setOverScrollRange(138);
        kLineChartView.setGridRows(5);
        kLineChartView.setGridColumns(5);
        kLineChartView.setAdapter(adapter);


        tv_coin_title = findViewById(R.id.toolbarTitle);
        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        timePanel = new TimePanel(this, kLineChartView).act();
        topPanel = new TopPanel(this, kLineChartView).act();

        findViewById(R.id.btn_back).setOnClickListener(o -> {
            finish();
        });

        dealFragment = DealFragment.getInstance();
        briefFragment = BriefFragment.getInstance();
        briefFragment.coinCode = coinCode;
        briefFragment.langCode = "zh_CN";
        dealFragment.coinCode = coinCode;
        //将fragment装进列表中
        List<Fragment> list_fragment = new ArrayList<>();
        list_fragment.add(dealFragment);
        list_fragment.add(briefFragment);
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        ArrayList<String> list_title = new ArrayList<>();
        list_title.add("成交");
        list_title.add("简介");

        //为TabLayout添加tab名称
        tab_layout.setTabMode(TabLayout.MODE_FIXED);

        //为TabLayout添加tab名称
        tab_layout.addTab(tab_layout.newTab().setText(list_title.get(0)));
        tab_layout.addTab(tab_layout.newTab().setText(list_title.get(1)));
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return list_fragment.get(position);
            }

            @Override
            public int getCount() {
                return list_title.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return list_title.get(position % list_title.size());
            }
        };

        tab_View.setOffscreenPageLimit(3);
        tab_View.setAdapter(mAdapter);
        tab_layout.setupWithViewPager(tab_View);
        tab_View.post(new Runnable() {
            @Override
            public void run() {

            }
        });

        kLineChartView.justShowLoading();
        if (false) {
            new Thread(() -> {
                List<KLineEntity> data = LocalTestData.getALL(MainActivity.this);
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
    private ViewPager tab_View;

    private TimePanel timePanel;
    private TopPanel topPanel;


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
        if (update) {
            currentList.set(currentList.size() - 1, data);
        } else {
            currentList.add(data);
        }
        DataHelper.calculate(currentList);
        runOnUiThread(() -> {
            adapter.addFooterData(currentList);
            adapter.notifyDataSetChanged();
            kLineChartView.refreshEnd();
            kLineChartView.setScrollEnable(true);
        });
    }

    @Override
    public void onSocketConnected(int index) {
        if (index == 0) {
            //订阅曲线
            runOnUiThread(() -> timePanel.timeSwitch("15"));
        } else {
            //订阅交易记录
            WebSocketService.getInstance().subscribeTrade();
        }
    }

    @Override
    public void onTradeChange(List<DealRecord> list) {
        runOnUiThread(() -> {
            dealFragment.onRecordChange(list);
        });
    }
}