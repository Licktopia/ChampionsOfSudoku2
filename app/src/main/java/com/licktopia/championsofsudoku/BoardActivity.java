package com.licktopia.championsofsudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.Random;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

public class BoardActivity extends AppCompatActivity {

    int boardNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
    }

    //used for all random number generation
    private static Random rand = new Random();

    /***************************************************
     * Function takes level/filename as paremeter. Gets a random
     * board from the matching file and returns an array of ints
     * representing a starting board. Sets boardNumber variable
     ***************************************************/
    private int[] readFileFromAssets(String filename) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        int[] values = new int[81];

        try {
            reader = new BufferedReader(new InputStreamReader(
                    getAssets().open(filename)));
            String line;
            int p = 0;
            int randomNum = rand.nextInt(9999);

            //loop until there are no more line or the randomNum line is found
            while ((line = reader.readLine())!= null && randomNum >= p){
                if(p==randomNum) {
                    builder.append(line);
                }

                p++;
            }
            String text = builder.toString();

            //checks if a valid size string was found.
            if(text.length() != values.length)
            {
                System.err.printf("readFileFromAssets: Text length is %d, but Values length is %d!\n", text.length(), values.length);
                return null;
            }


            for(int i =0; i < values.length; i++)
            {
                try
                {
                    values[i]= Integer.parseInt(String.valueOf(text.charAt(i)));
                    if(values[i] < 1 || values[i] > 9)
                    {
                        System.err.printf("readFileFromAssets: All values must be between 1 through 9!\n");
                        return null;
                    }
                }
                catch(NumberFormatException e)
                {
                    System.err.printf("readFileFromAssets: %s is not an int!\n", text.charAt(i));
                    return null;
                }
            }

            boardNumber = randomNum;
            reader.close();

            return values;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //failed to open the file
        return null;

    }

}
