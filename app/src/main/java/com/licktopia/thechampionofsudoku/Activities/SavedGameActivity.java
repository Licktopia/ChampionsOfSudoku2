package com.licktopia.thechampionofsudoku.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.widget.TextView;

import com.licktopia.thechampionofsudoku.CompletedGameListAdapter;
import com.licktopia.thechampionofsudoku.CompletedGameListItem;
import com.licktopia.thechampionofsudoku.Gamer;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.SavedGameListAdapter;
import com.licktopia.thechampionofsudoku.SavedGameListItem;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.licktopia.thechampionofsudoku.Activities.CompletedGameActivity.completedGameListItems;


public class SavedGameActivity extends AppCompatActivity {
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

        savedUser.setText(new StringBuilder().append(Gamer.getUsername()).append(getString(R.string.your_saved_games)).toString());



        adapter = new SavedGameListAdapter(savedGameListItems, this);
        recyclerView.setAdapter(adapter);
    }

    private void fillListItems(){
        savedGameListItems = new ArrayList<>();
        for(int i = 0; i < Gamer.getSavedGames().size(); i++){
            SavedGameListItem sGLI= new SavedGameListItem(
                    Gamer.getSavedGames().get(i).getDate(),
                    Gamer.getSavedGames().get(i).getTime(),
                    Gamer.getSavedGames().get(i).getColorMode(),
                    Gamer.getSavedGames().get(i).getExactTime(),
                    Gamer.getSavedGames().get(i).getLevel(),
                    Gamer.getSavedGames().get(i).getPuzzleNumber(),
                    Gamer.getSavedGames().get(i).getGamerID()
                    );
            savedGameListItems.add(sGLI);

        }
        Collections.sort(savedGameListItems, new RecentSorter());
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

    public Long formatExactTime(String time) {
        Long timeNumber;
        StringBuilder str = new StringBuilder();
        for(char c : time.toCharArray()){
            if(c != ','){
                str.append(c);
            }

        }
        timeNumber = Long.valueOf(str.toString());
        return timeNumber;
    }


}
