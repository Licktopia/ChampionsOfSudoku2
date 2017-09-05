package com.licktopia.thechampionofsudoku.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.UserBoardFragment;
import com.licktopia.thechampionofsudoku.DBOjects.CompletedGame;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.view.View.GONE;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.BOARD_FRAGMENT;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;

/**
 * Created by do_de on 3/30/2017.
 */

public class UserGameListAdapter extends RecyclerView.Adapter<UserGameListAdapter.ViewHolder> {

    public List<UserGameListItem> userGameListItems;
    private Context context;
    private String[] mColors = {"#1E91D6","#ea3f6a","#7D1538","#7c8597","#E18335","#4A2772","#F2CA08","#329d53"};

    private UserGameLAListener callback;
    public static boolean color = false;
    List<CompletedGame> thisBoardsCompletedGames;
    HashSet<Long> completedGameIds;
    private UserBoardFragment fragment;

    //Interface to be implemented by containing activity.
    public interface UserGameLAListener {
        void swapFragment(String fragmentName, Bundle args);

    }

    public UserGameListAdapter(List<UserGameListItem> userGameListItem, Context context, boolean colors, HashSet<Long> completedGameIds, UserBoardFragment fragment) {
        this.userGameListItems = userGameListItem;
        this.context = context;
        this.color = colors;
        this.completedGameIds = completedGameIds;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(viewType == R.layout.user_game_list_item){
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


        }else {

            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item_button, parent, false);
        }

