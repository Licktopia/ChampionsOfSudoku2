package com.licktopia.championsofsudoku.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.licktopia.championsofsudoku.BackgroundImages;
import com.licktopia.championsofsudoku.PlaySounds;
import com.licktopia.championsofsudoku.R;
import com.licktopia.championsofsudoku.Settings.SudokuSettings;
import com.licktopia.championsofsudoku.Settings.SudokuSettingsActivity;
import com.licktopia.championsofsudoku.SquareView;
import com.licktopia.championsofsudoku.Sudoku.Game;
import com.licktopia.championsofsudoku.Tags;

import java.util.Set;

import static android.view.View.GONE;

public class BoardActivity extends AppCompatActivity {

    private static final float UNDOREDOALPHA = .45f;
    private PlaySounds mPlaySounds;
    private Chronometer mChronometer;
    private SudokuSettings settings;
    private Handler startAnimationHandler;
    private SquareView[] squareViews;
    private int selectedSquare = -1;
    private boolean colorGame = false;
    private boolean rowColHighlights = true;
    private boolean keyboardState = true;
    private boolean solvedState = false;
    private boolean animationFinished = false;
    private TextView scoreBoard;
    private Game mGame;
    private int level = 1;
    private boolean smallNumPadState = false;
    private Button[] numberButtons;
    private Button clearButton;
    private ImageButton mUndoButton;
    private ImageButton mRedoButton;
    private ImageButton notepadIcon;
    private RelativeLayout numberPadButtonBackground;
    private ImageButton keyboardDownButton;
    private int size;
    private long timeWhenStopped;
    private Handler highlightHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        //pass in mode and level
        colorGame = getIntent().getExtras().getBoolean("COLOR_MODE");
        level = getIntent().getExtras().getInt("LEVEL"); //gets Store Number from start screen activity
        //initialize actiion bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        RelativeLayout toolbarContainer = (RelativeLayout)findViewById(R.id.toolbar_container);
        //bindings
        mChronometer = (Chronometer)findViewById(com.licktopia.championsofsudoku.R.id.chronometer);


        mUndoButton = (ImageButton)findViewById(R.id.backButton);
        mRedoButton = (ImageButton)findViewById(R.id.forwardButton);
        notepadIcon = (ImageButton) findViewById(R.id.scratch);
        scoreBoard = (TextView)findViewById(R.id.score);



        mPlaySounds = new PlaySounds();
        mGame = new Game(level, this);//Game instance
        size = mGame.totalBoardRows();
        startAnimationHandler = new Handler();
        squareViews = new SquareView[size * size];//array of 81 squareViews
        numberButtons = new Button[size];//keypad 1-9



