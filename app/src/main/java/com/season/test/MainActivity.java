package com.season.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.season.example.KLineChartActivity;
import com.season.klinechart.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KLineChartActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("coinCode", "BTC_USDT");
                bundle.putString("language", "ZH_CN");
                bundle.putString("webSocketUrl", Configure.socketUrl);
                bundle.putString("briefUrl", Configure.briefUrl);

                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.tv_start1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KLineChartActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("coinCode", "ETH_USDT");
                bundle.putString("language", "EN");
                bundle.putString("webSocketUrl", Configure.socketUrlPre);
                bundle.putString("briefUrl", Configure.briefUrlPre);

                intent.putExtra("bundle", bundle);
                startActivityForResult(intent, 100);
            }
        });

        findViewById(R.id.tv_start2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, KLineChartActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("coinCode", "ETH_USDT");
                bundle.putString("language", "FRAS");
                bundle.putString("webSocketUrl", Configure.socketUrlPre);
                bundle.putString("briefUrl", Configure.briefUrlPre);

                intent.putExtra("bundle", bundle);

                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String backType = data.getStringExtra("backType");
            switch (backType) {
                case "买入":
//                    this.result.success("1");
                    Log.e("TAG", "1");
                    break;
                case "卖出":
//                    this.result.success("2");
                    Log.e("TAG", "2");
                    break;
                default:
                    Log.e("TAG", "0");
                    break;
            }
        }
    }
}