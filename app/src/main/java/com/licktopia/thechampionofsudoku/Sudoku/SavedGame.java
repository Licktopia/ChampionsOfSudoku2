package com.licktopia.thechampionofsudoku.Sudoku;

/**
 * Created by do_de on 6/29/2017.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;


@DynamoDBTable(tableName = "SavedGame")
public class SavedGame {
    private String gamerID;
    private String exactTime;


    @DynamoDBRangeKey(attributeName = "ExactTime")
    public String getExactTime() {
        return exactTime;
    }
    public void setExactTime(String exactTime) {
        this.exactTime = exactTime;
    }

    @DynamoDBHashKey(attributeName = "GamerID")
    public String getGamerID() {
        return gamerID;
    }
    public void setGamerID(String gamerID) {
        this.gamerID = gamerID;
    }






}