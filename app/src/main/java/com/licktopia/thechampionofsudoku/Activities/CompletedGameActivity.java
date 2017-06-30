package com.licktopia.thechampionofsudoku.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.licktopia.thechampionofsudoku.CompletedGameListAdapter;

import com.licktopia.thechampionofsudoku.ListItem;
import com.licktopia.thechampionofsudoku.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CompletedGameActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Button tvSortRecent;
    private Button tvSortBest;
    private Button tvSortHigh;
    private Button tvSortTime;
    private TextView tvGpDisplay;
    private String gamerName;
    public static List<ListItem> listItems;



 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_completedgame);



        tvSortRecent = (Button)findViewById(R.id.sort_recent);
        tvSortBest = (Button)findViewById(R.id.sort_best);
        tvSortHigh = (Button)findViewById(R.id.sort_highscore);
        tvSortTime = (Button)findViewById(R.id.sort_time);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CompletedGameListAdapter(listItems, this);
        tvGpDisplay = (TextView)findViewById(R.id.user_name);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).size(12).build());

        tvGpDisplay.setText(gamerName +"'s Game History");
        recyclerView.setAdapter(adapter);
        tvSortRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(listItems,new RecentSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        tvSortBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(listItems,new BestSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        tvSortHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(listItems,new HighSorter());
                adapter.notifyDataSetChanged();
               // recyclerView.setAdapter(adapter);
            }
        });

        tvSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(listItems,new TimeSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

    }

    public class RecentSorter implements Comparator<ListItem> {

        public int compare(ListItem one, ListItem another){
            int returnVal = 0;
/*
            if(one.getId() < another.getId()){
                returnVal =  1;
            }else if(one.getId() > another.getId()){
                returnVal =  -1;
            }else if(one.getId() == another.getId()){
                returnVal =  0;
            }
            */
            return returnVal;

        }
    }

    public class BestSorter implements Comparator<ListItem> {

        public int compare(ListItem one, ListItem another){
            int returnVal = 0;

            if(one.getRealScore() < another.getRealScore()){
                returnVal =  1;
            }else if(one.getRealScore() > another.getRealScore()){
                returnVal =  -1;
            }else if(one.getRealScore() == another.getRealScore()){
                returnVal =  0;
            }
            return returnVal;

        }
    }

    public class HighSorter implements Comparator<ListItem> {

        public int compare(ListItem one, ListItem another){
            int returnVal = 0;

            if(one.getScore() < another.getScore()){
                returnVal =  1;
            }else if(one.getScore() > another.getScore()){
                returnVal =  -1;
            }else if(one.getScore() == another.getScore()){
                returnVal =  0;
            }
            return returnVal;

        }
    }

    public class TimeSorter implements Comparator<ListItem> {

        public int compare(ListItem one, ListItem another){
            int returnVal = 0;

            if(uFT(one.getTime()) < uFT(another.getTime())){
                returnVal =  -1;
            }else if(uFT(one.getTime()) > uFT(another.getTime())){
                returnVal =  1;
            }else if(one.getTime() == (another.getTime())){
                returnVal =  0;
            }
            return returnVal;

        }
    }

    public int uFT(String time) {
        int timeNumber;
        StringBuilder str = new StringBuilder();
        for(char c : time.toCharArray()){
            if(c != ':'){
                str.append(c);
            }

        }
        timeNumber = Integer.valueOf(str.toString());
        return timeNumber;
    }
}
