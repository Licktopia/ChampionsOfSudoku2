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
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.licktopia.thechampionofsudoku.Gamer;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


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
                obj.setTime("5:40");
                obj.setDate("June 29");
                obj.setColor(false);
                obj.setPuzzleNumber(8534);
                obj.setLevel(3);
                obj.setAccuracy(.95);
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
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String partitionKey = "g07411554930192965639";
                CompletedGame cg = new CompletedGame();
                cg.setGamerID(partitionKey);


                HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
                eav.put(":v1",  new AttributeValue().withS("g07411554930192965639"));

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
                        for(int i = 0; i<list[0].size();i++){
                            cGames.add(list[0].get(i));
                        }


                    }
                };
                Thread mythread = new Thread(runnable);
                mythread.start();

                processList(list[0]);




/*

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
*/
// Do something with result.
            }
        });


    }
    private void processList(List<CompletedGame> list){
        for(int i = 0; i < list.size(); i++){
            Toast.makeText(this,list.get(i).getExactTime(),Toast.LENGTH_SHORT).show();
        }
    }
    private void processItemList(List<CompletedGame>list){
        for(int i = 0; i< list.size();i++){
            Toast.makeText(this,list.get(i).getDate(),Toast.LENGTH_SHORT).show();
        }

    }
}
