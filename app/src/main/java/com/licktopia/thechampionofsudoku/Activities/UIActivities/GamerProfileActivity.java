package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.PlaySounds;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;
import com.licktopia.thechampionofsudoku.SudokuFacts;
import com.licktopia.thechampionofsudoku.Tags;

import java.util.ArrayList;
import java.util.List;

public class GamerProfileActivity extends BaseActivity {

    private SudokuFacts sudokuFacts;
    private GameHelper gameHelper;
    private PlaySounds mPlaySounds;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private CognitoSyncManager syncClient;
    private AmazonDynamoDBClient ddbClient;
    private DynamoDBMapper mapper;
    private Button mHistoryButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamer_profile);
        mHistoryButton = (Button) findViewById(R.id.history_button);
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:40802ef0-7537-4910-8442-31e2b30c5a7d", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );
        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);

        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        //TODO Remove this for production
        gameHelper.enableDebugLog(true);
        gameHelper.setup(this);
        queryCompletedGameDb();
        initializeSignOut();
        updateUI();
        listeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();


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

        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GamerProfileActivity.this, CompletedGameActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateUI() {
        TextView mTitleName = (TextView) findViewById(R.id.title_name);
        TextView mFactText = (TextView) findViewById(R.id.fact_text);
        TextView mDidYouKnow = (TextView) findViewById(R.id.did_you_know);
        TextView xpDisplay = (TextView) findViewById(R.id.xp_display);
        TextView nextPowerUpDisplay = (TextView) findViewById(R.id.powerup_display);
        LinearLayout mFactBackground = (LinearLayout) findViewById(R.id.fact_background);
        ImageView mRankDisplay = (ImageView)findViewById(R.id.rank_display);
        Button mHistoryButton = (Button) findViewById(R.id.history_button);

        sudokuFacts = new SudokuFacts();
        mFactText.setText(sudokuFacts.getFunFact());
        String color = sudokuFacts.getColorHex();
        mDidYouKnow.setBackgroundColor(Color.parseColor(color));
        mFactBackground.setBackgroundColor(Color.parseColor(color));
        mTitleName.setBackgroundColor(Color.parseColor(color));
        mTitleName.setText(getUsername());

        if(completedGames!= null && completedGames.isEmpty()){
            mHistoryButton.setAlpha(.5f);
        }
        else{
            mHistoryButton.setAlpha(1);
        }
        if(getLevel()==CUTOFF.length){
            nextPowerUpDisplay.setText(RANKNAMES[getLevel()]);
        }
        else{
            nextPowerUpDisplay.setText(new StringBuilder().append(getString(R.string.next_levelup)).append(" ").append(String.format("%,d", CUTOFF[getLevel()] - getXp())).append(" XP").toString());
        }
        xpDisplay.setText(new StringBuilder().append(String.format("%,d", Integer.parseInt(String.valueOf(getXp())))).append(" XP").toString());
        nextPowerUpDisplay.setText(new StringBuilder().append(getString(R.string.next_levelup)).append(" ").append(String.format("%,d", CUTOFF[getLevel() + 1] - getXp())).append(" XP").toString());
        mRankDisplay.setImageDrawable(getResources().getDrawable(BADGETAGS[getLevel()]));
        ImageView[] verticalBadges = new ImageView[52];
        for(int i = 0; i< Tags.verticalBadges.length;i++){
            verticalBadges[i]=(ImageView)findViewById(Tags.verticalBadges[i]);
            verticalBadges[i].setImageDrawable(getResources().getDrawable(Tags.badgeTags[i]));
            if(i<=getLevel()){
                verticalBadges[i].setVisibility(View.VISIBLE);
            }
            else{
                verticalBadges[i].setVisibility(View.INVISIBLE);
            }
        }

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
                    if(completedGames!= null && completedGames.isEmpty()){
                        mHistoryButton.setAlpha(.5f);
                    }
                    else{
                        mHistoryButton.setAlpha(1);
                    }
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();


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
