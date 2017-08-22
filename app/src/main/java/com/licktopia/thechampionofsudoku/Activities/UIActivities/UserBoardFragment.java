package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Adapters.UserGameListAdapter;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.licktopia.thechampionofsudoku.Database.DatabaseAccess;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;



import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;


public class UserBoardFragment extends Fragment {

    private int DISPLAY_AT_A_TIME = 20;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<UserGameListItem> userGameListItems;
    private ArrayList<UserGameListItem> ShuffledList;
    private List DisplayNowList;
    private ArrayList[] LevelLists;
    public static UserBoardFragment newInstance() {return new UserBoardFragment();}
    private View view;
    private int levelNow = 6;
    private DatabaseAccess databaseAccess;
    private ProgressBar mProgressBar;
    private boolean color = false;
    private ImageButton colorSwitcher;
    private int gridNum = 2;
    private GridLayoutManager gridLayoutManager;
    private List<Integer> smallRange;
    private List<Integer> bigRange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(phoneSize==1) {
            view = inflater.inflate(R.layout.fragment_user_board_small, container, false);
        }
        else if(phoneSize == 2){
            view = inflater.inflate(R.layout.fragment_user_board, container, false);
        }
        else if(phoneSize == 3){
            view = inflater.inflate(R.layout.fragment_user_board_phablet, container, false);
            gridNum = 3;
            DISPLAY_AT_A_TIME++;
        }
        else{
            view = inflater.inflate(R.layout.fragment_user_board_large, container, false);
            gridNum = 4;
        }
        smallRange = new ArrayList<Integer>();
        for(int i = 1; i < 10001; i++){
            smallRange.add(i);
        }
        bigRange = new ArrayList<Integer>();
        for(int i = 1; i < 60001; i++){
            bigRange.add(i);
        }
        Collections.shuffle(smallRange);
        smallRange = smallRange.subList(0,DISPLAY_AT_A_TIME);
        Collections.shuffle(bigRange);
        bigRange = bigRange.subList(0,DISPLAY_AT_A_TIME);
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        mProgressBar = (ProgressBar)view.findViewById(R.id.usr_progress);
        recyclerView = (RecyclerView)view.findViewById(R.id.user_board_rv);
        gridLayoutManager = new GridLayoutManager(getContext(),gridNum);
        recyclerView.setLayoutManager((gridLayoutManager));
        LevelLists = new ArrayList[Tags.Levels.length];




        Runnable runnable = new Runnable() {
            public void run() {
                String partitionKey = BaseActivity.getId();
                CompletedGame cg = new CompletedGame();
                cg.setGamerID(partitionKey);
                final DynamoDBQueryExpression<CompletedGame> queryExpression = new DynamoDBQueryExpression<CompletedGame>()
                        .withHashKeyValues(cg)
                        .withConsistentRead(false);

                final List<CompletedGame>[] list = new List[]{new ArrayList<>()};
                try {
                    list[0] = BaseActivity.mapper.query(CompletedGame.class, queryExpression);
                }
                catch(Exception e){

                }
                ArrayList<CompletedGame> cGames = new ArrayList<CompletedGame>();
                for (int i = 0; i < list[0].size(); i++) {
                    cGames.add(list[0].get(i));
                }
                BaseActivity.setCompletedGames(cGames);



                databaseAccess = DatabaseAccess.getInstance(getContext());
                databaseAccess.open();
                ShuffledList = new ArrayList<>();
                for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                    ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.get(i)));
                }
                /*
                assignAllLists();
                ShuffledList = userGameListItems;
                Collections.shuffle(ShuffledList);
                DisplayNowList = ShuffledList.subList(0,DISPLAY_AT_A_TIME);
                */
                adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                initializeButtons();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //execute code on main thread
                            mProgressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }catch(Exception e){

                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    private void assignAllLists() {
        int levelNum = 0;
        userGameListItems = new ArrayList<>();
        for(String i : Tags.Levels) {
            LevelLists[levelNum] = new ArrayList();
            LevelLists[levelNum] = databaseAccess.getStockGamesFromLevel(i, levelNum);
            userGameListItems.addAll(LevelLists[levelNum]);
            levelNum++;
        }
    }

    private void initializeButtons() {
        ImageButton[] ButtArray = new ImageButton[Tags.levelSorter.length];
        for(int i = 0; i < ButtArray.length; i++){
            ButtArray[i]=(ImageButton)view.findViewById(Tags.levelSorter[i]);
            final int finalI = i;
            final int finalI1 = i;
            ButtArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    levelNow = finalI;
                    Collections.shuffle(smallRange);
                    ShuffledList.clear();
                    //DisplayNowList = LevelLists[finalI].subList(0,DISPLAY_AT_A_TIME);
                    for(int j = 0; j < DISPLAY_AT_A_TIME; j++){
                        ShuffledList.add(databaseAccess.getSpecificBoardGame(smallRange.get(j)+(10000* finalI1)));
                    }
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
        ImageButton ranButton = (ImageButton)view.findViewById(R.id.ran);
        ranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelNow = 6;
                Collections.shuffle(bigRange);
                //DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
                ShuffledList.clear();
                for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                    ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.get(i)));
                }
                adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                recyclerView.setAdapter(adapter);
            }
        });

        ImageButton moreButton = (ImageButton) view.findViewById(R.id.more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(levelNow<6) {
                    Collections.shuffle(smallRange);
                    ShuffledList.clear();
                    for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                        ShuffledList.add(databaseAccess.getSpecificBoardGame(smallRange.get(i)+(10000*levelNow)));
                    }
                    //DisplayNowList = LevelLists[levelNow].subList(0, DISPLAY_AT_A_TIME);
                }
                else{
                    Collections.shuffle(bigRange);
                    ShuffledList.clear();
                    for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                        ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.get(i)));
                    }
                    //DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
                }
                adapter = null;
                adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                recyclerView.setAdapter(adapter);
            }
        });
        colorSwitcher = (ImageButton)view.findViewById(R.id.color_switcher);
        colorSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(color){
                    color = false;
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                    colorSwitcher.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.peacock));
                    recyclerView.setAdapter(adapter);
                }
                else{
                    color = true;
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color);
                    colorSwitcher.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.rocket));
                    recyclerView.setAdapter(adapter);
                }

            }
        });
    }



}
