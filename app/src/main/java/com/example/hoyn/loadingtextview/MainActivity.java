package com.example.hoyn.loadingtextview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.example.hoyn.widget.LoadingTextView;

public class MainActivity extends Activity {

    LoadingTextView tv_loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_loading = (LoadingTextView) findViewById(R.id.tv_loading);
        tv_loading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_loading.start(1000);
            }
        });
    }
}
