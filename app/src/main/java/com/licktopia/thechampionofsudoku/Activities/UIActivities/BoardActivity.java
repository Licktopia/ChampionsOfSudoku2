package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.BackgroundImages;
import com.licktopia.thechampionofsudoku.PlaySounds;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Settings.SudokuSettings;
import com.licktopia.thechampionofsudoku.Settings.SudokuSettingsActivity;
import com.licktopia.thechampionofsudoku.SquareView;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;

import com.licktopia.thechampionofsudoku.Sudoku.Game;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;
import com.licktopia.thechampionofsudoku.Sudoku.Square;
import com.licktopia.thechampionofsudoku.Sudoku.Strategies;
import com.licktopia.thechampionofsudoku.Tags;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.view.View.GONE;

public class BoardActivity extends BaseActivity {

    private static final float UNDOREDOALPHA = .45f;
    private String mCurrentSaveName = "snapshotTemp";
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
    private com.licktopia.thechampionofsudoku.Sudoku.Game mGame;
    private boolean smallNumPadState = false;
    private Button[] numberButtons;
    private TextView[] lilNums;
    private Button clearButton;
    private Button solveButton;
    private Button checkProgressButton;
    private Button nakedSingleButton;
    private Button nakedDoubleButton;
    private Button hiddenSingleButton;
    private Button hiddenPairButton;
    private Button lockedCandidateButton;
    private Button xWingButton;
    private ImageButton mUndoButton;
    private ImageButton mRedoButton;
    private ImageButton notepadIcon;
    private Button hintButton;
    private int size;
    private long timeWhenStopped;
    private Handler highlightHandler;
    private GameHelper gameHelper;
    private LinearLayout mHintsLayout;
    private LinearLayout mEndGameLayout;
    private ImageView userBadge;
    private TextView userText;
    private String gamerName;
    private String gamerID;
    private int level;
    private boolean resume;
    private boolean fromResume;
    private boolean mAutoSave;

    ShareDialog shareDialog;

    CognitoCachingCredentialsProvider credentialsProvider;
    CognitoSyncManager syncClient;
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        //pass in mode and level
        colorGame = getIntent().getExtras().getBoolean("COLOR_MODE");
        resume = getIntent().getExtras().getBoolean("RESUME");
        level = getIntent().getExtras().getInt("LEVEL");
        long sTime = getIntent().getExtras().getLong("STOPPED_TIME");
        //initialize actiion bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        shareDialog = new ShareDialog(BoardActivity.this);


        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //TODO Remove this for production
        gameHelper.enableDebugLog(true);
        gameHelper.setup(this);

