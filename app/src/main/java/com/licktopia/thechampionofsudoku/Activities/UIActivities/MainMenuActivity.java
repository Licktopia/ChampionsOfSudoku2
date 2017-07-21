package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.Snapshots;


import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.PlaySounds;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainMenuActivity  extends BaseActivity {
    private static final String PREF_FILE = "com.licktopia.com.licktopia.championofsudoku.preferences";
    private static final int RC_SAVED_GAMES = 9009;
    private static final float RESUME_ALPHA = .3f;
    private final static String TAG = "MainMenuActivity";
    private String mCurrentSaveName = "snapshotTemp";

    private int[] levelButtonTags = {R.id.beginnerButton,R.id.easyButton,R.id.mediumButton,R.id.hardButton,R.id.expertButton,R.id.masterButton};
    private int[] modeButtonTags = {R.id.topiaChoice,R.id.numberChoice};
    private ImageView userBadge;
    private LinearLayout mLevelSelect;
    private LinearLayout mModeSelect;
    private LinearLayout mSignInButton;
    private LinearLayout mBadge;
    private Button mSignOutButton;
    private LinearLayout mResumeButton;

    private int level = 3;
    private boolean colorMode = false;
    private Animation animShow, animHide, animHiSl, animShSlow;
    private PlaySounds mPlaySounds;

    private boolean clickedOut = false;
    private boolean userSignIn=false;
    private SharedPreferences.Editor ed;
    private GameHelper gameHelper;
    private byte[] mSaveGameData;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mLevelSelect = (LinearLayout)findViewById(R.id.level_select);
        mModeSelect = (LinearLayout)findViewById(R.id.mode_select);
        mBadge = (LinearLayout)findViewById(R.id.badge);
        mPlaySounds = new PlaySounds();
        SharedPreferences mSharedPreferences = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        ed = mSharedPreferences.edit();
        ed.apply();
        userBadge = (ImageView)findViewById(R.id.user_badge_level);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mResumeButton = (LinearLayout) findViewById(R.id.resume);
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


        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        mResumeButton.setClickable(false);
        signInInitialization();
        initAnimation();
        levelButtonInitialize();
        modeButtonInitialize();
        menuBackInitialize();
        setMessage();
        initializeLick();
        initializeShareBar();


    }

    private void initializeResumeButton(LinearLayout mResumeButton) {
        mResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SavedGameActivity.class);
                startActivity(intent);

            }
        });
    }


    /***************************************
     * Creates mGoogleApi Client and sets listeners
     * for sign in and sign out
     ****************************************/
    private void signInInitialization() {


        mSignInButton = (LinearLayout)findViewById(R.id.signin_button);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInClicked();
            }
        });
        mSignOutButton = (Button)findViewById(R.id.sign_out_button);
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOutClicked();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.onStart(this);


    }

    @Override
    protected void onStop() {
        gameHelper.onStop();
        super.onStop();

    }

    @Override
    protected void onResume(){
        super.onResume();
        userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
    }

    private void loadPlayer(){
        if(gameHelper.getApiClient().isConnected()) {
            setUsername(Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName());
            setId(Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getPlayerId());
            mBadgeButtonInitialize(getUsername(), getId());
        }
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

    private void queryCompletedGameDb(){
        if(isInternet()) {
            String partitionKey = getId();
            CompletedGame cg = new CompletedGame();
            cg.setGamerID(partitionKey);

            final DynamoDBQueryExpression<CompletedGame> queryExpression = new DynamoDBQueryExpression<CompletedGame>()
                    .withHashKeyValues(cg)
                    .withConsistentRead(false);

            final List<CompletedGame>[] list = new List[]{new ArrayList<CompletedGame>()};

            // id field is null at this point
            Runnable runnable = new Runnable() {
                public void run() {
                    //DynamoDB calls go here
                    list[0] = mapper.query(CompletedGame.class, queryExpression);
                    ArrayList<CompletedGame> cGames = new ArrayList<CompletedGame>();
                    for (int i = 0; i < list[0].size(); i++) {
                        cGames.add(list[0].get(i));
                    }
                    setCompletedGames(cGames);
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();
        }

    }

    private void querySavedGameDb() {
        if (isInternet()) {
            String partitionKey = getId();
            SavedGame sg = new SavedGame();
            sg.setGamerID(partitionKey);


            final DynamoDBQueryExpression<SavedGame> queryExpression = new DynamoDBQueryExpression<SavedGame>()
                    .withHashKeyValues(sg)
                    .withConsistentRead(false);

            final List<SavedGame>[] list = new List[]{new ArrayList<SavedGame>()};

            // id field is null at this point
            Runnable runnable = new Runnable() {
                public void run() {
                    //DynamoDB calls go here
                    list[0] = mapper.query(SavedGame.class, queryExpression);
                    ArrayList<SavedGame> sGames = new ArrayList<SavedGame>();
                    for (int i = 0; i < list[0].size(); i++) {
                        sGames.add(list[0].get(i));
                    }
                    setSavedGames(sGames);


                    updateActivity(true);


                }
            };

            Thread mythread = new Thread(runnable);

            updateActivity(false);
            mythread.start();





        }
    }

    //Updates the save game button based on the current state of the loading the saved game
    void updateActivity(final boolean finishedrunning)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (getSavedGames().isEmpty()) {
                    if(finishedrunning) {
                        mProgressBar.setVisibility(View.GONE);
                        mResumeButton.setVisibility(View.VISIBLE);
                        mResumeButton.setAlpha(RESUME_ALPHA);
                        mResumeButton.setClickable(false);
                    }


                }
                 else {
                    if(finishedrunning) {
                        initializeResumeButton(mResumeButton);
                        mResumeButton.setAlpha(1f);
                        mResumeButton.setClickable(true);
                        mProgressBar.setVisibility(View.GONE);
                        mResumeButton.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }


    /***************************************
    *Update UI when login or logout occurs
    ***************************************/
    private void updateUI(boolean loggedIn){
        if(loggedIn){
            if(userSignIn) {
                mPlaySounds.playSounds(this,7);
                userSignIn = false;
            }

            mProgressBar.setVisibility(View.VISIBLE);
            mResumeButton.setVisibility(View.GONE);


            mProgressBar.setVisibility(View.VISIBLE);
            mResumeButton.setVisibility(View.GONE);



            mSignInButton.setVisibility(View.GONE);
            mBadge.setVisibility(View.VISIBLE);
            loadPlayer();
            gameHelper.getApiClient().connect();
            if(gameHelper.getApiClient().isConnected()) {
                getHighestScoreFromLeaderBoard();
            }
            queryCompletedGameDb();
            querySavedGameDb();
        }
        else{
            setNullUser();
            if(clickedOut) {
                mPlaySounds.playSounds(this,8);
            }
            mProgressBar.setVisibility(View.GONE);
            mResumeButton.setAlpha(RESUME_ALPHA);
            mResumeButton.setVisibility(View.VISIBLE);
            clickedOut = false;
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.INVISIBLE);
            mBadge.setVisibility(View.GONE);
            userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
        }
        setMessage();

    }

    private void getHighestScoreFromLeaderBoard(){
        PendingResult result = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(gameHelper.getApiClient(),
               getString(R.string.leaderboard_most_total_xp), LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC);

        result.setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult result) {
                // check if valid score
                if (result != null
                        && GamesStatusCodes.STATUS_OK == result.getStatus().getStatusCode()
                        && result.getScore() != null) {
                    long highestScore = result.getScore().getRawScore();
                    setXp(highestScore);
                    userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
                }

            }

        });
    }

    // Call when the sign-in button is clicked
    private void signInClicked() {
        gameHelper.beginUserInitiatedSignIn();
        userSignIn = true;
    }

    // Call when the sign-out button is clicked
    private void signOutClicked() {
        gameHelper.signOut();
        clickedOut = true;
        updateUI(false);
    }

    private void initializeShareBar() {
        RelativeLayout mShareBar = (RelativeLayout)findViewById(R.id.share_bar);
        mShareBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void initializeLick() {
        LinearLayout mLick = (LinearLayout)findViewById(R.id.lick);
        mLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playLickSound();

            }
        });
    }

    private void menuBackInitialize() {
        ImageButton mMenuBack = (ImageButton)findViewById(R.id.menu_back);
        mMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();

            }


        });
    }

    private void mBadgeButtonInitialize(final String gamername, final String gamerID){
        userBadge.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
        mBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, GamerProfileActivity.class);
                intent.putExtra("GAMERNAME", gamername);
                intent.putExtra("GAMERID", gamerID);
                startActivity(intent);
            }
        });
    }

    private void modeButtonInitialize() {
        ImageButton[] modeButtons = new ImageButton[modeButtonTags.length];
        for(int i = 0; i< modeButtons.length; i++){
            modeButtons[i] =(ImageButton)findViewById(modeButtonTags[i]);
            final int finalI = i;
            modeButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalI ==0){
                        colorMode =true;
                    }
                    else{
                        colorMode=false;
                    }
                    Intent intent = new Intent(MainMenuActivity.this, BoardActivity.class);
                    boolean resume = false;
                    intent.putExtra("RESUME", resume);
                    intent.putExtra("LEVEL", level);
                    intent.putExtra("COLOR_MODE", colorMode);
                    startActivity(intent);
                }
            });
        }
    }

    private void levelButtonInitialize() {
        ImageButton[] levelButtons = new ImageButton[levelButtonTags.length];
        for(int i = 0; i< levelButtons.length; i++){
            levelButtons[i] = (ImageButton)findViewById(levelButtonTags[i]);
            final int finalI = i;
            levelButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    level = finalI;
                    setMessage();
                    chooseGameType();
                }
            });
        }
    }

    private void chooseGameType(){
        mPlaySounds.playSounds(this,10);
        mLevelSelect.setVisibility(View.GONE);
        mLevelSelect.startAnimation(animHide);
        mModeSelect.setVisibility(View.VISIBLE);
        mModeSelect.startAnimation(animShow);

    }

    private void goBack() {
        mModeSelect.setVisibility(View.INVISIBLE);
        mModeSelect.startAnimation(animHide);
        mLevelSelect.setVisibility(View.VISIBLE);
        mLevelSelect.startAnimation(animShow);
        mPlaySounds.playSounds(this,10);
    }

    private void playLickSound(){
        mPlaySounds.playSounds(this, 5);
    }

    private void showSavedGamesUI() {
        if(gameHelper.getApiClient().isConnected()) {
            int maxNumberOfSavedGamesToShow = 5;
            Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(gameHelper.getApiClient(),
                    "See My Saves", false, true, maxNumberOfSavedGamesToShow);
            startActivityForResult(savedGamesIntent, RC_SAVED_GAMES);
        }
        else{
            Toast.makeText(this, R.string.login_touse_thisfeature,Toast.LENGTH_SHORT).show();
        }
    }

    private void setMessage(){
        TextView mMessage = (TextView)findViewById(R.id.message);
        if(gameHelper.getApiClient().isConnected()){
            mMessage.setVisibility(View.INVISIBLE);
        }
        else{
            mMessage.setVisibility(View.VISIBLE);
        }
        if(gameHelper.getApiClient().isConnected()) {
            if (level < 2) {
                mMessage.setText(getString(R.string.harder_puzzle_message));
                mMessage.setVisibility(View.VISIBLE);
            } else {
                mMessage.setText("");
                mMessage.setVisibility(View.INVISIBLE);
            }
        }
        else{
            mMessage.setText(R.string.sign_in_message);
            mMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSignIn = true;
                    beginUserInitiatedSignIn();
                }
            });
        }
    }
    private void initAnimation()
    {
        animShow = AnimationUtils.loadAnimation( this, R.anim.view_show);
        animHide = AnimationUtils.loadAnimation( this, R.anim.view_hide);
        animHiSl = AnimationUtils.loadAnimation(this, R.anim.view_hideslow);
        animShSlow = AnimationUtils.loadAnimation(this, R.anim.view_showslow);
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

        if (gameHelper.getApiClient() != null && gameHelper.getApiClient().isConnected()) {
            // Call a Play Games services API method, for example:
            Games.Achievements.unlock(gameHelper.getApiClient(), getString(R.string.achievement_joy_giver));
        }



    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        gameHelper.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
                // Load a snapshot.
                SnapshotMetadata snapshotMetadata = (SnapshotMetadata)
                        data.getParcelableExtra(Snapshots.EXTRA_SNAPSHOT_METADATA);
                mCurrentSaveName = snapshotMetadata.getUniqueName();

                // Load the game data from the Snapshot
                // ...
            } else if (data.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
                // Create a new snapshot named with a unique string
                String unique = new BigInteger(281, new Random()).toString(13);
                mCurrentSaveName = "snapshotTemp-" + unique;

                // Create the new snapshot
                // ...
            }
        }

    }


    @Override
    public void onSignInFailed() {
        updateUI(false);
    }

    @Override
    public void onSignInSucceeded() {
        updateUI(true);
    }


}
