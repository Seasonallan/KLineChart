package com.season.klinechart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.fujianlian.klinechart.DataHelper;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.draw.Status;
import com.github.fujianlian.klinechart.formatter.DateFormatter;
import com.google.android.material.tabs.TabLayout;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    KLineChartView kLineChartView;
    KLineChartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        kLineChartView = findViewById(R.id.kLineChartView);
        adapter = new KLineChartAdapter();
        kLineChartView.setDateTimeFormatter(new DateFormatter());
        kLineChartView.setOverScrollRange(138);
        kLineChartView.setGridRows(4);
        kLineChartView.setGridColumns(4);
        kLineChartView.setAdapter(adapter);

        kLineChartView.justShowLoading();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<KLineEntity> data = DataRequest.getALL(MainActivity.this);
                DataHelper.calculate(data);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addFooterData(data);
                        adapter.notifyDataSetChanged();
                        //kLineChartView.scrollBy(-200, 0);
                        kLineChartView.startAnimation();
                        kLineChartView.refreshEnd();
                    }
                });
            }
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
    private TextView K_line;
    private TextView approx;
    private TextView apprise;
    private TextView app_zuigao;
    private TextView app_zuidi;
    private TextView app_liang;
    private LinearLayout amountL;
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
    private TextView mainHide;
    private RadioGroup k_Group;

    private PopupWindow timePopup;
    private TextView textViewSve;
    private TextView textViewNine;
    private TextView textViewSex;
    private TextView textViewFar;
    private void initView() {
        k_Price = findViewById(R.id.k_Price);
        K_line = findViewById(R.id.K_line);
        approx = findViewById(R.id.approx);
        apprise = findViewById(R.id.apprise);
        app_zuigao = findViewById(R.id.app_zuigao);
        app_zuidi = findViewById(R.id.app_zuidi);
        app_liang = findViewById(R.id.app_liang);
        amountL = findViewById(R.id.amountL);
        text_zuigao = findViewById(R.id.text_zuigao);
        text_zuidi = findViewById(R.id.text_zuidi);
        text_liang = findViewById(R.id.text_liang);
        tv_text_fen0 = findViewById(R.id.tv_text_fen0);
        tv_text_fen1 = findViewById(R.id.tv_text_fen1);
        tv_text_fen5 = findViewById(R.id.tv_text_fen5);
        tv_text_fen15 = findViewById(R.id.tv_text_fen15);
        tv_text_fen30 = findViewById(R.id.tv_text_fen30);
        time_all = findViewById(R.id.time_all);
        k_index = findViewById(R.id.k_index);
        tab_layout = findViewById(R.id.tab_layout);
        tab_View = findViewById(R.id.tab_View);

        initIndexPopup();
        initTimePopView();

        findViewById(R.id.btn_back).setOnClickListener(o -> {
            finish();
        });

        tv_text_fen0.setOnClickListener(o -> // 分时
        {
            timeSwitch("0", tv_text_fen0);
        });
        tv_text_fen1.setOnClickListener(o -> // 分时K线：点击1分钟
        {
            timeSwitch("1", tv_text_fen1);
        });
        tv_text_fen5.setOnClickListener(o -> // 分时K线：点击5分钟
        {
            timeSwitch("5", tv_text_fen5);
        });
        tv_text_fen15.setOnClickListener(o -> // 分时K线：点击15分钟
        {
            timeSwitch("15", tv_text_fen15);
        });
        tv_text_fen30.setOnClickListener(o -> // 分时K线：点击30分钟
        {
            timeSwitch("30", tv_text_fen30);
        });
        time_all.setOnClickListener(o -> // 分时K线：点击更多
        {
            timePopup.showAsDropDown(time_all);
        });
        k_index.setOnClickListener(o -> // 分时K线：点击指标
        {
            popupWindow.showAsDropDown(k_index);
        });
    }


    private void initIndexPopup() {
        final View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.kc_index_popup, null);

        maText = contentView.findViewById(R.id.maText);
        bollText = contentView.findViewById(R.id.bollText);
        mainHide = contentView.findViewById(R.id.mainHide);
        maText.setOnClickListener(this);
        bollText.setOnClickListener(this);
        mainHide.setOnClickListener(this);

        k_Group = contentView.findViewById(R.id.k_Group);

        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setTouchable(true);
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
                } else if (k_Group.getCheckedRadioButtonId() == R.id.subHide) {
                    subIndex = -1;
                    kLineChartView.hideChildDraw();
                }
                radioButton.setChecked(true);
            }
        });
    }

    private void initTimePopView() {
        final View timeView = LayoutInflater.from(MainActivity.this).inflate(R.layout.kc_time_popup, null);
        timePopup = new PopupWindow(timeView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        timePopup.setBackgroundDrawable(new ColorDrawable(0x00000000));
        timePopup.setOutsideTouchable(true);
        timePopup.setTouchable(true);

        textViewSve = timeView.findViewById(R.id.textViewSve);
        textViewNine = timeView.findViewById(R.id.textViewNine);
        textViewSex = timeView.findViewById(R.id.textViewSex);
        textViewFar = timeView.findViewById(R.id.textViewFar);
        textViewSve.setOnClickListener(this);
        textViewNine.setOnClickListener(this);
        textViewSex.setOnClickListener(this);
        textViewFar.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //60, 240, 1D, 1W
        int id = v.getId();
        if (id == R.id.textViewSve) {
            //60分钟
            timeSwitch("60", textViewSve);
        } else if (id == R.id.textViewNine) {
            //4小时
            timeSwitch("240", textViewNine);
        } else if (id == R.id.textViewSex) {
            //日线
            timeSwitch("1D", textViewSex);
        } else if (id == R.id.textViewFar) {
            //周线
            timeSwitch("1W", textViewFar);
        } else if (id == R.id.maText) {
            //主图 MA线
            if (mainIndex != 0) {
                kLineChartView.hideSelectData();
                mainIndex = 0;
                maText.setTextColor(Color.parseColor("#eeb350"));
                bollText.setTextColor(Color.WHITE);
                kLineChartView.changeMainDrawType(Status.MA);
            }
        } else if (id == R.id.bollText) {
            //主图 BOLL线
            if (mainIndex != 1) {
                kLineChartView.hideSelectData();
                mainIndex = 1;
                bollText.setTextColor(Color.parseColor("#eeb350"));
                maText.setTextColor(Color.WHITE);
                kLineChartView.changeMainDrawType(Status.BOLL);
            }
        } else if (id == R.id.mainHide) {
            //主图 隐藏
            if (mainIndex != -1) {
                kLineChartView.hideSelectData();
                mainIndex = -1;
                bollText.setTextColor(Color.WHITE);
                maText.setTextColor(Color.WHITE);
                mainHide.setTextColor(Color.parseColor("#1e82d2"));
                kLineChartView.changeMainDrawType(Status.NONE);
            }
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

        switch (interval) {
            case "0"://分时
                resetFenShiColor();
                selectTextView.setTextColor(Color.parseColor("#1e82d2"));
                kLineChartView.hideSelectData();
                kLineChartView.setMainDrawLine(true);
                break;
            case "1"://1分钟
            case "1D"://1天
            case "1W"://7天
            case "5"://5分钟
            case "15"://15分钟
            case "30"://30分钟
            case "60"://60分钟
            case "240"://4小时
                resetFenShiColor();
                selectTextView.setTextColor(Color.parseColor("#1e82d2"));
                kLineChartView.hideSelectData();
                kLineChartView.setMainDrawLine(false);
                break;
        }
    }
    /**
     * 重置副图文字颜色
     */
    private void resetFenShiColor() {
        tv_text_fen0.setTextColor(this.getResources().getColor(R.color.F888888));
        tv_text_fen1.setTextColor(this.getResources().getColor(R.color.F888888));
        tv_text_fen5.setTextColor(this.getResources().getColor(R.color.F888888));
        tv_text_fen15.setTextColor(this.getResources().getColor(R.color.F888888));
        tv_text_fen30.setTextColor(this.getResources().getColor(R.color.F888888));
        /******************/
        textViewSve.setTextColor(this.getResources().getColor(R.color.F888888));
        textViewNine.setTextColor(this.getResources().getColor(R.color.F888888));
        textViewSex.setTextColor(this.getResources().getColor(R.color.F888888));
        textViewFar.setTextColor(this.getResources().getColor(R.color.F888888));
    }

}