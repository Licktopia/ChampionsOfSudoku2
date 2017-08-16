package com.licktopia.thechampionofsudoku.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by do_de on 3/30/2017.
 */

public class UserGameListAdapter extends RecyclerView.Adapter<UserGameListAdapter.ViewHolder> {

    private List<UserGameListItem> userGameListItems;
    private Context context;
    private String[] mColors = {"#1E91D6","#ea3f6a","#7D1538","#7c8597","#E18335","#4A2772","#F2CA08","#329d53"};
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    TextView[] squares;




    public UserGameListAdapter(List<UserGameListItem> userGameListItem, Context context) {
        this.userGameListItems = userGameListItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_game_list_item, parent, false);

        squares = new TextView[81];
        for(int i = 0; i < 81; i++) {
            squares[i] = (TextView) v.findViewById(Tags.usSquareTags[i]);
        }
            return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position) {

        UserGameListItem userGameListItem = userGameListItems.get(position);
        int[] board = userGameListItem.getBoard();
        String gamerName = userGameListItem.getGamerName();
        int difficulty = userGameListItem.getDifficulty();
        String date = userGameListItem.getDate();
        int level = userGameListItem.getLevel();
        for(int i =0; i<81; i++){
            if(board[i]!=0) {
                squares[i].setText(String.format("%s",board[i]));
            }
            else{
                squares[i].setText("");
            }
        }
    
        holder.usUser.setText(gamerName);
        holder.usDate.setText(date);
        holder.usDiff.setText(String.format("%d",difficulty));
        holder.usNumb.setText(String.format("%d",position+1));


        if(level==0) {

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.beginnericon);
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
        TextView usNumb;
        ImageView usLevel;
        LinearLayout usBackground;
        


        public ViewHolder(View itemView) {
            super(itemView);
            usUser = (TextView)itemView.findViewById(R.id.us_user);
            usDate = (TextView)itemView.findViewById(R.id.us_date);
            usDiff = (TextView)itemView.findViewById(R.id.us_diff);
            usNumb = (TextView)itemView.findViewById(R.id.us_number);
            usLevel = (ImageView) itemView.findViewById(R.id.us_level);
            usBackground = (LinearLayout)itemView.findViewById(R.id.us_background);
            
        }

    }

}
