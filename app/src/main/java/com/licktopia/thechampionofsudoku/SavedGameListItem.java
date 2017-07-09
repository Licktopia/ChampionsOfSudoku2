package com.licktopia.thechampionofsudoku;



/**
 * Created by do_de on 3/30/2017.
 */

public class SavedGameListItem {


    private String date;
    private int level;
    private int puzzleNumber;
    private String time;
    private String exactTime;
    private String gamerID;




    private boolean color;

    public SavedGameListItem(String date,String time, boolean colorMode, String exactTime, int level, int puzzleNumber, String gamerID) {

        this.date = date;
        this.level = level;
        this.puzzleNumber = puzzleNumber;
        this.time = time;
        this.exactTime = exactTime;
        this.color = colorMode;
        this.gamerID = gamerID;

    }

    public String getDate() {
        return date;
    }
    public String getGamerId() { return gamerID;
    }

    public int getLevel() {
        return level;
    }

    public int getPuzzleNumber() {
        return puzzleNumber;
    }


    public String getTime() {

        return time;
    }


    public boolean isColor() {
        return color;
    }

    public String getExactTime() {
        return exactTime;
    }






}
