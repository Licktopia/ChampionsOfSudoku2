package com.licktopia.thechampionofsudoku.Activities.UIActivities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Adapters.CompletedGameListAdapter;

import com.licktopia.thechampionofsudoku.ListItems.CompletedGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.formatExactTime;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;


public class CompletedGameFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private Button tvSortRecent;
    private Button tvSortBest;
    private Button tvSortHigh;
    private Button tvSortTime;
    private TextView tvGpDisplay;
    public static List<CompletedGameListItem> completedGameListItems;
    private View view;

    public static CompletedGameFragment newInstance() {
        return new CompletedGameFragment();
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(phoneSize==1) {
            view = inflater.inflate(R.layout.activity_completedgame_small, container, false);
        }
        else if(phoneSize == 2){
            view = inflater.inflate(R.layout.activity_completedgame, container, false);
        }
        else if(phoneSize == 3){
            view = inflater.inflate(R.layout.activity_completedgame_phablet, container, false);
        }
        else{
            view = inflater.inflate(R.layout.activity_completedgame_large, container, false);
        }

        tvSortRecent = (Button)view.findViewById(R.id.sort_recent);
        tvSortBest = (Button)view.findViewById(R.id.sort_best);
        tvSortHigh = (Button)view.findViewById(R.id.sort_highscore);
        tvSortTime = (Button)view.findViewById(R.id.sort_time);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fillListItems();
        tvGpDisplay = (TextView)view.findViewById(R.id.user_name);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext()).size(12).build());

        tvGpDisplay.setText(new StringBuilder().append(BaseActivity.getUsername()).append(getString(R.string.game_history)).toString());

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

        adapter = new CompletedGameListAdapter(completedGameListItems, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fillListItems(){
        completedGameListItems = new ArrayList<>();
        ArrayList<CompletedGame> completedGames = BaseActivity.getCompletedGames();
        for(int i = 0; i < completedGames.size(); i++){
            CompletedGameListItem cGLI= new CompletedGameListItem(
                    completedGames.get(i).getAccuracy(),
                    completedGames.get(i).getDate(),
                    completedGames.get(i).getLevel(),
                    completedGames.get(i).getPuzzleNumber(),
                    completedGames.get(i).getRealScore(),
                    completedGames.get(i).getTime(),
                    completedGames.get(i).getXp(),
                    completedGames.get(i).getGamerID(),
                    completedGames.get(i).isColor(),
                    completedGames.get(i).getExactTime());
            completedGameListItems.add(cGLI);

        }
        Collections.sort(completedGameListItems,new RecentSorter());
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
