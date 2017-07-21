package com.licktopia.thechampionofsudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.BoardActivity;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;

import java.util.List;

/**
 * Created by do_de on 3/30/2017.
 */

public class SavedGameListAdapter extends RecyclerView.Adapter<SavedGameListAdapter.ViewHolder> {

    private List<SavedGameListItem> savedGameListItems;
    private Context context;
    private String[] mColors = {"#1E91D6","#ea3f6a","#7D1538","#7c8597","#E18335","#4A2772","#F2CA08","#329d53"};
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;



    public SavedGameListAdapter(List<SavedGameListItem> savedGameListItem, Context context) {
        this.savedGameListItems = savedGameListItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.saved_game_list_item, parent, false);

            return new ViewHolder(v);



    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SavedGameListItem savedGameListItem = savedGameListItems.get(position);
        final int boardNumber = savedGameListItem.getPuzzleNumber();
        final String exactTime = savedGameListItem.getExactTime();
        final String gamerId = savedGameListItem.getGamerId();

        holder.deleteSavedGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.puzzle_number) + boardNumber)
                        .setCancelable(true)
                        .setPositiveButton(R.string.delete_saved, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //delete from database here
                                deleteRecord(exactTime, gamerId);
                                boolean deleted = true;
                                if(deleted){
                                    savedGameListItems.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context, R.string.saved_game_succ_deleted, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context,"Unable to delete the selected item.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });


        holder.startSavedGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.puzzle_number) + boardNumber)
                        .setCancelable(true)
                        .setPositiveButton(R.string.resume_game, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    /***** Maybe we should tell the user that the game is loading... *****/
                                    /******* FOR THE PURPOSE OF DEBUGGING THE FIRST GAME ON THE LIST GETS PICKED******************/
                                    Intent intent = new Intent(context, BoardActivity.class);
                                    //Make level less than zero so that activity knows to load a game
                                    boolean resume = true;
                                    intent.putExtra("RESUME", resume);
                                    intent.putExtra("COLOR_MODE", BaseActivity.getSavedGames().get(position).getColorMode());
                                    intent.putExtra("SAVE_FILE", BaseActivity.getSavedGames().get(position).getSaveFile());
                                    intent.putExtra("STOPPED_TIME", BaseActivity.getSavedGames().get(position).getStoppedTime());
                                    context.startActivity(intent);



                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
            }
        });



        holder.savedGameTime.setText(savedGameListItem.getTime());
        holder.savedGameDate.setText(savedGameListItem.getDate());



        if(!savedGameListItem.isColor()) {
            String name = "rocket";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.rocket);
            holder.savedGameType.setImageDrawable(drawable);

        }
        else{
            String name = "peacock";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.peacock);
            holder.savedGameType.setImageDrawable(drawable);

        }


        if(savedGameListItem.getLevel()==0) {
            String name = "beginnericon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.beginnericon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[0]));

        }
        else if(savedGameListItem.getLevel()==1) {
            String name = "easyicon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.easyicon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[6]));
        }
        else if(savedGameListItem.getLevel()==2) {
            String name = "mediumicon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mediumicon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[1]));
        }
        else if(savedGameListItem.getLevel()==3) {
            String name = "hardicon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.hardicon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[5]));
        }
        else if(savedGameListItem.getLevel()==4) {
            String name = "experticon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.experticon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[2]));
        }
        else //if(completedGameListItem.getLevel()==5) {
        {
            String name = "mastericon";

            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mastericon);
            holder.savedGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[4]));
        }



    }

    private void deleteRecord(String eTime, String gID){
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-2:40802ef0-7537-4910-8442-31e2b30c5a7d", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));

        if(isInternet()) {
            final SavedGame sg = new SavedGame();
            sg.setGamerID(gID);
            sg.setExactTime(eTime);



            // id field is null at this point
            Runnable runnable = new Runnable() {
                public void run() {
                    //DynamoDB calls go here
                    mapper.delete(sg);

                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
        }
    }

    private boolean isInternet(){
        boolean isConnected;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }
    @Override
    public int getItemCount() {
        return savedGameListItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public Button startSavedGame;
        public Button deleteSavedGame;
        public TextView savedGameDate;
        public TextView savedGameTime;
        public ImageView savedGameType;
        public ImageView savedGameLevel;

        public ViewHolder(View itemView) {
            super(itemView);

            startSavedGame = (Button)itemView.findViewById(R.id.start_savedgame);
            deleteSavedGame = (Button)itemView.findViewById(R.id.delete_savedgame);
            savedGameDate = (TextView)itemView.findViewById(R.id.savedgame_date);
            savedGameTime = (TextView)itemView.findViewById(R.id.savedgame_time);
            savedGameType = (ImageView)itemView.findViewById(R.id.savedgame_type);
            savedGameLevel = (ImageView)itemView.findViewById(R.id.savedgame_level);

        }

    }

}
