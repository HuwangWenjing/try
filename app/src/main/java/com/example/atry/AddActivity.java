package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class AddActivity extends AppCompatActivity {
    String TAG = "AddActivity";

    EditText dollarText;
    EditText euroText;
    EditText wonText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add3);

        Intent intent = getIntent();
        //将变量取出 标签和值都对应RateActivity中的值
        float dollar2 = intent.getFloatExtra("dollarRate",0.0f);
        float won2 = intent.getFloatExtra("wonRate",0.0f);
        float euro2 = intent.getFloatExtra("euroRate",0.0f);
        Log.i(TAG, "onCreate: dollar2 =" + dollar2);
        Log.i(TAG, "onCreate: won2 =" + won2);
        Log.i(TAG, "onCreate: euro2 =" + euro2);

        //获取控件
        dollarText = (EditText)findViewById(R.id.editDollar);
        euroText = (EditText)findViewById(R.id.editEuro);
        wonText = (EditText)findViewById(R.id.editWon);
        //显示数据到控件
        dollarText.setText(String.valueOf(dollar2));
        euroText.setText(String.valueOf(euro2));
        wonText.setText(String.valueOf(won2));
    }

    public void save(View btn) {
        Log.i(TAG,"safe");
        //获取新的值
        float newDollar = Float.parseFloat(dollarText.getText().toString());
        float newEuro = Float.parseFloat(euroText.getText().toString());
        float newWon = Float.parseFloat(wonText.getText().toString());

        Log.i(TAG, "save: 获取到新的值");
        Log.i(TAG, "onCreate: newDollar=" + newDollar);
        Log.i(TAG, "onCreate: newEuro=" + newEuro);
        Log.i(TAG, "onCreate: newWon=" + newWon);

        //保存到bundle或收入extra
        Intent intent = getIntent();
        Bundle bdl = new Bundle();
        bdl.putFloat("key_newDollar",newDollar);
        bdl.putFloat("key_newEuro",newEuro);
        bdl.putFloat("key_newWon",newWon);
        intent.putExtras(bdl);
        setResult(2,intent);

        //返回到调用页面
        finish();
    }
}
