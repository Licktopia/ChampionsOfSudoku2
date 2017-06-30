package com.licktopia.thechampionofsudoku;



/**
 * Created by do_de on 3/30/2017.
 */

public class ListItem {
    private double accuracy;
    private int score;
    private String date;
    private int level;
    private int puzzleNumber;
    private int realScore;
    private String time;
    private int xp;
    private long GamerId;
    private long GameId;



    private boolean color;

    public ListItem(double accuracy, int score, String date, int level, int puzzleNumber, int realScore, String time, int xp, long gamerId, long gameId,boolean color) {
        this.accuracy = accuracy;
        this.score = score;
        this.date = date;
        this.level = level;
        this.puzzleNumber = puzzleNumber;
        this.realScore = realScore;
        this.time = time;
        this.xp = xp;
        this.GamerId = gamerId;
        this.GameId = gameId;

    }



    public double getAccuracy() {
        return accuracy;
    }

    public int getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public int getLevel() {
        return level;
    }

    public int getPuzzleNumber() {
        return puzzleNumber;
    }

    public int getRealScore() {
        return realScore;
    }

    public String getTime() {
        return time;
    }

    public int getXp() {
        return xp;
    }

    public long getGamerId() {
        return GamerId;
    }

    public long getGameId() {
        return GameId;
    }

    public boolean isColor() {
        return color;
    }




}
