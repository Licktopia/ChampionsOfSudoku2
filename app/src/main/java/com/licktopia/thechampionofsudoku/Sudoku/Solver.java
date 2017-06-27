package com.licktopia.thechampionofsudoku.Sudoku;

import java.util.Queue;
import java.util.Set;
import java.util.LinkedList;

/**
 * Created by John Konecny on 6/19/2017.
 */

class Solver {

    Board curBoard;
    Queue<Square> solvingSteps;

    protected Solver(Board curBoard)
    {
        this.curBoard = new Board(curBoard);
        Queue<Square> solvingSteps = new LinkedList<Square>();
    }

    //solves board using backtracking method and returns solved board
    //stores the steps used to solve along the way
    //ASSUMES ALL EMPTY SQUARES ARE EQUAL TO 0!!!
    //returns a null board if it cannot be solved
    protected  Board solve()
    {
        solvingSteps = new LinkedList<Square>();
        int i = 0;
        curBoard.resetToStart();
        updateAllNotepads(false);

        while(i < curBoard.totalBoardElements())
        {
            if(!curBoard.isStartingValue(i))
            {
                int testVal = curBoard.getValueAt(i);
                //loop until a valid number can be entered
                do
                {
                    //value which will be tested to see if it is valid
                    testVal++;

                    //if all options have been tried for this cell then go back
                    if(testVal > curBoard.getSize())
                    {
                        //loop until a non-starting value is found
                        //which also can have a valid entry
                        while(i >= 0 && testVal > curBoard.getSize())
                        {
                            //blank out the square with all of its options exhausted
                            curBoard.setValueAt(i, 0);
                            solvingSteps.add(curBoard.getSquare(i));
                            i--;
                            if(!curBoard.isStartingValue(i))
                            {
                                testVal = curBoard.getValueAt(i) + 1;
                            }
                        }

                        //board can't be solved
                        if(i < 0)
                        {
                            return null;
                        }
                    }
                }while(!curBoard.setValueAt(i, testVal).isEmpty());
                solvingSteps.add(curBoard.getSquare(i));
            }
            i++;
        }

        return curBoard;
    }

    //updates all squares' notepads so that only valid moves are true
    private void updateAllNotepads(boolean removeOnly)
    {
        for(int i = 0; i < curBoard.totalBoardRows(); i++)
        {
            for(int j = 0; j < curBoard.totalBoardCols(); j++)
            {
                updateNotepad(i, j, removeOnly);
            }
        }
    }

    // updates notepad for square at row, col so that only valid moves are true
    // if removeOnly is set, update will only remove notepad values, not add any
    private void updateNotepad(int row, int col, boolean removeOnly)
    {
        if(!curBoard.isStartingValue(row, col))
        {
            for(int i = 1; i < curBoard.getSize(); i++)
            {


                Set<Integer> testStk = curBoard.setValueAt(row, col, i);
                if(testStk.isEmpty() && !removeOnly)
                {
                    curBoard.setNotepadAt(row, col, i, true);

                }
                else
                {
                    curBoard.setNotepadAt(row, col, i, false);
                }
                curBoard.setValueAt(row, col, 0);

            }
            //add the change to the state to the cell as a solving step
            solvingSteps.add(curBoard.getSquare(row, col));
        }

    }

    protected Queue<Square> getSolvingSteps()
    {
        return  solvingSteps;
    }

}
