package com.licktopia.championsofsudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.licktopia.championsofsudoku.Sudoku.Board;
import com.licktopia.championsofsudoku.Sudoku.Game;

public class BoardActivity extends AppCompatActivity {

    private Square[] squares;
    private int selectedSquare = -1;
    private boolean colorGame = false;
    private boolean rowColHighlights = true;
    private Game mGame;
    private int level = 1;
    private boolean smallNumPadState = false;
    private Button[] numberButtons;
    private Button clearButton;
    private RelativeLayout numberPadButton;
    private ImageButton keyboardDownButton;
    private Board mBoard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        squares = new Square[Board.SIZE * Board.SIZE];//array of 81 squares
        mGame = new Game(level, this);//Game instance
        mBoard= new Board(level,this); //test passing in "Easy level"
        numberButtons = new Button[Board.SIZE];//keypad 1-9


        initializeButtons();
        initializeBoard(mBoard.getStartBoard());//get starting board
        listeners();//set all listeners for BoardActivity layout

    }


    private void initializeButtons() {
        for(int i = 0; i < Tags.numberButtonTags.length; i++){//getButtons
            numberButtons[i] = (Button)findViewById(Tags.numberButtonTags[i]);
        }
        clearButton = (Button)findViewById(R.id.clear);
        numberPadButton = (RelativeLayout) findViewById(R.id.scratch_bg);
        keyboardDownButton = (ImageButton)findViewById(R.id.down);
    }
    /************************************
     * Function binds 81 squares and sets
     * number/color to starting value
     *********************************/
    private void initializeBoard(int[] startBoard) {
        for(int i = 0; i < squares.length; i++) {//set up starting board
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squares[i] = new Square(this, rl);
            if(mGame.setBoardValueAt(i)) {
                if(!colorGame) {//set number
                    squares[i].setBigNumber(startBoard[i]);
                }
                else{//set color
                    squares[i].setSquareColor(startBoard[i]);

                }
            }
            else{

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

    /************************************
     * Calls appropriate listener functions
     *********************************/
    public void listeners(){
        squareListeners();
        numberListeners();
        smallNumPadListener();
        clearButtonLisener();
        keyboardDownButtonListener();

    }
    /************************************
     * Listeners for 81 squares
     *********************************/
    private void squareListeners() {
        for(int i = 0; i <81; i++){
            final int finalI = i;
            final int finalI1 = i;
            squares[i].getSquareHolder().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    squareCommands(finalI);
                }
            });
        }
    }
    /************************************
     * Listeners for keypad 1-9
     *********************************/
    private void numberListeners(){
        for(int i = 0; i<Board.SIZE; i++){
            final int finalI = i;
            numberButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberCommands(finalI);
                }
            });
        }
    }
    /************************************
     * Listeners numberPadButton
     *********************************/
    private void clearButtonLisener(){

    }
    /************************************
     * Listener numberPadButton
     *********************************/
    private void smallNumPadListener() {
        if (selectedSquare > -1) {
            if (smallNumPadState) {
                smallNumPadState = false;

            } else {
                smallNumPadState = true;
            }
        }
    }
    /************************************
     * Listener keboardDownButton
     *********************************/
    private void keyboardDownButtonListener(){
        selectedSquare = -1;
    }



    public void squareCommands(int squareNumber){
        selectedSquare = squareNumber;


    };
    private void numberCommands(int keypadNumber){
        if(selectedSquare>-1){

        }

    }



}
