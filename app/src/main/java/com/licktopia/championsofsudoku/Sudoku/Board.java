package com.licktopia.championsofsudoku.Sudoku;

import android.content.Context;

import java.util.Arrays;
import java.util.Stack;

/**
 * Board class represents a single instance of a Sudoku puzzle
 */

public class Board {

    public static int SIZE = 9;         // Board size
    private static int CLUSTER_SIZE = 3; // Size of N x N cluster
    private Square[][] contents;           // Holds values of the sudoku puzzle
    private boolean[][] startingValue;  // Tracks which values are part of the starting board
    private int openSquares;            // Tracks how many squares are empty


    /* Constructor*/
    protected Board(int level, Context ctx) {


        int[] values = FileIO.readFileFromAssets(level, ctx);
        // Check parameter has correct number of values
        if (values.length != SIZE * SIZE) {
            // Handle error
        }

        contents = new Square[SIZE][SIZE];
        startingValue = new boolean[SIZE][SIZE];
        openSquares = 0;

        // Copy parameter values into contents array
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {   // Non-empty square
                contents[i / SIZE][i % SIZE] = new Square(values[i]);
                startingValue[i / SIZE][i % SIZE] = true;
            }
            else                    // Empty square
                openSquares++;
        }
    }


    /* Inserts parameter value at the board location specified by row and col.
    *  Returns true if value inserted successfully, false if not */
    protected boolean setValueAt(int row, int col, int value) {

        if (value < 0 || value > SIZE)      // Check value input
            return false;
        if (startingValue[row][col])        // Check not trying to change a starting value
            return false;

        boolean isValid = false;

        if (value == 0) {                   // Removing value
            openSquares++;                  // Increment number of open squares
            isValid = true;
        }
        else {
            int clusterNum = ((row / CLUSTER_SIZE) * CLUSTER_SIZE) + (col / CLUSTER_SIZE);  // Get cluster number
            isValid = (isRowValid(row) && isColumnValid(col) && isClusterValid(clusterNum));
        }

        if(isValid)
        {
            openSquares--; // Decrement number of open squares
            //clear redo
            redoStk = new Stack();
            undoStk.push(contents[row][col].saveState(row, col, this.SIZE));
            contents[row][col].setValue(value);     // Assign value
        }

        return isValid;
    }


    /* Get value at given row/column */
    protected int getValueAt(int row, int col) {
        return contents[row][col].getValue();
    }

    void setNotepadAt(int row, int col, int num, boolean value) {
        contents[row][col].setNotepad(num, value);
    }

    boolean getNotepadAt(int row, int col, int num) {
        return contents[row][col].getNotepad(num);
    }




    /* Returns whether or not every space has been filled */
    protected boolean isBoardFull() {
        return (openSquares == 0);
    }


    /* Checks if current board is valid */
    protected boolean isBoardValid() {
        for (int i = 0; i < SIZE; i++) {
            if (!isRowValid(i) || !isColumnValid(i) || !isClusterValid(i))
                return false; // Board not correct
        }
        return true;
    }

    /* Checks if board is complete and correct */
    public boolean isSolution() {
        return isBoardFull() && isBoardValid(); // Board is complete and correct
    }


    /* Check that row provided does not contain conflicts */
    private boolean isRowValid(int row) {

        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[row][i].getValue() != 0 && contents[row][i] == contents[row][j])
                    return false;  // Found repeat, row invalid
            }
        }
        return true; // Row is valid
    }


    /* Check that column provided does not contain conflicts */
     private boolean isColumnValid(int col) {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[i][col].getValue() != 0 && contents[i][col] == contents[j][col])
                    return false; // Found repeat, column invalid
            }
        }
        return true; // Column is valid
    }


    /* Check that cluster provided does not contain conflicts */
    public boolean isClusterValid(int cluster) {

        // Get cluster start indices based on board size
        int rowStart = (cluster / CLUSTER_SIZE) * CLUSTER_SIZE;
        int colStart = (cluster % CLUSTER_SIZE) * CLUSTER_SIZE;

        // Add clusterNums to array
        int[] clusterNums = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            clusterNums[i] = contents[(i / CLUSTER_SIZE) + rowStart][(i % CLUSTER_SIZE) + colStart].getValue();
        }

        // Sort array and check for no repeats
        Arrays.sort(clusterNums);
        for (int i = 0; i < SIZE - 1; i++) {
            if (clusterNums[i] != 0 && clusterNums[i] == clusterNums[i + 1])
                return false; // Found repeat, cluster invalid
        }
        return true; // Cluster is valid
    }


    /* Clear board to original state */
    public void resetToStart() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(!startingValue[i][j])
                    contents[i][j].setValue(0);
            }
        }
    }


    //returns current state of a altered cell after an undo
    //returns null if no undo occurred
    private Stack<SquareState> undoStk = new Stack<>();
    protected SquareState undo()
    {
        if(undoStk.isEmpty()){
            return null;
        }
        SquareState temp = undoStk.pop();

        contents[temp.getRow()][temp.getCol()].restoreState(temp);

        redoStk.push(contents[temp.getRow()][temp.getCol()].saveState(temp.getRow(), temp.getCol(), this.SIZE));
        return temp;
    }

    //returns true if a undo can occur
    protected boolean canUndo()
    {
        return !undoStk.isEmpty();
    }

    //returns current state of a altered cell after an redo
    //returns null if no redo occurred
    private Stack<SquareState> redoStk = new Stack<>();
    protected SquareState redo()
    {
        if(redoStk.isEmpty()){
            return null;
        }

        SquareState temp = redoStk.pop();

        contents[temp.getRow()][temp.getCol()].restoreState(temp);

        undoStk.push(contents[temp.getRow()][temp.getCol()].saveState(temp.getRow(), temp.getCol(), this.SIZE));
        return temp;
    }

    //returns true if a redo can occur
    protected boolean canRedo()
    {
        return !redoStk.isEmpty();
    }

    //returns true if the save was successful
    protected boolean saveGame()
    {
        return false;
    }

    //gets the length for both the rows and columns of the game
    protected int getSize()
    {
            return  SIZE;
    }
}