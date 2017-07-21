package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.licktopia.thechampionofsudoku.Activities.BaseActivity;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;

import java.util.ArrayList;
import java.util.List;


public class DynamoDbTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamo_db_test);

        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:40802ef0-7537-4910-8442-31e2b30c5a7d", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_2, // Region
                credentialsProvider);



        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        final DynamoDBMapper mapper = new DynamoDBMapper(ddbClient);

        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));


        Button queryButton = (Button)findViewById(R.id.query);



        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String partitionKey = BaseActivity.getId();
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
                        for(int i = 0; i<list[0].size();i++){
                            sGames.add(list[0].get(i));
                        }


                    }
                };


                Thread mythread = new Thread(runnable);
                mythread.start();
                try
                {
                    /***** Maybe we should tell the user that the game is loading... *****/
                    mythread.join();
                    /******* FOR THE PURPOSE OF DEBUGGING THE FIRST GAME ON THE LIST GETS PICKED******************/
                    Intent intent = new Intent(DynamoDbTestActivity.this, BoardActivity.class);
                    //Make level less than zero so that activity knows to load a game
                    intent.putExtra("LEVEL", -5);
                    intent.putExtra("COLOR_MODE", list[0].get(0).getColorMode());
                    intent.putExtra("SAVE_FILE", list[0].get(0).getSaveFile());
                    startActivity(intent);
                }
                catch(InterruptedException e)
                {
                    Log.d("Load Interrupted", "That was rude");
                }


            }
        });


    }



}
