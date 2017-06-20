package com.licktopia.championsofsudoku;

import java.util.Arrays;

/**
 * Board class represents a single instance of a Sudoku puzzle
 */

public class Board {

    public static int SIZE = 9;         // Board size
    public static int CLUSTER_SIZE = 3; // Size of N x N cluster
    private int[][] contents;           // Holds values of the sudoku puzzle
    private boolean[][] startingValue;  // Tracks which values are part of the starting board
    private int openSquares;            // Tracks how many squares are empty


    /* Constructor*/
    public Board(int[] values) {

        // Check parameter has correct number of values
        if (values.length != SIZE * SIZE) {
            // Handle error
        }

        contents = new int[SIZE][SIZE];
        startingValue = new boolean[SIZE][SIZE];
        openSquares = 0;

        // Copy parameter values into contents array
        for (int i = 0; i < values.length; i++) {
            if (values[i] != 0) {   // Non-empty square
                contents[i / SIZE][i % SIZE] = values[i];
                startingValue[i / SIZE][i % SIZE] = true;
            }
            else                    // Empty square
                openSquares++;
        }
    }


    /* Inserts parameter value at the board location specified by row and col.
    *  Returns true if value inserted is valid with current board, false if not */
    //TODO Does the game allow you to enter conflicting numbers (i.e. two of same number in row)?
    public boolean setValueAt(int row, int col, int value) {

        if (value < 0 || value > SIZE)  // Check value input
            return false;
        if (startingValue[row][col])    // Check not trying to change a starting value
            return false;

        contents[row][col] = value;     // Assign value

        if (value == 0) {               // Removing value
            openSquares++;              // Increment number of open squares
            return true;
        }
        else {
            openSquares--;              // Decrement number of open squares
            int clusterNum = ((row / CLUSTER_SIZE) * CLUSTER_SIZE) + (col / CLUSTER_SIZE);  // Get cluster number
            return (isRowValid(row) && isColumnValid(col) && isClusterValid(clusterNum));
        }
    }


    /* Get value at given row/column */
    public int getValueAt(int row, int col) {
        return contents[row][col];
    }


    /* Returns whether or not every space has been filled */
    public boolean isBoardFull() {
        return (openSquares == 0);
    }


    /* Checks if board is complete and correct */
    public boolean isSolution() {
        if (isBoardFull()) {
            for (int i = 0; i < SIZE; i++) {
                if (!isRowValid(i) || !isColumnValid(i) || !isClusterValid(i))
                    return false; // Board not correct
            }
        }
        return true; // Board is complete and correct
    }


    /* Check that row provided does not contain conflicts */
    public boolean isRowValid(int row) {

        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[row][i] != 0 && contents[row][i] == contents[row][j])
                    return false;  // Found repeat, row invalid
            }
        }
        return true; // Row is valid
    }


    /* Check that column provided does not contain conflicts */
    public boolean isColumnValid(int col) {
        for (int i = 0; i < SIZE - 1; i++) {
            for (int j = i + 1; j < SIZE; j++) {
                if (contents[i][col] != 0 && contents[i][col] == contents[j][col])
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
            clusterNums[i] = contents[(i / CLUSTER_SIZE) + rowStart][(i % CLUSTER_SIZE) + colStart];
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
                    contents[i][j] = 0;
            }
        }
    }
}