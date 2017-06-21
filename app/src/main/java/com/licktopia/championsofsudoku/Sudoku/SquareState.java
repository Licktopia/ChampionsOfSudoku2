package com.licktopia.championsofsudoku.Sudoku;

/**
 * Created by John Konecny on 6/19/2017.
 */

//stores the state of a given cell
public class SquareState
{
    private int row = -1;
    private int col = -1;
    private boolean[] notepad;
    private int value = -1;
    private int boardSize = -1;

    //row is the row location of cell
    //col is the column location of cell
    //notepad is the current state of the notepad
    //entry is the current state of the square entry
    SquareState(int row, int col, boolean[] notepad, int value, int boardSize) throws InvalidRow, InvalidCol, InvalidNotepad
    {
        if(row < 0)
        {
            throw new InvalidRow();
        }
        else if(col < 0)
        {
            throw new InvalidCol();
        }
        //must be a 3x3 matrix
        else if(notepad.length < 1 || notepad.length > 3)
        {
            throw new InvalidNotepad();
        }

        this.row = row;
        this.col = col;
        this.value = value;

        this.notepad = new boolean [notepad.length];

        for(int i = 0; i < notepad.length; i++)
        {
            this.notepad[i] = notepad[i];
        }
        this.boardSize = boardSize;
    }

    public int getCol()
    {
        return col;
    }
    public int getRow()
    {
        return row;
    }
    public int getIdx() { return col + boardSize * row;}

    //num is the value which is the current state of the notepad
    //returns the boolean for the given num
    public boolean getNotepad(int num) {

        return notepad[num - 1];
    }




    int getValue() {
        return value;
    }

    class InvalidNotepad extends Exception{}
    class InvalidRow extends Exception{}
    class InvalidCol extends Exception{}
}
