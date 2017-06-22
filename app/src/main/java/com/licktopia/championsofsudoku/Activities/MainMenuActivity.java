package com.licktopia.championsofsudoku.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;

import com.google.android.gms.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameUtils;

import com.licktopia.championsofsudoku.PlaySounds;
import com.licktopia.championsofsudoku.R;

public class MainMenuActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String PREF_FILE = "com.licktopia.championsofsudoku.preferences";
    private static final String SIGN_IN_FLOW = "signinflow";
    private int[] levelButtonTags = {R.id.beginnerButton,R.id.easyButton,R.id.mediumButton,R.id.hardButton,R.id.expertButton,R.id.masterButton};
    private int[] modeButtonTags = {R.id.topiaChoice,R.id.numberChoice};
    LinearLayout mLevelSelect;
    LinearLayout mModeSelect;
    private LinearLayout mSignInButton;
    private LinearLayout mBadge;
    private Button mSignOutButton;

    private int level = 3;
    private boolean colorMode = false;
    private Animation animShow, animHide, animHiSl, animShSlow;
    private PlaySounds mPlaySounds;

    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;
    private boolean mResolvingConnectionFailure = false;
    private boolean mAutoStartSignInFlow = false;
    private boolean mSignInClicked = false;
    private SharedPreferences.Editor ed;

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
        mAutoStartSignInFlow = mSharedPreferences.getBoolean(SIGN_IN_FLOW, false);
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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                // add other APIs and scopes here as needed
                .build();

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
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        ed.putBoolean(SIGN_IN_FLOW, mAutoStartSignInFlow);
        ed.apply();

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // The player is signed in. Hide the sign-in button and allow the
        // player to proceed.
        updateUI(true);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingConnectionFailure) {
            // already resolving
            return;
        }

        // if the sign-in button was clicked or if auto sign-in is enabled,
        // launch the sign-in flow
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = true;

            // Attempt to resolve the connection failure using BaseGameUtils.
            // The R.string.signin_other_error value should reference a generic
            // error string in your strings.xml file, such as "There was
            // an issue with sign-in, please try again later."
            if (!BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult,
                    RC_SIGN_IN, R.string.signin_other_error)) {
                mResolvingConnectionFailure = false;
            }
        }

        // Put code here to display the sign-in button
        updateUI(false);

    }

    @Override
    public void onConnectionSuspended(int i) {
        // Attempt to reconnect
        mGoogleApiClient.connect();
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                // Bring up an error dialog to alert the user that sign-in
                // failed. The R.string.signin_failure should reference an error
                // string in your strings.xml file that tells the user they
                // could not be signed in, such as "Unable to sign in."
                BaseGameUtils.showActivityResultError(this,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }
    private void updateUI(boolean loggedIn){
        if(loggedIn){
            mSignInButton.setVisibility(View.GONE);
            mSignOutButton.setVisibility(View.VISIBLE);
            mBadge.setVisibility(View.VISIBLE);
            mAutoStartSignInFlow = true;
            setMessage();
        }
        else{
            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.INVISIBLE);
            mBadge.setVisibility(View.GONE);
            setMessage();
        }
    }
    // Call when the sign-in button is clicked
    private void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    // Call when the sign-out button is clicked
    private void signOutClicked() {
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        mGoogleApiClient.disconnect();
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
        if(mGoogleApiClient.isConnected()){
            mMessage.setVisibility(View.INVISIBLE);
        }
        else{
            mMessage.setVisibility(View.VISIBLE);
        }
        if(mGoogleApiClient.isConnected()) {
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
                    mSignInClicked = true;
                    mGoogleApiClient.connect();
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

    }
}
