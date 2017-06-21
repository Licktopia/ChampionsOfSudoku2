package com.licktopia.championsofsudoku.Sudoku;

import java.util.Queue;
import java.util.Stack;
import java.util.LinkedList;

/**
 * Created by John Konecny on 6/19/2017.
 */

class Solver {

    Board curBoard;
    Queue<SquareState> solvingSteps;

    protected Solver(Board curBoard)
    {
        this.curBoard = new Board(curBoard);
        Queue<SquareState> solvingSteps = new LinkedList<SquareState>();
    }

    //solves board using backtracking method and returns solved board
    //stores the steps used to solve along the way
    //ASSUMES ALL EMPTY SQUARES ARE EQUAL TO 0!!!
    //returns a null board if it cannot be solved
    protected  Board solve()
    {
        solvingSteps = new LinkedList<SquareState>();
        int i = 0;
        curBoard.resetToStart();

        while(i < curBoard.totalBoardElements())
        {
            if(!curBoard.isStartingValue(i))
            {

                //loop until a valid number can be entered
                do
                {
                    //value which will be tested to see if it is valid
                    int testVal = curBoard.getValueAt(i) + 1;

                    //if all options have been tried for this cell then go back
                    if(testVal > 9)
                    {
                        //loop until a non-starting value is found
                        //which also can have a valid entry
                        while(i >= 0 && testVal > 9)
                        {
                            //blank out the square with all of its options exhausted
                            curBoard.setValueAt(i, 0);
                            solvingSteps.add(curBoard.getState(i));
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
                }while(!curBoard.setValueAt(i, curBoard.getValueAt(i) + 1).isEmpty());
                solvingSteps.add(curBoard.getState(i));
            }
            i++;
        }

        return curBoard;
    }

    //updates all squares' notepads so that only valid moves are true
    private void updateAllNotepads()
    {
        for(int i = 0; i < curBoard.totalBoardRows(); i++)
        {
            for(int j = 0; j < curBoard.totalBoardCols(); j++)
            {
                updateNotepad(i, j);
            }
        }
    }

    //updates notepad for square at row, col so that only valid moves are true
    private void updateNotepad(int row, int col)
    {
        for(int i = 1; i < curBoard.getSize(); i++)
        {
            if(!curBoard.isStartingValue(row, col))
            {
                Stack<Integer> testStk = curBoard.setValueAt(row, col, i);
                if(testStk.isEmpty())
                {
                    curBoard.setNotepadAt(row, col, i, true);
                }
                else
                {
                    curBoard.setNotepadAt(row, col, i, false);
                }
                curBoard.setValueAt(row, col, 0);
                //add the change to the state to the cell as a solving step
                solvingSteps.add(curBoard.getState(row, col));
            }

        }
    }

    protected Queue<SquareState> getSolvingSteps()
    {
        return  solvingSteps;
    }

}
