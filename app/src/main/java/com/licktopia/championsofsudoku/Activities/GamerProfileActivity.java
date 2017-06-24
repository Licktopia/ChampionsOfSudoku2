package com.licktopia.championsofsudoku.Activities;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.championsofsudoku.R;
import com.licktopia.championsofsudoku.SudokuFacts;

public class GamerProfileActivity extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;
    SudokuFacts sudokuFacts;
    private boolean mAutoSignInFlow = false;
    GameHelper gameHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamer_profile);

        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);

        GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener()
        {
            @Override
            public void onSignInFailed(){ }

            @Override
            public void onSignInSucceeded(){ }
        };

        gameHelper.setup(gameHelperListener);



        mAutoSignInFlow = getIntent().getExtras().getBoolean("AUTOSIGN");
        updateUI();
        listeners();
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
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    // Call a Play Games services API method, for example:

                    startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient),
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
        LinearLayout mFactBackground = (LinearLayout) findViewById(R.id.fact_background);

        sudokuFacts = new SudokuFacts();
        mFactText.setText(sudokuFacts.getFunFact());
        String color = sudokuFacts.getColorHex();
        mDidYouKnow.setBackgroundColor(Color.parseColor(color));
        mFactBackground.setBackgroundColor(Color.parseColor(color));
        mTitleName.setBackgroundColor(Color.parseColor(color));
        mTitleName.setText(gamerName);
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
}
