package com.licktopia.thechampionofsudoku.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.example.games.basegameutils.BaseGameActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.AboutFragment;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.BoardActivity;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.CompletedGameFragment;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.GamerProfileFragment;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.InstructionsFragment;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.MainMenuFragment;
import com.licktopia.thechampionofsudoku.Activities.UIActivities.SavedGameFragment;
import com.licktopia.thechampionofsudoku.R;
import com.licktopia.thechampionofsudoku.Sudoku.CompletedGame;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Matt on 6/22/2017.
 */

public class BaseActivity extends BaseGameActivity
        implements MainMenuFragment.MainMenuFragmentListener,
        GamerProfileFragment.GamerProfileFragmentListener,
        BoardActivity.BoardFragmentListener {

    public static final String SAVED_KEY = "SAVED_KEY";
    public static final String COMPLETED_KEY = "COMPLETED_KEY";
    public static final String XP_KEY = "XP_KEY";
    public static final String USERNAME_KEY = "USERNAME_KEY";
    public static final String LEVEL_KEY = "LEVEL_KEY";

    public static final String MAIN_MENU_FRAGMENT = "MAIN_MENU_FRAGMENT";
    public static final String ABOUT_FRAGMENT = "ABOUT_FRAGMENT";
    public static final String BOARD_FRAGMENT = "BOARD_FRAGMENT";
    public static final String COMPLETED_GAME_FRAGMENT = "COMPLETED_GAME_FRAGMENT";
    public static final String GAMER_PROFILE_FRAGMENT = "GAMER_PROFILE_FRAGMENT";
    public static final String INSTRUCTIONS_FRAGMENT = "INSTRUCTIONS_FRAGMENT";
    public static final String SAVED_GAME_FRAGMENT = "SAVED_GAME_FRAGMENT";


    public static int[] CUTOFF = {0,250,5000,11000,18000,30000,42000,55000,70000,90000,115000,140000,167000,195000,225000,262000,305000,355000,413000,480000,560000,650000,
            755000, 880000, 1025000, 1190000, 1375000, 1600000, 1850000, 2145000, 2485000, 2875000, 3320000, 3835000, 4425000, 5107000, 5890000,6785000,
            7815000, 9000000, 10345000,11890000,13665000,15695000,18020000,20675000,25625000,35705000,46000000,61625000,77500000,99000000};
    public static double[] MULTIPLIER = {1.0,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2.0,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3.0,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,
                                         4.0,4.1,4.2,4.3,4.4,4.5,4.6,4.7,4.8,4.9,5.0,5.1,5.2,5.3,5.4,5.5,5.6,5.7,5.8,5.9,6.0,6.1};
    public static  int[] BADGETAGS = {R.drawable.rank0,R.drawable.rank1,R.drawable.rank2,R.drawable.rank3,R.drawable.rank4,R.drawable.rank5,R.drawable.rank6,R.drawable.rank7,
            R.drawable.rank8,R.drawable.rank9,R.drawable.rank10,R.drawable.rank11,R.drawable.rank12,R.drawable.rank13,R.drawable.rank14,R.drawable.rank15,
            R.drawable.rank16,R.drawable.rank17,R.drawable.rank18,R.drawable.rank19,R.drawable.rank20,R.drawable.rank21,R.drawable.rank22,R.drawable.rank23,
            R.drawable.rank24,R.drawable.rank25,R.drawable.rank26,R.drawable.rank27,R.drawable.rank28,R.drawable.rank29,R.drawable.rank30,R.drawable.rank31,
            R.drawable.rank32,R.drawable.rank33,R.drawable.rank34,R.drawable.rank35,R.drawable.rank36,R.drawable.rank37,R.drawable.rank38,R.drawable.rank39,
            R.drawable.rank40,R.drawable.rank41,R.drawable.rank42,R.drawable.rank43,R.drawable.rank44,R.drawable.rank45,R.drawable.rank46,R.drawable.rank47,R.drawable.rank48,R.drawable.rank49,R.drawable.rank50,R.drawable.rank51};
    public static String[] RANKNAMES = {"Elementary","Freshman","Sophomore","Junior","Senior","Graduate","Bronze I","Bronze II","Bronze III","Bronze IV","Bronze V","Bronze Eagle","Silver I","Silver II","Silver III","Silver IV","Silver V",
            "Silver Eagle","Gold I","Gold II","Gold III","Gold IV","Gold V","Golden Eagle", "Sapphire I", "Sapphire II", "Sapphire III", "Sapphire IV", "Sapphire Elite",
            "Diamond I", "Diamond II", "Diamond III", "Diamond IV", "Diamond Elite","Master I","Master II","Master III","Master IV","Master V","Grand Master","Legend I","Legend II","Legend III","Legend IV","Legend V",
            "Super Legend","Ace","Genius","Wizard","Champion","Super Champion","Grand Champion"};

    public static  String username;
    public static String id = "0";
    public static int level=0;
    public static long xp;
    public static ArrayList<CompletedGame> completedGames;
    public static ArrayList<SavedGame> savedGames;
    public static CognitoCachingCredentialsProvider credentialsProvider;
    public static AmazonDynamoDBClient ddbClient;
    public static DynamoDBMapper mapper;


    public void swapFragment(String fragmentName, Bundle args) {

        Fragment fragment = null;

        switch(fragmentName) {
            case ABOUT_FRAGMENT:
                fragment = AboutFragment.newInstance();
                break;
            case BOARD_FRAGMENT:
                fragment = BoardActivity.newInstance();
                break;
            case COMPLETED_GAME_FRAGMENT:
                fragment = CompletedGameFragment.newInstance();
                break;
            case GAMER_PROFILE_FRAGMENT:
                fragment = GamerProfileFragment.newInstance();
                break;
            case INSTRUCTIONS_FRAGMENT:
                fragment = InstructionsFragment.newInstance();
                break;
            case SAVED_GAME_FRAGMENT:
                fragment = SavedGameFragment.newInstance();
                break;
            case MAIN_MENU_FRAGMENT:
            default:
                fragment = MainMenuFragment.newInstance();
                break;
        }

        if (fragment != null)
            fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public static ArrayList<SavedGame> getSavedGames() {
        return savedGames;
    }

    public static byte[] getThisSavedGame(String exactTime){
        for(int i = 0; i < savedGames.size(); i ++){
            if(exactTime.compareTo(savedGames.get(i).getExactTime()) == 0){
                return savedGames.get(i).getSaveFile();
            }
        }
        return null;
    }

    public static void setSavedGames(ArrayList<SavedGame> savedGames) {
        Collections.sort(savedGames, new BaseActivity.RecentSorter());
        BaseActivity.savedGames = savedGames;
    }

    public static ArrayList<CompletedGame> getCompletedGames() {
        return completedGames;
    }

    public static void setCompletedGames(ArrayList<CompletedGame> completedGames) {
        BaseActivity.completedGames = completedGames;

    }

    public static boolean addExperience(long xp) {
        BaseActivity.xp += xp;
        return setLevel(xp);

    }


    /* Check if a level up has occurred */
    public static boolean setLevel(long totalXP) {
        // Upgrade level as much as necessary, stopping at last level
        int oldLevel = level;
        int counter = 0;
        boolean flag=false;
        while (counter<CUTOFF.length && level < CUTOFF.length &&flag==false) {
            if (xp >= CUTOFF[level]) {
                    level++;
            }
            else{
                flag=true;
            }

        }
        level--;

        if(level>oldLevel){
            return true;
        }
        else{
            return false;
        }
    }

    /**** Basic getters & setters ****/

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        BaseActivity.username = username;
    }

    public static int getLevel() {
        return level;
    }



    public static long getXp() {
        return xp;
    }

    public static void setXp(long xp) {

        BaseActivity.xp = xp;
        setLevel(xp);
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        BaseActivity.id = id;
    }

    public static void setNullUser(){
        BaseActivity.username = null;
        BaseActivity.id = null;
        BaseActivity.xp = 0;
        BaseActivity.level = 0;
        if(completedGames!=null) {
            BaseActivity.completedGames.clear();
        }
        if(savedGames!=null) {
            BaseActivity.savedGames.clear();
        }
    }

    public static class RecentSorter implements Comparator<SavedGame> {

        public int compare(SavedGame one, SavedGame another){
            int returnVal = 0;

            if(formatExactTime(one.getExactTime()) < formatExactTime(another.getExactTime())){
                returnVal =  1;
            }else if(formatExactTime(one.getExactTime()) > formatExactTime(another.getExactTime())){
                returnVal =  -1;
            }else if(formatExactTime(one.getExactTime()) == formatExactTime(another.getExactTime())){
                returnVal =  0;
            }

            return returnVal;

        }
    }

    public void querySavedGameDb() {

        String partitionKey = getId();
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
                for (int i = 0; i < list[0].size(); i++) {
                    sGames.add(list[0].get(i));
                }
                setSavedGames(sGames);


                //updateActivity();


            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }


    public static Long formatExactTime(String time) {
        Long timeNumber;
        StringBuilder str = new StringBuilder();
        for(char c : time.toCharArray()){
            if(c != ','){
                str.append(c);
            }

        }
        timeNumber = Long.valueOf(str.toString());
        return timeNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        //Check if there is a fragment container
        if (findViewById(R.id.fragment_container) != null) {

            //Check if there is already a fragment displayed
            if (savedInstanceState != null) {
                return;
            }

            MainMenuFragment menuFragment = MainMenuFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, menuFragment).commit();
        }

        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-2:40802ef0-7537-4910-8442-31e2b30c5a7d", // Identity Pool ID
                Regions.US_EAST_2 // Region
        );


        ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
        ddbClient.setRegion(Region.getRegion(Regions.US_EAST_2));
        //completedGames = new ArrayList<>();
        //savedGames = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Type type = new TypeToken<ArrayList<SavedGame>>() {}.getType();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        if(sharedPrefs.getString(SAVED_KEY,null)!=null) {
            String json = sharedPrefs.getString(SAVED_KEY, null);
            savedGames = gson.fromJson(json, type);
        }
        if(sharedPrefs.getLong(XP_KEY, 0)!=0) {
            setXp(sharedPrefs.getLong(XP_KEY, 0));
        }
        type = new TypeToken<ArrayList<CompletedGame>>() {}.getType();
        if(sharedPrefs.getString(COMPLETED_KEY,null)!=null) {
            String json = sharedPrefs.getString(COMPLETED_KEY, null);
            completedGames = gson.fromJson(json, type);
        }
        if(sharedPrefs.getInt(LEVEL_KEY, 0)!=0) {
            level = sharedPrefs.getInt(LEVEL_KEY, 0);
        }
        if(sharedPrefs.getString(USERNAME_KEY, null)!=null) {
            username = sharedPrefs.getString(USERNAME_KEY, null);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(savedGames);
        editor.putString(SAVED_KEY, json);
        json = gson.toJson(completedGames);
        editor.putString(COMPLETED_KEY, json);
        editor.putLong(XP_KEY, xp);
        editor.putString(USERNAME_KEY, username);
        editor.putInt(LEVEL_KEY, level);
        editor.apply();

    }

    /* Implemented from MainMenuFragmentListener */
    public int getBadgetag() {
        return BADGETAGS[level];
    }
    public void beginUserInitSignIn() {
        beginUserInitiatedSignIn();
    }

    /* Implemented from GamerProfileFragmentListener*/
    public String getRank() { return RANKNAMES[level]; }
    public int getCutoff() { return CUTOFF[level]; }
    public int getNextCutoff() { return CUTOFF[level + 1]; }

    /* Implemented from BoardFragmentListener */
    public double getMultiplier() { return MULTIPLIER[level]; }


    @Override
    public void onSignInFailed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof MainMenuFragment) {
            MainMenuFragment mmf = (MainMenuFragment)f;
            mmf.updateUI(false);
            return;
        }
        else if (f instanceof BoardActivity) {
            BoardActivity ba = (BoardActivity)f;
            ba.updateUI(false);
            return;
        }
        else if (f instanceof GamerProfileFragment) {
            GamerProfileFragment gpf = (GamerProfileFragment)f;
            gpf.signInFailed();
            return;
        }
    }

    @Override
    public void onSignInSucceeded() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof MainMenuFragment) {
            MainMenuFragment mmf = (MainMenuFragment)f;
            mmf.updateUI(true);
            return;
        }
        else if (f instanceof BoardActivity) {
            BoardActivity ba = (BoardActivity)f;
            ba.updateUI(true);
            return;
        }
        else if (f instanceof GamerProfileFragment) {
            GamerProfileFragment gpf = (GamerProfileFragment)f;
            gpf.signInSucceeded();
            return;
        }
    }
}
