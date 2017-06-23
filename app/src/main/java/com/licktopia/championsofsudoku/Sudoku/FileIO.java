package com.licktopia.championsofsudoku.Sudoku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;
import android.content.Context;

/**
 * Created by John Konecny on 6/19/2017.
 */

class FileIO {
    private static ThreadLocalRandom _Rand = ThreadLocalRandom.current();

    //loads a game
    protected static int[] loadBoard(int level, Context ctx, int totalBoardElements) {
        String filename = "boards/" + String.valueOf(level) +".txt";

        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        int[] values = new int[totalBoardElements];

        try {
            reader = new BufferedReader(new InputStreamReader(
                    ctx.getAssets().open(filename)));
            String line;
            int p=0;
            int randomNum = _Rand.nextInt(0, 9999 + 1);
            while ((line = reader.readLine())!= null){
                if(p==randomNum) {
                    builder.append(line);
                }

                p++;
            }
            String text = builder.toString();
            for(int i =0; i < totalBoardElements; i++)
            {
                values[i]= Integer.parseInt(String.valueOf(text.charAt(i)));
            }
            reader.close();
            return values;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    //loads all static weights for
    protected static ScoreWeights loadScoreWeights(int level, Context ctx) {
        String filename = "score.csv";
        String cvsSplitBy = ",";

        BufferedReader reader = null;
        //stores which column the level's score weight are
        int levelCol = -1;
        long basePoints = -1,
                oneTimeGuessBonus = -1;
        double runningTimePenalty = -1,
                incorrectGuessPenalty = -1;


        try {
            reader = new BufferedReader(new InputStreamReader(
                    ctx.getAssets().open(filename)));
            String line;

            //get column of the level
            if((line = reader.readLine())!= null)
            {
                String[] lineArr = line.split(cvsSplitBy);
                for(int i = 0; i < lineArr.length; i++)
                {
                    try
                    {
                        if(level == Integer.valueOf(lineArr[i]))
                        {
                            levelCol = i;
                            break;
                        }
                    }
                    catch (IllegalArgumentException e)
                    {
                        //do nothing
                    }

                }

                //if the level's column is found then continue
                if(levelCol >= 0)
                {
                    line = reader.readLine();
                    lineArr = line.split(cvsSplitBy);
                    basePoints = Long.valueOf(lineArr[levelCol]);

                    line = reader.readLine();
                    lineArr = line.split(cvsSplitBy);
                    oneTimeGuessBonus = Long.valueOf(lineArr[levelCol]);

                    line = reader.readLine();
                    lineArr = line.split(cvsSplitBy);
                    runningTimePenalty = Double.valueOf(lineArr[levelCol]);

                    line = reader.readLine();
                    lineArr = line.split(cvsSplitBy);
                    incorrectGuessPenalty = Double.valueOf(lineArr[levelCol]);
                }
            }


            reader.close();
        } catch (IOException e) {
            System.out.printf("Failed to open %s\n", filename);
            e.printStackTrace();
        }
        return new ScoreWeights(basePoints, oneTimeGuessBonus, runningTimePenalty, incorrectGuessPenalty);

    }
}
