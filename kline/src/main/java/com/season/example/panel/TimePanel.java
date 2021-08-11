package com.season.example.panel;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.season.klinechart.KLineChartView;
import com.season.klinechart.draw.Status;
import com.season.example.net.WebSocketService;
import com.season.mylibrary.R;


public abstract class TimePanel {

    final int[] textIdsPop = {R.id.tv_text_fen0, R.id.tv_text_fen1, R.id.tv_text_fen5, R.id.tv_text_fen30, R.id.tv_text_week};
    final String[] intervalsPop = {"-1", "1", "5", "30", "1W"};

    final int[] textIds = {R.id.tv_text_fen15, R.id.tv_text_hour1, R.id.tv_text_hour4, R.id.tv_text_day};
    final String[] intervals = {"15", "60", "240", "1D"};

    private final KLineChartView kLineChartView;
    private final Activity context;

    public TimePanel(Activity context, KLineChartView kLineChartView) {
        this.context = context;
        this.kLineChartView = kLineChartView;
    }


    public String currentTimeSwitch = "-10086";//当前显示的分时类型   默认1分钟
    public void timeSwitch(String interval) {
        if (currentTimeSwitch != interval) {
            currentTimeSwitch = interval;
            WebSocketService.getInstance().req(interval);
        }
        resetView(interval);
    }

    private void resetView(String interval){
        kLineChartView.hideSelectData();

        for (int i = 0; i < intervals.length; i++) {
            TextView textView = findViewById(textIds[i]);
            if (interval.equals(intervals[i])) {
                textView.setTextColor(getResources().getColor(R.color.chart_line));
                time_all.setText(getResources().getString(R.string.more));
                time_all.setTextColor(getResources().getColor(R.color.chart_text));
            } else {
                textView.setTextColor(getResources().getColor(R.color.chart_text));
            }
        }

        for (int i = 0; i < intervalsPop.length; i++) {
            TextView textView = timeView.findViewById(textIdsPop[i]);
            if (interval.equals(intervalsPop[i])) {
                textView.setTextColor(getResources().getColor(R.color.chart_line));
                textView.setBackgroundResource(R.drawable.chart_btn_deep_stroke);
                time_all.setText(textView.getText().toString());
                time_all.setTextColor(getResources().getColor(R.color.chart_line));
            } else {
                textView.setTextColor(getResources().getColor(R.color.chart_text));
                textView.setBackgroundResource(R.drawable.chart_btn_deep);
            }
        }
        if ("depth".equals(interval)){
            k_depth.setTextColor(getResources().getColor(R.color.chart_line));
        }else{
            k_depth.setTextColor(getResources().getColor(R.color.chart_text));
        }
        if ("-1".equals(interval)) {
            kLineChartView.setMainDrawLine(true);
        } else {
            kLineChartView.setMainDrawLine(false);
        }
    }


    View timeView;
    View timeTopView;
    View timeBackgroundView;

