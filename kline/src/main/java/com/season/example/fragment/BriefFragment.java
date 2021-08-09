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
import com.season.mylibrary.R;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BriefFragment extends Fragment {

    TextView text_one;
    TextView text_two;
    TextView text_three;
    TextView text_for;
    TextView text_see;
    TextView text_sve;
    TextView text_fiv;
    TextView text_night;
    TextView text_nie;

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
        View parent = inflater.inflate(R.layout.kc_fragment_brief, null);
        initView(parent);
        new Thread(() -> {
            try {
                String res = SimpleRequest.getRequest(briefUrl + "?symbol=" + coinCode + "&langCode=" + langCode);
                JSONObject jsonObject = new JSONObject(res).getJSONObject("exProduct");
                if (jsonObject.has("coinCode") && jsonObject.has("name"))
                    text_one.setText(jsonObject.getString("name") + "(" + jsonObject.getString("coinCode") + ")");
                if (jsonObject.has("issueTime"))
                    text_two.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(jsonObject.getLong("issueTime"))));
                DecimalFormat dfRMB = new DecimalFormat("#,###");
                if (jsonObject.has("totalNum"))
                    text_three.setText(dfRMB.format(new BigDecimal(jsonObject.getString("totalNum"))));

                if (jsonObject.has("stock"))
                    text_for.setText(dfRMB.format(new BigDecimal(jsonObject.getString("stock"))));

                text_see.setText(jsonObject.has("crowdfundingPrice") ? jsonObject.getString("crowdfundingPrice") : "");
                text_sve.setText(jsonObject.has("writeBook") ? jsonObject.getString("writeBook") : "");
                text_fiv.setText(jsonObject.has("officialWebsite") ? jsonObject.getString("officialWebsite") : "");
                text_night.setText(jsonObject.has("blockBrowser") ? jsonObject.getString("blockBrowser") : "");
                text_nie.setText(jsonObject.has("productReferral") ? jsonObject.getString("productReferral") : "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return parent;
    }


    private void initView(View parentView) {
        text_one = parentView.findViewById(R.id.text_one);
        text_two = parentView.findViewById(R.id.text_two);
        text_three = parentView.findViewById(R.id.text_three);
        text_for = parentView.findViewById(R.id.text_for);
        text_see = parentView.findViewById(R.id.text_see);
        text_sve = parentView.findViewById(R.id.text_sve);
        text_fiv = parentView.findViewById(R.id.text_fiv);
        text_night = parentView.findViewById(R.id.text_night);
        text_nie = parentView.findViewById(R.id.text_nie);

        text_sve.setOnClickListener(v -> {
            openBrowser(text_sve.getText().toString());
        });
        text_night.setOnClickListener(v -> {
            openBrowser(text_night.getText().toString());
        });
        text_fiv.setOnClickListener(v -> {
            openBrowser(text_fiv.getText().toString());
        });

        parentView.findViewById(R.id.text_sve_copy).setOnClickListener(v -> {
            copy(text_sve.getText().toString());
        });
        parentView.findViewById(R.id.text_night_copy).setOnClickListener(v -> {
            copy(text_night.getText().toString());
        });
        parentView.findViewById(R.id.text_fiv_copy).setOnClickListener(v -> {
            copy(text_fiv.getText().toString());
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
        Toast.makeText(getContext(), "copy successfully", Toast.LENGTH_SHORT).show();
    }
}
