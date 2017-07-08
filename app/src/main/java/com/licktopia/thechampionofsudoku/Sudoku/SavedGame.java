package com.licktopia.thechampionofsudoku.Sudoku;

/**
 * Created by do_de on 6/29/2017.
 */

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.io.File;


@DynamoDBTable(tableName = "SavedGame")
public class SavedGame {
    private String gamerID;
    private String exactTime;
    private byte[] saveFile;
    private boolean colorMode;

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

    //must be a binary file type
    @DynamoDBAttribute(attributeName = "saveFile")
    public byte[] getSaveFile() {
        return saveFile;
    }
    public void setSaveFile(byte[] saveFile) {
        this.saveFile = saveFile;
    }

    //must be a binary file type
    @DynamoDBAttribute(attributeName = "colorMode")
    public boolean getColorMode() {
        return colorMode;
    }
    public void setColorMode(boolean colorMode) {
        this.colorMode = colorMode;
    }




}