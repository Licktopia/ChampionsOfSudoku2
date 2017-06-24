package com.licktopia.championsofsudoku.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;



import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.championsofsudoku.PlaySounds;
import com.licktopia.championsofsudoku.R;

public class MainMenuActivity  extends BaseGameActivity{
    private static final String PREF_FILE = "com.licktopia.championsofsudoku.preferences";

    private int[] levelButtonTags = {R.id.beginnerButton,R.id.easyButton,R.id.mediumButton,R.id.hardButton,R.id.expertButton,R.id.masterButton};
    private int[] modeButtonTags = {R.id.topiaChoice,R.id.numberChoice};
    private LinearLayout mLevelSelect;
    private LinearLayout mModeSelect;
    private LinearLayout mSignInButton;
    private LinearLayout mBadge;
    private Button mSignOutButton;

    private int level = 3;
    private boolean colorMode = false;
    private Animation animShow, animHide, animHiSl, animShSlow;
    private PlaySounds mPlaySounds;

    private boolean clickedOut = false;
    private boolean userSignIn=false;
    private SharedPreferences.Editor ed;
    private GameHelper gameHelper;

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

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //TODO Remove this for production
        gameHelper.enableDebugLog(true);
        gameHelper.setup(this);


        signInInitialization();
        initAnimation();
        levelButtonInitialize();
        modeButtonInitialize();
        menuBackInitialize();
        setMessage();
        initializeLick();
        initializeShareBar();
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


    private void loadPlayer(){
        if(gameHelper.getApiClient().isConnected()) {
            String gamerName = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName();
            String gamerId = Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getPlayerId();
            mBadgeButtonInitialize(gamerName, gamerId);
        }
    }
    private void updateUI(boolean loggedIn){
        if(loggedIn){
            if(userSignIn) {
                mPlaySounds.playSounds(this,7);
                userSignIn = false;
            }
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
            mBadge.setVisibility(View.VISIBLE);
            loadPlayer();
            setMessage();
        }
        else{
            if(clickedOut) {
                mPlaySounds.playSounds(this,8);
            }
            clickedOut = false;
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.INVISIBLE);
            mBadge.setVisibility(View.GONE);
            setMessage();
        }
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
        ImageView mLick = (ImageView)findViewById(R.id.lick);
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

    private void mBadgeButtonInitialize(final String gamername, String gamerID){
        mBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, GamerProfileActivity.class);
                intent.putExtra("GAMERNAME", gamername);
                intent.putExtra("GAMERID", gamername);
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
