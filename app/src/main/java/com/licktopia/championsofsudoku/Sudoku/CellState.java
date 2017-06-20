package com.licktopia.championsofsudoku.Sudoku;

/**
 * Created by John Konecny on 6/19/2017.
 */

//stores the state of a given cell
public class CellState
{
    private int row = -1;
    private int col = -1;
    private boolean[][] notepad;
    private int value = -1;

    //row is the row location of cell
    //col is the column location of cell
    //notepad is the current state of the notepad
    //entry is the current state of the square entry
    CellState(int row, int col, boolean[][] notepad, int value) throws InvalidRow, InvalidCol, InvalidNotepad
    {
        if(row <= 0)
        {
            throw new InvalidRow();
        }
        else if(col <= 0)
        {
            throw new InvalidCol();
        }
        //must be a 3x3 matrix
        else if(notepad[0].length < 1 || notepad.length < 1 || notepad[0].length > 3 || notepad.length > 3)
        {
            throw new InvalidNotepad();
        }

        this.row = row;
        this.col = col;
        this.value = value;
        notepad = new boolean[3][3];

    }

    public int getCol()
    {
        return col;
    }
    public int getRow()
    {
        return row;
    }

    //num is the value which is the current state of the notepad
    //returns the boolean for the given num
    public boolean getNotepad(int num) {

        switch (num){
            case 1: return notepad[0][0];
            case 2: return notepad[0][1];
            case 3: return notepad[0][2];
            case 4: return notepad[1][0];
            case 5: return notepad[1][1];
            case 6: return notepad[1][2];
            case 7: return notepad[2][0];
            case 8: return notepad[2][1];
            case 9: return notepad[2][2];
            default: return false;

        }
    }

    int getValue() {
        return value;
    }

    class InvalidNotepad extends Exception{}
    class InvalidRow extends Exception{}
    class InvalidCol extends Exception{}
}
