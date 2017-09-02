package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.google.android.gms.games.Games;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Adapters.UserGameListAdapter;
import com.licktopia.thechampionofsudoku.CircleTransform;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.licktopia.thechampionofsudoku.Database.DatabaseAccess;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.gameHelper;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;


public class UserBoardFragment extends Fragment {

    private int DISPLAY_AT_A_TIME = 13;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<UserGameListItem> ShuffledList;
    public static UserBoardFragment newInstance() {return new UserBoardFragment();}
    private View view;
    private int levelNow = 6;
    private DatabaseAccess databaseAccess;
    private ProgressBar mProgressBar;
    private TextView progressText;
    private boolean color = false;
    private ImageButton colorSwitcher;
    private int gridNum = 2;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Integer> smallRange;
    private ArraySet<Integer> bigRange;
    private TextView titleLabel;
    private UserBoardFragment.UserBoardFragmentListener callback;
    private HashSet<Long> completedGameIds;
    private ImageView iconView;
    private int numBoards;
    private ArrayList<Integer> orderList;


    //Checks interface is implemented in the activity.
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            callback = (UserBoardFragment.UserBoardFragmentListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement BoardFragmentListener");
        }
    }

    //Interface to be implemented by containing activity.
    public interface UserBoardFragmentListener {
        void swapFragment(String fragmentName, Bundle args);
    }

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
            DISPLAY_AT_A_TIME+=2;
        }

        
        titleLabel = (TextView)view.findViewById(R.id.board_layout_title);

        iconView = (ImageView)view.findViewById(R.id.user_icon_usr);
        Picasso.with(getContext()).load(R.mipmap.ic_launcher).into(iconView);

        ImageView ubBackground = (ImageView)view.findViewById(R.id.ub_background);
        Picasso.with(getContext()).load(R.drawable.coolbackground).into(ubBackground);


        ImageButton colorSwitcher = (ImageButton)view.findViewById(R.id.color_switcher);
        Picasso.with(getContext()).load(R.drawable.peacock).into(colorSwitcher);

        ImageView ubToolbarBg = (ImageView)view.findViewById(R.id.ub_tool_background);
        Picasso.with(getContext()).load(R.drawable.keyboardbg).into(ubToolbarBg);

        ImageView keyIcon = (ImageView)view.findViewById(R.id.key_icon);
        Picasso.with(getContext()).load(R.drawable.key).into(keyIcon);

        ImageView questionIcon = (ImageView)view.findViewById(R.id.question_icon);
        Picasso.with(getContext()).load(R.drawable.question_white).into(questionIcon);

        ImageView refreshIcon = (ImageView)view.findViewById(R.id.refresh_icon);
        Picasso.with(getContext()).load(R.drawable.more_white).into(refreshIcon);

        ImageView whiteCheckIcon = (ImageView)view.findViewById(R.id.whitecheck_icon);
        Picasso.with(getContext()).load(R.drawable.whitecheck).into(whiteCheckIcon);

        ImageView keyButtonIcon = (ImageView)view.findViewById(R.id.keybutton_icon);
        Picasso.with(getContext()).load(R.drawable.key).into(keyButtonIcon);

        ImageView userButtonsBackground = (ImageView)view.findViewById(R.id.user_buttons_background);
        Picasso.with(getContext()).load(R.drawable.keyboardbg).into(userButtonsBackground);

        ImageButton beg = (ImageButton)view.findViewById(R.id.beg);
        Picasso.with(getContext()).load(R.drawable.beginnericon_white).into(beg);
        ImageButton eas = (ImageButton)view.findViewById(R.id.eas);
        Picasso.with(getContext()).load(R.drawable.easyicon_white).into(eas);
        ImageButton med = (ImageButton)view.findViewById(R.id.med);
        Picasso.with(getContext()).load(R.drawable.mediumicon_white).into(med);
        ImageButton har = (ImageButton)view.findViewById(R.id.har);
        Picasso.with(getContext()).load(R.drawable.hardicon_white).into(har);
        ImageButton exp = (ImageButton)view.findViewById(R.id.exp);
        Picasso.with(getContext()).load(R.drawable.experticon_white).into(exp);
        ImageButton mas = (ImageButton)view.findViewById(R.id.mas);
        Picasso.with(getContext()).load(R.drawable.mastericon_white).into(mas);
        bigRange = new ArraySet<>();
        smallRange = new ArrayList<>();
        orderList = new ArrayList<>();

        titleLabel.setText(R.string.all_level);

        if(ShuffledList==null) {
            mProgressBar = (ProgressBar) view.findViewById(R.id.usr_progress);
            progressText = (TextView) view.findViewById(R.id.loading_usr);
            recyclerView = (RecyclerView) view.findViewById(R.id.user_board_rv);
            gridLayoutManager = new GridLayoutManager(getContext(), gridNum);
            recyclerView.setLayoutManager((gridLayoutManager));
            completedGameIds = new HashSet<>();
            /*
            DividerItemDecoration itemDecoratorVer = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            itemDecoratorVer.setDrawable(getResources().getDrawable(R.drawable.spacer_vertical, null));
            DividerItemDecoration itemDecoratorHor = new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL);
            itemDecoratorHor.setDrawable(getResources().getDrawable(R.drawable.spacer_horizontal, null));
            recyclerView.addItemDecoration(itemDecoratorVer);
            recyclerView.addItemDecoration(itemDecoratorHor);
            */

            final boolean[] loading = {true};
            final int[] pastVisiblesItems = new int[1];
            final int[] visibleItemCount = new int[1];
            final int[] totalItemCount = new int[1];



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
                    } catch (Exception e) {

                    }
                    completedGameIds.clear();
                    for (int i = 0; i < list[0].size(); i++) {
                        completedGameIds.add(list[0].get(i).getPuzzleNumber());
                    }
                    databaseAccess = DatabaseAccess.getInstance(getContext());
                    databaseAccess.open();
                    numBoards = databaseAccess.getNumBoards();
                    resetBigRange();
                    ShuffledList = new ArrayList<>();
                    for (int i = 0; i < DISPLAY_AT_A_TIME; i++) {
                        ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.valueAt(orderList.get(i))));
                    }

                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                    initializeButtons();
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //execute code on main thread
                                mProgressBar.setVisibility(GONE);
                                progressText.setVisibility(GONE);
                                recyclerView.setAdapter(adapter);
                                /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
                                {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        if (levelNow != 7){
                                            if (dy > 0) //check for scroll down
                                            {
                                                visibleItemCount[0] = gridLayoutManager.getChildCount();
                                                totalItemCount[0] = gridLayoutManager.getItemCount();
                                                pastVisiblesItems[0] = gridLayoutManager.findFirstVisibleItemPosition();

                                                if (loading[0]) {
                                                    if ((visibleItemCount[0] + pastVisiblesItems[0]) >= totalItemCount[0]) {
                                                        loading[0] = false;
                                                        Log.v("...", "Last Item Wow !");
                                                        mProgressBar.setVisibility(View.VISIBLE);
                                                        progressText.setVisibility(View.VISIBLE);
                                                        Handler loadMoreHandle = new Handler();
                                                        loadMoreHandle.postDelayed(new Runnable() {  // Wait then remove error color
                                                            @Override
                                                            public void run() {
                                                                paginationList();
                                                                loading[0] = true;
                                                                mProgressBar.setVisibility(View.GONE);
                                                                progressText.setVisibility(View.GONE);
                                                            }
                                                        }, 1500);

                                                    }
                                                }
                                            }
                                        }
                                    }
                                });*/
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
        }

        return view;
    }

    private void resetBigRange() {
        orderList.clear();
        bigRange.clear();
        Random rand = new Random();
        int randomNum;
        while(bigRange.size()<DISPLAY_AT_A_TIME){
            randomNum = rand.nextInt(numBoards)+1;
            bigRange.add(randomNum);
        }
        for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
            orderList.add(i);
        }
        Collections.shuffle(orderList);

    }

    private void resetSmallRange(int level) {
        smallRange.clear();
        smallRange = databaseAccess.getIdentifiersFromLevel(level, DISPLAY_AT_A_TIME);

    }

    @Override
    public void onPause(){
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        if(gameHelper.isSignedIn()){
            CircleTransform cf = new CircleTransform();
            try {
                String me = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getIconImageUrl();
                Picasso.with(getActivity()).load(me).transform(cf).into(iconView);
            }catch (Exception e){

            }
        }
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.swapFragment(BaseActivity.MAIN_MENU_FRAGMENT, new Bundle());
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();

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
                    titleLabel.setText(new StringBuilder().append(Tags.Levels[levelNow]).append(" Sudokus").toString());
                    ShuffledList.clear();
                    //DisplayNowList = LevelLists[finalI].subList(0,DISPLAY_AT_A_TIME);
                    resetSmallRange(finalI1);
                    for(int j = 0; j < DISPLAY_AT_A_TIME; j++){
                        ShuffledList.add(databaseAccess.getSpecificBoardGame(smallRange.get(j)));
                    }
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
        LinearLayout ranButton = (LinearLayout)view.findViewById(R.id.ran);
        ranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelNow = 6;
                titleLabel.setText(R.string.all_level);
                resetBigRange();
                //DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
                ShuffledList.clear();
                for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                    ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.valueAt(orderList.get(i))));
                }
                adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds, UserBoardFragment.this);
                recyclerView.setAdapter(adapter);
            }
        });

        LinearLayout moreButton = (LinearLayout) view.findViewById(R.id.more);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginationList();
            }
        });
        colorSwitcher = (ImageButton)view.findViewById(R.id.color_switcher);
        colorSwitcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(color){
                    color = false;
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                    Picasso.with(getContext()).load(R.drawable.peacock).into(colorSwitcher);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    color = true;
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                    Picasso.with(getContext()).load(R.drawable.rocket).into(colorSwitcher);
                    recyclerView.setAdapter(adapter);
                }

            }
        });
        LinearLayout userPlayedButton = (LinearLayout) view.findViewById(R.id.my_completed_button);
        userPlayedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelNow = 7;
                ShuffledList.clear();
                for(Long i : completedGameIds){
                    ShuffledList.add(databaseAccess.getSpecificBoardGame(i));
                }
                adapter = null;
                adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                recyclerView.setAdapter(adapter);
            }
        });

        final RelativeLayout enterBoard = (RelativeLayout)view.findViewById(R.id.num_picker);
        final EditText boardNum = (EditText) view.findViewById(R.id.num_board);
        Button okButton = (Button)view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredNum = boardNum.getText().toString();
                UserGameListItem usgli;
                try {
                    usgli = databaseAccess.getSpecificBoardGame(Long.valueOf(enteredNum));
                    ShuffledList.clear();
                    ShuffledList.add(usgli);
                    adapter = null;
                    adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
                    recyclerView.setAdapter(adapter);
                }catch(Exception e){
                    Log.d(TAG, "onClick: Couldn't retrieve user entered board");
                    Toast.makeText(getContext(), new StringBuilder().append(getString(R.string.couldnt_find)).append(enteredNum).toString(), Toast.LENGTH_SHORT).show();
                }
                try  {
                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
                enterBoard.setVisibility(GONE);
            }
        });
        LinearLayout enterBoardNumber = (LinearLayout) view.findViewById(R.id.enter_board_key);
        enterBoardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterBoard.setVisibility(View.VISIBLE);
            }
        });
    }

    public void paginationList() {
        if(levelNow<6) {
            resetSmallRange(levelNow);
            ShuffledList.clear();
            for(int j = 0; j < DISPLAY_AT_A_TIME; j++){
                ShuffledList.add(databaseAccess.getSpecificBoardGame(smallRange.get(j)));
            }
            //DisplayNowList = LevelLists[levelNow].subList(0, DISPLAY_AT_A_TIME);
            adapter = null;
            adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
            recyclerView.setAdapter(adapter);
        }
        else if(levelNow == 6){
            resetBigRange();
            ShuffledList.clear();
            for(int i = 0; i < DISPLAY_AT_A_TIME; i++){
                ShuffledList.add(databaseAccess.getSpecificBoardGame(bigRange.valueAt(orderList.get(i))));
            }
            //DisplayNowList = ShuffledList.subList(0, DISPLAY_AT_A_TIME);
            adapter = null;
            adapter = new UserGameListAdapter(ShuffledList, getContext(), color, completedGameIds,UserBoardFragment.this);
            recyclerView.setAdapter(adapter);
        }
    }
}
