package com.licktopia.championsofsudoku;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.licktopia.championsofsudoku.Sudoku.Board;
import com.licktopia.championsofsudoku.Sudoku.Game;

import static android.graphics.Color.WHITE;
import static android.view.View.GONE;

public class BoardActivity extends AppCompatActivity {

    private SquareView[] squareViews;
    private int selectedSquare = -1;
    private boolean colorGame = true;
    private boolean rowColHighlights = true;
    private boolean keyboardState = true;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //RelativeLayout toolbarContainer = (RelativeLayout)findViewById(R.id.toolbar_container);
        this.setSupportActionBar(toolbar);
        //mGame = new Game(level, this);//Game instance
        //size = mGame.totalBoardRows();
        size = 9;
        squareViews = new SquareView[size * size];//array of 81 squareViews
        numberButtons = new Button[size];//keypad 1-9


        initializeButtons();
        // initializeBoard();//get starting board
        listeners();//set all listeners for BoardActivity layout
        customizeLayout();

    }


    private void initializeButtons() {
        for (int i = 0; i < Tags.numberButtonTags.length; i++) {//getButtons
            numberButtons[i] = (Button) findViewById(Tags.numberButtonTags[i]);
        }
        clearButton = (Button) findViewById(R.id.clear);
        numberPadButton = (RelativeLayout) findViewById(R.id.scratch_bg);
        keyboardDownButton = (ImageButton) findViewById(R.id.down);
    }

    /************************************
     * Function binds 81 squareViews and sets
     * number/color to starting value
     *********************************/
    private void initializeBoard() {
        for (int i = 0; i < squareViews.length; i++) {//set up starting board
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squareViews[i] = new SquareView(this, rl);
            if (mGame.getBoardValueAt(i) != 0) {
                if (!colorGame) {//set number
                    squareViews[i].setBigNumber(mGame.getBoardValueAt(i));
                } else {//set color
                    squareViews[i].setSquareColor(mGame.getBoardValueAt(i));

                }
            } else {

            }
        }
    }


    /************************************
     * Calls appropriate listener functions
     *********************************/
    public void listeners() {
        squareListeners();
        keyboardDownButtonListener();
        if(selectedSquare>-1) {
            numberListeners();
            smallNumPadListener();
            clearButtonLisener();
        }


    }

    /************************************
     * Listeners for 81 squareViews
     *********************************/
    private void squareListeners() {
        for (int i = 0; i < (size * size); i++) {
            final int finalI = i;
            final int finalI1 = i;
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]);
            squareViews[i] = new SquareView(this, rl);
            squareViews[i].getSquareHolder().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //squareCommands(finalI);
                }
            });
        }
    }

    /************************************
     * Listeners for keypad 1-9
     *********************************/
    private void numberListeners() {
        for (int i = 0; i < size; i++) {
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
    private void clearButtonLisener() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /************************************
     * Listener numberPadButton
     *********************************/
    private void smallNumPadListener() {
        numberPadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSquare > -1) {
                    RelativeLayout notepadBackground = (RelativeLayout) findViewById(R.id.scratch_bg);
                    if (smallNumPadState) {
                        smallNumPadState = false;
                        notepadBackground.setBackgroundColor(getColor(R.color.colorKeypad));

                    } else {
                        smallNumPadState = true;
                        if (!colorGame) {
                            notepadBackground.setBackgroundColor(getColor(R.color.notepadNumberHighlight));
                        } else {
                            notepadBackground.setBackgroundColor(getColor(R.color.notepadColorHighlight));
                        }
                    }
                }
            }
        });

    }

    /************************************
     * Listener keboardDownButton
     *********************************/
    private void keyboardDownButtonListener() {
        keyboardDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSquare = -1;
                closeKeyboard();
                removeAllHighlights();
            }
        });

    }

    /************************************
     * Clears highlights from squares
     *********************************/
    private void removeAllHighlights() {
        for (int i = 0; i < squareViews.length; i++) {
            squareViews[i].setHighlighted(false);
        }
    }

    /************************************
     * Logic for 81 squares
     *********************************/
    public void squareCommands(int squareNumber) {
        if(!keyboardState){
            openKeyboard();
        }
        removeAllHighlights();
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

    }

    /************************************
     * Logic for 1-9 keypadbuttons
     *********************************/
    private void numberCommands(int keypadNumber) {


    }

    /************************************
     * Number vs Color initial setup
     *********************************/
    private void customizeLayout() {
        /////set keypad colors
        for (int i = 0; i < size; i++) {
            if (!colorGame) {
                numberButtons[i].setBackgroundColor(getResources().getColor(R.color.colorKeypad));
                numberButtons[i].setText(String.format("%d", i + 1));
            } else {
                numberButtons[i].setBackgroundColor(getResources().getColor(Tags.colors[i]));
                numberButtons[i].setText("");
            }
        }
        /////reset all keypad buttons to original color
        RelativeLayout notepadBackground = (RelativeLayout) findViewById(R.id.scratch_bg);
        notepadBackground.setBackgroundColor(getResources().getColor(R.color.colorKeypad));
        clearButton.setBackgroundColor(getResources().getColor(R.color.colorKeypad));
        keyboardDownButton.setBackgroundColor(getResources().getColor(R.color.colorKeypad));

        /////////set notepadbutton with correct image, color or number
        ImageButton notepadIcon = (ImageButton) findViewById(R.id.scratch);
        ImageView artHolder = (ImageView)findViewById(R.id.artHolder);
        if (!colorGame) {
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.numbers));
            artHolder.setImageDrawable(null);

        } else {
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.colors));
            BackgroundImages mBackgroundImages = new BackgroundImages();
            artHolder.setImageDrawable(getResources().getDrawable(mBackgroundImages.getImage()));
            //int imageNumber = mBackgroundImages.getPictureNumber();
        }


    }

    /************************************
     * Close keyboard animation
     *********************************/
    private void closeKeyboard() {
        keyboardState=false;
        removeAllHighlights();
        LinearLayout mBottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
        RelativeLayout mKeypad = (RelativeLayout) findViewById(R.id.keypad);
        mBottomLayout.setVisibility(View.VISIBLE);

        ObjectAnimator scalex = ObjectAnimator.ofFloat(mBottomLayout, "scaleY", 0, 1);
        scalex.setDuration(50);


        ObjectAnimator scaley = ObjectAnimator.ofFloat(mKeypad, "scaleY", 1, 0);
        scaley.setDuration(50);
        AnimatorSet set2 = new AnimatorSet();
        set2.playSequentially(scaley, scalex);
        set2.start();
        mKeypad.setVisibility(GONE);


    }
    /************************************
     * Open keyboard animation
     *********************************/
    private void openKeyboard() {
            keyboardState = true;
            LinearLayout mBottomLayout = (LinearLayout)findViewById(R.id.bottomLayout);
            RelativeLayout mKeypad = (RelativeLayout) findViewById(R.id.keypad);
            mBottomLayout.setVisibility(View.GONE);

            mKeypad.setVisibility(View.VISIBLE);
            ObjectAnimator scalex = ObjectAnimator.ofFloat(mBottomLayout, "scaleY", 1, 0);
            scalex.setDuration(50);

            ObjectAnimator scaley = ObjectAnimator.ofFloat(mKeypad, "scaleY", 0, 1);
            scaley.setDuration(50);
            AnimatorSet set2 = new AnimatorSet();
            set2.playSequentially(scalex, scaley);
            set2.start();



    }
    /************************************
     * Overflow menu functions
     *********************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.licktopia.championsofsudoku.R.menu.menu_main, menu);

       /*
        if(!gameHelper.isSignedIn()){
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(true);
        }
        else {
            menu.getItem(4).setVisible(true);
            menu.getItem(5).setVisible(false);
        }
        */

        return true;
    }
