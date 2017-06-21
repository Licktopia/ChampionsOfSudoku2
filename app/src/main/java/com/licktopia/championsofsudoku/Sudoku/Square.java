package com.licktopia.championsofsudoku.Sudoku;

/**
 * Created by John Konecny on 6/19/2017.
 */


//Represents a single cell on a Sudoku board
class Square
{
    private boolean isStartingValue;
    private boolean[] notepad;
    private int value = -1;
    //true if the user has changed the square number
    private boolean hasBeenChange = false;
    //true if the square has been change no more than once
    private boolean noMoreThanOneGuess = true;
    protected Square(int entry, boolean isStartingValue, int boardSize)
    {
        notepad = new boolean[boardSize];
        this.value = entry;
        this.isStartingValue = isStartingValue;
    }

    //num is the value which is the current state of the notepad
    //sets the boolean for num to the passed value
    protected void setNotepad(int num, boolean value) {

        notepad[num - 1] = value;
    }

    //num is the value which is the current state of the notepad
    //returns the boolean for the given num
    protected boolean getNotepad(int num) {

        return notepad[num - 1];
    }

    protected void setValue(int entry) {
        if(!hasBeenChange)
        {
            hasBeenChange = true;
        }
        else
        {
            noMoreThanOneGuess = false;
        }
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
            notepad[i] = false;
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

    protected boolean noMoreThanOneGuess(){return this.noMoreThanOneGuess;}

}
