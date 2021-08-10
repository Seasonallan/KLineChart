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
import com.season.klinechart.ColorStrategy;
import com.season.mylibrary.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DealFragment extends Fragment {
    RecyclerView recycler_view;

    private List<DealRecord> items = new ArrayList<>();
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_view.setLayoutManager(linearLayoutManager);

        for (int i = 0; i < 20; i++) {
            items.add(new DealRecord());
        }
        myAdapter = new DealRecordAdapter(getContext(), items);
        recycler_view.setAdapter(myAdapter);

        return parent;
    }

    public String coinCode = "";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onRecordChange(List<DealRecord> list) {
        for (int i = 0; i < list.size(); i++) {
            items.add(0, list.get(i));
            items.remove(items.size() - 1);
        }
        myAdapter.notifyDataSetChanged();
    }


    public class DealRecordAdapter extends RecyclerView.Adapter<DealRecordAdapter.DealHolder> {

        List<DealRecord> list;
        Context context;
        private DecimalFormat dfCoinNumber;//币种数量小数位限制
        private DecimalFormat dfCoinPrice;//币种价格小数位限制

        DealRecordAdapter(Context context, List<DealRecord> list) {
            this.list = list;
            this.context = context;
            this.dfCoinPrice = new DecimalFormat(CoinCodeDecimalUtil.getDecimalFormatPrice(coinCode));//币种价格小数位限制
            this.dfCoinNumber = new DecimalFormat(CoinCodeDecimalUtil.getDecimalFormatNumber(coinCode));//币种数量小数位限制
        }

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? 0 : 1;
        }

        @Override
        public DealHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(viewType == 0 ? R.layout.kc_item_deal_record0 :
                    R.layout.kc_item_deal_record, parent, false);
            DealHolder holder = new DealHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(DealRecordAdapter.DealHolder holder, int position) {
            if (position == 0) {
                return;
            }
            DealRecord item = list.get(position - 1);
            if (item.getId() == -1) {
                holder.timeView.setText("");
                holder.directionView.setText("");
                holder.priceView.setText("");
                holder.numView.setText("");
            } else {
                holder.timeView.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(item.getTime() * 1000L)));
                holder.directionView.setText(context.getResources().getString(item.getDirection() == 1 ? R.string.buy : R.string.sale));
                holder.directionView.setTextColor(context.getResources().getColor(item.getDirection() == 1 ?
                        ColorStrategy.getStrategy().getRiseColor() : ColorStrategy.getStrategy().getFallColor()));
                holder.priceView.setText(dfCoinPrice.format(Double.parseDouble(item.getPrice())));
                holder.numView.setText(dfCoinNumber.format(item.getAmount()));
            }
        }

        @Override
        public int getItemCount() {
            return list.size() + 1;
        }


        class DealHolder extends RecyclerView.ViewHolder {
            public TextView timeView, directionView, priceView, numView;

            public DealHolder(@NonNull View itemView) {
                super(itemView);
                timeView = itemView.findViewById(R.id.time_list);
                directionView = itemView.findViewById(R.id.direction_list);
                priceView = itemView.findViewById(R.id.price_list);
                numView = itemView.findViewById(R.id.num_list);
            }

        }

    }

}
