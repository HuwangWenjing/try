package com.example.atry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"one", "two","three"};
    Handler handler;
    private final String TAG = "RateListActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);  //父类已包含布局 不需要另外引用布局 所以要注释掉这句
        List<String> list1 = new ArrayList<String>();
        for(int i = 0; i <100; i++) {
            list1.add("item" + i);
        }
        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        setListAdapter(adapter); //调用父类的方法

        Thread thread = new Thread(this);
        thread.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 7) {
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        //获取网络数据
        List<String> rateList = new ArrayList<String>();
        Document doc = null;
        Bundle bundle = new Bundle(); //用于保存从网页上获取的汇率
        try {
            //Thread.sleep(2000);
            doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
            Log.i(TAG, "run: " + doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table2 = tables.get(1);
            Log.i(TAG, "run: tables2 = " + table2);

            //获取TD中的数据
            Elements tds = table2.getElementsByTag("td");
            for (int i = 0; i < tds.size(); i++) {
                Element td1 = tds.get(i); //币种
                Element td2 = tds.get(i + 5); //汇率

                String str1 = td1.text();
                String val = td2.text();
                Log.i(TAG, "run: text=" + td1.text() + "==>" + td2.text());
                rateList.add(str1 + "==>" + val);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象用于返回主线程
        Message msg = handler.obtainMessage(7);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
}

