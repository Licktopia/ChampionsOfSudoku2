package com.licktopia.championsofsudoku;

import android.app.ActivityOptions;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.licktopia.championsofsudoku.R;

import static android.view.View.GONE;

public class MainMenuActivity extends AppCompatActivity {
    private int[] levelButtonTags = {R.id.beginnerButton,R.id.easyButton,R.id.mediumButton,R.id.hardButton,R.id.expertButton,R.id.masterButton};
    private int[] modeButtonTags = {R.id.topiaChoice,R.id.numberChoice};
    LinearLayout mLevelSelect;
    LinearLayout mModeSelect;
    private ImageButton[] levelButtons;
    private ImageButton[] modeButtons;

    private int level;
    private boolean colorMode = false;
    private Animation animShow, animHide, animHiSl, animShSlow;
    private PlaySounds mPlaySounds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        mLevelSelect = (LinearLayout)findViewById(R.id.level_select);
        mModeSelect = (LinearLayout)findViewById(R.id.mode_select);
        mPlaySounds = new PlaySounds();
        initAnimation();
        levelButtonInitialize();
        modeButtonInitialize();
        menuBackInitialize();
        setMessage();
        initializeLick();
        initializeShareBar();
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
        modeButtons  = new ImageButton[modeButtonTags.length];
        for(int i =0; i<modeButtons.length;i++){
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
        levelButtons = new ImageButton[levelButtonTags.length];
        for(int i =0; i<levelButtons.length;i++){
            levelButtons[i] = (ImageButton)findViewById(levelButtonTags[i]);
            final int finalI = i;
            levelButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    level = finalI;
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
        if(level<2){
            mMessage.setText(getString(R.string.harder_puzzle_message));
            mMessage.setVisibility(View.VISIBLE);
        }
        else{
            mMessage.setText("");
            mMessage.setVisibility(View.INVISIBLE);
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
