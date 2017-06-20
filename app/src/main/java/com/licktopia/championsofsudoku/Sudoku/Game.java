package com.licktopia.championsofsudoku.Sudoku;

import android.content.Context;

/**
 * Created by John Konecny on 6/19/2017.
 */

//Runs a game of Sudoku
//UI uses only this class to interact with the game
public class Game
{
    //time in seconds
    private long runningTime = 0;

    //Time which timer was last started
    private long startTime;

    //context is need to load from assets folder
    protected Game(int level, Context ctx)
    {
        curBoard = new Board(level, ctx);
        this.unpauseTimer();
    }

    private Board curBoard;
    public void setBoardValueAt(int row, int col, int value) {
        this.curBoard.setValueAt(row, col, value);
    }

    public int getBoardValueAt(int row, int col) {
        return curBoard.getValueAt(row, col);
    }

    //set cell notepad value for given num
    public void setBoardNotepadAt(int row, int col, int num, boolean value) {
        this.curBoard.setNotepadAt(row, col, num, value);
    }

    //get cell notepad value for given num
    public boolean getBoardNotepadAt(int row, int col, int num) {
        return curBoard.getNotepadAt(row, col, num);
    }

    //returns current state of a altered cell after an undo
    //returns null if no undo occurred
    public CellState undo()
    {
        return curBoard.undo();
    }

    //returns true if a undo can occur
    public boolean canUndo()
    {
        return curBoard.canUndo();
    }

    //returns current state of a altered cell after an redo
    //returns null if no redo occurred
    public CellState redo()
    {
        return curBoard.redo();
    }

    //returns true if a redo can occur
    public boolean canRedo()
    {
        return curBoard.canRedo();
    }

    //returns true if the save was successful
    public boolean saveGame()
    {
        return false;
    }

    private boolean isPaused = true;
    //pauses timer
    public void pauseTimer()
    {
        //don't do anything if already paused
        if(!isPaused)
        {
            isPaused = true;
            runningTime = this.getRunningTime();
        }
    }

    public void unpauseTimer()
    {
        //don't do anything if already unpaused
        if(isPaused)
        {
            isPaused = false;
            startTime = System.currentTimeMillis();
        }
    }

    //returns running time in seconds
    public long getRunningTime()
    {
        long endTime = System.currentTimeMillis();
        return runningTime + (long)((endTime - startTime) / 1000.0);
    }

}
