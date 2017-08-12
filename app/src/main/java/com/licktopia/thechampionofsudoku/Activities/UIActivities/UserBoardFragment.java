package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Adapters.UserGameListAdapter;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.licktopia.thechampionofsudoku.ListItems.CompletedGameListItem;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.ListItems.SavedGameListItem;
import com.licktopia.thechampionofsudoku.Sudoku.FileIO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserBoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public static List<UserGameListItem> userGameListItems;







    public static UserBoardFragment newInstance() {return new UserBoardFragment();}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_board, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.user_board_rv);
        recyclerView.setLayoutManager((new GridLayoutManager(getContext(),2)));
        fillListItems();
        adapter = new UserGameListAdapter(userGameListItems , getContext());
        recyclerView.setAdapter(adapter);


        return view;
    }


    private void fillListItems(){
        userGameListItems = new ArrayList<>();
        FileIO fileIO = new FileIO();
        for(int i = 0; i<500;i++){
            UserGameListItem uGLI= new UserGameListItem(
                    BaseActivity.getId(),
                    String.format("%d",SystemClock.elapsedRealtime()),
                    BaseActivity.getUsername(),
                    fileIO.loadSpecificBoard(3, getActivity(),81,i),
                    507,
                    "Aug 11, 2017",
                    3);
            userGameListItems.add(uGLI);
        }

    }

}
