package com.licktopia.thechampionofsudoku;

import android.content.Context;
import android.media.MediaPlayer;

import com.licktopia.thechampionofsudoku.Settings.SudokuSettings;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by do_de on 6/21/2017.
 */

public class PlaySounds {
    private MediaPlayer mp = null;

    public void setMute(boolean mute) {
        this.mute = mute;
    }
/*pos Sound: 1
    neg Sound: 2
    finish game: 3
    level up: 4
    lick sound: 5
    start game: 6
    sign in: 7
    disconnect : 8
    clock: 9
    whoosh : 10

 */

    private boolean mute;
    public void playSounds(Context context, int sound){

        SudokuSettings settings = new SudokuSettings(context);
        mute = settings.getSoundPreference();
        if(!mute){

            if(sound ==1) {
                int randomNum = ThreadLocalRandom.current().nextInt(2, 5);
                if(randomNum == 1) {
                    try{
                        mp = MediaPlayer.create(context,R.raw.pos1);
                        mp.start();

                    }
                    catch(Exception e){}

                }
                else if(randomNum == 2){
                    try{
                        mp = MediaPlayer.create(context,R.raw.pos2);
                        mp.start();

                    }
                    catch(Exception e){}
                }
                else if(randomNum==3){
                    try{
                        mp = MediaPlayer.create(context,R.raw.pos3);
                        mp.start();

                    }
                    catch(Exception e){}
                }
                else if(randomNum==4){
                    try{
                        mp = MediaPlayer.create(context,R.raw.pos4);
                        mp.start();

                    }
                    catch(Exception e){}
                }
            }
            else if(sound ==2){
                int randomNum = ThreadLocalRandom.current().nextInt(1, 4);
                if(randomNum == 1) {
                    try{
                        mp = MediaPlayer.create(context,R.raw.neg1);
                        mp.start();

                    }
                    catch(Exception e){}
                }
                else if(randomNum == 2){
                    try{
                        mp = MediaPlayer.create(context,R.raw.neg2);
                        mp.start();

                    }
                    catch(Exception e){}
                }
                else if(randomNum==3){
                    try{
                        mp = MediaPlayer.create(context,R.raw.neg3);
                        mp.start();

                    }
                    catch(Exception e){}
                }
            }
            else if(sound == 3){
                try {
                    mp = MediaPlayer.create(context, R.raw.win);
                    mp.start();
                }catch(Exception e){

                }

            }
            else if(sound == 4){
                try {
                    mp = MediaPlayer.create(context, R.raw.level_up);
                    mp.start();
                }catch(Exception e){

                }

            }

            else if(sound == 5){
                try {
                    mp = MediaPlayer.create(context, R.raw.lick);
                    mp.start();
                }catch(Exception e){

                }

            }

            else if(sound == 6){
                try {
                mp = MediaPlayer.create(context,R.raw.startgame);
                mp.start();
                } catch(Exception e){
                }
            }

            else if(sound == 7){
                try {
                    mp = MediaPlayer.create(context, R.raw.connected);
                    mp.start();
                }catch(Exception e){

                }
            }

            else if(sound == 8){
                try {
                    mp = MediaPlayer.create(context, R.raw.disconnected);
                    mp.start();
                }catch(Exception e){

                }
            }

            else if(sound == 9){
                try {
                    mp = MediaPlayer.create(context, R.raw.clock);
                    mp.start();
                }catch(Exception e){

                }
            }

            else if(sound == 10){
                try {
                    mp = MediaPlayer.create(context, R.raw.whoosh);
                    mp.start();
                }catch(Exception e){

                }
            }
            else if(sound == 11){
                int randomNum = ThreadLocalRandom.current().nextInt(1, 4);
                try {
                    if(randomNum == 1) {
                        mp = MediaPlayer.create(context, R.raw.aura);
                    }
                    else if(randomNum ==2){
                        mp = MediaPlayer.create(context, R.raw.sonar);
                    }
                    else{
                        mp = MediaPlayer.create(context, R.raw.fairytale);
                    }
                    mp.start();
                    mp.setLooping(true);
                }catch(Exception e){

                }
            }
            else if(sound == 12){
                try {
                    mp = MediaPlayer.create(context, R.raw.goodboard);
                    mp.start();
                }catch(Exception e){

                }
            }
            else if(sound == 13){
                try {
                    mp = MediaPlayer.create(context, R.raw.failure);
                    mp.start();
                }catch(Exception e){

                }
            }
            else if(sound == 14){
                try {
                    mp = MediaPlayer.create(context, R.raw.incorrect);
                    mp.start();
                }catch(Exception e){

                }
            }


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try{
                    if(mp.isPlaying()) {
                        mp.stop();
                        }
                    }catch(Exception e){
                    }
                    try {
                        if (mp != null) {
                            mp.release();
                        }
                    }catch(Exception e){
                    }
                }
            });

        }
    }
    public void stopPlaying(){
        try {
            if (mp.isPlaying()) {
                mp.stop();
            }
        }catch(Exception e){

        }
        try {
            if (mp != null) {
                mp.release();
            }
        }catch(Exception e){

        }
    }
}
