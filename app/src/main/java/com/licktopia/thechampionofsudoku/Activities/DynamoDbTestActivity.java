package com.licktopia.thechampionofsudoku.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.licktopia.thechampionofsudoku.Gamer;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;

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

        Button saveButton = (Button)findViewById(R.id.save_game);
        Button queryButton = (Button)findViewById(R.id.query);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CompletedGame obj = new CompletedGame();
                obj.setGamerID(Gamer.getId());
                obj.setRealScore(1000);
                obj.setXp(1200);
                obj.setTime(560);
                obj.setDate("June 29");
                obj.setColor(false);
                obj.setPuzzleNumber(8534);
                obj.setLevel(3);
                obj.setAccuracy(.95);



                // id field is null at this point
                Runnable runnable = new Runnable() {
                    public void run() {
                        //DynamoDB calls go here
                        mapper.save(obj);
                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();


                System.out.println("Object was saved with id " + obj.getGameID());
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompletedGame it = new CompletedGame();
                it.setGamerID("g07411554930192965639");

                final DynamoDBQueryExpression<CompletedGame> queryExpression = new DynamoDBQueryExpression<CompletedGame>()
                        .withHashKeyValues(it);


                Runnable runnable = new Runnable() {
                    public void run() {
                        //DynamoDB calls go here
                        List<CompletedGame> itemList = mapper.query(CompletedGame.class, queryExpression);
                        processItemList(itemList);


                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();







                int a = 5;
                int b = 6;
                int c=a+b;
// Do something with result.
            }
        });


    }
    private void processItemList(List<CompletedGame>list){
        for(int i = 0; i< list.size();i++){
            Toast.makeText(this,list.get(i).getDate(),Toast.LENGTH_SHORT).show();
        }

    }
}
