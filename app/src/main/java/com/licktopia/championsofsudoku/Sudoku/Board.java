package com.licktopia.championsofsudoku.Sudoku;

import android.content.Context;

import java.util.Arrays;
import java.util.Stack;

/**
 * Board class represents a single instance of a Sudoku puzzle
 */

public class Board {

    private static int SIZE = 9;         // Board size
    private static int CLUSTER_SIZE = 3; // Size of N x N cluster
    private Square[][] contents;           // Holds values of the sudoku puzzle
    private int openSquares;            // Tracks how many squares are empty


    /* Constructor*/
    protected Board(int level, Context ctx) {


        int[] values = FileIO.readFileFromAssets(level, ctx);
        // Check parameter has correct number of values
        if (values.length != SIZE * SIZE) {
            // Handle error
        }

        contents = new Square[SIZE][SIZE];
        openSquares = 0;

        // Copy parameter values into contents array
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {   // Non-empty square
                contents[i / SIZE][i % SIZE] = new Square(values[i], true);
            }
            else                    // Empty square
            {
                contents[i / SIZE][i % SIZE] = new Square(values[i], false);
                openSquares++;
            }


        }
    }

    //creates exact copy of passed board
    protected Board(Board oldBoard)
    {


        contents = new Square[SIZE][SIZE];
        openSquares = oldBoard.openSquares;

        // copy all data from oldBoard into this
        for (int i = 0; i < this.totalBoardRows(); i++) {
            for (int j = 0; j < this.totalBoardCols(); j++) {

                contents[i][j] = new Square(oldBoard.getValueAt(i, j), oldBoard.isStartingValue(i, j));
                for(int k = 1; k <= 9; k ++)
                {
                    this.setNotepadAt(i, j, k, oldBoard.getNotepadAt(i, j, k));
                }
            }
        }
    }


    //stores the ids of squares which prevent setValueAt from making a move
    private Stack<Integer> problemSqrs;
    /* Inserts parameter value at the board location specified by row and col.
    *  Returns empty stack if value inserted successfully, not empty if not */
    protected Stack<Integer> setValueAt(int row, int col, int value) {
        //return this stack
        problemSqrs = new Stack();

        if (value < 0 || value > SIZE)      // Check value input
            return problemSqrs;
        if (contents[row][col].isStartingValue())        // Check not trying to change a starting value
            return problemSqrs;

        boolean isValid = false;

        if (value == 0) {                   // Removing value
            openSquares++;                  // Increment number of open squares
            isValid = true;
        }
        else {
            int clusterNum = ((row / CLUSTER_SIZE) * CLUSTER_SIZE) + (col / CLUSTER_SIZE);  // Get cluster number
            //isRowValid, isColumnValid and isClusterValid all add to this.problemSqrs if they are false
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

        return problemSqrs;
    }

    //sets value of square given the index
    //returns empty stack if successful
    //returns ids of problem squares in stack if failed
    protected Stack<Integer> setValueAt(int idx, int value) {
        return this.setValueAt(idxToRow(idx), idxToCol(idx), value);
    }


    /* Get value at given row/column */
    protected int getValueAt(int row, int col) {
        return contents[row][col].getValue();
    }

    //gets value of square given the index
    protected int getValueAt(int idx) {
        return this.getValueAt(idxToRow(idx), idxToCol(idx));
    }

    protected void setNotepadAt(int row, int col, int num, boolean value) {
        contents[row][col].setNotepad(num, value);
    }

    //set cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    protected void setNotepadAt(int idx, int num, boolean value) {
        this.setNotepadAt(idxToRow(idx), idxToCol(idx), num, value);
    }

    protected boolean getNotepadAt(int row, int col, int num) {
        return contents[row][col].getNotepad(num);
    }

    //get cell notepad value for given num
    protected boolean getNotepadAt(int idx, int num) {
        return this.getNotepadAt(idxToRow(idx), idxToCol(idx), num);
    }

    //return if a square at row, col is a starting value
    protected boolean isStartingValue(int row, int col)
    {
        return contents[row][col].isStartingValue();
    }

    //return if a square at the index is a starting value
    protected boolean isStartingValue(int idx)
    {
        return this.isStartingValue(idxToRow(idx), idxToCol(idx));
    }


    /* Returns whether or not every space has been filled */
    protected boolean isBoardFull() {
        return (openSquares == 0);
    }


    /* Checks if current board is valid */
    //adds id of problem square to problemSqrs
    protected boolean isBoardValid() {
        //must be reset before using isRowValid isColumnValid and !isClusterValid
        problemSqrs = new Stack();
        for (int i = 0; i < SIZE; i++) {
            if (!isRowValid(i) || !isColumnValid(i) || !isClusterValid(i)){}
                return false; // Board not correct
        }
        return true;
    }

    /* Checks if board is complete and correct */
    public boolean isSolution() {
        return isBoardFull() && isBoardValid(); // Board is complete and correct
    }


    /* Check that row provided does not contain conflicts */
    // adds to this.problemSqr if false
    private boolean isRowValid(int row) {

        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[row][i].getValue() != 0 && contents[row][i] == contents[row][j])
                {
                    problemSqrs.push(row * SIZE + i);
                    return false;  // Found repeat, row invalid
                }

            }
        }
        return true; // Row is valid
    }


    /* Check that column provided does not contain conflicts */
    // adds to this.problemSqr if false
     private boolean isColumnValid(int col) {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[i][col].getValue() != 0 && contents[i][col] == contents[j][col])
                {
                    problemSqrs.push(i * SIZE + col);
                    return false; // Found repeat, column invalid
                }
            }
        }
        return true; // Column is valid
    }


    /* Check that cluster provided does not contain conflicts */
    // adds to this.problemSqr if false
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
            {
                problemSqrs.push(((i / CLUSTER_SIZE) + rowStart) * SIZE + ((i % CLUSTER_SIZE) + colStart));
                return false; // Found repeat, cluster invalid
            }
        }
        return true; // Cluster is valid
    }


    /* Clear board to original state */
    public void resetToStart() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(!contents[i][j].isStartingValue())
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

    //gets the length for both the rows and columns of a cluster
    protected int getClusterSize() {
        return CLUSTER_SIZE;
    }

    //returns the total number of elements in the board
    protected  int totalBoardElements() {return totalBoardRows() * totalBoardCols(); }

    //returns the total number of rows in the board
    protected  int totalBoardRows() {return contents.length; }

    //returns the total number of columns in the board
    protected  int totalBoardCols() {return contents[0].length; }

    //retuns the current state of a square at row, col
    protected  SquareState getState(int row, int col) {return contents[row][col].saveState(row, col, SIZE);}

    //retuns the current state of a square at idx
    protected  SquareState getState(int idx) {return this.getState(idxToRow(idx), idxToCol(idx));}

    //given an index for a single dimensional array of the board
    //converts it into the correct row of a 2d board array
    private int idxToRow(int idx)
    {
        return idx / this.getSize();
    }

    //given an index for a single dimensional array of the board
    //converts it into the correct column of a 2d board array
    private int idxToCol(int idx)
    {
        return idx % this.getSize();
    }
}