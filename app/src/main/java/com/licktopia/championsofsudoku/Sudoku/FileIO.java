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

    //
    protected static int[] readFileFromAssets(int level, Context ctx) {
        String filename;


        if(level == 0){
            filename="0.txt";
        }
        else if(level==1){
            filename="1.txt";
        }
        else if(level==2){
            filename="2.txt";
        }
        else if(level==3){
            filename="3.txt";
        }
        else if(level==4){
            filename="4.txt";
        }
        else {
            filename="5.txt";
        }
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        int[] values = new int[81];

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
            for(int i =0; i <81; i++)
            {
                values[i]= Integer.parseInt(String.valueOf(text.charAt(i)));
            }

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
