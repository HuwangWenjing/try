package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RateCalc extends AppCompatActivity {

    String TAG = "rateCalc";
    float rate = 0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_calc);

        rate = getIntent().getFloatExtra("rate",0f);
        String title = getIntent().getStringExtra("title");

        Log.i(TAG,"onCreate:Title = " + title);
        Log.i(TAG, "onCreate: rate" + rate);
        ((TextView)findViewById(R.id.title2)).setText(title);
    }
}
