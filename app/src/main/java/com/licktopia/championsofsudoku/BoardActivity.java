package com.licktopia.championsofsudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class BoardActivity extends AppCompatActivity {

    private int[] bgTags = {com.licktopia.championsofsudoku.R.id.squareholder1, com.licktopia.championsofsudoku.R.id.squareholder2, com.licktopia.championsofsudoku.R.id.squareholder3, com.licktopia.championsofsudoku.R.id.squareholder4, com.licktopia.championsofsudoku.R.id.squareholder5, com.licktopia.championsofsudoku.R.id.squareholder6, com.licktopia.championsofsudoku.R.id.squareholder7, com.licktopia.championsofsudoku.R.id.squareholder8, com.licktopia.championsofsudoku.R.id.squareholder9, com.licktopia.championsofsudoku.R.id.squareholder10, com.licktopia.championsofsudoku.R.id.squareholder11
            , com.licktopia.championsofsudoku.R.id.squareholder12, com.licktopia.championsofsudoku.R.id.squareholder13, com.licktopia.championsofsudoku.R.id.squareholder14, com.licktopia.championsofsudoku.R.id.squareholder15, com.licktopia.championsofsudoku.R.id.squareholder16, com.licktopia.championsofsudoku.R.id.squareholder17, com.licktopia.championsofsudoku.R.id.squareholder18, com.licktopia.championsofsudoku.R.id.squareholder19, com.licktopia.championsofsudoku.R.id.squareholder20, com.licktopia.championsofsudoku.R.id.squareholder21, com.licktopia.championsofsudoku.R.id.squareholder22, com.licktopia.championsofsudoku.R.id.squareholder23,
            com.licktopia.championsofsudoku.R.id.squareholder24, com.licktopia.championsofsudoku.R.id.squareholder25, com.licktopia.championsofsudoku.R.id.squareholder26, com.licktopia.championsofsudoku.R.id.squareholder27, com.licktopia.championsofsudoku.R.id.squareholder28, com.licktopia.championsofsudoku.R.id.squareholder29, com.licktopia.championsofsudoku.R.id.squareholder30, com.licktopia.championsofsudoku.R.id.squareholder31, com.licktopia.championsofsudoku.R.id.squareholder32, com.licktopia.championsofsudoku.R.id.squareholder33, com.licktopia.championsofsudoku.R.id.squareholder34, com.licktopia.championsofsudoku.R.id.squareholder35,
            com.licktopia.championsofsudoku.R.id.squareholder36, com.licktopia.championsofsudoku.R.id.squareholder37, com.licktopia.championsofsudoku.R.id.squareholder38, com.licktopia.championsofsudoku.R.id.squareholder39, com.licktopia.championsofsudoku.R.id.squareholder40, com.licktopia.championsofsudoku.R.id.squareholder41, com.licktopia.championsofsudoku.R.id.squareholder42, com.licktopia.championsofsudoku.R.id.squareholder43, com.licktopia.championsofsudoku.R.id.squareholder44, com.licktopia.championsofsudoku.R.id.squareholder45, com.licktopia.championsofsudoku.R.id.squareholder46, com.licktopia.championsofsudoku.R.id.squareholder47,
            com.licktopia.championsofsudoku.R.id.squareholder48, com.licktopia.championsofsudoku.R.id.squareholder49, com.licktopia.championsofsudoku.R.id.squareholder50, com.licktopia.championsofsudoku.R.id.squareholder51, com.licktopia.championsofsudoku.R.id.squareholder52, com.licktopia.championsofsudoku.R.id.squareholder53, com.licktopia.championsofsudoku.R.id.squareholder54, com.licktopia.championsofsudoku.R.id.squareholder55, com.licktopia.championsofsudoku.R.id.squareholder56, com.licktopia.championsofsudoku.R.id.squareholder57, com.licktopia.championsofsudoku.R.id.squareholder58, com.licktopia.championsofsudoku.R.id.squareholder59,
            com.licktopia.championsofsudoku.R.id.squareholder60, com.licktopia.championsofsudoku.R.id.squareholder61, com.licktopia.championsofsudoku.R.id.squareholder62, com.licktopia.championsofsudoku.R.id.squareholder63, com.licktopia.championsofsudoku.R.id.squareholder64, com.licktopia.championsofsudoku.R.id.squareholder65, com.licktopia.championsofsudoku.R.id.squareholder66, com.licktopia.championsofsudoku.R.id.squareholder67, com.licktopia.championsofsudoku.R.id.squareholder68, com.licktopia.championsofsudoku.R.id.squareholder69, com.licktopia.championsofsudoku.R.id.squareholder70, com.licktopia.championsofsudoku.R.id.squareholder71,
            com.licktopia.championsofsudoku.R.id.squareholder72, com.licktopia.championsofsudoku.R.id.squareholder73, com.licktopia.championsofsudoku.R.id.squareholder74, com.licktopia.championsofsudoku.R.id.squareholder75, com.licktopia.championsofsudoku.R.id.squareholder76, com.licktopia.championsofsudoku.R.id.squareholder77, com.licktopia.championsofsudoku.R.id.squareholder78, com.licktopia.championsofsudoku.R.id.squareholder79, com.licktopia.championsofsudoku.R.id.squareholder80, com.licktopia.championsofsudoku.R.id.squareholder81};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        SudokuSolver mSudokuSolver = new SudokuSolver(1,this); //test passing in "Easy level"
        int boardNumber = mSudokuSolver.getBoardNumber();
        int[] startBoard = mSudokuSolver.getStartBoard();
        int[] solvedBoard = mSudokuSolver.getSolution();
        String sBoard = "";
        String fBoard = "";
        for(int i = 0; i<81; i++){
            sBoard+=startBoard[i];
            fBoard+=solvedBoard[i];
        }
        Toast.makeText(BoardActivity.this, "Board number is " + boardNumber, Toast.LENGTH_SHORT).show();
        Toast.makeText(BoardActivity.this, "Beginning board is " + sBoard, Toast.LENGTH_SHORT).show();
        Toast.makeText(BoardActivity.this, "Solved board is " + fBoard, Toast.LENGTH_SHORT).show();

        if(preCheck(startBoard)){
            Toast.makeText(BoardActivity.this, "Board is good ", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(BoardActivity.this, "Does not adhere to rules of Sudoku ", Toast.LENGTH_SHORT).show();
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
