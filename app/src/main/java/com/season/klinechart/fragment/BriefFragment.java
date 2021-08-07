package com.season.klinechart.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.season.klinechart.R;

/**
 * 简介
 */
public class BriefFragment extends Fragment {
    private static final String TAG = "BriefFragment";

    //    public static final String URL_BRIEF = "https://" + KChartConstant.apiHost + "/api/klinevtwo/tradingview?";
    TextView text_one;
    TextView app_two;
    TextView text_two;
    TextView app_three;
    TextView text_three;
    TextView app_for;
    TextView text_for;
    TextView app_see;
    TextView text_see;
    TextView app_sve;
    TextView text_sve;
    TextView app_fiv;
    TextView text_fiv;
    TextView app_night;
    TextView text_night;
    TextView app_nie;
    TextView text_nie;

    private String url = "";
    private String coinCode = "";
    private String langCode = "";

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


        return parent;
    }


    private void initView() {
        text_one = getActivity().findViewById(R.id.text_one);
        app_two = getActivity().findViewById(R.id.app_two);
        text_two = getActivity().findViewById(R.id.text_two);
        app_three = getActivity().findViewById(R.id.app_three);
        text_three = getActivity().findViewById(R.id.text_three);
        app_for = getActivity().findViewById(R.id.app_for);
        text_for = getActivity().findViewById(R.id.text_for);
        app_see = getActivity().findViewById(R.id.app_see);
        text_see = getActivity().findViewById(R.id.text_see);
        app_sve = getActivity().findViewById(R.id.app_sve);
        text_sve = getActivity().findViewById(R.id.text_sve);
        app_fiv = getActivity().findViewById(R.id.app_fiv);
        text_fiv = getActivity().findViewById(R.id.text_fiv);
        app_night = getActivity().findViewById(R.id.app_night);
        text_night = getActivity().findViewById(R.id.text_night);
        app_nie = getActivity().findViewById(R.id.app_nie);
        text_nie = getActivity().findViewById(R.id.text_nie);
    }
}
