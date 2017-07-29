package com.licktopia.thechampionofsudoku.Activities.UIActivities;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.SavedGameListAdapter;
import com.licktopia.thechampionofsudoku.SavedGameListItem;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SavedGameActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ProgressBar mProgressIndicator;
    private TextView savedUser;
    public static List<SavedGameListItem> savedGameListItems;





 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savedgame);
        recyclerView = (RecyclerView)findViewById(R.id.savedrecyclerView);
        mProgressIndicator = (ProgressBar)findViewById(R.id.progressBar2);
        mProgressIndicator.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        savedUser = (TextView)findViewById(R.id.saveduser_name);
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this).size(12).build());

        savedUser.setText(new StringBuilder().append(getUsername()).append(getString(R.string.your_saved_games)).toString());





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
                    getSavedGames().get(i).getGamerID(),
                    //getSavedGames().get(i).getSaveFile(),
                    getSavedGames().get(i).getStoppedTime()
                    );
            savedGameListItems.add(sGLI);

        }
        Collections.sort(savedGameListItems, new RecentSorter());
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onStart(){
        super.onStart();
        querySavedGameDb();
        fillListItems();
        adapter = new SavedGameListAdapter(savedGameListItems, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }

    @Override
    public void querySavedGameDb() {

        String partitionKey = getId();
        SavedGame sg = new SavedGame();
        sg.setGamerID(partitionKey);


        final DynamoDBQueryExpression<SavedGame> queryExpression = new DynamoDBQueryExpression<SavedGame>()
                .withHashKeyValues(sg)
                .withConsistentRead(false);

        final List<SavedGame>[] list = new List[]{new ArrayList<SavedGame>()};

        // id field is null at this point
        Runnable runnable = new Runnable() {
            public void run() {
                //DynamoDB calls go here
                list[0] = mapper.query(SavedGame.class, queryExpression);
                ArrayList<SavedGame> sGames = new ArrayList<SavedGame>();
                for (int i = 0; i < list[0].size(); i++) {
                    sGames.add(list[0].get(i));
                }
                setSavedGames(sGames);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //execute code on main thread
                        mProgressIndicator.setVisibility(View.GONE);
                    }
                });



            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
        try {
            mythread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
