package com.bigyoshi.qrhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity {

    ListView rankList;
    ArrayAdapter<String> rankAdapter;
    ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        rankList = findViewById(R.id.list_rankings);
        dataList = new ArrayList<>();

        rankAdapter = new ArrayAdapter<>(this, R.layout.leaderboard_list_content, dataList);

        rankList.setAdapter(rankAdapter);



    }
}
