package com.example.atry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends ArrayAdapter {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_my_adapter);
//    }
    public MyAdapter(Context context, int resource, ArrayList<HashMap<String,String>> list){
        super(context, resource,list);
    }

//    public View getView(int position, View convertView, ViewGroup parent) {
//        View itemView = convertView;
//        if(itemView == null ) {
//            itemView = LayoutInflater.from(getContext().inflate(R.layout.List_item,parent,false));
//        }
//        Map<String,String> map = (Map<String,String>) getItem(position);
//        TextView title = (TextView)itemView.findViewById(R.id.itemTitle);
//        TextView detail = (TextView)itemView.findViewById(R.id.itemDetail);
//
//        title.setText("Title" +map.get("Itemtitle"));
//        detail.setText("detail" + map.get("ItemDetail"));
//
//        return itemView;
//    }

}
