package com.peterzhang.androidhookdemo;

import android.content.Intent;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class LauncherActivity extends AppCompatActivity {
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        listView = findViewById(R.id.listview);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,android.R.id.text1,new String[]{"HookCloseGuard"});
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(LauncherActivity.this,MainActivity.class);
                        startActivity(intent);
                        break;
                        default:
                            break;
                }
            }
        });
    }
}
