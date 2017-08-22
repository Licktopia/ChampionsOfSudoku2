package com.licktopia.thechampionofsudoku.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Tags;

import java.util.List;

import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.BOARD_FRAGMENT;
import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;

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


        setView(holder, level);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.usBackground.setBackgroundColor(ContextCompat.getColor(context,R.color.color5));
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
                                setView(holder,level);
                            }
                        })
                        .show();
            }
        });


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
        LinearLayout usBackground;

        


        public ViewHolder(View itemView) {
            super(itemView);
            usUser = (TextView)itemView.findViewById(R.id.us_user);
            usDate = (TextView)itemView.findViewById(R.id.us_date);
            usDiff = (TextView)itemView.findViewById(R.id.us_diff);
            usLevel = (ImageView) itemView.findViewById(R.id.us_level);
            usBackground = (LinearLayout)itemView.findViewById(R.id.us_background);
        }

    }

}
