package com.season.example;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.season.example.fragment.BriefFragment;
import com.season.example.fragment.DealFragment;
import com.season.example.fragment.DealRecord;
import com.season.example.fragment.DepthFragment;
import com.season.example.net.BeanPrice;
import com.season.example.net.LocalTestData;
import com.season.example.net.WebSocketService;
import com.season.example.panel.TimePanel;
import com.season.example.panel.TopPanel;
import com.season.klinechart.ColorStrategy;
import com.season.klinechart.DataHelper;
import com.season.klinechart.DepthDataBean;
import com.season.klinechart.DepthMapView;
import com.season.klinechart.KLineChartAdapter;
import com.season.klinechart.KLineChartView;
import com.season.klinechart.KLineEntity;
import com.season.klinechart.formatter.DateFormatter;
import com.season.mylibrary.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KLineChartActivity extends AppCompatActivity implements WebSocketService.IMessageResponseListener {


    KLineChartView kLineChartView;
    KLineChartAdapter adapter;

    DealFragment dealFragment;
    BriefFragment briefFragment;
    DepthFragment depthFragment;

    String mCoinCode = "ETH_USDT";
    String mLanguage = "zh_CN";
    String webSocketUrl;
    String briefUrl;


    String mLanguageSocket = "";//语言

    public void setLanguage(String languageKey) {
        Locale myLocale;
        switch (languageKey) {
            case "ZH_CN":
                myLocale = Locale.CHINA;
                mLanguageSocket = "zh_CN";
                break;
            case "en":
                myLocale = Locale.ENGLISH;
                mLanguageSocket = "en";
                break;
            case "JP":
                myLocale = Locale.JAPAN;
                mLanguageSocket = "jp";
                break;
            case "FRA":
                myLocale = Locale.FRENCH;
                mLanguageSocket = "fr";
                break;
            case "KR":
                myLocale = Locale.KOREA;
                mLanguageSocket = "kor";
                break;
            default:
                myLocale = Locale.TAIWAN;
                mLanguageSocket = "zh_TW";
                break;
        }
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    DepthMapView depth_view;
    View depth_top_view, riseView, fallView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            mCoinCode = bundle.getString("coinCode");
            mLanguage = bundle.getString("language");
            webSocketUrl = bundle.getString("webSocketUrl");
            briefUrl = bundle.getString("briefUrl");
            boolean color = bundle.getBoolean("riseGreen", true);
            if (color){
                ColorStrategy.getStrategy().setGreenRiseRedFall();
            }else{
                ColorStrategy.getStrategy().setRedRiseGreenFall();
            }
        }
        setLanguage(mLanguage);
        setContentView(R.layout.activity_chart);

        depth_view = findViewById(R.id.depth_view);
        depth_top_view = findViewById(R.id.depth_top);
        riseView = findViewById(R.id.depth_top_rise);
        fallView = findViewById(R.id.depth_top_fall);
        riseView.setBackgroundResource(ColorStrategy.getStrategy().isRiseGreen()?R.drawable.circle_green:R.drawable.circle_red);
        fallView.setBackgroundResource(!ColorStrategy.getStrategy().isRiseGreen()?R.drawable.circle_green:R.drawable.circle_red);

        kLineChartView = findViewById(R.id.kLineChartView);
        adapter = new KLineChartAdapter();
        kLineChartView.setDateTimeFormatter(new DateFormatter());
        kLineChartView.setOverScrollRange(138);
        kLineChartView.setGridRows(5);
        kLineChartView.setGridColumns(5);
        kLineChartView.setAdapter(adapter);


        tv_coin_title = findViewById(R.id.toolbarTitle);
        tv_coin_title.setText(mCoinCode.replace("_", "/"));
        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        postRun();

    }

    private void postRun(){
        new Handler().post(() -> {
            timePanel = new TimePanel(KLineChartActivity.this, kLineChartView) {
                @Override
                protected View getDepthMapView() {
                    return depth_view;
                }

                @Override
                protected View getDepthTopView() {
                    return depth_top_view;
                }
            }.act();
            topPanel = new TopPanel(KLineChartActivity.this, kLineChartView).act();

            findViewById(R.id.btn_back).setOnClickListener(o -> {
                finish();
            });

            dealFragment = DealFragment.getInstance();
            briefFragment = BriefFragment.getInstance();
            depthFragment = DepthFragment.getInstance();
            briefFragment.coinCode = mCoinCode;
            briefFragment.langCode = mLanguage;
            briefFragment.briefUrl = briefUrl;
            dealFragment.coinCode = mCoinCode;
            //将fragment装进列表中
            List<Fragment> list_fragment = new ArrayList<>();
            list_fragment.add(depthFragment);
            list_fragment.add(dealFragment);
            list_fragment.add(briefFragment);
            //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
            ArrayList<String> list_title = new ArrayList<>();
            list_title.add(getResources().getString(R.string.order));
            list_title.add(getResources().getString(R.string.deal));
            list_title.add(getResources().getString(R.string.brief));

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

            kLineChartView.justShowLoading();
            if (TextUtils.isEmpty(webSocketUrl)) {
                new Thread(() -> {
                    List<KLineEntity> data = LocalTestData.getALL(KLineChartActivity.this);
                    DataHelper.calculate(data);
                    runOnUiThread(() -> {
                        adapter.addFooterData(data);
                        adapter.notifyDataSetChanged();
                        kLineChartView.startAnimation();
                        kLineChartView.refreshEnd();
                    });
                }).start();
            } else {
                WebSocketService.getInstance().register(KLineChartActivity.this);
                WebSocketService.getInstance().connect(webSocketUrl, mCoinCode);
            }

        }) ;
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
            WebSocketService.getInstance().subscribeTrade(mLanguageSocket);
        }
    }

    @Override
    public void onTradeChange(List<DealRecord> list) {
        runOnUiThread(() -> {
            dealFragment.onRecordChange(list);
        });
    }

    @Override
    public void onDepthChange(List<DepthDataBean> buyList, List<DepthDataBean> sellList) {
        runOnUiThread(() -> {
            depth_view.setData(buyList, sellList);
            depthFragment.onRecordChange(buyList, sellList);
        });
    }

}