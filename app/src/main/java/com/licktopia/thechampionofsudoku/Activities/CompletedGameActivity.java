package com.licktopia.thechampionofsudoku.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.licktopia.thechampionofsudoku.CompletedGameListAdapter;

import com.licktopia.thechampionofsudoku.CompletedGameListItem;
import com.licktopia.thechampionofsudoku.Gamer;
import com.licktopia.thechampionofsudoku.R;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CompletedGameActivity extends Gamer {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Button tvSortRecent;
    private Button tvSortBest;
    private Button tvSortHigh;
    private Button tvSortTime;
    private TextView tvGpDisplay;
    public static List<CompletedGameListItem> completedGameListItems;



 
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
        fillListItems();
        tvGpDisplay = (TextView)findViewById(R.id.user_name);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).size(12).build());

        tvGpDisplay.setText(new StringBuilder().append(getUsername()).append(getString(R.string.game_history)).toString());

        tvSortRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(completedGameListItems,new RecentSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        tvSortBest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(completedGameListItems,new BestSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        tvSortHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(completedGameListItems,new HighSorter());
                adapter.notifyDataSetChanged();
               // recyclerView.setAdapter(adapter);
            }
        });

        tvSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(completedGameListItems,new TimeSorter());
                adapter.notifyDataSetChanged();
                //recyclerView.setAdapter(adapter);
            }
        });

        adapter = new CompletedGameListAdapter(completedGameListItems, this);
        recyclerView.setAdapter(adapter);
    }

    private void fillListItems(){
        completedGameListItems = new ArrayList<>();
        for(int i = 0; i < getCompletedGames().size(); i++){
            CompletedGameListItem cGLI= new CompletedGameListItem(
                    getCompletedGames().get(i).getAccuracy(),
                    getCompletedGames().get(i).getDate(),
                    getCompletedGames().get(i).getLevel(),
                    getCompletedGames().get(i).getPuzzleNumber(),
                    getCompletedGames().get(i).getRealScore(),
                    getCompletedGames().get(i).getTime(),
                    getCompletedGames().get(i).getXp(),
                    getCompletedGames().get(i).getGamerID(),
                    getCompletedGames().get(i).isColor(),
                    getCompletedGames().get(i).getExactTime());
            completedGameListItems.add(cGLI);

        }
        Collections.sort(completedGameListItems,new RecentSorter());
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }


    public class RecentSorter implements Comparator<CompletedGameListItem> {

        public int compare(CompletedGameListItem one, CompletedGameListItem another){
            int returnVal = 0;

            if(formatExactTime(one.getExactTime()) < formatExactTime(another.getExactTime())){
                returnVal =  1;
            }else if(formatExactTime(one.getExactTime()) > formatExactTime(another.getExactTime())){
                returnVal =  -1;
            }else if(formatExactTime(one.getExactTime()) == formatExactTime(another.getExactTime())){
                returnVal =  0;
            }

            return returnVal;

        }
    }

    public class BestSorter implements Comparator<CompletedGameListItem> {

        public int compare(CompletedGameListItem one, CompletedGameListItem another){
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

    public class HighSorter implements Comparator<CompletedGameListItem> {

        public int compare(CompletedGameListItem one, CompletedGameListItem another){

            int returnVal = 0;

            if(one.getRealScore() < another.getRealScore()){
                returnVal =  1;
            }else if(one.getXp() > another.getXp()){
                returnVal =  -1;
            }else if(one.getXp() == another.getXp()){
                returnVal =  0;
            }

            return returnVal;
        }

    }

    public class TimeSorter implements Comparator<CompletedGameListItem> {

        public int compare(CompletedGameListItem one, CompletedGameListItem another){
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
