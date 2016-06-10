package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by eignatik on 20.05.15.
 */
public class BlogsActivity extends Activity {
    private App app;
    private List<TestData> testDataList=new ArrayList<>();
    private ListView dataListView;
    private TestAdapter testAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blogscreen);
        app = (App)getApplicationContext();
        testAdapter = new TestAdapter(this);

        TestData td = new TestData();
        for(int i=0; i<10; i++){
            td = new TestData();
            td.setTitleBlog("Blog " + i);
            td.setDateBlog(Calendar.getInstance().getTime());
            td.setNameUser("User " + i);
            testDataList.add(td);
        }
        dataListView = (ListView) findViewById(R.id.listProfiles);
        dataListView.setAdapter(testAdapter);
    }

    @Override
    public void onBackPressed() {

        if(app.isLoggened()==false) super.onBackPressed();
    }

    class TestAdapter extends ArrayAdapter<TestData>{

        public TestAdapter(Context context) {
            super(context, R.layout.itemdatalist, testDataList);
        }
        private SimpleDateFormat sDateFormat = new SimpleDateFormat("DD.MM.yyyy HH:mm:ss");
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TestData item = getItem(position);

            if(convertView == null){
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.itemdatalist, null);
            }
            ((TextView) convertView.findViewById(R.id.title))
                    .setText(item.getTitleBlog());
            ((TextView) convertView.findViewById(R.id.author))
                    .setText(item.getNameUser());
            ((TextView) convertView.findViewById(R.id.dateBlog))
                    .setText(sDateFormat.format(item.getDateBlog()));
            return convertView;
        }
    }


}