package com.licktopia.championsofsudoku.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;

import com.google.android.gms.games.Player;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.championsofsudoku.BackgroundImages;
import com.licktopia.championsofsudoku.PlaySounds;
import com.licktopia.championsofsudoku.R;
import com.licktopia.championsofsudoku.Settings.SudokuSettings;
import com.licktopia.championsofsudoku.Settings.SudokuSettingsActivity;
import com.licktopia.championsofsudoku.SquareView;
import com.licktopia.championsofsudoku.Sudoku.Game;
import com.licktopia.championsofsudoku.Sudoku.Square;
import com.licktopia.championsofsudoku.Tags;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;

import static android.graphics.Color.WHITE;
import static android.view.View.GONE;

public class BoardActivity extends BaseGameActivity{

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
    private boolean solveClicked = false;
    private TextView scoreBoard;
    private Game mGame;
    private boolean smallNumPadState = false;
    private Button[] numberButtons;
    private Button clearButton;
    private Button solveButton;
    private ImageButton mUndoButton;
    private ImageButton mRedoButton;
    private ImageButton notepadIcon;
    private ImageButton keyboardDownButton;
    private int size;
    private long timeWhenStopped;
    private Handler highlightHandler;
    private GameHelper gameHelper;
    private LinearLayout mHintsLayout;
    private LinearLayout mEndGameLayout;
    private String gamerName;
    private String gamerID;
    ShareDialog shareDialog;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        //pass in mode and level
        colorGame = getIntent().getExtras().getBoolean("COLOR_MODE");
        int level = getIntent().getExtras().getInt("LEVEL");
        //initialize actiion bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        shareDialog = new ShareDialog(BoardActivity.this);


        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //TODO Remove this for production
        gameHelper.enableDebugLog(true);
        gameHelper.setup(this);

        //bindings
        mChronometer = (Chronometer)findViewById(com.licktopia.championsofsudoku.R.id.chronometer);
        mUndoButton = (ImageButton)findViewById(R.id.backButton);
        mRedoButton = (ImageButton)findViewById(R.id.forwardButton);
        notepadIcon = (ImageButton) findViewById(R.id.scratch);
        scoreBoard = (TextView)findViewById(R.id.score);
        mHintsLayout = (LinearLayout)findViewById(R.id.hints_layout);
        mEndGameLayout = (LinearLayout)findViewById(R.id.end_game_layout);
        highlightHandler = new Handler();


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

    private void updateUI(boolean isLoggedIn){
        if(isLoggedIn){
            loadPlayer();
        }
        else{

        }
    }

    private void loadPlayer(){
        if(gameHelper.getApiClient().isConnected()) {
            gamerName = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName();
            gamerID = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getPlayerId();

        }
    }

