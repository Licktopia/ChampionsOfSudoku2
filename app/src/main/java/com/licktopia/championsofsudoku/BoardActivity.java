package com.licktopia.championsofsudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class BoardActivity extends AppCompatActivity {
    RelativeLayout[] squareHolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        Tags mTags = new Tags();//tag object
        squareHolders = new RelativeLayout[81]; //array of Relative layouts
        SudokuSolver mSudokuSolver = new SudokuSolver(1,this); //test passing in "Easy level"
        int boardNumber = mSudokuSolver.getBoardNumber();//get board number
        int[] startBoard = mSudokuSolver.getStartBoard();//get starting board
        int[] solvedBoard = mSudokuSolver.getSolution();//get solved board

        for(int i = 0; i<81; i++) {//set up starting board
            squareHolders[i] = (RelativeLayout) findViewById(mTags.squareTags[i]);//bind Relative layout
            if(startBoard[i]!=0) {
                squareHolders[i].setBackground(getResources().getDrawable(mTags.numberImageTags[startBoard[i] - 1]));//assign number to square
            }
        }

    }
    /************************************
     * PreCheck checks if an entry is valid. Checks the
     * state of the board to make sure it adheres
     * to the rules of Sudoku. Returns true if valid,
     * false if not valid.
     *********************************/
    public boolean preCheck(int[] board){
        int[][] nowBoard = new int[9][9];
        int count = 0;
        for(int i = 0; i < 9; i++){
            for (int j = 0; j<9; j++){
                nowBoard[i][j] = board[count];
                count++;
            }
        }

        for (int i = 0; i < 9; i++)//for each row, column and cluster - outer of nested loop
        {

            int rowbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty row to fill
            int colbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty column to fill
            int clusterbank[] = { 0,0,0,0,0,0,0,0,0,0 };//create new empty cluster to fill

            //SECTION CONVERTS CLUSTER INTO A FLAT ARRAY

            int edgerow = 0;//Setting Edge parameters
            int edgecol = 0;
            edgecol = 3*((i + 3) % 3);

            if (i == 3 || i == 4 || i ==5) { edgerow += 3; }
            if (i == 6 || i == 7 || i == 8) { edgerow += 6; }

            int val = 0;
            int clusterArray[] = { 0,0,0,0,0,0,0,0,0 };//array that will hold cluster

            for (int m = 0; m < 3; m++)
            {
                for (int p = 0; p < 3; p++)
                {
                    //copying current cluster into flat cluster Array
                    clusterArray[val] = nowBoard[m + edgerow][p+edgecol];
                    val++;
                }
            }

            for (int j = 0; j < 9; j++)//puts number 1-9 into bank slot of number
            {
                if(rowbank[nowBoard[i][j]] == 0) {
                    rowbank[nowBoard[i][j]] = nowBoard[i][j];
                }
                else {
                    return false;
                }
                if(colbank[nowBoard[j][i]] == 0) {
                    colbank[nowBoard[j][i]] = nowBoard[j][i];
                }
                else{
                    return false;
                }
                //put value found in clustArrray[j] into empty cluster vector
                if(clusterbank[clusterArray[j]] == 0) {
                    clusterbank[clusterArray[j]] = clusterArray[j];
                }
                else{
                    return false;
                }
            }
        }
        return true;
    }



}
