package com.bigyoshi.qrhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bigyoshi.qrhunt.R;

import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity {

    ListView rankList;
    ArrayAdapter<String> rankAdapter;
    ArrayList<String> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        rankList = findViewById(R.id.rankings);
        dataList = new ArrayList<>();

        rankAdapter = new ArrayAdapter<>(this, R.layout.leaderboard_list_content, dataList);

        rankList.setAdapter(rankAdapter);



    }
}
