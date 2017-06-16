package com.licktopia.championsofsudoku;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
            int p=0;
            int randomNum = ThreadLocalRandom.current().nextInt(0, 9999 + 1);
            while ((line = reader.readLine())!= null){
                if(p==randomNum) {
                    builder.append(line);
                }

                p++;
            }
            String text = builder.toString();
            for(int i =0; i <81; i++)
            {
                values[i]= Integer.parseInt(String.valueOf(text.charAt(i)));
            }

            boardNumber = randomNum;
            return values;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
