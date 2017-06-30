package com.licktopia.thechampionofsudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.List;

/**
 * Created by do_de on 3/30/2017.
 */

public class CompletedGameListAdapter extends RecyclerView.Adapter<CompletedGameListAdapter.ViewHolder> {

    private List<ListItem> listItems;
    private Context context;
    private String[] mColors = {"#1E91D6","#ea3f6a","#7D1538","#7c8597","#E18335","#4A2772","#F2CA08","#329d53"};



    public CompletedGameListAdapter(List<ListItem> listItem, Context context) {
        this.listItems = listItem;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.completed_game_list_item, parent, false);

            return new ViewHolder(v);



    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ListItem listItem = listItems.get(position);
        final int boardNumber = listItem.getPuzzleNumber();
        final long idNumber = listItem.getGamerId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Puzzle number #" + boardNumber)
                        .setCancelable(true)
                        .setPositiveButton("Delete Record", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //delete from database here
                                boolean deleted = true;
                                if(deleted){
                                    listItems.remove(position);
                                    notifyDataSetChanged();
                                    Toast.makeText(context,"Item successfully deleted.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(context,"Unable to delete the selected item.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("Keep Record", null)
                        .show();
            }
        });



        holder.tvGameScore.setText(String.format("%1$d",listItem.getScore()) + "XP");
        holder.tvGameTime.setText(listItem.getTime());
        holder.tvGameDate.setText(listItem.getDate());
        holder.tvGameSquares.setText(String.format("%,s",listItem.getAccuracy()));


        if(!listItem.isColor()) {
            String name = "rocket";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.rocket);
            holder.tvGameType.setImageDrawable(drawable);

        }
        else{
            String name = "peacock";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.peacock);
            //Drawable drawable = context.getResources().getDrawable(id, null);
            holder.tvGameType.setImageDrawable(drawable);

        }


        if(listItem.getLevel()==0) {
            String name = "beginnericon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.beginnericon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[0]));

        }
        else if(listItem.getLevel()==1) {
            String name = "easyicon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.easyicon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[6]));
        }
        else if(listItem.getLevel()==2) {
            String name = "mediumicon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mediumicon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[1]));
        }
        else if(listItem.getLevel()==3) {
            String name = "hardicon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.hardicon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[5]));
        }
        else if(listItem.getLevel()==4) {
            String name = "experticon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.experticon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[2]));
        }
        else //if(listItem.getLevel()==5) {
        {
            String name = "mastericon";
            int id = context.getResources().getIdentifier(name, "drawable",
                    context.getPackageName());
            //Drawable drawable = context.getResources().getDrawable(id, null);
            Drawable drawable = ContextCompat.getDrawable(context,R.drawable.mastericon);
            holder.tvGameLevel.setImageDrawable(drawable);
            holder.itemView.setBackgroundColor(Color.parseColor(mColors[4]));
        }



    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvGameScore;
        public TextView tvGameTime;
        public TextView tvGameDate;
        public TextView tvGameSquares;
        public ImageView tvGameType;
        public ImageView tvGameLevel;

        public ViewHolder(View itemView) {
            super(itemView);

            tvGameScore = (TextView)itemView.findViewById(R.id.game_score);
            tvGameTime = (TextView)itemView.findViewById(R.id.game_time);
            tvGameDate = (TextView)itemView.findViewById(R.id.game_date);
            tvGameSquares = (TextView)itemView.findViewById(R.id.game_squares);
            tvGameType = (ImageView)itemView.findViewById(R.id.game_type);
            tvGameLevel = (ImageView)itemView.findViewById(R.id.game_level);

        }

    }

}
