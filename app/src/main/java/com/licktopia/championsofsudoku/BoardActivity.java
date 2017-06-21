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

    private SquareView[] squareViews;
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
    private int size;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        mGame = new Game(level, this);//Game instance
        mBoard = new Board(level,this); //test passing in "Easy level"
        size = mGame.totalBoardRows();
        squareViews = new SquareView[size * size];//array of 81 squareViews
        numberButtons = new Button[size];//keypad 1-9


        initializeButtons();
        initializeBoard();//get starting board
        listeners();//set all listeners for BoardActivity layout
        customizeLayout();

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
     * Function binds 81 squareViews and sets
     * number/color to starting value
     *********************************/
    private void initializeBoard() {
        for(int i = 0; i < squareViews.length; i++) {//set up starting board
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squareViews[i] = new SquareView(this, rl);
            if(mGame.setBoardValueAt(i)) {
                if(!colorGame) {//set number
                    squareViews[i].setBigNumber(mGame.getBoardValueAt(i));
                }
                else{//set color
                    squareViews[i].setSquareColor(mGame.getBoardValueAt(i));

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
     * Listeners for 81 squareViews
     *********************************/
    private void squareListeners() {
        for(int i = 0; i < (size * size); i++){
            final int finalI = i;
            final int finalI1 = i;
            squareViews[i].getSquareHolder().setOnClickListener(new View.OnClickListener() {
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
        for(int i = 0; i < size; i++){
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
            RelativeLayout notepadBackground = (RelativeLayout)findViewById(R.id.scratch_bg);
            if (smallNumPadState) {
                smallNumPadState = false;
                notepadBackground.setBackgroundColor(getColor(R.color.colorKeypad));

            } else {
                smallNumPadState = true;
                if(!colorGame) {
                    notepadBackground.setBackgroundColor(getColor(R.color.notepadNumberHighlight));
                }
                else{
                    notepadBackground.setBackgroundColor(getColor(R.color.notepadColorHighlight));
                }
            }
        }
    }
    /************************************
     * Listener keboardDownButton
     *********************************/
    private void keyboardDownButtonListener(){
        selectedSquare = -1;
        closeKeyboard();
        removeAllHighlights();
    }


    private void removeAllHighlights() {
        for (int i = 0; i < squareViews.length; i++) {
            squareViews[i].setHighlighted(false);
        }
    }


    public void squareCommands(int squareNumber){

        // Get location information for selected square
        selectedSquare = squareNumber;
        int row = selectedSquare / size;
        int col = selectedSquare % size;
        int clusterSize = mGame.getClusterSize();
        int cluster = ((row / clusterSize) * clusterSize) + (col / clusterSize);

        // Set highlights for every square based on selection
        for (int i = 0; i < squareViews.length; i++) {
            // Get location info for each
            int iRow = i / size;
            int iCol = i % size;
            int iCluster = ((iRow / clusterSize) * clusterSize) + (iCol / clusterSize);
            if (iRow == row || iCol == col || iCluster == cluster)
                squareViews[i].setHighlighted(true); // Set true if current square is connected to selected
            else
                squareViews[i].setHighlighted(false);
        }

    };

    private void numberCommands(int keypadNumber){
        if(selectedSquare>-1){

        }

    }
    private void customizeLayout(){
        /////set keypad colors
        for(int i=0; i < size; i++){
            if(!colorGame){
                numberButtons[i].setBackgroundColor(getResources().getColor(R.color.colorKeypad));
                numberButtons[i].setText(i+1);
            }
            else{
                numberButtons[i].setBackgroundColor(Tags.colors[i]);
                numberButtons[i].setText("");
            }
        }
        /////reset all keypad buttons to original color
        RelativeLayout notepadBackground = (RelativeLayout)findViewById(R.id.scratch_bg);
        notepadBackground.setBackgroundColor(getResources().getColor(R.color.colorKeypad));
        clearButton.setBackgroundColor(getResources().getColor(R.color.colorKeypad));
        keyboardDownButton.setBackgroundColor(getResources().getColor(R.color.colorKeypad));

        /////////set notepadbutton with correct image, color or number
        ImageButton notepadIcon = (ImageButton)findViewById(R.id.scratch);
        if(!colorGame){
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.numbers));
        }
        else{
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.colors));
        }


    }

    private void closeKeyboard() {


           /*
            mBottomLayout.setVisibility(View.VISIBLE);
            mHintsLayout.setVisibility(View.VISIBLE);

            ObjectAnimator scalex = ObjectAnimator.ofFloat(mBottomLayout, "scaleY", 0, 1);
            scalex.setDuration(50);


            ObjectAnimator scaley = ObjectAnimator.ofFloat(mKeypad, "scaleY", 1, 0);
            scaley.setDuration(50);
            AnimatorSet set2 = new AnimatorSet();
            set2.playSequentially(scaley, scalex);
            set2.start();
            mKeypad.setVisibility(GONE);


            mSolveButton.setClickable(true);
            mCheckProgress.setClickable(true);
            mKeypad.setClickable(false);
            mKeypad.setClickable(true);
            for (int i = 0; i < 10; i++) {
                mArrayButtons[i].setClickable(false);
            }
            mDown.setClickable(false);
            mScratch.setClickable(false);
            keyboardOpen = false;
            for (int i = 0; i < 81; i++) {
                if (colorState) {
                    mRelLay[i].setAlpha(1);
                    mBigNum[i].setAlpha(1);
                    if (mBigNum[i].getText() == "") {
                        mRelLay[i].setBackgroundColor(WHITE);
                    }

                } else {
                    mRelLay[i].setBackgroundColor(WHITE);
                }
                */
            }

    }


