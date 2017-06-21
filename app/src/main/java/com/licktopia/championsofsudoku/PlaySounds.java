package com.licktopia.championsofsudoku;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by do_de on 6/21/2017.
 */

public class PlaySounds {
    private MediaPlayer mp = null;
    private boolean mute = false;
    public void playSounds(Context context, int sound){

        // if(mp!=null&&(mp.isPlaying()||mp.isLooping())){
        // mp.stop();
        //mp.release();
        //}
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
                mp = MediaPlayer.create(context, R.raw.win);
                mp.start();

            }
            else if(sound == 4){
                mp = MediaPlayer.create(context, R.raw.level_up);
                mp.start();

            }

            else if(sound == 5){
                mp = MediaPlayer.create(context,R.raw.lick);
                mp.start();

            }

            else if(sound == 6){
                mp = MediaPlayer.create(context,R.raw.startgame);
                mp.start();
            }

            else if(sound == 7){
                mp = MediaPlayer.create(context,R.raw.connected);
                mp.start();
            }

            else if(sound == 8){
                mp = MediaPlayer.create(context,R.raw.disconnected);
                mp.start();
            }

            else if(sound == 9){
                mp = MediaPlayer.create(context,R.raw.clock);
                mp.start();
            }

            else if(sound == 10){
                mp = MediaPlayer.create(context,R.raw.whoosh);
                mp.start();
            }


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    if (mp != null) {
                        mp.release();
                    }

                }
            });

        }
    }
}