        // Initialize the Amazon Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:40802ef0-7537-4910-8442-31e2b30c5a7d", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        // Initialize the Cognito Sync client
        syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_2, // Region
                credentialsProvider);


        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);

        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));


        //bindings
        mChronometer = (Chronometer)findViewById(com.licktopia.thechampionofsudoku.R.id.chronometer);
        mUndoButton = (ImageButton)findViewById(R.id.backButton);
        mRedoButton = (ImageButton)findViewById(R.id.forwardButton);
        notepadIcon = (ImageButton) findViewById(R.id.scratch);
        scoreBoard = (TextView)findViewById(R.id.score);
        mHintsLayout = (LinearLayout)findViewById(R.id.hints_layout);
        mEndGameLayout = (LinearLayout)findViewById(R.id.end_game_layout);
        userBadge = (ImageView)findViewById(R.id.user_image);
        userText = (TextView)findViewById(R.id.user_text);
        highlightHandler = new Handler();


        mPlaySounds = new PlaySounds();

        //Game instance
        //LOAD GAME STUFF IS HERE
        if(resume)
        {
            byte[] temp = getIntent().getExtras().getByteArray("SAVE_FILE");
            try(ByteArrayInputStream b = new ByteArrayInputStream(getIntent().getExtras().getByteArray("SAVE_FILE"))){


                ObjectInputStream o = new ObjectInputStream(b);
                mGame = (Game)o.readObject();
            }
            catch(IOException e)
            {
                Log.d("IOException", "Failed to load the game!");
                mGame = new Game(0, this);
            }
            catch(ClassNotFoundException e)
            {
                Log.d("ClassNotFoundException", "What class are you talking about?");
            }
        }
        //START NEW GAME HERE
        else
        {
            mGame = new Game(level, this);
        }

        size = mGame.totalBoardRows();
        startAnimationHandler = new Handler();
        squareViews = new SquareView[size * size];//array of 81 squareViews
        numberButtons = new Button[size];//keypad 1-9
        lilNums = new TextView[size];



        updateUndoRedoButtons();
        initializeButtons();
        if(resume){
            initializeBoardFromResume(sTime);
        }
        else {
            initializeBoard();//get starting board
        }
        customizeLayout(resume);
        listeners(toolbar);//set all listeners for BoardActivity layout
        updateScore();
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                updateScore();
            }
        });

    }

    private boolean isInternet(){
        boolean isConnected;
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    private void uploadSavedGame(boolean autosave){
        if(isInternet()&&gameHelper.getApiClient().isConnected()) {
            long stoppedTime = mChronometer.getBase() - SystemClock.elapsedRealtime();
            Date dt = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aaa");
            String today = dateFormat.format(dt);
            String now = sdf.format(dt);
            final SavedGame obj = new SavedGame();
            obj.setGamerID(getId());
            obj.setExactTime(String.format("%,d",System.currentTimeMillis()));
            obj.setColorMode(colorGame);
            obj.setDate(today);
            if(!autosave) {
                obj.setTime(now);
            }
            else{
                obj.setTime(new StringBuilder().append(now).append(" -autosave").toString());
            }
            obj.setPuzzleNumber(mGame.getBoardNumber()+1+level*10000);
            obj.setLevel(level);
            obj.setStoppedTime(stoppedTime);
            //game object to byte array
            try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
                ObjectOutputStream o = new ObjectOutputStream(b);
                o.writeObject(mGame);
                obj.setSaveFile(b.toByteArray());
            }
            catch(IOException e)
            {
                Log.d("IOException", "Failed to save the game!");
            }

            // id field is null at this point
            Runnable runnable = new Runnable() {
                public void run() {
                    //DynamoDB calls go here
                    mapper.save(obj);
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
            Toast.makeText(this, R.string.game_saved,Toast.LENGTH_SHORT).show();



        }
        else{
            Toast.makeText(this, R.string.not_upload,Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadCompletedGame(int xp){
        if(isInternet()&&gameHelper.getApiClient().isConnected()) {
            Date dt = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
            String today = dateFormat.format(dt);
            final CompletedGame obj = new CompletedGame();
            obj.setGamerID(getId());
            obj.setRealScore((int) mGame.getScore());
            obj.setXp(xp);
            obj.setTime(String.format("%,d",(int) mGame.getRunningTime()));
            obj.setDate(today);
            obj.setColor(colorGame);
            obj.setPuzzleNumber(mGame.getBoardNumber()+1+level*10000);
            obj.setLevel(level);
            obj.setAccuracy(mGame.getAccuracy());
            obj.setExactTime(String.format("%,d",System.currentTimeMillis()));


            // id field is null at this point
            Runnable runnable = new Runnable() {
                public void run() {
                    //DynamoDB calls go here
                    mapper.save(obj);
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
        }
        else{
            Toast.makeText(this, R.string.not_upload,Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(boolean isLoggedIn){
        if(isLoggedIn){
            loadPlayer();
        }
        else{
            userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[0]));
            userText.setText(R.string.me);
        }
    }

    private void loadPlayer(){
        gameHelper.getApiClient().connect();
        if(gameHelper.getApiClient().isConnected()) {
            gamerName = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName();
            gamerID = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getPlayerId();
            userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
            userText.setText(getUsername());
        }
    }

    /************************************
     * Handles the end of the game
     *********************************/
    private void endOfGame(boolean wasSolvedButtonClicked){
        gameHelper.getApiClient().connect();
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
            updateScore();
            long gameXP = (long) (mGame.getScore()* MULTIPLIER[getLevel()]);
            boolean levelUp = addExperience(gameXP);
            if(!solveClicked) {
                uploadCompletedGame((int) gameXP);
                sendToLeaderBoard(gameXP);
                handleAchievements(gameXP);
                setStatsBox(gameXP);
                mStatsBox.setVisibility(View.VISIBLE);
                closeKeyboard();
            }

            mCloseStats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mStatsBox.setVisibility(GONE);
                }
            });

            if(!levelUp){//Not level up
                sound = 3;
            }
            else {//Level Up
                sound = 4;
                ImageView levelUpBadge = (ImageView)findViewById(R.id.level_up_badge);
                ImageButton shareLevelUp = (ImageButton) findViewById(R.id.share_levelup);
                TextView newMultiplier = (TextView) findViewById(R.id.new_multiplier);
                levelUpBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
                userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
                userText.setText(getUsername());
                shareLevelUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        facebookShare();
                    }
                });
                newMultiplier.setText("Multiplier " + String.format("%,2.1f",MULTIPLIER[level])+"x");
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

    /********************************
    * Updates Little numbers on keyboard that track number of values on board
     * ***************************************/
    private void updateLilNums(){

        int[] array = mGame.getValueTotals();
        for(int i=0; i< array.length;i++){
             lilNums[i]=(TextView)findViewById(Tags.lilNumTags[i]);
             lilNums[i].setText(String.valueOf(array[i]));
        }

    }

    /************************************
     * Send to leaderboard at end of game
     *********************************/
    private void sendToLeaderBoard(long gameXP){
        gameHelper.getApiClient().connect();
        if(gameHelper.getApiClient().isConnected()) {
            if(mGame.getAccuracy()>=.8) {
                Games.Leaderboards.submitScore(gameHelper.getApiClient(),getString(R.string.leaderboard_highest_single_game_score), (int)gameXP,"1337");
                Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(Tags.leaderboards[level]), (int)mGame.getRunningTime() * 1000,"1337");
            }
            Games.Leaderboards.submitScore(gameHelper.getApiClient(), getString(R.string.leaderboard_most_total_xp), (int)getXp(),"1337");
            Toast.makeText(this, R.string.scoretime_submitted, Toast.LENGTH_SHORT).show();
        }


    }

    /************************************
     * Unlock achievements at end of game
     *********************************/
    private void handleAchievements(long gameXP){
        gameHelper.getApiClient().connect();
        if(gameHelper.getApiClient().isConnected()){
            unlockAchievements(R.string.achievement_up_and_comer);
            if(level>=2&&mGame.getAccuracy()>=1){
                unlockAchievements(R.string.achievement_sharp_shooter);
            }
            if(level>=2&&mGame.getRunningTime()<12*60){
                unlockAchievements(R.string.achievement_speed_demon);
            }
            if(gameXP>=10000){
                unlockAchievements(R.string.achievement_big_time_scorer);
            }
            if(getXp()>=100000){
                unlockAchievements(R.string.achievement_up_and_comer_ii);
            }
            if(level>=3&&mGame.getAccuracy()>=1&&mGame.getRunningTime()<15*60){
                unlockAchievements(R.string.achievement_sharp_shooter_ii);
            }
            if(level>=3&&mGame.getRunningTime()<10*60){
                unlockAchievements(R.string.achievement_speed_demon_ii);
            }
            if(gameXP>=15000){
                unlockAchievements(R.string.achievement_big_time_scorer_ii);
            }
            if(getXp()>=1000000){
                unlockAchievements(R.string.achievement_up_and_comer_iii);
            }
            if(level>=4&&mGame.getAccuracy()>=1&&mGame.getRunningTime()<10*60){
                unlockAchievements(R.string.achievement_sharp_shooter_iii);
            }
            if(level>=5&&mGame.getRunningTime()<8*60){
                unlockAchievements(R.string.achievement_speed_demon_iii);
            }
            if(gameXP>=20000){
                unlockAchievements(R.string.achievement_big_time_scorer_iii);
            }
        }
    }

    /************************************
     * Set stats box at end of game
     *********************************/
    private void setStatsBox(long gameXP){
        TextView mTitleStats = (TextView)findViewById(R.id.title_stats);
        TextView mTotalXpStats = (TextView)findViewById(R.id.totalxp_stats);
        TextView mRpStats = (TextView)findViewById(R.id.regular_points);
        TextView mLevelBonusStats = (TextView)findViewById(R.id.levelbonus_stats);
        TextView mTimeBonusStats = (TextView)findViewById(R.id.timebonus_stats);
        TextView mGoodSquaresStats = (TextView)findViewById(R.id.goodsquares_stats);
        TextView mAccuracyStats = (TextView)findViewById(R.id.accurracy_stats);
        TextView mPenalty = (TextView)findViewById(R.id.penalty_stats);
        mTitleStats.setText(getUsername());
        mTotalXpStats.setText(String.format("%,d",(int) gameXP)+ " XP");
        mRpStats.setText("Multiplier: " + String.format("%,d",(int) mGame.getScore())+ " x " + String.format("%2.1f",MULTIPLIER[getLevel()]));
        mLevelBonusStats.setText("Level Bonus: " + String.format("%,d",(int) mGame.getLevelBonus()));
        if((mGame.getEarlyFinishTime() - mGame.getRunningTime()) >= 0)
        {
            mTimeBonusStats.setText("Time Bonus: " + String.format("%,d",(int) (mGame.getEarlyFinishTime() - mGame.getRunningTime())) + " x " + String.format("%,d",(int) mGame.getEarlyFinishBonus()) );
        }
        else
        {
            mTimeBonusStats.setText("Time Bonus: 0 x " + String.format("%,d",(int) mGame.getEarlyFinishBonus()) );
        }

        mGoodSquaresStats.setText("Square Points: " + String.format("%,d",(int) mGame.getTotalOneTimeGuesses())+ " x " + String.format("%,d",(int) mGame.getOneTimeGuessSquareBonus()));
        mAccuracyStats.setText("Accuracy: " + String.format("%,d",(int)Math.round(mGame.getAccuracy()*100),2)+"%");
        mPenalty.setText("Penalty: " + String.format("%,d",(int) mGame.totalPenaltyTimeIntervals()) + " x " + String.format("%,d",(int) mGame.getTimePenalty()));



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
                    if (!colorGame) {
                        squareViews[square].setBigNumber(value, false);
                    }
                    else{
                        squareViews[square].setSquareColor(value, false);
                    }

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
        mAutoSave = settings.getAutoSavePreference();

        for (int i=0;i<lilNums.length;i++) {
            lilNums[i]=(TextView)findViewById(Tags.lilNumTags[i]);
            if (settings.getLilNumPreference()) {
                lilNums[i].setVisibility(View.VISIBLE);
            } else {
                lilNums[i].setVisibility(View.INVISIBLE);
            }
        }

        if(animationFinished) {
            if(!fromResume) {
                mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
            }
            if(!solvedState) {
                mChronometer.start();
                mGame.unpauseTimer();
            }
            mGame.getRunningTime();
        }
        fromResume = false;
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
        checkProgressButton = (Button) findViewById(R.id.progressButton);
        nakedSingleButton = (Button) findViewById(R.id.naked_single);
        nakedDoubleButton = (Button) findViewById(R.id.naked_double);
        hiddenSingleButton = (Button) findViewById(R.id.hidden_single);
        hiddenPairButton = (Button) findViewById(R.id.hidden_pair);
        lockedCandidateButton = (Button) findViewById(R.id.locked_candidate);
        xWingButton = (Button) findViewById(R.id.x_wing);
        hintButton = (Button) findViewById(R.id.hints);
    }

    /************************************
     * Function binds 81 squareViews and sets
     * number/color to starting value with animation
     * Then sets starts the clock
     *********************************/
    private void initializeBoard() {
        fromResume = false;
        for (int i = 0; i < squareViews.length; i++) {//set up starting board
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squareViews[i] = new SquareView(this, rl,colorGame);
            final int value = mGame.getBoardValueAt(i);

            int timer;
            if(value == 0){
                timer = 0;
            }else {
                timer = value * 250 + 200;
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
                updateLilNums();


            }
        },2950);
    }
    private void initializeBoardFromResume(Long stoppedTime) {
        fromResume = true;
        for (int i = 0; i < squareViews.length; i++){
            RelativeLayout rl = (RelativeLayout) findViewById(Tags.squareTags[i]); // get Relative layout
            squareViews[i] = new SquareView(this, rl,colorGame);
            for (int j = 0; j < 9; j++){
                squareViews[i].showNotepadNumber(j+1,mGame.getBoardNotepadAt(i,j+1));

            }
        }
        for (int i = 0; i < squareViews.length; i++) {//set up starting board

            int value = mGame.getBoardValueAt(i);
            if(colorGame){
                squareViews[i].setSquareColor(value, mGame.isStartingValue(i));
            }
            else {
                squareViews[i].setBigNumber(value, mGame.isStartingValue(i));
            }
        }

        mGame.unpauseTimer();

        mChronometer.setBase(SystemClock.elapsedRealtime() + stoppedTime);
        mChronometer.start();

        updateScore();
        animationFinished = true;

        ///Make board clickable
        allClickable(true);
        updateLilNums();
    }



    /************************************
     * Calls appropriate listener functions
     *********************************/
    public void listeners(Toolbar toolbar) {
        squareListeners();
        hintButtonListener();
        toolbarButtonsListener(toolbar);
        numberListeners();
        smallNumPadListener();
        clearButtonListener();
        checkProgressButtonListener();
        nakedSingleButtonListener();
        nakedDoubleButtonListener();
        hiddenSingleButtonListener();
        hiddenPairButtonListener();
        lockedCandidateListener();
        xWingListener();
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
                gameHelper.getApiClient().connect();
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
        gameHelper.getApiClient().connect();
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

                facebookShare();
            }
        });
    }

    private void facebookShare() {
        gameHelper.getApiClient().connect();
        String imageTag;
        if(gameHelper.getApiClient().isConnected()) {
            if(!colorGame){
                imageTag = Tags.postScoreImagesNumber[getLevel()];
            }
            else{
               imageTag = Tags.postScoreImagesColor[getLevel()];
            }

        }
        else{
            if(!colorGame){
                imageTag = Tags.postScoreNum;
            }
            else{
                imageTag = Tags.postScoreCol;
            }
        }
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(imageTag))
                .build();
        shareDialog.show(content);
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
                        notepadBackground.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorKeypad));

                    } else {
                        smallNumPadState = true;
                        if (!colorGame) {
                            notepadBackground.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.notepadNumberHighlight));
                        } else {
                            notepadBackground.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.notepadColorHighlight));
                        }
                    }
                }
            }
        });

    }

    /************************************
     * Listener keboardDownButton
     *********************************/
    private void hintButtonListener() {
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSquare = -1;
                closeKeyboard();
                highlightHandler.removeCallbacksAndMessages(null);
                mGame.updateSolver();
                removeAllHighlights();
            }
        });

    }

    /************************************
     * Listeners solveButton
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
     * Listeners checkProgressButton
     *********************************/
    private void checkProgressButtonListener() {
        checkProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBoard();
            }
        });
    }

    /************************************
     * Listeners nakedSingleButton
     *********************************/
    private void nakedSingleButtonListener() {
        nakedSingleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    nakedSingleButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showNakedSingle();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    nakedSingleButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Listeners nakedDoubleButton
     *********************************/
    private void nakedDoubleButtonListener() {
        nakedDoubleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    nakedDoubleButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showHint(Strategies.NAKED_DOUBLE);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    nakedDoubleButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Listeners hiddenSingleButton
     *********************************/
    private void hiddenSingleButtonListener() {
        hiddenSingleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hiddenSingleButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showHint(Strategies.HIDDEN_SINGLE);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hiddenSingleButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Listeners hiddenPairButton
     *********************************/
    private void hiddenPairButtonListener() {
        hiddenPairButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hiddenPairButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showHint(Strategies.HIDDEN_PAIR);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    hiddenPairButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Listeners lockedCandidateButton
     *********************************/
    private void lockedCandidateListener() {
        lockedCandidateButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    lockedCandidateButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showHint(Strategies.LOCKED_CANDIDATE);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    lockedCandidateButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Listeners xWingButton
     *********************************/
    private void xWingListener() {
        xWingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    xWingButton.setBackground(getResources().getDrawable(R.drawable.hint_button_pressed));
                    showHint(Strategies.XWING);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    xWingButton.setBackground(getResources().getDrawable(R.drawable.hint_rounded_button));
                    removeAllHighlights();
                }
                return true;
            }
        });
    }

    /************************************
     * Update board
     *********************************/
    boolean removeOnly = false; // TODO test value, remove
    private void updateBoard() {
        mGame.updateAllNotepads(removeOnly);
        removeOnly = true;
        for (int idx = 0; idx < mGame.totalBoardElements(); idx++) {
            if (mGame.isStartingValue(idx) || mGame.getBoardValueAt(idx) != 0) continue;
            for (int num = 1; num <= mGame.totalBoardRows(); num++) {
                squareViews[idx].showNotepadNumber(num, mGame.getBoardNotepadAt(idx, num));
            }
        }
    }

    /************************************
     * Show one instance of a given hint
     ***********************************/
    private void showHint(int hintType) {
        updateBoard();
        Strategies.Hint hint = mGame.getHint(hintType);
        if (hint != null) {
            hintDisplay(hint);
        }
        else {
            // TODO None exist, display message?
        }
    }

    /************************************
     * Show one instance of a naked single
     *********************************/
    private void showNakedSingle() {
        int index = mGame.getNakedSingle();
        if (index != -1) {
            removeAllHighlights();
            squareViews[index].setDarkHighlight();
            for (int i = 1; i <= 9; i++) {
                if (mGame.getBoardNotepadAt(index, i))
                    squareViews[index].setNotepadHighlightGreen(i);
            }
        }
        else {
            // TODO None exist, display message?
        }
    }

    /************************************
     * Handles highlighting for hint display
     *********************************/
    private void hintDisplay(Strategies.Hint hint) {
        removeAllHighlights();
        for (int i : hint.hintInfo[Strategies.CONNECTED]) {
            squareViews[i].setHighlighted(true, true);
        }
        for (int i : hint.hintInfo[Strategies.SELECTED]) {
            squareViews[i].setDarkHighlight();
        }
        if (hint.getRemoveFrom() == Strategies.SELECTED) {
            for (int i : hint.hintInfo[Strategies.SELECTED]) {
                boolean[] colors = new boolean[9];
                for (int num : hint.hintInfo[Strategies.NOTEPAD])
                    colors[num - 1] = true;
                for (int j = 1; j <= colors.length; j++) {
                    if (colors[j - 1])
                        squareViews[i].setNotepadHighlightRed(j);
                    else
                        squareViews[i].setNotepadHighlightGreen(j);
                }
            }
        }
        else {
            for (int i : hint.hintInfo[Strategies.CONNECTED]) {
                for (int num : hint.hintInfo[Strategies.NOTEPAD])
                    squareViews[i].setNotepadHighlightRed(num);
            }
            for (int i : hint.hintInfo[Strategies.SELECTED]) {
                for (int num : hint.hintInfo[Strategies.NOTEPAD])
                    squareViews[i].setNotepadHighlightGreen(num);
            }
        }
    }

    /************************************
     * Clears highlights from squares
     *********************************/
    private void removeAllHighlights() {
        for (int i = 0; i < squareViews.length; i++) {
            if(!colorGame) {
                squareViews[i].setHighlighted(false, true);
                squareViews[i].clearNotepadHighlights();
            }
            else{
                squareViews[i].setHighlighted(false, false);
            }
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
                if(!colorGame) {
                    squareViews[i].setHighlighted(true, true); // Set true if current square is connected to selected
                }
                else{
                    squareViews[i].setHighlighted(true, false); // Set true if current square is connected to selected
                }
            else {
                if(!colorGame) {
                    squareViews[i].setHighlighted(false, true);
                }
                else{
                    squareViews[i].setHighlighted(false, false);
                }
            }
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
                highlightConnected();



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
        gameHelper.getApiClient().connect();

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
                if(!solveClicked&&!mGame.isGameFinished()&&mGame.getBoardValueAt(selectedSquare)!=0) {
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
                    squareViews[conflict].setErrorHighlight(colorGame);     // Display error highlight
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
        updateLilNums();
        if(mGame.isGameFinished()&&!solveClicked){
                endOfGame(false);
        }

    }

    /* Uses the BoardActivity UI to solve a puzzle given the solution steps */
    public void solvePuzzle() {

        Handler solutionHandler = new Handler();

        // Get solution steps

        final ArrayList<Square> newSteps = new ArrayList(mGame.getSolutionSteps());

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
                    updateBoard();

                }
            }, speed * i);
        }
    }

    /************************************
     * Number vs Color initial setup
     *********************************/
    private void customizeLayout(boolean resume) {
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
        hintButton.setBackgroundColor(getResources().getColor(R.color.colorKeypad));

        /////////set notepadbutton with correct image, color or number

        ImageView artHolder = (ImageView)findViewById(R.id.artHolder);
        if (!colorGame) {
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.numbers));
            artHolder.setImageDrawable(null);

        } else {
            notepadIcon.setImageDrawable(getResources().getDrawable(R.drawable.colors));
            BackgroundImages mBackgroundImages = new BackgroundImages();
            mGame.setImageNumber(mBackgroundImages.getPictureNumber());
            if(resume){
                artHolder.setImageDrawable(getResources().getDrawable(mBackgroundImages.getImage(mGame.getImageNumber())));
            }
            else {
                artHolder.setImageDrawable(getResources().getDrawable(mBackgroundImages.getImage()));
            }
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
        hintButton.setClickable(clickable);
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
        getMenuInflater().inflate(com.licktopia.thechampionofsudoku.R.menu.menu_main, menu);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        /*
        if(solvedState) {
            SpannableString s = new SpannableString("Save game");
            s.setSpan(new ForegroundColorSpan(Color.LTGRAY), 0, s.length(), 0);
            menu.getItem(5).setTitle(s);
        }
        */
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
            uploadSavedGame(false);

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
        gameHelper.getApiClient().connect();
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
        if(mAutoSave && isInternet() && gameHelper.getApiClient().isConnected()&&!solvedState){
            uploadSavedGame(true);
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        gameHelper.onActivityResult(requestCode, resultCode, intent);


        if (intent != null) {
            if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                // Load a snapshot.
                SnapshotMetadata snapshotMetadata = (SnapshotMetadata)
                        intent.getParcelableExtra(Snapshots.EXTRA_SNAPSHOT_METADATA);
                mCurrentSaveName = snapshotMetadata.getUniqueName();

                // Load the game data from the Snapshot
                // ...
            } else if (intent.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                // Create a new snapshot named with a unique string
                String unique = new BigInteger(281, new Random()).toString(13);
                mCurrentSaveName = "snapshotTemp-" + unique;

                // Create the new snapshot
                // ...
            }
        }
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