            return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position == userGameListItems.size()) {
            if(fragment.levelNow>6){
                holder.itemView.setVisibility(GONE);
            }else {
                ImageView loadMoreButton = (ImageView) holder.itemView.findViewById(R.id.load_more);
                loadMoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragment.paginationList();
                    }
                });
            }


        }else {
            final UserGameListItem userGameListItem = userGameListItems.get(position);
            final int[] board = userGameListItem.getBoard();
            String gamerName = userGameListItem.getGamerName();
            int difficulty = userGameListItem.getDifficulty();
            String identifier = userGameListItem.getBoardIdentifier();
            final int level = userGameListItem.getLevel();
            setUpItem(holder, userGameListItem, board, gamerName, difficulty, identifier);
            setView(holder, level);
            setHolderClickListener(holder, userGameListItem, level);
            thisBoardsCompletedGames = new ArrayList<>();
        }
    }

    private void setUpItem(ViewHolder holder, UserGameListItem userGameListItem, int[] board, String gamerName, int difficulty, String identifier) {
        for(int i =0; i<81; i++){
            if(board[i]!=0) {
                if(!color) {
                    holder.squares[i].setText(String.format("%s", board[i]));
                }
                else{
                    holder.squares[i].setBackgroundColor(ContextCompat.getColor(context, Tags.colors[board[i]-1]));
                }
            }
            else{
                if(!color) {
                    holder.squares[i].setText("");
                }
                else{
                    holder.squares[i].setBackgroundColor(Color.WHITE);
                }
            }
        }
        holder.usUser.setText(new StringBuilder().append(gamerName).toString());
        holder.usNumber.setText(String.format("%s", identifier));
        holder.usDiff.setText(String.format(new StringBuilder().append(context.getString(R.string.difficulty)).append(" %d").toString(),difficulty));
        Picasso.with(context).load(R.drawable.key).into(holder.key);
        Picasso.with(context).load(R.drawable.share).into(holder.shareButton);

        if(completedGameIds.contains(Long.valueOf(userGameListItem.getBoardIdentifier()))){
            Picasso.with(context).load(R.drawable.whitecheck).into(holder.isCompleted);
        }
        else{
            Picasso.with(context).load(R.drawable.white_circle).into(holder.isCompleted);
        }
        /*
        for(Long i : UserBoardFragment.completedGameIds){
            if(i == Long.valueOf(userGameListItem.getBoardIdentifier())){
                holder.isCompleted.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.whitecheck));
            }
            else{
                holder.isCompleted.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.white_circle));
            }
        }
        */
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
                Long puzzleNo = thisUserGameListItem.getBoardIdentifierAsLong();
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
                            holder.progressBar.setVisibility(GONE);
                            showBoardStats(holder, thisUserGameListItem);
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
        int topScore = 0;
        double topTime = Double.POSITIVE_INFINITY;
        int usrTopScore = 0;
        double usrTopTime = Double.POSITIVE_INFINITY;
        String communityBlurb = "Be the first community member to play this Sudoku!";
        String playerBlurb = "Give this one a try?";
        final StringBuilder shareBlurb = new StringBuilder();
        shareBlurb.append("");
        int userScoreCounter = 0;
        int userTimeCounter = 0;
        for(CompletedGame i : thisBoardsCompletedGames){
            if(i.getRealScore()>topScore){
                topScore = i.getRealScore();
            }
            if(i.getGamerID().equals(BaseActivity.getId()) && i.getRealScore()>usrTopScore){
                usrTopScore = i.getRealScore();
                userScoreCounter++;
            }
            if(getSeconds(i.getTime())<topTime){
                topTime = getSeconds(i.getTime());
            }
            if(i.getGamerID().equals(BaseActivity.getId()) && getSeconds(i.getTime())<usrTopTime){
                usrTopTime = getSeconds(i.getTime());
                userTimeCounter++;
            }
            totalTime += getSeconds(i.getTime());
            totalScore += i.getRealScore();
        }

        if(size>0) {
            String bestTime = "";
            String topTimeWord = "";
            String bestScore = "";
            String topScoreWord = "";
            if(topTime>0 && topTime < Double.POSITIVE_INFINITY){
                bestTime = formatTime((int)topTime) + ". ";
                topTimeWord = "Top time ";
            }
            if(topScore>0){
                bestScore = topScore + ". ";
                topScoreWord = "Top score ";
            }
            int avgScore = Math.round(totalScore / size);
            String timeWord = " times ";
            if(size==1){
                timeWord= " time ";
            }
            String avgTime = formatTime(Math.round(totalTime / size));
            communityBlurb = new StringBuilder().append(size).append(timeWord).append("played. ").append(topTimeWord).append(bestTime).append(topScoreWord).append(bestScore).append("Average time ").append(avgTime).append(". Average score ").append(avgScore).toString();
            if(userScoreCounter>0 || userTimeCounter>0) {
                String userBestTime = "";
                String userBestTimeWord = "";
                String userBestScore = "";
                String userBestScoreWord = "";
                shareBlurb.append(". ");
                if (usrTopTime > 0) {
                    userBestTime = formatTime((int)usrTopTime);
                    userBestTimeWord = " Best time ";
                    shareBlurb.append(context.getString(R.string.my_best_time)).append(" ").append(userBestTime);
                }
                if (usrTopScore > 0) {
                    userBestScore = String.valueOf(usrTopScore) + ". ";
                    userBestScoreWord = " Best score ";
                    shareBlurb.append(". ").append(context.getString(R.string.my_best_sc)).append(" ").append(String.valueOf(usrTopScore)).append(" ");
                }
                playerBlurb = new StringBuilder().append(userBestScoreWord).append(userBestScore).append(userBestTimeWord).append(userBestTime).toString();
            }
        }
        holder.statsBox.setVisibility(View.VISIBLE);
        holder.communityText.setText(communityBlurb);
        holder.playerText.setText(playerBlurb);
        holder.playerLabel.setText(BaseActivity.getUsername());
        holder.statsBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.statsBox.setVisibility(GONE);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(userGameListItem, shareBlurb);
            }
        });
        holder.playNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.usBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.color5));
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
                                holder.statsBox.setVisibility(GONE);
                            }
                        })
                        .show();
            }
        });
    }

    private int getSeconds(String seconds) {
        seconds = seconds.replace(",", "");
        int intSeconds = (int) Math.round(Double.valueOf(seconds)/1000);
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

    private void share(UserGameListItem uGLI, StringBuilder shareBlurb) {
        Intent i = new Intent(
                Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, new StringBuilder().append("You have received a Sudoku Challenge from ").append(BaseActivity.getUsername()).append(". BOARD KEY: ").append(uGLI.getBoardIdentifier()).append(shareBlurb).append("\n\nDownload the app at https://play.google.com/store/apps/details?id=com.licktopia.thechampionofsudoku").toString());

        context.startActivity(Intent.createChooser(
                i,
                "Share key via"));
    }

    private void setView(ViewHolder holder, int level) {
        if(level==0) {
            Picasso.with(context).load(R.drawable.beginnericon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[0]));

        }
        else if(level==1) {
            Picasso.with(context).load(R.drawable.easyicon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[6]));
        }
        else if(level==2) {
            Picasso.with(context).load(R.drawable.mediumicon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[1]));
        }
        else if(level==3) {
            Picasso.with(context).load(R.drawable.hardicon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[5]));
        }
        else if(level==4) {
            Picasso.with(context).load(R.drawable.experticon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[2]));
        }
        else
        {
            Picasso.with(context).load(R.drawable.mastericon_white).into(holder.usLevel);
            holder.usBackground.setBackgroundColor(Color.parseColor(mColors[4]));
        }
    }


    @Override
    public int getItemCount() {
        if(fragment.levelNow<= 6) {
            return userGameListItems.size() + 1;
        }else{
            return userGameListItems.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == userGameListItems.size()) ? R.layout.user_game_list_item_button : R.layout.user_game_list_item;
        //return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView usUser;
        TextView usNumber;
        TextView usDiff;
        ImageView usLevel;
        ImageView isCompleted;
        LinearLayout usBackground;
        LinearLayout statsBox;
        TextView[] squares;
        TextView communityText;
        TextView playerText;
        TextView playerLabel;


        Button playNow;
        ImageView shareButton;
        ImageView key;
        ProgressBar progressBar;


        public ViewHolder(View itemView) {
            super(itemView);
            usUser = (TextView)itemView.findViewById(R.id.us_user);
            usNumber = (TextView)itemView.findViewById(R.id.us_number);
            usDiff = (TextView)itemView.findViewById(R.id.us_diff);
            communityText = (TextView)itemView.findViewById(R.id.comm_stats_text);
            playerText = (TextView)itemView.findViewById(R.id.user_stats_text);
            playerLabel = (TextView)itemView.findViewById(R.id.usr_label);
            usLevel = (ImageView) itemView.findViewById(R.id.us_level);
            isCompleted = (ImageView) itemView.findViewById(R.id.is_completed);
            usBackground = (LinearLayout)itemView.findViewById(R.id.us_background);
            statsBox = (LinearLayout)itemView.findViewById(R.id.board_stats_box);
            playNow = (Button)itemView.findViewById(R.id.us_play_now);
            shareButton = (ImageView)itemView.findViewById(R.id.us_share);
            key = (ImageView)itemView.findViewById(R.id.key);
            progressBar = (ProgressBar) itemView.findViewById(R.id.us_progress);
            squares = new TextView[81];
            for(int i = 0; i < 81; i++) {
                squares[i] = (TextView) itemView.findViewById(Tags.usSquareTags[i]);
            }
        }
    }
}
