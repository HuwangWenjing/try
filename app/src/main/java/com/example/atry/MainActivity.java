package com.example.atry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity implements Runnable{

    EditText rmb;
    TextView show;
    float dollarRate = 6.5f;
    float euroRate = 10.0f;
    float wonRate = 500f;
    String TAG = "RateActivity";
    Handler handler;

    private String updateDate = "";

    //汇率的简单计算
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rmb = (EditText)findViewById(R.id.inputRMB);
        show = (TextView) findViewById(R.id.showText);

        //获得在sharedpreferences里保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE); //通常情况下都把配置文件做成私有的
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this); //另一获取文件的方式，高版本可用；本方式只能获取一个配置文件
        dollarRate = sharedPreferences.getFloat("dollar_rate", 0.0f);
        wonRate = sharedPreferences.getFloat("won_rate", 0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate", 0.0f);
        updateDate = sharedPreferences.getString("update_date","");

        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG, "onCreate: sp euroRate=" + euroRate);
        Log.i(TAG, "onCreate: sp wonRate=" + wonRate);
        Log.i(TAG,"onCreate: sp updateDate=" + updateDate);

        //判断时间
        if(todayStr.equals(updateDate)) {
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }

        //开启子线程
//        int count = 0;
//        while(count == 0) {
//            Thread t = new Thread(this);
//            t.start();
//            count += 1;
//        }

        //使用handler实现线程之间的同步
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 5) {
                    Bundle bdl = (Bundle)msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");

                    Log.i(TAG, "handleMessage: dollarRate" + dollarRate);
                    Log.i(TAG, "handleMessage: euroRate" + euroRate);
                    Log.i(TAG, "handleMessage: wonRate" + wonRate);

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.putFloat("dollar_rate", dollarRate);
                    editor.putFloat("euro_rate", euroRate);
                    editor.putFloat("won_rate", wonRate);
                    editor.commit();

                    Toast.makeText(MainActivity.this,"汇率已经更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };
    }

    public void onClick(View btn) {
        String str = rmb.getText().toString();
        float r = 0;
        if(str.length() > 0) {
            r = Float.parseFloat(str);
        }else {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show();
        }
        float val;
        if(btn.getId() == R.id.dollarRate) {
            show.setText(String.format("%2f",r/dollarRate));
        }else if(btn.getId() == R.id.euroRate) {
            show.setText(String.format("%2f",r/euroRate));
        }else {
            show.setText(String.format("%2f",r*wonRate));
        }
    }

    //新窗口的打开，在打开的新窗口中进行汇率的手动设置更新（参数在页面之间的传递）
    public void openOne(View btn) {
        openAdd();
    }
    public void openAdd(){
        Intent add = new Intent(this, AddActivity.class);
        startActivity(add);
        add.putExtra("dollarRate",dollarRate);
        add.putExtra("wonRate",wonRate);
        add.putExtra("euroRate",euroRate);
        Log.i(TAG, "openOne: dollarRate=" + dollarRate);
        Log.i(TAG, "openOne: wonRate=" + wonRate);
        Log.i(TAG, "openOne: euroRate=" + euroRate);

        startActivityForResult(add, 1);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.rate,menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.btn_add){
            openAdd();
        }else if(item.getItemId() == R.id.btn_list){
            //打开列表窗口
            Intent list = new Intent(this,RateListActivity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode ==2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_newDollar", 0.1f);
            euroRate = bundle.getFloat("key_newEuro", 0.1f);
            wonRate = bundle.getFloat("key_newWon", 0.1f);

            Log.i(TAG,"onActivityResult: dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult: wonRate=" + wonRate);
            Log.i(TAG,"onActivityResult: euroRate=" + euroRate);

            //将新设置的汇率写入SP
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到SharedPreferences");
        }


    }

    //多线程: 主函数实现runnable接口及其中的run()方法，后新建线程t，t调用start()方法后，线程开启，run()方法执行
    public void run() {
        Log.i(TAG, "run: run()...");
        //获取网络数据
//        URL url = null;
//        try{
//            url = new URL("https://www.boc.cn/sourcedb/whpj/");
//            HttpURLConnection http = (HttpURLConnection)url.openConnection();
//            InputStream in = http.getInputStream();
//
//            String html = inputStream2String(in);
//            Log.i(TAG, "run: html=" + html);
//            //Document doc = Jsoup.parse(html);
//        }catch(MalformedURLException e){
//            e.printStackTrace();
//        }catch(IOException e){
//            e.printStackTrace();
//        }

        Document doc = null;
        Bundle bundle = new Bundle(); //用于保存从网页上获取的汇率
        try{
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: "+ doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table2 = tables.get(1);
            Log.i(TAG, "run: tables2 = " + table2);

            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for(int i = 0; i<tds.size(); i++){
                Element td1 = tds.get(i); //币种
                Element td2 = tds.get(i+5); //汇率

                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());

                if("美元".equals(str1)) {
                    bundle.putFloat("dollar-rate", Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("euro-rate", Float.parseFloat(val));
                }else if("韩国元".equals(str1)) {
                    bundle.putFloat("won-rate", Float.parseFloat(val));
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        //msg.obj = "hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
    //此方法将读取到的输入流转换为String
//    private String inputStream2String(InputStream inputStream) throws IOException {
//        final int bufferSize = 1024;
//        final char[] buffer = new char[bufferSize];
//        final StringBuilder out = new StringBuilder();
//        Reader in = new InputStreamReader(inputStream, "UTF-8");
//        while (true) {
//            int rsz = in.read(buffer,0,buffer.length);
//            if(rsz < 0) {
//                break;
//            }
//            out.append(buffer,0,rsz);
//        }
//        return out.toString();
//    }

    public void openTwo(View btn) {
        openSearch();
    }
    public void openSearch() {
        Intent search = new Intent(this, SearchActivity.class);
        startActivity(search);
    }


}
