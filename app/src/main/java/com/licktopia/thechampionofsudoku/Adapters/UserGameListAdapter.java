package com.licktopia.thechampionofsudoku.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.google.android.gms.games.Games;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.UserBoardFragment;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.licktopia.thechampionofsudoku.Database.DatabaseAccess;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.amazonaws.services.dynamodbv2.model.ComparisonOperator.EQ;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.BOARD_FRAGMENT;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.gameHelper;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;
import static com.licktopia.thechampionofsudoku.Activities.UIActivities.MainMenuFragment.mProgressBar;

/**
 * Created by do_de on 3/30/2017.
 */

public class UserGameListAdapter extends RecyclerView.Adapter<UserGameListAdapter.ViewHolder> {

    public void setUserGameListItems(List<UserGameListItem> userGameListItems) {
        this.userGameListItems = userGameListItems;
    }

    public List<UserGameListItem> userGameListItems;
    private Context context;
    private String[] mColors = {"#1E91D6","#ea3f6a","#7D1538","#7c8597","#E18335","#4A2772","#F2CA08","#329d53"};
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    TextView[] squares;
    private UserGameLAListener callback;
    public static boolean color = false;
    List<CompletedGame> thisBoardsCompletedGames;

    //Interface to be implemented by containing activity.
    public interface UserGameLAListener {
        void swapFragment(String fragmentName, Bundle args);

    }




