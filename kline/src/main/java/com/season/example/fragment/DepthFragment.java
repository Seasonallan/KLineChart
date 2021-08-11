package com.season.example.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.season.example.util.CoinCodeDecimalUtil;
import com.season.klinechart.DepthDataBean;
import com.season.klinechart.ColorStrategy;
import com.season.mylibrary.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DepthFragment extends Fragment {

    RecyclerView recycler_view;

    private List<DepthDataBean> buyItems = new ArrayList<>();
    private List<DepthDataBean> sellItems = new ArrayList<>();
    private DealRecordAdapter myAdapter;


    public static DepthFragment getInstance() {
        Bundle args = new Bundle();
        DepthFragment fragment = new DepthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.kc_fragment_deal, null);

        recycler_view = parent.findViewById(R.id.recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);

        for (int i = 0; i < 20; i++) {
            buyItems.add(new DepthDataBean());
            sellItems.add(new DepthDataBean());
        }
        myAdapter = new DealRecordAdapter(getContext(), buyItems, sellItems);
        recycler_view.setAdapter(myAdapter);

        return parent;
    }

    public String coinCode = "";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onRecordChange(List<DepthDataBean> buyData, List<DepthDataBean> sellData) {
        buyItems.clear();
        for (int i = 0; i < buyData.size(); i++) {
            buyItems.add(0, buyData.get(i));
        }
        sellItems.clear();
        sellItems.addAll(sellData);

        myAdapter.notifyDataSetChanged();
    }


    public class DealRecordAdapter extends RecyclerView.Adapter<DealRecordAdapter.DealHolder> {

        List<DepthDataBean> buyData, sellData;
        Context context;
        private DecimalFormat dfCoinNumber;//币种数量小数位限制
        private DecimalFormat dfCoinPrice;//币种价格小数位限制

        DealRecordAdapter(Context context, List<DepthDataBean> buyData, List<DepthDataBean> sellData) {
            this.buyData = buyData;
            this.sellData = sellData;
            this.context = context;
            this.dfCoinPrice = new DecimalFormat(CoinCodeDecimalUtil.getDecimalFormatPrice(coinCode));//币种价格小数位限制
            this.dfCoinNumber = new DecimalFormat(CoinCodeDecimalUtil.getDecimalFormatNumber(coinCode));//币种数量小数位限制
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public DealRecordAdapter.DealHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(ColorStrategy.getStrategy().isBlackTheme()?(viewType == 0 ? R.layout.kc_item_order_record0 :
                    R.layout.kc_item_order_record):viewType == 0 ? R.layout.kc_item_order_record0_white :
                    R.layout.kc_item_order_record_white, parent, false);
            DealRecordAdapter.DealHolder holder = new DealRecordAdapter.DealHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(DealRecordAdapter.DealHolder holder, int position) {
            if (position == 0) {
                return;
            }
            holder.price_buy_list.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getRiseColor()));
            holder.price_sell_list.setTextColor(context.getResources().getColor(ColorStrategy.getStrategy().getFallColor()));
            int realPosition = position - 1;
            if (buyData.size() > realPosition) {
                DepthDataBean item = buyData.get(realPosition);
                if (item.getPrice() == -1) {
                    holder.index_buy_list.setText("");
                    holder.number_buy_list.setText("");
                    holder.price_buy_list.setText("");
                } else {
                    holder.index_buy_list.setText(position + "");
                    holder.number_buy_list.setText(dfCoinNumber.format(item.getVolume()));
                    holder.price_buy_list.setText(dfCoinPrice.format(item.getPrice()));
                }
            } else {
                holder.index_buy_list.setText("");
                holder.number_buy_list.setText("");
                holder.price_buy_list.setText("");
            }

            if (sellData.size() > realPosition) {
                DepthDataBean item = sellData.get(realPosition);
                if (item.getPrice() == -1) {
                    holder.price_sell_list.setText("");
                    holder.number_sell_list.setText("");
                    holder.index_sell_list.setText("");
                } else {
                    holder.index_sell_list.setText(position + "");
                    holder.number_sell_list.setText(dfCoinNumber.format(item.getVolume()));
                    holder.price_sell_list.setText(dfCoinPrice.format(item.getPrice()));
                }
            } else {
                holder.price_sell_list.setText("");
                holder.number_sell_list.setText("");
                holder.index_sell_list.setText("");
            }
        }

        @Override
        public int getItemCount() {
            return buyData.size() + 1;
        }


        class DealHolder extends RecyclerView.ViewHolder {
            public TextView index_buy_list, number_buy_list, price_buy_list, price_sell_list, number_sell_list, index_sell_list;

            public DealHolder(@NonNull View itemView) {
                super(itemView);
                index_buy_list = itemView.findViewById(R.id.index_buy_list);
                number_buy_list = itemView.findViewById(R.id.number_buy_list);
                price_buy_list = itemView.findViewById(R.id.price_buy_list);
                price_sell_list = itemView.findViewById(R.id.price_sell_list);
                number_sell_list = itemView.findViewById(R.id.number_sell_list);
                index_sell_list = itemView.findViewById(R.id.index_sell_list);
            }

        }

    }

}
