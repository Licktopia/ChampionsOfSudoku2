package com.licktopia.championsofsudoku.Sudoku;

/**
 * Created by John Konecny on 6/19/2017.
 */


//Represents a single cell on a Sudoku board
class Square
{
    private boolean isStartingValue;
    private boolean[][] notepad;
    private int value = -1;
    protected Square(int entry, boolean isStartingValue)
    {
        notepad = new boolean[3][3];
        this.value = entry;
        this.isStartingValue = isStartingValue;
    }

    //num is the value which is the current state of the notepad
    //sets the boolean for num to the passed value
    protected void setNotepad(int num, boolean value) {

        switch (num) {
            case 1:
                notepad[0][0] = value;
                break;
            case 2:
                notepad[0][1] = value;
                break;
            case 3:
                notepad[0][2] = value;
                break;
            case 4:
                notepad[1][0] = value;
                break;
            case 5:
                notepad[1][1] = value;
                break;
            case 6:
                notepad[1][2] = value;
                break;
            case 7:
                notepad[2][0] = value;
                break;
            case 8:
                notepad[2][1] = value;
                break;
            case 9:
                notepad[2][2] = value;
                break;
            default:
                break;
        }
    }

    //num is the value which is the current state of the notepad
    //returns the boolean for the given num
    protected boolean getNotepad(int num) {

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

    protected void setValue(int entry) {
        this.value = entry;
    }

    protected int getValue() {
        return value;
    }

    //saves state of cell given the row and column
    //returns null if the save state failed
    protected SquareState saveState(int row, int col, int boardSize)
    {
        SquareState sav = null;
        try
        {
            sav = new SquareState(row, col, this.notepad, this.value, boardSize);

        }
        catch(SquareState.InvalidRow e)
        {
            System.err.printf("%d is not a valid row!", row);
        }
        catch(SquareState.InvalidCol e)
        {
            System.err.printf("%d is not a valid column!", col);
        }
        catch(SquareState.InvalidNotepad e)
        {
            System.err.printf("Notepad is not a valid size!");
        }
        return sav;
    }

    //resets cell to default values
    protected void reset()
    {
        for(int i = 0; i < notepad.length; i++)
        {
            for(int j = 0; j < notepad[i].length; j++)
            {
                notepad[i][j] = false;
            }
        }

        value = 0;
    }

    //given a SquareState object restore the cell values
    protected void restoreState(SquareState state)
    {
        //restore state
        for(int i = 1; i <= 9; i++)
        {
            this.setNotepad(i, state.getNotepad(i));
        }
        this.setValue(state.getValue());
    }

    protected boolean isStartingValue()
    {
        return this.isStartingValue;
    }

}