    private void initTimePopView() {
        timeView = findViewById(R.id.timePanelExtra);
        timeTopView = findViewById(R.id.time_top);
        timeBackgroundView = findViewById(R.id.time_background);
        timeView.setOnClickListener(v -> {
            switchTimePop(false);
        });
        for (int i = 0; i < textIdsPop.length; i++) {
            TextView textView = timeView.findViewById(textIdsPop[i]);
            int finalI = i;
            textView.setOnClickListener(v -> {
                switchTimePop(false);
                getDepthMapView().setVisibility(View.GONE);
                getDepthTopView().setVisibility(View.GONE);
                ValueAnimator animator = ValueAnimator.ofInt(((LinearLayout.LayoutParams) k_line.getLayoutParams()).leftMargin, 4 * itemWidth + itemWidth / 4);
                animator.addUpdateListener(animator1 -> {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) k_line.getLayoutParams();
                    params.leftMargin = (Integer) animator1.getAnimatedValue();
                    k_line.requestLayout();
                });
                animator.setDuration(200);
                animator.start();
                timeSwitch(intervalsPop[finalI]);
            });
        }
    }

    private ValueAnimator animator;
    private boolean switchTimePop(boolean show) {
        if (timeView.getVisibility() == View.GONE && !show) {
            return true;
        }
        if (animator != null) {
            float currentValue = (float) animator.getAnimatedValue();
            animator.pause();
            animator.cancel();
            if (show) {
                animator = ValueAnimator.ofFloat(currentValue, 0);
            } else {
                animator = ValueAnimator.ofFloat(currentValue, -1);
            }
        } else {
            if (show) {
                animator = ValueAnimator.ofFloat(-1, 0);
            } else {
                animator = ValueAnimator.ofFloat(0, -1);
            }
        }
        animator.addUpdateListener(animator1 -> {
            float currentValue = (float) animator.getAnimatedValue();
            timeBackgroundView.setAlpha(0.5f + currentValue);
            timeTopView.setTranslationY(currentValue * 50 * context.getResources().getDisplayMetrics().density);
        });
        animator.setDuration(360);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                timeView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                timeView.setVisibility(show ? View.VISIBLE : View.GONE);
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        return false;
    }


    private boolean switchIndexPop(boolean show) {
        if (indexView.getVisibility() == View.GONE && !show) {
            return true;
        }
        if (animator != null) {
            float currentValue = (float) animator.getAnimatedValue();
            animator.pause();
            animator.cancel();
            if (show) {
                animator = ValueAnimator.ofFloat(currentValue, 0);
            } else {
                animator = ValueAnimator.ofFloat(currentValue, -1);
            }
        } else {
            if (show) {
                animator = ValueAnimator.ofFloat(-1, 0);
            } else {
                animator = ValueAnimator.ofFloat(0, -1);
            }
        }
        animator.addUpdateListener(animator1 -> {
            float currentValue = (float) animator.getAnimatedValue();
            indexBackgroundView.setAlpha(0.5f + currentValue);
            indexTopView.setTranslationY(currentValue * 148 * context.getResources().getDisplayMetrics().density);
        });
        animator.setDuration(360);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                indexView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                indexView.setVisibility(show ? View.VISIBLE : View.GONE);
                animator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
        return false;
    }

    private TextView maText;
    private TextView bollText;
    private RadioGroup k_Group;

    private void resetMaBoll(int index) {
        maText.setTextColor(getResources().getColor(index == 0 ? R.color.chart_line : R.color.chart_text));
        maText.setBackgroundResource(index == 0 ? R.drawable.chart_btn_deep_stroke : R.drawable.chart_btn_deep);

        bollText.setTextColor(getResources().getColor(index == 1 ? R.color.chart_line : R.color.chart_text));
        bollText.setBackgroundResource(index == 1 ? R.drawable.chart_btn_deep_stroke : R.drawable.chart_btn_deep);

        if (index == 0) {
            kLineChartView.changeMainDrawType(Status.MA);
        } else if (index == 1) {
            kLineChartView.changeMainDrawType(Status.BOLL);
        } else {
            kLineChartView.changeMainDrawType(Status.NONE);
        }
    }

    // 主图指标下标
    private int mainIndex = 0;
    // 副图指标下标
    private int subIndex = -1;

    View indexView;
    View indexTopView;
    View indexBackgroundView;

    private void initIndexPopup() {
        indexView = findViewById(R.id.indexPanelExtra);
        indexTopView = findViewById(R.id.index_top);
        indexBackgroundView = findViewById(R.id.index_background);

        indexView.setOnClickListener(v -> {
            switchIndexPop(false);
        });

        maText = findViewById(R.id.maText);
        bollText = findViewById(R.id.bollText);
        maText.setOnClickListener(v -> {
            kLineChartView.hideSelectData();
            //主图 MA线
            if (mainIndex != 0) {
                mainIndex = 0;
            } else {
                mainIndex = -1;
            }
            resetMaBoll(mainIndex);
        });
        bollText.setOnClickListener(v -> {
            kLineChartView.hideSelectData();
            //主图 BOLL线
            if (mainIndex != 1) {
                mainIndex = 1;
            } else {
                mainIndex = -1;
            }
            resetMaBoll(mainIndex);
        });
        mainIndex = 0;
        resetMaBoll(mainIndex);

        k_Group = findViewById(R.id.k_Group);

        k_Group.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton radioButton = findViewById(k_Group.getCheckedRadioButtonId());
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
        });
        k_Group.check(R.id.macdText);
    }


    private Resources getResources() {
        return context.getResources();
    }

    public <T extends View> T findViewById(int id) {
        return context.findViewById(id);
    }


    private View k_line;
    private TextView time_all;
    private TextView k_index;
    private TextView k_depth;
    private int itemWidth;

    public TimePanel act() {
        time_all = findViewById(R.id.time_all);
        k_index = findViewById(R.id.k_index);
        k_depth = findViewById(R.id.k_depth);
        k_line = findViewById(R.id.k_line);
        initIndexPopup();
        initTimePopView();
        itemWidth = context.getResources().getDisplayMetrics().widthPixels / 7;
        for (int i = 0; i < textIds.length; i++) {
            TextView textView = findViewById(textIds[i]);
            int finalI = i;
            textView.setOnClickListener(v -> {
                switchTimePop(false);
                switchIndexPop(false);
                getDepthMapView().setVisibility(View.GONE);
                getDepthTopView().setVisibility(View.GONE);
                ValueAnimator animator = ValueAnimator.ofInt(((LinearLayout.LayoutParams) k_line.getLayoutParams()).leftMargin, finalI * itemWidth + itemWidth / 4);
                animator.addUpdateListener(animator1 -> {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) k_line.getLayoutParams();
                    params.leftMargin = (Integer) animator1.getAnimatedValue();
                    ;
                    k_line.requestLayout();
                });
                animator.setDuration(200);
                animator.start();
                timeSwitch(intervals[finalI]);
            });
        }

        k_line.post(() -> {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) k_line.getLayoutParams();
            params.width = itemWidth / 2;
            params.leftMargin = itemWidth / 4;
            k_line.requestLayout();
        });
        k_depth.setOnClickListener(o -> // 分时K线：点击更多
        {
            if (getDepthMapView().getVisibility() == View.VISIBLE){
                return;
            }
            getDepthMapView().setVisibility(View.VISIBLE);
            getDepthTopView().setVisibility(View.VISIBLE);
            resetView("depth");
            ValueAnimator animator = ValueAnimator.ofInt(((LinearLayout.LayoutParams) k_line.getLayoutParams()).leftMargin, 5 * itemWidth + itemWidth / 4);
            animator.addUpdateListener(animator1 -> {
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) k_line.getLayoutParams();
                params.leftMargin = (Integer) animator1.getAnimatedValue();
                k_line.requestLayout();
            });
            animator.setDuration(200);
            animator.start();
            switchIndexPop(false);
            switchTimePop(false);
        });
        time_all.setOnClickListener(o -> // 分时K线：点击更多
        {
            if (switchIndexPop(false))
                switchTimePop(timeView.getVisibility() == View.VISIBLE ? false : true);
        });
        k_index.setOnClickListener(o -> // 分时K线：点击指标
        {
            if (switchTimePop(false))
                switchIndexPop(indexView.getVisibility() == View.VISIBLE ? false : true);
        });
        return this;
    }

    protected abstract View getDepthMapView();
    protected abstract View getDepthTopView();
}
