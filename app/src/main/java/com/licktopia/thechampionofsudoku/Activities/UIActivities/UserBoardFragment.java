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
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.RawBoards;
import com.licktopia.thechampionofsudoku.Sudoku.FileIO;
import java.util.ArrayList;
import java.util.List;

import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;


public class UserBoardFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    public static List<UserGameListItem> userGameListItems;
    public static UserBoardFragment newInstance() {return new UserBoardFragment();}
    private View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(phoneSize==1) {
            view = inflater.inflate(R.layout.fragment_user_board_small, container, false);
        }
        else if(phoneSize == 3){
            view = inflater.inflate(R.layout.fragment_user_board_large, container, false);
        }
        else{
            view = inflater.inflate(R.layout.fragment_user_board, container, false);
        }
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
                    convert(RawBoards.easyBoard[i].toCharArray()),
                    507,
                    "Aug 11, 2017",
                    3);
            userGameListItems.add(uGLI);
        }

    }

    private int[] convert(char[] board){
        int newBoard[] = new int[81];
        for(int i =0; i < 81; i++){
            newBoard[i] = Character.getNumericValue(board[i]);
        }
        return newBoard;
    }

}
