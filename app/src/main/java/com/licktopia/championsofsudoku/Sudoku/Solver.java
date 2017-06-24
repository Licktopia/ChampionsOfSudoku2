package com.licktopia.championsofsudoku.Sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;
import java.util.Set;
import java.util.LinkedList;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

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
        updateAllNotepads();

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
        if(!curBoard.isStartingValue(row, col))
        {
            for(int i = 1; i < curBoard.getSize(); i++)
            {


                Set<Integer> testStk = curBoard.setValueAt(row, col, i);
                if(testStk.isEmpty())
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


    /**** SOLVING STRATEGIES */

    private Queue<Square> getSolvingStepsFromStrategies() {

        int numSteps = curBoard.getOpenSquares();   // Number of squares to be filled
        int totalStrategiesUsed = 0;                // Track how many strategies performed to measure difficulty
        Queue<Square> steps = new LinkedList<>();   // To store steps taken

        while (steps.size() < numSteps) {
            updateAllNotepads();
            Square square;
            square = getNakedSingle();
            if (square != null)
                steps.add(square);
            else {
                // TODO Try next hardest strategy, and so on
            }
        }

        return steps;
    }

    /* Return a square, randomly, from the list of squares that have only one possible value */
    private Square getNakedSingle() {

        ArrayList<Integer> nakedSquares = new ArrayList<>();
        for (int i = 0; i < curBoard.totalBoardElements(); i++) {
            // If not a starting square and only has one notepad value, add to list
            if (!curBoard.isStartingValue(i) && curBoard.getSquare(i).getTotalNotepadValues() == 1) {
                    nakedSquares.add(i);
            }
        }

        // No available naked singles
        if (nakedSquares.isEmpty())
            return null;

        else {
            // Choose a random square from available singles, set value and return
            int rand = ThreadLocalRandom.current().nextInt(0, nakedSquares.size());
            int index = nakedSquares.get(rand);
            Square square = curBoard.getSquare(index);
            // Find the single notepad value
            for (int i = 1; i <= curBoard.getSize(); i++) {
                if (curBoard.getNotepadAt(square.getIdx(), i)) {
                    square.setValue(i);
                    break;
                }
            }

            return square;
        }
    }

    /* Find instances of naked pairs, and adjust notepads accordingly */
    private void findNakedPair() {

        class NakedPair {

            NakedPair(int index1, int index2, String type) {
                this.index1 = index1;
                this.index2 = index2;
                this.type = type;
            }

            int index1;
            int index2;
            String type;
        }

        ArrayList<NakedPair> nakedPairs = new ArrayList<>();

        // Iterate through rows finding pairs of squares that have exact same two remaining notepad values
        for (int row = 0; row < curBoard.totalBoardRows(); row++) {
            for (int i = 0; i < curBoard.totalBoardCols(); i++) {

                Square square1 = curBoard.getSquare(row, i);
                if (square1.getTotalNotepadValues() == 2)
                    for (int j = i + 1; j < curBoard.totalBoardCols(); j++) {
                        Square square2 = curBoard.getSquare(row, j);
                        // Found match, add to list
                        if (square1.hasSameNotepad(square2)) {
                            int index1 = row * curBoard.getSize() + i;
                            int index2 = row * curBoard.getSize() + j;
                            nakedPairs.add(new NakedPair(index1, index2, "row"));
                        }
                    }
            }
        }

        // Iterate through cols finding pairs of squares that have exact same two remaining notepad values
        for (int col = 0; col < curBoard.totalBoardCols(); col++) {
            for (int i = 0; i < curBoard.totalBoardCols(); i++) {

                Square square1 = curBoard.getSquare(i, col);
                if (square1.getTotalNotepadValues() == 2)
                    for (int j = i + 1; j < curBoard.totalBoardCols(); j++) {
                        Square square2 = curBoard.getSquare(j, col);
                        // Found match, add to list
                        if (square1.hasSameNotepad(square2)) {
                            int index1 = i * curBoard.getSize() + col;
                            int index2 = j * curBoard.getSize() + col;
                            nakedPairs.add(new NakedPair(index1, index2, "col"));
                        }
                    }
            }
        }

        for (int i = 0; i < curBoard.totalBoardRows(); i++) {
            // Iterate through clusters finding pairs of squares that have exact same two remaining notepad values
        }

        // Pick random nakedPair
        int rand = ThreadLocalRandom.current().nextInt(0, nakedPairs.size());
        NakedPair np = nakedPairs.get(rand);

        // Remove the two notepad values from the rest of row/col/cluster
        // Check for new naked single
    }
}
