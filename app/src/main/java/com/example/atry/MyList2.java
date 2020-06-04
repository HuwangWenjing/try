package com.example.atry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyList2 extends AppCompatActivity implements  Runnable{

    Handler handler;
    private ArrayList<HashMap<String,String>> listItems;
    private SimpleAdapter listItemAdapter;

    String TAG = "MyList2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);
        initListView();
        //this.setListAdapter(listItemAdapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if(msg.what == 5) {
                    List<HashMap<String,String>> list2 = (List<HashMap<String,String>>) msg.obj;
                    listItemAdapter = new SimpleAdapter(MyList2.this,list2,
                            R.layout.list_item,
                            new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private AdapterView getListView() {
        return null;
    }

    private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for(int i =0; i <10; i++) {
            HashMap<String,String> map = new HashMap<String, String>();
            map.put("ItemTitle","Rate:" + i);
            map.put("ItemDetail","Detail:" + i);
            listItems.add(map);
        }
        listItemAdapter = new SimpleAdapter(this,listItems,
                R.layout.list_item,
                new String[]{"ItemTitle", "ItemDetail"},
                new int[]{R.id.itemTitle, R.id.itemDetail}
                );
    }


    @Override
    public void run() {
        Log.i(TAG, "run: run()...");

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

                Log.i(TAG,"run:" + str1 + "==>" + val);
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemTitle",str1);
                map.put("ItemDetail",val);
                listItems.add(map);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.obj = bundle;
        handler.sendMessage(msg);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
        Log.i(TAG, "onItemClick:parent" + parent);
        Log.i(TAG, "onItemClick:view" + view);
        Log.i(TAG, "onItemClick:position" + position);
        Log.i(TAG, "onItemClick:id" + id);
        HashMap<String,String> map = (HashMap<String,String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick:titleStr" + titleStr);
        Log.i(TAG, "onItemClick:detailStr" + detailStr);

        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick:title2" + title2);
        Log.i(TAG, "onItemClick:detail2" + detail2);

        //打开新的页面传入数据
        Intent rateCalc = new Intent(this,RateCalc.class);
        rateCalc.putExtra("title", titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);
    }

}
