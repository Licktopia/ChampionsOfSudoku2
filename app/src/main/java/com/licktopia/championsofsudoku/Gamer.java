package com.licktopia.championsofsudoku;

/**
 * Created by Matt on 6/22/2017.
 */

public class Gamer {

    protected static int[] LEVEL_MARKERS = {100, 200, 400, 800, 1600, 3200, 6400}; //TODO Decide on level values

    private static  String username;
    private int id;
    private int level;
    private int xp;

    /* Constructor */
    public Gamer(int id, String username) {
        this(id, username, 1, 0);
    }

    public Gamer(int id, String username, int level, int xp) {
        this.id = id;
        this.username = username;
        this.level = level;
        this.xp = xp;
    }

    /* Add experience to current total */
    public void addExperience(int xp) {
        this.xp += xp;
        checkLevel();
    }

    /* Check if a level up has occurred */
    private void checkLevel() {
        // Upgrade level as much as necessary, stopping at last level
        while (xp < LEVEL_MARKERS[level] && level < LEVEL_MARKERS.length) {
            if (xp >= LEVEL_MARKERS[level]) {
                level++;
            }
        }
    }

    /**** Basic getters & setters ****/

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

}
