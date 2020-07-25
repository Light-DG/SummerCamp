package com.example.customviewtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    List<String> list;
    MyDrawerView myDrawerView;
    int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyDrawerView myDrawerView = findViewById(R.id.drawer);


    }




    public void initData(){
        list = new ArrayList<String>();
        String[] strings = new String[]{"111111","22222222","333333333","44444444","5555555","6666666","77777777"
        ,"8888888888","999999999","aaaaaaa","tttttttt","eeeeeeee"};
        for (int i = 0;i<strings.length;i++){
            list.add(strings[i]);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }
    }
}

