package com.season.example.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.season.example.net.SimpleRequest;
import com.season.klinechart.ColorStrategy;
import com.season.mylibrary.R;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BriefFragment extends Fragment {

    TextView titleView;
    TextView timeView;
    TextView amountView;
    TextView totalView;
    TextView priceView;
    TextView bookView;
    TextView webView;
    TextView blockView;
    TextView introView;

    public String coinCode = "";
    public String langCode = "";
    public String briefUrl = "";

    public static BriefFragment getInstance() {
        Bundle args = new Bundle();
        BriefFragment fragment = new BriefFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(ColorStrategy.getStrategy().isBlackTheme()?R.layout.kc_fragment_brief:R.layout.kc_fragment_brief_white, null);
        initView(parent);
        new Thread(() -> {
            try {
                String res = SimpleRequest.getRequest(briefUrl + "?symbol=" + coinCode + "&langCode=" + langCode);
                JSONObject jsonObject = new JSONObject(res).getJSONObject("exProduct");
                if (jsonObject.has("coinCode") && jsonObject.has("name"))
                    titleView.setText(jsonObject.getString("name") + "(" + jsonObject.getString("coinCode") + ")");
                if (jsonObject.has("issueTime"))
                    timeView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(jsonObject.getLong("issueTime"))));
                DecimalFormat dfRMB = new DecimalFormat("#,###");
                if (jsonObject.has("totalNum"))
                    amountView.setText(dfRMB.format(new BigDecimal(jsonObject.getString("totalNum"))));

                if (jsonObject.has("stock"))
                    totalView.setText(dfRMB.format(new BigDecimal(jsonObject.getString("stock"))));

                priceView.setText(jsonObject.has("crowdfundingPrice") ? jsonObject.getString("crowdfundingPrice") : "");
                bookView.setText(jsonObject.has("writeBook") ? jsonObject.getString("writeBook") : "");
                webView.setText(jsonObject.has("officialWebsite") ? jsonObject.getString("officialWebsite") : "");
                blockView.setText(jsonObject.has("blockBrowser") ? jsonObject.getString("blockBrowser") : "");
                introView.setText(jsonObject.has("productReferral") ? jsonObject.getString("productReferral") : "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return parent;
    }


    private void initView(View parentView) {
        titleView = parentView.findViewById(R.id.brief_tv_title);
        timeView = parentView.findViewById(R.id.brief_tv_time);
        amountView = parentView.findViewById(R.id.brief_tv_amount);
        totalView = parentView.findViewById(R.id.brief_tv_total);
        priceView = parentView.findViewById(R.id.brief_tv_price);
        bookView = parentView.findViewById(R.id.brief_tv_book);
        webView = parentView.findViewById(R.id.brief_tv_web);
        blockView = parentView.findViewById(R.id.brief_tv_block);
        introView = parentView.findViewById(R.id.brief_tv_intro);

        bookView.setOnClickListener(v -> {
            openBrowser(bookView.getText().toString());
        });
        blockView.setOnClickListener(v -> {
            openBrowser(blockView.getText().toString());
        });
        webView.setOnClickListener(v -> {
            openBrowser(webView.getText().toString());
        });

        parentView.findViewById(R.id.text_sve_copy).setOnClickListener(v -> {
            copy(bookView.getText().toString());
        });
        parentView.findViewById(R.id.text_night_copy).setOnClickListener(v -> {
            copy(blockView.getText().toString());
        });
        parentView.findViewById(R.id.text_fiv_copy).setOnClickListener(v -> {
            copy(webView.getText().toString());
        });
    }

    public void openBrowser(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            getContext().startActivity(Intent.createChooser(intent, "Select Browser"));
        } else {
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        }
    }

    public void copy(String data) {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, data);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getContext(), "copied", Toast.LENGTH_SHORT).show();
    }
}
