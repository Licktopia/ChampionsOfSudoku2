package com.licktopia.championsofsudoku.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.championsofsudoku.Gamer;
import com.licktopia.championsofsudoku.PlaySounds;
import com.licktopia.championsofsudoku.R;
import com.licktopia.championsofsudoku.SudokuFacts;
import com.licktopia.championsofsudoku.Tags;

public class GamerProfileActivity extends BaseGameActivity {

    private SudokuFacts sudokuFacts;
    private GameHelper gameHelper;
    PlaySounds mPlaySounds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamer_profile);

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //TODO Remove this for production
        gameHelper.enableDebugLog(true);
        gameHelper.setup(this);
        initializeSignOut();
        updateUI();
        listeners();
    }

    private void initializeSignOut() {
        mPlaySounds = new PlaySounds();
        Button mSignOut = (Button)findViewById(R.id.sign_out_new);
        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameHelper.signOut();
                playDisconnect();
                Intent intent = new Intent(GamerProfileActivity.this, MainMenuActivity.class);
                startActivity(intent);

            }
        });
    }

    private void playDisconnect() {
        mPlaySounds.playSounds(this, 8);
    }
    private void listeners() {
        Button mLeaderButton = (Button)findViewById(R.id.leader);
        mLeaderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameHelper.getApiClient() != null && gameHelper.getApiClient().isConnected()) {
                    // Call a Play Games services API method, for example:
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(gameHelper.getApiClient()), 5);;
                } else {
                    // Alternative implementation (or warn user that they must
                    // sign in to use this feature)
                    Toast.makeText(GamerProfileActivity.this, R.string.login_tosee_leaderboards , Toast.LENGTH_SHORT).show();
                }

            }

        });
        Button mAchievementButton = (Button)findViewById(R.id.achievements);
        mAchievementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameHelper.getApiClient() != null && gameHelper.getApiClient().isConnected()) {
                    // Call a Play Games services API method, for example:

                    startActivityForResult(Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()),
                            5434);
                } else {
                    // Alternative implementation (or warn user that they must
                    // sign in to use this feature)
                    Toast.makeText(GamerProfileActivity.this, R.string.mustbe_loggedin_viewachievements, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateUI() {
        String gamerName = getIntent().getExtras().getString("GAMERNAME");
        String gamerId = getIntent().getExtras().getString("GAMERID");
        TextView mTitleName = (TextView) findViewById(R.id.title_name);
        TextView mFactText = (TextView) findViewById(R.id.fact_text);
        TextView mDidYouKnow = (TextView) findViewById(R.id.did_you_know);
        TextView xpDisplay = (TextView) findViewById(R.id.xp_display);
        TextView nextPowerUpDisplay = (TextView) findViewById(R.id.powerup_display);
        LinearLayout mFactBackground = (LinearLayout) findViewById(R.id.fact_background);
        ImageView mRankDisplay = (ImageView)findViewById(R.id.rank_display);

        sudokuFacts = new SudokuFacts();
        mFactText.setText(sudokuFacts.getFunFact());
        String color = sudokuFacts.getColorHex();
        mDidYouKnow.setBackgroundColor(Color.parseColor(color));
        mFactBackground.setBackgroundColor(Color.parseColor(color));
        mTitleName.setBackgroundColor(Color.parseColor(color));
        mTitleName.setText(Gamer.getUsername());
        xpDisplay.setText(String.format("%,d",Integer.parseInt(String.valueOf(Gamer.getXp()))) +" XP");
        nextPowerUpDisplay.setText( "Next Level Up " + String.format("%,d",Gamer.CUTOFF[Gamer.getLevel()]-Gamer.getXp())+ " XP");
        mRankDisplay.setImageDrawable(getResources().getDrawable(Gamer.BADGETAGS[Gamer.getLevel()]));
        ImageView[] verticalBadges = new ImageView[52];
        for(int i = 0; i< Tags.verticalBadges.length;i++){
            verticalBadges[i]=(ImageView)findViewById(Tags.verticalBadges[i]);
            verticalBadges[i].setImageDrawable(getResources().getDrawable(Tags.badgeTags[i]));
            if(i<=Gamer.getLevel()){
                verticalBadges[i].setVisibility(View.VISIBLE);
            }
            else{
                verticalBadges[i].setVisibility(View.INVISIBLE);
            }
        }

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
        Toast.makeText(this, R.string.nolonger_loggedin,Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(GamerProfileActivity.this, GamerProfileActivity.class);
        startActivity(intent);
    }


    @Override
    public void onSignInSucceeded() {
        if(gameHelper.getApiClient().isConnected()) {
            TextView mTitleName = (TextView) findViewById(R.id.title_name);
            mTitleName.setText(Games.Players.getCurrentPlayer(gameHelper.getApiClient()).getDisplayName());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        gameHelper.onActivityResult(requestCode, resultCode, data);

    }
}
