package com.licktopia.thechampionofsudoku.Database;

/**
 * Created by do_de on 8/19/2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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




    private int[] convert(char[] board){
        int newBoard[] = new int[81];
        for(int i =0; i < 81; i++){
            newBoard[i] = Character.getNumericValue(board[i]);
        }
        return newBoard;
    }
}

