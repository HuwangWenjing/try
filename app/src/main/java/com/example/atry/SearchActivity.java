package com.example.atry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.os.Handler;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import static java.util.Calendar.DAY_OF_YEAR;

public class SearchActivity extends AppCompatActivity implements Runnable, Filterable{
    EditText keyword;
    final String TAG = "searchActivity";
    Handler handler;
    private String updateDate = "";
    List<String> list2;
    ListView listView;
    ListAdapter adapter;
    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //获取当前系统时间
        Calendar now = Calendar.getInstance();
        Date today = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        List<String> list1 = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list1.add("item" + i);
        }
        //使用handler实现线程之间的同步
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    //Bundle bdl = (Bundle)msg.obj;
                    //Log.i(TAG, "handleMessage: title" + dollarRate);
                    list2 = (List<String>) msg.obj;
                    adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, list2);
                    listView = (ListView) findViewById(R.id.topicList);
                    listView.setAdapter(adapter);  //当前控件listView的adapter对象

                    //保存更新的日期
                    SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", updateDate);
                    editor.commit();

                    Toast.makeText(SearchActivity.this, "通知公告已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

        //listView.getListView().setOnItemClickListener(this);

        //使用ListView完成标题显示
        String data[] = {"waiting...", "12345", "67890"};

        Thread thread = new Thread(this);
        thread.start();

        //取出存在SP中的数据
        SharedPreferences sp = getSharedPreferences("title", Activity.MODE_PRIVATE);
        String titles = sp.getString("title", " ");

        //判断时间
//        now.add(DAY_OF_YEAR, -7);
//        Date newDate = now.getTime();
//        if (updateDate.equals(newDate)) {
//            Log.i(TAG, "onCreate: 需要更新");
//            //开启子线程
//            Thread t = new Thread(this);
//            t.start();
//        } else {
//            Log.i(TAG, "onCreate: 不需要更新");
//        }

        //searchView实现搜索
        SearchView searchView = findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("请输入关键字");
        listView.setTextFilterEnabled(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                if(query != null) {
//                    listView.setTextFilterEnabled(true);
//                    listView.setFilterText(query);
//                }else{
//                    Toast.makeText(SearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
//                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    listView.clearTextFilter();
                }else{
                    listView.setTextFilterEnabled(true);
                    listView.setFilterText(newText);
                    //adapter.getFilter().filter(newText);
                }
//                if(adapter instanceof Filterable) {
//                    Filter filter = ((Filterable)adapter).getFilter();
//                    if(newText == null || newText.length() == 0) {
//                        filter.filter(null);
//                    }else{
//                        filter.filter(newText);
//                    }
//                }
                return false;
            }
        });


    }

    //onClick里要写搜索按钮btn_search的操作代码
//    public void onClick(View btn) {
//        keyword = (EditText) findViewById(R.id.keyword);
//        if (keyword != null && list2.contains(keyword)) {
//
//        } else {
//            Toast.makeText(SearchActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    public void run() {
        Log.i(TAG, "run: run()...");
        //用于将获得的标题存入SP
        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //获取官网通知公告标题数据
        List<String> titleList = new ArrayList<String>();
        Document doc = null;
        Bundle bundle = new Bundle(); //用于保存从网页上获取的公告标题
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
            //doc = Jsoup.parse(html);
            Log.i(TAG, "run: " + doc.title());
            Elements uls = doc.getElementsByTag("ul");
            Element ul18 = uls.get(17);
            Log.i(TAG, "run: ul18 = " + ul18);

            //获取标签span中的标题数据
            Elements spans = ul18.getElementsByTag("span");
            for (int i = 0; i < spans.size(); i += 2) {
                Element title = spans.get(i); //通知公告标题
                String str1 = title.text();
                //String val = td2.text();
                Log.i(TAG, "run: title=" + title.text());
                titleList.add("title: " + title);
                //将获得的标题数据存入SP
                editor.putString("title", String.valueOf(title));
                editor.commit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取Msg对象用于返回主线程
        Message msg = handler.obtainMessage(5);
        msg.obj = titleList;
        handler.sendMessage(msg);
    }

//    @Override
//    public void onClick(View v) {
//
//    }
//
//    public void onItemClick(AdapterView<?>parent, View view,int position, long id) {
//        //Object itemAtPosition = getListView().getItemAtposition(position);
//        HashMap<String, String> map = (HashMap<String, String>)itemAtPosition;
//        String titleStr = map.get("titles");
//        //String detailStr = map.get("detail");
//
//        TextView title = (TextView)view.findViewById(R.id.title);
//        String title1 = String.valueOf(title.getText());
//    }
//
//
//
//    public void getListView() {
//    }


//    private void initSearchView() {
//        SearchView searchView = findViewById(R.id.searchview);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            //输入完成后，提交时触发的方法，一般情况是点击输入法中的搜索按钮才会触发，表示现在正式提交了
//            public boolean onQueryTextSubmit(String query) {
//                if (TextUtils.isEmpty(query)) {
//                    Toast.makeText(SearchActivity.this, "请输入查找内容", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(SearchActivity.this, query, Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//
//            //在输入时触发的方法，当字符真正显示到searchView中才触发，像是拼音，在输入法组词的时候不会触发
//            public boolean onQueryTextChange(String newText) {
//                if (TextUtils.isEmpty(newText)) {
//                    Toast.makeText(SearchActivity.this, "请输入查找内容", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(SearchActivity.this, newText, Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        });
//    }



}