/*
    public boolean onPrepareOptionsMenu(Menu menu){

        SpannableString s = new SpannableString("Save and Exit");
        s.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, s.length(), 0);


        menu.getItem(7).setTitle(s);

        if(!gameHelper.isSignedIn()){
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(true);
        }
        else {
            menu.getItem(4).setVisible(true);
            menu.getItem(5).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == com.licktopia.championsofsudoku.R.id.new_board) {
            mChronometer.stop();
            if(solvedState==false) {


                closeKeyboard();

                for (int i = 0; i < 81; i++) {
                    //mBigNum[i] = (TextView) findViewById(bigNumTags[i]);
                    mBigNum[i].setText(null);
                    mRelLay[i].setBackgroundColor(WHITE);
                    mBigNum[i].setVisibility(View.VISIBLE);
                    //mSmallGrid[i].setVisibility(View.VISIBLE);
                    //mRelLay[i].setClickable(true);
                    //for (int j = 0; j < 9; j++) {
                    // mSmallNums[i][j].setVisibility(View.INVISIBLE);
                    //mSmallNums[i][j].setText("");
                    //}

                }

                mBottomLayout.setVisibility(View.GONE);


                continueFunction();

            }
            return true;
        }


        if (id == com.licktopia.sudokutopia.R.id.action_share) {
            share();
        }

        if (id == R.id.action_logout) {
            gameHelper.signOut();

            Toast.makeText(MainActivity.this, "Logging out..." , Toast.LENGTH_SHORT).show();
            clickedOut = true;
            updateUi(false);
            boolean change = myDb.changeUser(currentGamerName,currentGamerId,myStats.XP);
            if(change){
                // Toast.makeText(MainActivity.this, "Changed successfully" , Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Not Changed" , Toast.LENGTH_SHORT).show();
            }


        }
        if (id == R.id.action_login) {

            gameHelper.beginUserInitiatedSignIn();

        }
        if (id == R.id.action_instructions) {
            if (solvedState == false) {
                Intent intent = new Intent(this, InstructionsActivity.class);

                if(!isOld) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);//fade in out transition
                    startActivity(intent, options.toBundle());
                }
                else{
                    startActivity(intent);
                }
            }
        }
        if (id == R.id.action_about) {
            if (solvedState == false) {
                Intent intent = new Intent(this, AboutActivity.class);
                if(!isOld) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);//fade in out transition
                    startActivity(intent, options.toBundle());
                }
                else{
                    startActivity(intent);
                }
            }
        }
        if (id == R.id.settingsMenu) {
            Intent intent = new Intent(this, SudokuSettingsActivity.class);
            if(!isOld) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);//fade in out transition
                startActivity(intent, options.toBundle());
            }
            else{
                startActivity(intent);
            }

        }

        if (id == R.id.save_and_exit) {

            Toast.makeText(MainActivity.this, R.string.coming_soon, Toast.LENGTH_SHORT).show();//show warning

        }

        return true;

    }
    */
}


