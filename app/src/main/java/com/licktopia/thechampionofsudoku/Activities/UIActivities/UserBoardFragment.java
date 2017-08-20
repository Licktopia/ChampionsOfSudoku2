package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Adapters.SavedGameListAdapter;
import com.licktopia.thechampionofsudoku.Adapters.UserGameListAdapter;
import com.licktopia.thechampionofsudoku.DBOjects.SavedGame;
import com.licktopia.thechampionofsudoku.Database.DatabaseAccess;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;


public class UserBoardFragment extends Fragment {

    private static final int DISPLAY_AT_A_TIME = 20;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<UserGameListItem> userGameListItems;
    private ArrayList<UserGameListItem> ShuffledList;
    private ArrayList<UserGameListItem> BegList;
    private ArrayList<UserGameListItem> EasList;
    private ArrayList<UserGameListItem> MedList;
    private ArrayList<UserGameListItem> HarList;
    private ArrayList<UserGameListItem> ExpList;
    private ArrayList<UserGameListItem> MasList;
    private List DisplayNowList;
    private ArrayList[] LevelLists = {BegList,EasList,MedList,HarList,ExpList,MedList};
    public static UserBoardFragment newInstance() {return new UserBoardFragment();}
    private View view;
    private int levelNow = 6;
    private DatabaseAccess databaseAccess;
    private ProgressBar mProgressBar;
    private boolean color = false;
    private ImageButton colorSwitcher;

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
        }
        else{
            view = inflater.inflate(R.layout.fragment_user_board_large, container, false);
        }

        mProgressBar = (ProgressBar)view.findViewById(R.id.usr_progress);
        Runnable runnable = new Runnable() {
            public void run() {
                recyclerView = (RecyclerView)view.findViewById(R.id.user_board_rv);
                recyclerView.setLayoutManager((new GridLayoutManager(getContext(),2)));
                databaseAccess = DatabaseAccess.getInstance(getContext());
                databaseAccess.open();
                assignAllLists();


                ShuffledList = userGameListItems;
                Collections.shuffle(ShuffledList);
                DisplayNowList = ShuffledList.subList(0,DISPLAY_AT_A_TIME);
                adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
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


        return view;
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
            ButtArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    levelNow = finalI;
                    Collections.shuffle(LevelLists[finalI]);
                    DisplayNowList = LevelLists[finalI].subList(0,DISPLAY_AT_A_TIME);
                    adapter = null;
                    adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
        ImageButton ranButton = (ImageButton)view.findViewById(R.id.ran);
        ranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelNow = 6;
                Collections.shuffle(ShuffledList);
                DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
                adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
                recyclerView.setAdapter(adapter);
            }
        });

        ImageButton moreButton = (ImageButton) view.findViewById(R.id.more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(levelNow<6) {
                    Collections.shuffle(LevelLists[levelNow]);
                    DisplayNowList = LevelLists[levelNow].subList(0, DISPLAY_AT_A_TIME);
                }
                else{
                    Collections.shuffle(ShuffledList);
                    DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
                }
                adapter = null;
                adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
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
                    adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
                    colorSwitcher.setImageDrawable(getResources().getDrawable(R.drawable.peacock));
                    recyclerView.setAdapter(adapter);
                }
                else{
                    color = true;
                    adapter = null;
                    adapter = new UserGameListAdapter(DisplayNowList, getContext(), color);
                    colorSwitcher.setImageDrawable(getResources().getDrawable(R.drawable.rocket));
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }



}
