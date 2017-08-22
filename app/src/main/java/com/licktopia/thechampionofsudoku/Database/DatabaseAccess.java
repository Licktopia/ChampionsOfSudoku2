package com.licktopia.thechampionofsudoku.Database;

/**
 * Created by do_de on 8/19/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.SweepGradient;

import com.licktopia.thechampionofsudoku.ListItems.UserGameListItem;
import com.licktopia.thechampionofsudoku.Tags;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.
     *
     * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.
     *
     * @param context the Context
     * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    /**
     * Read all quotes from the database.
     *
     * @return a List of quotes
     */
    public ArrayList<UserGameListItem> getStockGamesFromLevel(String level, int levelNum) {
        ArrayList <UserGameListItem> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + level, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserGameListItem userGameListItem = new UserGameListItem(
                    cursor.getString(2),
                    cursor.getString(0),
                    cursor.getString(3),
                    convert(cursor.getString(1).toCharArray()),
                    Integer.parseInt(cursor.getString(5)),
                    cursor.getString(4),
                    levelNum);
            list.add(userGameListItem);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public int[] getSpecificBoard(long boardIndex){
        String tableName;
        int levelNum = (int) (boardIndex/10000);

        String identifier = Long.toString(boardIndex);
        String rowName = "Identifier";

        switch(levelNum)
        {
            case 0:
                tableName = "Beginner";
                break;
            case 1:
                tableName = "Easy";
                break;
            case 2:
                tableName = "Medium";
                break;
            case 3:
                tableName = "Hard";
                break;
            case 4:
                tableName = "Expert";
                break;
            case 5:
                tableName = "Master";
                break;
            default:
                tableName = "Medium";
                break;
        }
        Cursor cursor = database.rawQuery("select * from " + tableName + " WHERE " + rowName + "=?", new String[] {identifier});
        cursor.moveToFirst();
        int[] board = convert(cursor.getString(1).toCharArray());
        cursor.close();
        return board;
    }

    public UserGameListItem getSpecificBoardGame(long boardIndex){
        String tableName;
        int levelNum = (int) (boardIndex/10000);

        String identifier = Long.toString(boardIndex);
        String rowName = "Identifier";

        switch(levelNum)
        {
            case 0:
                tableName = "Beginner";
                break;
            case 1:
                tableName = "Easy";
                break;
            case 2:
                tableName = "Medium";
                break;
            case 3:
                tableName = "Hard";
                break;
            case 4:
                tableName = "Expert";
                break;
            case 5:
                tableName = "Master";
                break;
            default:
                tableName = "Medium";
                break;
        }
        Cursor cursor = database.rawQuery("select * from " + tableName + " WHERE " + rowName + "=?", new String[] {identifier});
        cursor.moveToFirst();
        UserGameListItem userGameListItem = new UserGameListItem(
                cursor.getString(2),
                cursor.getString(0),
                cursor.getString(3),
                convert(cursor.getString(1).toCharArray()),
                Integer.parseInt(cursor.getString(5)),
                cursor.getString(4),
                levelNum);
        cursor.close();
        return userGameListItem;
    }


    private int[] convert(char[] board){
        int newBoard[] = new int[81];
        for(int i =0; i < 81; i++){
            newBoard[i] = Character.getNumericValue(board[i]);
        }
        return newBoard;
    }
}

