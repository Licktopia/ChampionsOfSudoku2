package com.licktopia.thechampionofsudoku;

/**
 * Created by Matt on 6/22/2017.
 */

public class Gamer {



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
    public static String id;
    public static int level=0;
    public static long xp;

    /* Constructor */
    /*
    public Gamer(int id, String username) {
        this(id, username, 1, 0);
    }

    public Gamer(int id, String username, int level, int xp) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.xp = xp;
    }
*/

    /* Add experience to current total */

    public static boolean addExperience(long xp) {
        Gamer.xp += xp;
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
        Gamer.username = username;
    }

    public static int getLevel() {
        return level;
    }



    public static long getXp() {
        return xp;
    }

    public static void setXp(long xp) {

        Gamer.xp = xp;
        setLevel(xp);
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Gamer.id = id;
    }

    public static void setNullUser(){
        Gamer.username = null;
        Gamer.id = null;
        Gamer.xp = 0;
        Gamer.level = 0;
    }

}
