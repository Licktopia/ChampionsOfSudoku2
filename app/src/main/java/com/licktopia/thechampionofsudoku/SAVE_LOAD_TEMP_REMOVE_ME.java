package com.licktopia.thechampionofsudoku;
import com.licktopia.thechampionofsudoku.Sudoku.Game;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
/**
 * Created by John Konecny on 6/25/2017.
 */

public class SAVE_LOAD_TEMP_REMOVE_ME {
    public static void Save(String fileName, Game gm)
    {
        try
        {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(fileName));
            objectOutputStream.writeObject(gm);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (IOException e)
        {
            System.out.printf("I failed to save!\n");
        }


    }

    public static Game Load(String fileName)
    {
        try
        {

            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(fileName));

            Game gm = (Game)objectInputStream.readObject();

            objectInputStream.close();
            return gm;
        }
        catch (IOException e)
        {
            System.out.printf("I failed to load!\n");
        }
        catch (ClassNotFoundException e)
        {
            System.out.printf("What class are you taking about!\n");
        }

        return null;
    }
}
