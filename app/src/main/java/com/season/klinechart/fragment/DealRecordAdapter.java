package com.season.klinechart.fragment;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by 火龙裸 on 2020/8/29 0010.
 */
public class DealRecordAdapter extends RecyclerView.Adapter<DealRecordAdapter.DealHolder> {

    DealRecordAdapter(List<DealRecordAdapterBean> list){

    }

    @Override
    public DealHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(DealRecordAdapter.DealHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class DealHolder extends RecyclerView.ViewHolder {


        public DealHolder(@NonNull View itemView) {
            super(itemView);
        }

    }

}
