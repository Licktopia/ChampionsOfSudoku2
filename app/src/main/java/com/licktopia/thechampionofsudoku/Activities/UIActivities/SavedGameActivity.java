package com.licktopia.thechampionofsudoku.Activities.UIActivities;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.TextView;

import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.SavedGameListAdapter;
import com.licktopia.thechampionofsudoku.SavedGameListItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SavedGameActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView savedUser;
    public static List<SavedGameListItem> savedGameListItems;



 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedgame);

        recyclerView = (RecyclerView)findViewById(R.id.savedrecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fillListItems();
        savedUser = (TextView)findViewById(R.id.saveduser_name);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).size(12).build());

        savedUser.setText(new StringBuilder().append(getUsername()).append(getString(R.string.your_saved_games)).toString());



        adapter = new SavedGameListAdapter(savedGameListItems, this);
        recyclerView.setAdapter(adapter);
    }

    private void fillListItems(){
        savedGameListItems = new ArrayList<>();
        for(int i = 0; i < getSavedGames().size(); i++){
            SavedGameListItem sGLI= new SavedGameListItem(
                    getSavedGames().get(i).getDate(),
                    getSavedGames().get(i).getTime(),
                    getSavedGames().get(i).getColorMode(),
                    getSavedGames().get(i).getExactTime(),
                    getSavedGames().get(i).getLevel(),
                    getSavedGames().get(i).getPuzzleNumber(),
                    getSavedGames().get(i).getGamerID()
                    );
            savedGameListItems.add(sGLI);

        }
        Collections.sort(savedGameListItems, new RecentSorter());
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }


    public class RecentSorter implements Comparator<SavedGameListItem> {

        public int compare(SavedGameListItem one, SavedGameListItem another){
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


}
