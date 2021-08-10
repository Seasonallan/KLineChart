package com.season.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.season.example.KLineChartActivity;
import com.season.klinechart.R;
import com.season.klinechart.ColorStrategy;


public class MainActivity extends AppCompatActivity {

    RadioGroup envRadioGroup;
    RadioGroup colorRadioGroup;
    RadioGroup languageRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        envRadioGroup = findViewById(R.id.rg_environment);
        colorRadioGroup = findViewById(R.id.rg_color);
        languageRadioGroup = findViewById(R.id.rg_language);

        findViewById(R.id.tv_start).setOnClickListener(view -> onButtonClicked(view));
        findViewById(R.id.tv_start1).setOnClickListener(view -> onButtonClicked(view));
        findViewById(R.id.tv_start2).setOnClickListener(view -> onButtonClicked(view));
    }

    private void onButtonClicked(View view) {
        Intent intent = new Intent(MainActivity.this, KLineChartActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("coinCode", view.getTag().toString());
        bundle.putString("language", findViewById(languageRadioGroup.getCheckedRadioButtonId()).getTag().toString());
        bundle.putBoolean("riseGreen", colorRadioGroup.getCheckedRadioButtonId() == R.id.rb_color1);

        bundle.putString("webSocketUrl", envRadioGroup.getCheckedRadioButtonId() == R.id.rb_environment1 ? Configure.socketUrl
                : envRadioGroup.getCheckedRadioButtonId() == R.id.rb_environment2 ? Configure.socketUrlPre : Configure.socketUrlTest);
        bundle.putString("briefUrl", envRadioGroup.getCheckedRadioButtonId() == R.id.rb_environment1 ? Configure.briefUrl
                : envRadioGroup.getCheckedRadioButtonId() == R.id.rb_environment2 ? Configure.briefUrlPre : Configure.briefUrlTest);

        intent.putExtra("bundle", bundle);

        startActivityForResult(intent, 100);
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