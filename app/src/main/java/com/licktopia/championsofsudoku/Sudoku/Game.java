package com.licktopia.championsofsudoku.Sudoku;

import android.content.Context;
import java.util.Stack;

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
    private long startTime = -1;

    //context is need to load from assets folder
    public Game(int level, Context ctx)
    {
        curBoard = new Board(level, ctx);
    }

    private Board curBoard;

    //sets value of square given row and col
    //returns empty stack if successful
    //returns ids of problem squares in stack if failed
    public Stack<Integer> setBoardValueAt(int row, int col, int value) {
        return this.curBoard.setValueAt(row, col, value);
    }

    //sets value of square given the index
    //returns empty stack if successful
    //returns ids of problem squares in stack if failed
    public Stack<Integer> setBoardValueAt(int idx, int value) {
        return this.curBoard.setValueAt(idxToRow(idx), idxToCol(idx), value);
    }

    //gets value of square given row and col
    public int getBoardValueAt(int row, int col) {
        return curBoard.getValueAt(row, col);
    }

    //gets value of square given the index
    public int getBoardValueAt(int idx) {
        return curBoard.getValueAt(idxToRow(idx), idxToCol(idx));
    }

    //set cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    public void setBoardNotepadAt(int idx, int num, boolean value) {
        this.curBoard.setNotepadAt(idxToRow(idx), idxToCol(idx), num, value);
    }

    //set cell notepad value for given num
    public void setBoardNotepadAt(int row, int col, int num, boolean value) {
        this.curBoard.setNotepadAt(row, col, num, value);
    }

    //gets the notepad value for boolean num
    // for a square given its index for a single dimensional array of the board
    public boolean getBoardNotepadAt(int idx, int num) {
        return curBoard.getNotepadAt(idxToRow(idx), idxToCol(idx), num);
    }

    //get cell notepad value for given num
    public boolean getBoardNotepadAt(int row, int col, int num) {
        return curBoard.getNotepadAt(row, col, num);
    }

    //returns current state of a altered cell after an undo
    //returns null if no undo occurred
    public SquareState undo()
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
    public SquareState redo()
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

    //given an index for a single dimensional array of the board
    //converts it into the correct row of a 2d board array
    private int idxToRow(int idx)
    {
        return idx / curBoard.getSize();
    }

    //given an index for a single dimensional array of the board
    //converts it into the correct column of a 2d board array
    private int idxToCol(int idx)
    {
        return idx % curBoard.getSize();
    }

}