    /************************************
     * Handles the end of the game
     *********************************/
    private void endOfGame(boolean wasSolvedButtonClicked){
        solvedState = true;
        mGame.pauseTimer();
        mChronometer.stop();
        // If notepad selected, unselect
        if (smallNumPadState)
            notepadIcon.performClick();
        //make board unclickable
        allClickable(false);

        if(wasSolvedButtonClicked){
            solveClicked = true;
            solvePuzzle();
            scoreBoard.setText("0");
            mHintsLayout.setVisibility(GONE);
            mEndGameLayout.setVisibility(View.VISIBLE);

        }
        else{
            final RelativeLayout mStatsBox = (RelativeLayout) findViewById(R.id.game_stats_box);
            final LinearLayout mLevelUpBox = (LinearLayout) findViewById(R.id.level_up_view);
            final ImageButton mCloseLevelUp = (ImageButton) findViewById(R.id.close_levelup);
            ImageButton mCloseStats = (ImageButton) findViewById(R.id.close_stats);
            int sound;

            if(!solveClicked){
                    closeKeyboard();
            }
            mStatsBox.setVisibility(View.VISIBLE);
            mCloseStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStatsBox.setVisibility(GONE);
                }
            });
            boolean levelUp = false;
            if(!levelUp){
                sound = 3;
            }
            else {
                sound = 4;
                mLevelUpBox.setVisibility(View.VISIBLE);
                mCloseLevelUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLevelUpBox.setVisibility(GONE);
                    }
                });

            }
            if(!solveClicked) {
                mPlaySounds.playSounds(this, sound);
            }

        }

    }
    /************************************
     * Initialize undo redo buttons and sets
     *********************************/

    private void updateScore(){
        if(!solveClicked) {
            scoreBoard.setText("" + mGame.getScore());
        }
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
        removeAllHighlights();
        selectedSquare = -1;
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
        solveButton = (Button) findViewById(R.id.solveButton);
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
                ///Make board clickable
                allClickable(true);


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
        solveButtonListener();
        undoRedoListener();
        continueListener();
        userBadgeListener();
        leaderBoardsListener();
        facebookListener();
    }

    private void continueListener(){
        LinearLayout continueButton =(LinearLayout)findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainMenu();
            }
        });
    }
    private void userBadgeListener(){
        LinearLayout userBadgeButton =(LinearLayout)findViewById(R.id.user_button);
        userBadgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gameHelper.getApiClient().isConnected()) {
                    Intent intent = new Intent(BoardActivity.this, GamerProfileActivity.class);
                    intent.putExtra("GAMERNAME", gamerName);
                    intent.putExtra("GAMERID", gamerID);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(BoardActivity.this,getString(R.string.login_toview_profile),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void leaderBoardsListener(){
        LinearLayout leaderBoardButton =(LinearLayout)findViewById(R.id.leader_button);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameHelper.getApiClient() != null && gameHelper.getApiClient().isConnected()) {
                    // Call a Play Games services API method, for example:
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), 5);;
                } else {
                    // Alternative implementation (or warn user that they must
                    // sign in to use this feature)
                    Toast.makeText(BoardActivity.this, R.string.login_tosee_leaderboards , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void facebookListener(){
        LinearLayout facebookButton =(LinearLayout)findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse(Tags.postScoreImagesColor[1]))
                        .build();
                shareDialog.show(content);
            }
        });
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
                highlightHandler.removeCallbacksAndMessages(null);
                removeAllHighlights();
            }
        });

    }

    /************************************
     * Listeners numberPadButtonBackground
     *********************************/
    private void solveButtonListener() {
        solveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               endOfGame(true);
            }
        });
    }

    /************************************
     * Clears highlights from squares
     *********************************/
    private void removeAllHighlights() {
        for (int i = 0; i < squareViews.length; i++) {
            squareViews[i].setHighlighted(false);
            squareViews[i].setFullAlpha();
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
                    squareViews[i].setSelectedHighlight();
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

        if(!keyboardState && !solveClicked){
            openKeyboard();

        }

        selectedSquare = squareNumber;
        highlightHandler.removeCallbacksAndMessages(null);
        removeAllHighlights();

        if (mGame.isStartingValue(squareNumber)) { // Starting square clicked
            int value = mGame.getBoardValueAt(squareNumber);
            for (int i = 0; i < mGame.totalBoardElements(); i++) {
                if (mGame.getBoardValueAt(i) == value) {
                    if(!colorGame) {
                        squareViews[i].setSameNumberHighlight();
                    }
                }
                else{
                    if(colorGame){
                        squareViews[i].setSameColorHighlight();
                    }
                }
            }
            // TODO disable all buttons except down
            // TODO toggle highlights?
        }

        else { // Input square clicked

            //Set highlights
            squareViews[squareNumber].setSelectedHighlight();
            if(rowColHighlights)
                if(!colorGame) {
                    highlightConnected();
                }

            // Bold similar numbers
            if (mGame.getBoardValueAt(selectedSquare) != 0) {
                int value = mGame.getBoardValueAt(squareNumber);
                for (int i = 0; i < mGame.totalBoardElements(); i++) {
                    if (mGame.getBoardValueAt(i) == value) {
                        if(!colorGame) {
                            squareViews[i].setSameNumberHighlight();
                        }
                    }
                    else{
                        if(colorGame){
                            squareViews[i].setSameColorHighlight();
                        }
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

        // Cancel any delayed highlighting
        highlightHandler.removeCallbacksAndMessages(null);

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
                if(!solveClicked) {
                    mPlaySounds.playSounds(this, 1);
                }
                if (!colorGame)
                    currSquare.setBigNumber(keypadNumber,false);   // Display number
                else
                    currSquare.setSquareColor(keypadNumber,false); // Display color
            }
            else {
                if(!solveClicked) {
                    mPlaySounds.playSounds(this, 2);
                }
                for (int conflict : conflicts) {
                    squareViews[conflict].setErrorHighlight();     // Display error highlight
                    highlightHandler.postDelayed(new Runnable() {  // Wait then remove error color
                        @Override
                        public void run() {
                            // Reset to regular selection highlights
                            removeAllHighlights();
                            squareViews[selectedSquare].setSelectedHighlight();
                            if (rowColHighlights)
                                highlightConnected();
                        }
                    }, 2000);
                }
            }
        }
        updateScore();
        updateUndoRedoButtons();
        if(mGame.isGameFinish()){
            endOfGame(false);
        }

    }

    /* Uses the BoardActivity UI to solve a puzzle given the solution steps */
    public void solvePuzzle() {

        Handler solutionHandler = new Handler();

        // Get solution steps
        Queue<Square> steps = mGame.getSolutionSteps();
        final ArrayList<Square>newSteps = new ArrayList<>();

        while(!steps.isEmpty()){
            newSteps.add(steps.poll());

        }
        double regression = 1340100 * Math.pow(.88316, 81-mGame.getNumberOpenSquares());
        int speedControl = (int) regression;
        int speed = speedControl /newSteps.size();

        for(int i = 0; i<newSteps.size();i++) {


            final int finalI = i;
            solutionHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Square square = newSteps.get(finalI);
                    selectedSquare = square.getIdx();
                    numberCommands(square.getValue());


                }
            }, speed * i);
        }
    }

    /************************************
     * Number vs Color initial setup
     *********************************/
    private void customizeLayout() {
        /////make board unclickable, made clickable after start animation
        allClickable(false);
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

    private void allClickable(boolean clickable){
        mUndoButton.setClickable(clickable);
        mRedoButton.setClickable(clickable);

        for (SquareView squareView : squareViews) {
            squareView.getSquareHolder().setClickable(clickable);
        }
        for (Button numberButton : numberButtons) {
            numberButton.setClickable(clickable);
        }
        clearButton.setClickable(clickable);
        keyboardDownButton.setClickable(clickable);
        notepadIcon.setClickable(clickable);


    }

    /************************************
     * Close keyboard animation
     *********************************/
    private void closeKeyboard() {
        if(solvedState&&!solveClicked) {
            mHintsLayout.setVisibility(GONE);
            mEndGameLayout.setVisibility(View.VISIBLE);
        }
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


            mKeypad.setVisibility(View.VISIBLE);
            ObjectAnimator scalex = ObjectAnimator.ofFloat(mBottomLayout, "scaleY", 1, 0);
            scalex.setDuration(50);

            ObjectAnimator scaley = ObjectAnimator.ofFloat(mKeypad, "scaleY", 0, 1);
            scaley.setDuration(50);
            AnimatorSet set2 = new AnimatorSet();
            set2.playSequentially(scalex, scaley);
            set2.start();
            mBottomLayout.setVisibility(View.GONE);



    }
    /************************************
     * Overflow menu functions
     *********************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.licktopia.championsofsudoku.R.menu.menu_main, menu);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        SpannableString s = new SpannableString("Save and Exit");
        s.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, s.length(), 0);
        menu.getItem(5).setTitle(s);
        return true;
    }

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

        if (id == R.id.action_instructions) {
                Intent intent = new Intent(this, InstructionsActivity.class);
                    startActivity(intent);

        }
        if (id == R.id.action_about) {
                Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
        }

        if (id == R.id.save_and_exit) {

        }
        if (id == R.id.main_menu) {
            goToMainMenu();
        }

        if (id == R.id.action_share) {
            share();
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
        goToMainMenu();
    }

    private void goToMainMenu() {
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

    private void share() {

        Intent i = new Intent(

                Intent.ACTION_SEND);

        i.setType("text/plain");
        i.putExtra(

                Intent.EXTRA_TEXT, this.getString(R.string.app_url));

        startActivity(Intent.createChooser(

                i,

                "Share via"));
        unlockAchievements(R.string.achievement_joy_giver);

    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHelper.onStop();
    }


    @Override
    public void onSignInFailed() {
        updateUI(false);
    }

    @Override
    public void onSignInSucceeded() {
        updateUI(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        gameHelper.onActivityResult(requestCode, resultCode, data);

    }

    private void unlockAchievements(int achievement){
        if(gameHelper.isSignedIn()) {
            Games.Achievements.unlock(gameHelper.getApiClient(), getString(achievement));
        }
    }

    private PendingResult<Snapshots.CommitSnapshotResult> writeSnapshot(Snapshot snapshot,
                                                                        byte[] data, Bitmap coverImage, String desc) {

        // Set the data payload for the snapshot
        snapshot.getSnapshotContents().writeBytes(data);

        // Create the change operation
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setCoverImage(coverImage)
                .setDescription(desc)
                .build();

        // Commit the operation
        return Games.Snapshots.commitAndClose(gameHelper.getApiClient(), snapshot, metadataChange);
    }


}


