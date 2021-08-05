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

    // 主图指标下标
    private int mainIndex = 0;
    // 副图指标下标
    private int subIndex = -1;
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
    private TextView time_all;
    private TextView k_index;
    private TabLayout tab_layout;
    private AutofitHeightViewPager tab_View;

    private PopupWindow popupWindow;
    private TextView maText;
    private TextView bollText;
    private RadioGroup k_Group;

    private PopupWindow timePopup;
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


        time_all = findViewById(R.id.time_all);
        k_index = findViewById(R.id.k_index);
        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        initIndexPopup();
        initTimePopView();

        findViewById(R.id.btn_back).setOnClickListener(o -> {
            finish();
        });

        for (int i = 0; i < textIds.length; i++) {
            TextView textView = findViewById(textIds[i]);
            int finalI = i;
            textView.setOnClickListener(v -> {
                timeSwitch(intervals[finalI], textView);
            });
        }
        time_all.setOnClickListener(o -> // 分时K线：点击更多
        {
            timePopup.showAsDropDown(time_all, 0, 38);
        });
        k_index.setOnClickListener(o -> // 分时K线：点击指标
        {
            popupWindow.showAsDropDown(k_index, 0, 38);
        });
    }


    private void resetMaBoll(int index){
        maText.setTextColor(getResources().getColor(index == 0?R.color.chart_line:R.color.chart_text));
        maText.setBackgroundResource(index == 0?R.drawable.btn_line_deep:R.drawable.btn_circle_deep);

        bollText.setTextColor(getResources().getColor(index == 1?R.color.chart_line:R.color.chart_text));
        bollText.setBackgroundResource(index == 1?R.drawable.btn_line_deep:R.drawable.btn_circle_deep);

        if (index == 0) {
            kLineChartView.changeMainDrawType(Status.MA);
        }else if (index == 1) {
            kLineChartView.changeMainDrawType(Status.BOLL);
        }else{
            kLineChartView.changeMainDrawType(Status.NONE);
        }
    }
    private void initIndexPopup() {
        final View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.kc_index_popup, null);

        maText = contentView.findViewById(R.id.maText);
        bollText = contentView.findViewById(R.id.bollText);
        maText.setOnClickListener(v -> {
            kLineChartView.hideSelectData();
            //主图 MA线
            if (mainIndex != 0) {
                mainIndex = 0;
            }else{
                mainIndex = -1;
            }
            resetMaBoll(mainIndex);
        });
        bollText.setOnClickListener(v -> {
            kLineChartView.hideSelectData();
            //主图 BOLL线
            if (mainIndex != 1) {
                mainIndex = 1;
            }else{
                mainIndex = -1;
            }
            resetMaBoll(mainIndex);
        });
        mainIndex = 0;
        resetMaBoll(mainIndex);

        k_Group = contentView.findViewById(R.id.k_Group);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
        popupWindow.setAnimationStyle(R.style.contextMenuAnim);
        k_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = contentView.findViewById(k_Group.getCheckedRadioButtonId());
                kLineChartView.hideSelectData();
                if (k_Group.getCheckedRadioButtonId() == R.id.macdText) {
                    subIndex = 0;
                    kLineChartView.setChildDraw(subIndex);
                } else if (k_Group.getCheckedRadioButtonId() == R.id.kdjText) {
                    subIndex = 1;
                    kLineChartView.setChildDraw(subIndex);
                } else if (k_Group.getCheckedRadioButtonId() == R.id.rsiText) {
                    subIndex = 2;
                    kLineChartView.setChildDraw(subIndex);
                } else if (k_Group.getCheckedRadioButtonId() == R.id.wrText) {
                    subIndex = 3;
                    kLineChartView.setChildDraw(subIndex);
                }
                radioButton.setChecked(true);
            }
        });
        k_Group.check(R.id.macdText);
    }

    View timeView;
    private void initTimePopView() {
        timeView = LayoutInflater.from(MainActivity.this).inflate(R.layout.kc_time_popup, null);
        timePopup = new PopupWindow(timeView, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (getResources().getDisplayMetrics().density * 48 ));
        timePopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        timePopup.setOutsideTouchable(true);
        timePopup.setAnimationStyle(R.style.contextMenuAnim);
        timePopup.setFocusable(true);

        for (int i = 0; i < textIdsPop.length; i++) {
            TextView textView = timeView.findViewById(textIdsPop[i]);
            int finalI = i;
            textView.setOnClickListener(v -> {
                timeSwitch(intervalsPop[finalI], textView);
            });
        }
    }


    private String currentTimeSwitch = "-1";//当前显示的分时类型   默认1分钟
    private boolean isReceivedSub = false;//是否继续接收Sub数据
    private void timeSwitch(String interval, TextView selectTextView) {
        if (currentTimeSwitch == interval) {
            return;
        }
        isReceivedSub = false;
        currentTimeSwitch = interval;
        if (!currentTimeSwitch.equals("0")) {
            //CmdUtil.cmdReq(mCoinCode, currentTimeSwitch);//查询历史
        }

        kLineChartView.hideSelectData();

        for (int i = 0; i < intervals.length; i++) {
            TextView textView = findViewById(textIds[i]);
            if (interval.equals(intervals[i])){
                textView.setTextColor(getResources().getColor(R.color.chart_line));
            }else{
                textView.setTextColor(getResources().getColor(R.color.chart_text));
            }
        }

        for (int i = 0; i < intervalsPop.length; i++) {
            TextView textView = timeView.findViewById(textIdsPop[i]);
            if (interval.equals(intervalsPop[i])){
                textView.setTextColor(getResources().getColor(R.color.chart_line));
                textView.setBackgroundResource(R.drawable.btn_line_deep);
            }else{
                textView.setTextColor(getResources().getColor(R.color.chart_text));
                textView.setBackgroundResource(R.drawable.btn_circle_deep);
            }
        }
        if ("0".equals(interval)){
            kLineChartView.setMainDrawLine(true);
        }else{
            kLineChartView.setMainDrawLine(false);
        }
    }

    final int[] textIdsPop = {R.id.tv_text_fen0, R.id.tv_text_fen1, R.id.tv_text_fen5, R.id.tv_text_fen30, R.id.tv_text_mouth};
    final String[] intervalsPop = {"0", "1", "5", "30", "1M"};

    final int[] textIds = {R.id.tv_text_fen15, R.id.tv_text_hour1, R.id.tv_text_hour4, R.id.tv_text_day, R.id.tv_text_week};
    final String[] intervals = {"15", "60", "240", "1D", "1W"};


}