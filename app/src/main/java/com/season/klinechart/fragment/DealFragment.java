package com.season.klinechart.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.season.klinechart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 成交
 */
public class DealFragment extends Fragment {
    private static final String TAG = "DealFragment";
    TextView text_time;
    TextView text_fangxiang;
    TextView text_jiage;
    TextView text_shuliang;
    RecyclerView recycler_view;
    LinearLayout empty_view;

    private List<DealRecordAdapterBean> items = new ArrayList<>();
    private DealRecordAdapter myAdapter;

    public static DealFragment getInstance() {
        Bundle args = new Bundle();
        DealFragment fragment = new DealFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.kc_fragment_deal, null);

        recycler_view = parent.findViewById(R.id.recycler_view);
        empty_view = parent.findViewById(R.id.empty_view);

        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setFocusable(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayoutManager);
        myAdapter = new DealRecordAdapter(items);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recycler_view.setAdapter(myAdapter);

        return parent;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