    public UserGameListAdapter(List<UserGameListItem> userGameListItem, Context context, boolean color) {
        this.userGameListItems = userGameListItem;
        this.context = context;
        this.color = color;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(phoneSize==1) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item_small, parent, false);
        }
        else if(phoneSize == 2){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item, parent, false);
        }
        else if(phoneSize == 3){
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item_phablet, parent, false);
        }
        else{
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item_large, parent, false);
        }

        squares = new TextView[81];
        for(int i = 0; i < 81; i++) {
            squares[i] = (TextView) v.findViewById(Tags.usSquareTags[i]);
        }
            return new ViewHolder(v);
    }

    public void toggleColor(boolean col){
        if(col==false){
            this.color = false;
        }
        else{
            this.color = true;
        }


    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final UserGameListItem userGameListItem = userGameListItems.get(position);
        final int[] board = userGameListItem.getBoard();
        String gamerName = userGameListItem.getGamerName();
        int difficulty = userGameListItem.getDifficulty();
        String date = userGameListItem.getDate();
        final int level = userGameListItem.getLevel();
        setUpItem(holder, userGameListItem, board, gamerName, difficulty, date);
        setView(holder, level);
        setHolderClickListener(holder, userGameListItem, level);
        thisBoardsCompletedGames = new ArrayList<>();


    }

    private void setUpItem(ViewHolder holder, UserGameListItem userGameListItem, int[] board, String gamerName, int difficulty, String date) {
        for(int i =0; i<81; i++){
            if(board[i]!=0) {
                if(!color) {
                    squares[i].setText(String.format("%s", board[i]));
                }
                else{
                    squares[i].setBackgroundColor(ContextCompat.getColor(context, Tags.colors[board[i]-1]));
                }
            }
            else{
                if(!color) {
                    squares[i].setText("");
                }
                else{
                    squares[i].setBackgroundColor(Color.WHITE);
                }
            }
        }
        holder.usUser.setText(new StringBuilder().append(gamerName).toString());
        holder.usDate.setText(date);
        holder.usDiff.setText(String.format(new StringBuilder().append("Diff.").append(" %d").toString(),difficulty));
        for(CompletedGame i : BaseActivity.getCompletedGames()){
            if(i.getPuzzleNumber() == Long.valueOf(userGameListItem.getBoardIdentifier())){
                holder.isCompleted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cbut_darkgreendot));
            }
            else{
                holder.isCompleted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.cbut_darkreddot));
            }
        }
    }

    private void setHolderClickListener(final ViewHolder holder, final UserGameListItem userGameListItem, final int level) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*

                */
            getCommunityStatsForBoard(userGameListItem, holder);
            }
        });
    }

    private void getCommunityStatsForBoard(final UserGameListItem thisUserGameListItem, final ViewHolder holder){
        thisBoardsCompletedGames.clear();
        holder.progressBar.setVisibility(View.VISIBLE);
        Runnable runnable = new Runnable() {
            public void run() {
                Long puzzleNo = Long.valueOf(4225);
                CompletedGame cg = new CompletedGame();
                cg.setPuzzleNumber(puzzleNo);

                final DynamoDBQueryExpression<CompletedGame> queryExpression = new DynamoDBQueryExpression<CompletedGame>()
                        .withHashKeyValues(cg)
                        .withIndexName("PuzzleNo-index")
                        .withConsistentRead(false);

                final List<CompletedGame>[] list = new List[]{new ArrayList<>()};
                try {
                    list[0] = BaseActivity.mapper.query(CompletedGame.class, queryExpression);
                }
                catch(Exception e){
                    Log.d("DYNAMO", String.valueOf(e));
                }

                for (int i = 0; i < list[0].size(); i++) {
                   thisBoardsCompletedGames.add(list[0].get(i));
                }
                try {
                        ((BaseActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //execute code on main thread
                            //mProgressBar.setVisibility(View.GONE);
                            if(thisBoardsCompletedGames.size()>0) {
                                holder.progressBar.setVisibility(View.GONE);
                                showBoardStats(holder, thisUserGameListItem);
                            }

                        }
                    });
                }catch(Exception e){

                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    private void showBoardStats(final ViewHolder holder, final UserGameListItem userGameListItem){
        int size = thisBoardsCompletedGames.size();
        int totalTime = 0;
        int totalScore = 0;
        for(CompletedGame i : thisBoardsCompletedGames){
            totalTime += getSeconds(i.getTime());
            totalScore += i.getRealScore();
        }
        int avgScore = Math.round(totalScore/size);
        String avgTime = formatTime(Math.round(totalTime/size));
        holder.statsBox.setVisibility(View.VISIBLE);
        holder.timesPlayed.setText(new StringBuilder().append("Times played").append(" ").append(size).toString());
        holder.averageScore.setText(new StringBuilder().append("Avg score").append(" ").append(avgScore).toString());
        holder.avgTime.setText(new StringBuilder().append("Avg time").append(" ").append(avgTime));
        holder.statsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.statsBox.setVisibility(View.GONE);
            }
        });
        holder.playNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                holder.usBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.color5));
                String colOrNum;
                if(!color){
                    colOrNum = context.getString(R.string.num);
                }
                else{
                    colOrNum = context.getString(R.string.art);
                }
                new AlertDialog.Builder(context)
                        .setMessage(new StringBuilder().append(context.getString(R.string.start_ga)).append(" ").append(colOrNum).append(" ").append(context.getString(R.string.mode)).toString())
                        .setCancelable(false)
                        .setPositiveButton(R.string.begin, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Bundle args = new Bundle();



                                //Make level less than zero so that activity knows to load a game

                                args.putBoolean("RESUME", false);//resume is false
                                args.putBoolean("COLOR_MODE", color);
                                args.putInt("LEVEL", userGameListItem.getLevel());
                                args.putIntArray("USER_BOARD", userGameListItem.getBoard());
                                args.putLong("BOARD_IDENTIFIER", userGameListItem.getBoardIdentifierAsLong());
                                try {
                                    callback = (UserGameLAListener) context;
                                } catch (ClassCastException e) {
                                    throw new ClassCastException(context.toString() +
                                            " must implement UserGameLAFragmentListener");
                                }
                                callback.swapFragment(BOARD_FRAGMENT, args);


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                holder.statsBox.setVisibility(View.GONE);
                            }
                        })
                        .show();
                */
            share(userGameListItem);

            }
        });
    }

    private int getSeconds(String seconds) {
        seconds = seconds.replace(",", "");
        int intSeconds = Integer.valueOf(seconds);
        return intSeconds;
    }


    private String formatTime(int intSeconds){
        String formattedTime;
        int minutes = intSeconds/60;
        int secs = intSeconds%60;
        String minutos = String.format("%d",minutes);
        String segundos = String.format("%d",secs);
        if(segundos.length()==1){
            segundos = "0" + segundos;
        }
        formattedTime = minutos +  ":" +segundos;

        return formattedTime;
    }

    private void share(UserGameListItem uGLI) {

        Intent i = new Intent(

                Intent.ACTION_SEND);

        i.setType("text/plain");
        i.putExtra(

                Intent.EXTRA_TEXT, "Sudoku Challenge from " + BaseActivity.getUsername() + ". CODE: " + uGLI.getBoardIdentifier()+ "?" + uGLI.getGamerID());

        context.startActivity(Intent.createChooser(

                i,

                "Share via"));
    }

    private void setView(ViewHolder holder, int level) {
        if(level==0) {

            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.beginnericon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[0]));

        }
        else if(level==1) {


            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.easyicon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[6]));
        }
        else if(level==2) {


            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mediumicon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[1]));
        }
        else if(level==3) {

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.hardicon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[5]));
        }
        else if(level==4) {


            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.experticon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[2]));
        }
        else
        {


            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mastericon);
            holder.usLevel.setImageDrawable(drawable);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[4]));
        }
    }


    @Override
    public int getItemCount() {
        return userGameListItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView usUser;
        TextView usDate;
        TextView usDiff;
        ImageView usLevel;
        ImageView isCompleted;
        LinearLayout usBackground;
        LinearLayout statsBox;
        TextView averageScore;
        TextView timesPlayed;
        TextView avgTime;
        Button playNow;
        ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            usUser = (TextView)itemView.findViewById(R.id.us_user);
            usDate = (TextView)itemView.findViewById(R.id.us_date);
            usDiff = (TextView)itemView.findViewById(R.id.us_diff);
            averageScore = (TextView)itemView.findViewById(R.id.avg_score);
            timesPlayed = (TextView)itemView.findViewById(R.id.times_played);
            avgTime = (TextView)itemView.findViewById(R.id.avg_time);
            usLevel = (ImageView) itemView.findViewById(R.id.us_level);
            isCompleted = (ImageView) itemView.findViewById(R.id.is_completed);
            usBackground = (LinearLayout)itemView.findViewById(R.id.us_background);
            statsBox = (LinearLayout)itemView.findViewById(R.id.board_stats_box);
            playNow = (Button)itemView.findViewById(R.id.us_play_now);
            progressBar = (ProgressBar) itemView.findViewById(R.id.us_progress);
        }
    }
}