        updateUndoRedoButtons();
        initializeButtons();
        initializeBoard();//get starting board
        customizeLayout();
        listeners(toolbar);//set all listeners for BoardActivity layout
        updateScore();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                updateScore();
            }
        });









    }

    /************************************
     * Handles the end of the game
     *********************************/
    private void endOfGame(){

    }
    /************************************
     * Initialize undo redo buttons and sets
     *********************************/

    private void updateScore(){
        scoreBoard.setText(""+mGame.getScore());
    }
    private void undoRedoListener() {

        mUndoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGame.canUndo()) {
                    int square =  mGame.undo();
                    int value = mGame.getBoardValueAt(square);
                    if(value!=0) {
                        if (!colorGame) {
                            squareViews[square].setBigNumber(0, false);
                        } else {
                            squareViews[square].setSquareColor(0, false);
                        }
                    }
                    else{
                        if(!colorGame) {
                            squareViews[square].setBigNumber(value, false);
                        }
                        else{
                            squareViews[square].setSquareColor(value, false);
                        }
                    }
                }
                updateUndoRedoButtons();

            }
        });
        mRedoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGame.canRedo()) {
                    int square = mGame.redo();
                    int value = mGame.getBoardValueAt(square);
                    squareViews[square].setBigNumber(value, false);
                }
                updateUndoRedoButtons();
            }
        });
    }

    /************************************
     * Dims or brightens undo redo buttons
     * if undo or redo is not possible or possible
     *********************************/
    private void updateUndoRedoButtons() {
        if(mGame.canUndo()){
            mUndoButton.setAlpha(1f);
        }
        else{
            mUndoButton.setAlpha(UNDOREDOALPHA);
        }
        if(mGame.canRedo()){
            mRedoButton.setAlpha(1f);
        }
        else{
            mRedoButton.setAlpha(UNDOREDOALPHA);
        }
    }

    /************************************
     * Starts Chronometer for the first time
     *********************************/
    private void startTimer() {
        mGame.unpauseTimer();
        mGame.getRunningTime();
        mChronometer.start();
        mChronometer.setBase(SystemClock.elapsedRealtime());
    }

    @Override
    protected void onResume() {
        settings = new SudokuSettings(this);
        rowColHighlights= settings.getHighlightPreference();
        mPlaySounds.setMute(settings.getSoundPreference());
        if(animationFinished) {
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            mChronometer.start();
            mGame.unpauseTimer();
            mGame.getRunningTime();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        settings = new SudokuSettings(this);
        rowColHighlights= settings.getHighlightPreference();
        mPlaySounds.setMute(settings.getSoundPreference());
        mGame.pauseTimer();
        timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
        mChronometer.stop();
        super.onPause();
    }
    /************************************
     * Initialize all keypad buttons
     *********************************/
    private void initializeButtons() {
        for (int i = 0; i < Tags.numberButtonTags.length; i++) {//getButtons
            numberButtons[i] = (Button) findViewById(Tags.numberButtonTags[i]);
        }
        clearButton = (Button) findViewById(R.id.clear);
        numberPadButtonBackground = (RelativeLayout) findViewById(R.id.scratch_bg);
        keyboardDownButton = (ImageButton) findViewById(R.id.down);
    }

    /************************************
     * Function binds 81 squareViews and sets
     * number/color to starting value with animation
     * Then sets starts the clock
     *********************************/
    private void initializeBoard() {
        for (int i = 0; i < squareViews.length; i++) {//set up starting board
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squareViews[i] = new SquareView(this, rl,colorGame);
            final int value = mGame.getBoardValueAt(i);

            int timer;
            if(value == 0){
                timer = 0;
            }else {
                timer = value * 250 + 700;
            }

            final int finalI = i;
            startAnimationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    if(colorGame){
                        squareViews[finalI].setSquareColorAnimated(value);
                    }
                    else {
                        squareViews[finalI].setBigNumberAnimated(value);
                    }
                }
            }, timer);

        }
        Handler startClockHandler = new Handler();
        startClockHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                startTimer();
                animationFinished = true;



            }
        },2950);
    }


    /************************************
     * Calls appropriate listener functions
     *********************************/
    public void listeners(Toolbar toolbar) {
        squareListeners();
        keyboardDownButtonListener();
        toolbarButtonsListener(toolbar);
        numberListeners();
        smallNumPadListener();
        clearButtonListener();
        undoRedoListener();



    }
    /************************************
     * Listener for toolbar icon
     *********************************/
    private void toolbarButtonsListener(Toolbar toolbar) {
        toolbar.findViewById(R.id.home_menu_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeClick();

            }
        });

    }

    /************************************
     * Listeners for 81 squareViews
     *********************************/
    private void squareListeners() {
        for (int i = 0; i < (size * size); i++) {
            final int finalI = i;
            final int finalI1 = i;
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]);
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
    private void numberListeners() {
        for (int i = 0; i < size; i++) {
            final int finalI = i + 1;
            numberButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numberCommands(finalI);
                }
            });
        }
    }

    /************************************
     * Listeners numberPadButtonBackground
     *********************************/
    private void clearButtonListener() {
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberCommands(0);
            }
        });
    }

    /************************************
     * Listener numberPadButtonBackground
     *********************************/
    private void smallNumPadListener() {
        notepadIcon.setOnClickListener(new View.OnClickListener() {
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
     * Highlight connected squares
     *********************************/
    private void highlightConnected() {
        // Get location information for selected square
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
            if(i==selectedSquare){
                if(!colorGame){
                    squareViews[i].setSelectedHightlight();
                }
                else{

                }
            }
            else if (iRow == row || iCol == col || iCluster == cluster)
                squareViews[i].setHighlighted(true); // Set true if current square is connected to selected
            else
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

        selectedSquare = squareNumber;
        removeAllHighlights();

        if (mGame.isStartingValue(squareNumber)) { // Starting square clicked
            int value = mGame.getBoardValueAt(squareNumber);
            for (int i = 0; i < mGame.totalBoardElements(); i++) {
                if (mGame.getBoardValueAt(i) == value) {
                    squareViews[i].setSameNumberHighlight();
                }
            }
            // TODO disable all buttons except down
            // TODO toggle highlights?
        }

        else { // Input square clicked

            //Set highlights
            squareViews[squareNumber].setHighlighted(true); //TODO change to highlight different color than others
            if(rowColHighlights)
                highlightConnected();

            // Bold similar numbers
            if (mGame.getBoardValueAt(selectedSquare) != 0) {
                int value = mGame.getBoardValueAt(squareNumber);
                for (int i = 0; i < mGame.totalBoardElements(); i++) {
                    if (mGame.getBoardValueAt(i) == value && i != squareNumber) {
                        squareViews[i].setSameNumberHighlight();
                    }
                }
            }
        }
    }

    /************************************
     * Logic for 1-9 keypadbuttons, always
     * updates undo-redo, score, and checks for
     * end of game
     *********************************/
    private void numberCommands(int keypadNumber) {

        // If nothing selected or square is a starting square, do nothing
        if (selectedSquare == -1 || mGame.isStartingValue(selectedSquare))
            return;

        SquareView currSquare = squareViews[selectedSquare];

        // If notepad entry is active
        if (smallNumPadState) {
            // If square has value, remove it
            if (mGame.getBoardValueAt(selectedSquare) != 0) {
                mGame.setBoardValueAt(selectedSquare, 0);
                if (!colorGame)
                    currSquare.setBigNumber(0,false);
                else
                    currSquare.setSquareColor(0,false);
            }
            // Clearing notepad of all values
            if (keypadNumber == 0) {
                for (int i = 1; i <= mGame.totalBoardRows(); i++) {
                    mGame.setBoardNotepadAt(selectedSquare, i, false);
                    currSquare.showNotepadNumber(i, false);
                }
            }
            else {
                // Toggle notepad number and display notepad
                mGame.setBoardNotepadAt(selectedSquare, keypadNumber, !(mGame.getBoardNotepadAt(selectedSquare, keypadNumber)));
                currSquare.toggleNotepadNumber(keypadNumber);
            }
        }

        // Enter number into square, if valid, and display
        else {

            // Clearing on empty square, removes any notepad entries
            if (mGame.getBoardValueAt(selectedSquare) == 0 && keypadNumber == 0) {
                for (int i = 1; i <= mGame.totalBoardRows(); i++) {
                    mGame.setBoardNotepadAt(selectedSquare, i, false);
                    currSquare.showNotepadNumber(i, false);
                }
            }

            // If number clicked matches value already in square, remove number
            if (mGame.getBoardValueAt(selectedSquare) == keypadNumber)
                keypadNumber = 0;

            Set<Integer> conflicts = mGame.setBoardValueAt(selectedSquare, keypadNumber);  // Set board value to number
            if (conflicts.isEmpty()) {
                if (!colorGame)
                    currSquare.setBigNumber(keypadNumber,false);   // Display number
                else
                    currSquare.setSquareColor(keypadNumber,false); // Display color
            }
            else {
                for (int conflict : conflicts) {
                    squareViews[conflict].setErrorHighlight(); // Display error highlight
                }
            }
        }
        updateScore();
        updateUndoRedoButtons();
        if(mGame.isGameFinish()){
            endOfGame();
        }

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
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.settingsMenu) {
            Intent intent = new Intent(this, SudokuSettingsActivity.class);


                startActivity(intent);


        }

        return true;

    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent(BoardActivity.this, MainMenuActivity.class);//state intent to go to Shopping Cart Activity
        if(!solvedState) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.are_you_sure))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(intent);//GO
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
        else{
            startActivity(intent);//GO
        }
    }
    private void homeClick() {
        final Intent intent = new Intent(BoardActivity.this, MainMenuActivity.class);//state intent to go to Shopping Cart Activity
        if(!solvedState) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.quit_to_menu))
                    .setCancelable(true)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(intent);//GO
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }
        else{
            startActivity(intent);//GO
        }
    }

}


