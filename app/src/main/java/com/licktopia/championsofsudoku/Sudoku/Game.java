package com.licktopia.championsofsudoku.Sudoku;

import android.content.Context;

import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

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
    //time in seconds
    private long startTime = -1;

    //stores the total bonus given for the score
    private int level = -1;

    //context is need to load from assets folder
    public Game(int level, Context ctx)
    {
        this.level = level;
        curBoard = new Board(level, ctx);
        solutionSolver = new Solver(curBoard);
        if(solutionSolver.solve() == null)
        {
            System.err.printf("Game cannot be solved!\n");
        }
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
        return this.curBoard.setValueAt(idx, value);
    }

    //gets value of square given row and col
    public int getBoardValueAt(int row, int col) {
        return curBoard.getValueAt(row, col);
    }

    //gets value of square given the index
    public int getBoardValueAt(int idx) {
        return curBoard.getValueAt(idx);
    }

    //return if a square at the index is a starting value
    public boolean isStartingValue(int idx)
    {
        return curBoard.isStartingValue(idx);
    }


    //return if a square at row, col is a starting value
    public boolean isStartingValue(int row, int col)
    {
        return curBoard.isStartingValue(row, col);
    }

    //set cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    public void setBoardNotepadAt(int idx, int num, boolean value) {
        this.curBoard.setNotepadAt(idx, num, value);
    }

    //set cell notepad value for given num
    public void setBoardNotepadAt(int row, int col, int num, boolean value) {
        this.curBoard.setNotepadAt(row, col, num, value);
    }

    //toggles cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    public void toggleBoardNotepadAt(int idx, int num) {
        this.curBoard.setNotepadAt(idx, num, !this.getBoardNotepadAt(idx, num));
    }

    //toggles cell notepad value for given num
    public void toggleBoardNotepadAt(int row, int col, int num) {
        this.curBoard.setNotepadAt(row, col, num, !this.getBoardNotepadAt(row, col, num));
    }

    //gets the notepad value for boolean num
    // for a square given its index for a single dimensional array of the board
    public boolean getBoardNotepadAt(int idx, int num) {
        return curBoard.getNotepadAt(idx, num);
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
            startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        }
    }

    //returns running time in seconds
    public long getRunningTime()
    {
        long endTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        return runningTime + endTime - startTime;
    }

    //gets the total score for the game
    public long getScore()
    {
        return getLevelBonus() +
                totalOneTimeGuesses() * getOneTimeGuessSquareBonus() -
                (long)(getTimePenalty() * (double)getRunningTime()) -
                (long)(getIncorrectGuessesPenalty() * (double)totalIncorrectGuesses());
    }

    private long getLevelBonus()
    {
        switch(level)
        {
            //beginner
            case 0:
                return 0;
            //easy
            case 1:
                return 20;
            //medium
            case 2:
                return 100;
            //hard
            case 3:
                return 200;
            //expert
            case 4:
                return 300;
            //master
            case 5:
                return 400;
            default:
                return 0;
        }
    }

    private long totalOneTimeGuesses()
    {
        long totalOneTimeGuesses = 0;
        for(int i = 0; i < curBoard.totalBoardElements(); i++)
        {
            if(curBoard.noMoreThanOneGuessAt(i) &&
                    !curBoard.isStartingValue(i) &&
                    curBoard.getValueAt(i) != 0)
            {
                totalOneTimeGuesses++;
            }
        }
        return totalOneTimeGuesses;
    }


    private long getOneTimeGuessSquareBonus()
    {
        switch(level)
        {
            //beginner
            case 0:
                return 30;
            //easy
            case 1:
                return 60;
            //medium
            case 2:
                return 100;
            //hard
            case 3:
                return 110;
            //expert
            case 4:
                return 120;
            //master
            case 5:
                return 130;
            default:
                return 0;
        }
    }

    //in points per seconds
    private double getTimePenalty()
    {
        switch(level)
        {
            //beginner
            case 0:
                return 0;
            //easy
            case 1:
                return 1 / 30;
            //medium
            case 2:
                return 10 / 25;
            //hard
            case 3:
                return 10 / 25;
            //expert
            case 4:
                return 10 / 25;
            //master
            case 5:
                return 10 / 25;
            default:
                return 0;
        }
    }

    private long totalIncorrectGuesses()
    {
        return curBoard.totalIncorrectGuesses();
    }


    //in points per seconds
    private double getIncorrectGuessesPenalty()
    {
        switch(level)
        {
            //beginner
            case 0:
                return 0;
            //easy
            case 1:
                return 1 / 30;
            //medium
            case 2:
                return 10 / 25;
            //hard
            case 3:
                return 10 / 25;
            //expert
            case 4:
                return 10 / 25;
            //master
            case 5:
                return 10 / 25;
            default:
                return 0;
        }
    }
    //stores solution to game
    private Solver solutionSolver;

    //returns the steps required to solve from a blank board
    public Queue<SquareState> getSolutionSteps()
    {
        return solutionSolver.getSolvingSteps();
    }


    //returns the total number of elements in the board
    public  int totalBoardElements() {return curBoard.totalBoardElements(); }

    //returns the total number of rows in the board
    public  int totalBoardRows() {return curBoard.totalBoardRows(); }

    //returns the total number of columns in the board
    public  int totalBoardCols() {return curBoard.totalBoardCols(); }

    //returns the column/row size of a cluster
    public  int getClusterSize() {return curBoard.getClusterSize(); }
}
