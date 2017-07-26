package com.licktopia.thechampionofsudoku.Multiplayer;

/**
 * Created by John Konecny on 7/24/2017.
 */

import android.content.Context;
import android.util.Log;

import android.os.Bundle;

import com.licktopia.thechampionofsudoku.GameInterface;
import com.licktopia.thechampionofsudoku.Sudoku.Game;
import com.licktopia.thechampionofsudoku.Sudoku.SavedGame;
import com.licktopia.thechampionofsudoku.Sudoku.Solver;
import com.licktopia.thechampionofsudoku.Sudoku.Square;
import com.licktopia.thechampionofsudoku.Sudoku.Strategies;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.Set;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;


import static java.lang.System.exit;

public class MultiGame extends GameInterface implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener,
        RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener {

    final static String TAG = "MultiGame";

    private byte[] msgBuffer;
    final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
    private Game mGame;

    //stores the bots and the threads they are playing on
    private Bot[] allBots = new Bot[MAX_OPPONENTS];

    private GameMode mode;
    private GoogleApiClient client;
    boolean isBotGame = false;

    public enum GameMode {
        RACE,
        BATTLE,
        COOP
    }

    public enum ConnectType{
            BOT_GAME,
            QUICK_GAME,
            SEND_INVITE,
            ACCEPT_INVITE
    }


    /****************************************
     * start functions already implemented
     **************************************/

    /****
     * Constructor
     * @param mode what type of multiplayer game the user wants
     * @param cType how user wants to find people to play against
     * @param level difficulty of board
     * @param ctx
     */
    public MultiGame(GameMode mode, ConnectType cType, int level, Context ctx)
    {



        this.mode = mode;
        switch (cType)
        {
            case BOT_GAME:
                isBotGame = true;
                botGame(level, ctx);
                break;
            case QUICK_GAME:
                Log.d(TAG, "QUICK_GAME is not coded yet");
                exit(1);
                break;
            case SEND_INVITE:
                Log.d(TAG, "SEND_INVITE is not coded yet");
                exit(1);
                break;
            case ACCEPT_INVITE:
                Log.d(TAG, "ACCEPT_INVITE is not coded yet");
                exit(1);
                break;
            default:
                Log.d(TAG, "Invalid ConnectionType Used");
                exit(1);
                break;
        }

    }

    //send byte array with game state in buffer
    private void sendGameState(int level, Context ctx)
    {
        mGame = new Game(level, ctx);
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            ObjectOutputStream o = new ObjectOutputStream(b);

            //header = G to indicate game being sent
            o.writeByte('G');
            o.writeObject(mGame);

            msgBuffer = b.toByteArray();
            o.close();
        }
        catch(IOException e)
        {
            Log.d(TAG, "Failed to save the game!");
        }

        //debug multiplayer UI using bots
        if(isBotGame)
        {
            //done to check and see if the byte array is working
            try(ByteArrayInputStream b = new ByteArrayInputStream(msgBuffer)){

                ObjectInputStream o = new ObjectInputStream(b);
                o.readByte();
                Game curGame = (Game)o.readObject();
                for(int i = 0; i < allBots.length; i++)
                {
                    allBots[i] = new Bot(new Game(curGame), "SudokuBot" + Integer.toString(i) + "000", 5);
                }


            }
            catch(IOException e)
            {
                Log.d(TAG, "Failed to load the game from byte array!");
                exit(1);
            }
            catch(ClassNotFoundException e)
            {
                Log.d(TAG, "What class are you talking about?");
                exit(1);
            }
        }
        else
        {
            //send game state away to other clients
        }
    }

    /**
     * setup a bot game
     * @param level difficulty of the board
     * @param ctx activity context
     */
    private void botGame(int level, Context ctx)
    {
        isBotGame = true;
        sendGameState(level, ctx);
    }

    /*****
     *
     * @return total number of bots
     */
    public int getTotalOpponents()
    {
        if(isBotGame)
        {
            return allBots.length;
        }
        else
        {
            return 0;
        }
    }

    /******
     * get the opponent score
     * @param i index of the opponent
     * @return the opponent score
     */
    public long getOpponentScore(int i)
    {
        if(isBotGame)
        {
            return this.allBots[i].getGameObj().getScore();
        }
        else
        {
            return -100;
        }
    }

    public int getOpponentFilledSquares(int i)
    {
        if(isBotGame)
        {
            return  this.allBots[i].getGameObj().totalBoardElements()
                    - this.allBots[i].getGameObj().getNumberOpenSquares();
        }
        else
        {
            return -100;
        }
    }

    public boolean isOpponentGameFinished(int i)
    {
        return this.allBots[i].getGameObj().isGameFinished();
    }

    /***********************************************
     * Total board elements opponent's board has
     * @param i index of opponent
     * @return total board elements opponent's board has
     */
    public int getOpponentTotalBoardElements(int i)
    {
        if(isBotGame)
        {
            return  this.allBots[i].getGameObj().totalBoardElements();
        }
        else
        {
            return -100;
        }
    }

    /************************************************
     * Return the name of the opponent
     * @param i index of opponent
     * @return name of opponent
     */
    public String getOpponentName(int i)
    {
        if(isBotGame)
        {
            return this.allBots[i].getName();
        }
        else
        {
            return "Not Coded Yet";
        }
    }

    /****
     *
     * @param i total number of open squares for bot i
     * @return total number of filled in squares
     */
    public long getOpponentNumberFilledSquares(int i)
    {
        return this.allBots[i].getGameObj().totalBoardElements() - this.allBots[i].getGameObj().getNumberOpenSquares();
    }

    /*******
     * get total number of squares filled in by the user
     * @return total number of squares filled in by the user
     */
    public long getNumberFilledSquares()
    {
        return this.mGame.totalBoardElements() - this.mGame.getNumberOpenSquares();
    }

    /****************************************
     * end of functions already implemented
     **************************************/

    // TODO: quickGame
    private void quickGame(int level, Context ctx)
    {


        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);

        RoomConfig.Builder rtmConfigBuilder = RoomConfig.builder(this);
        rtmConfigBuilder.setMessageReceivedListener(this);
        rtmConfigBuilder.setRoomStatusUpdateListener(this);
        rtmConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
    }



    //TODO onRealTimeMessageReceived
    // Called when we receive a real-time message from the network.
    // Messages in our game are made up of 2 bytes: the first one is 'F' or 'U'
    // indicating
    // whether it's a final or interim score. The second byte is the score.
    // There is also the
    // 'S' message, which indicates that the game should start.
    @Override
    public void onRealTimeMessageReceived(RealTimeMessage rtm) {
        byte[] buf = rtm.getMessageData();
        String sender = rtm.getSenderParticipantId();
        Log.d(TAG, "Message received: " + (char) buf[0] + "/" + (int) buf[1]);
        /*
        if (buf[0] == 'F' || buf[0] == 'U') {
            // score update.
            int existingScore = mParticipantScore.containsKey(sender) ?
                    mParticipantScore.get(sender) : 0;
            int thisScore = (int) buf[1];
            if (thisScore > existingScore) {
                // this check is necessary because packets may arrive out of
                // order, so we
                // should only ever consider the highest score we received, as
                // we know in our
                // game there is no way to lose points. If there was a way to
                // lose points,
                // we'd have to add a "serial number" to the packet.
                mParticipantScore.put(sender, thisScore);
            }

            // update the scores on the screen
            updatePeerScoresDisplay();

            // if it's a final score, mark this participant as having finished
            // the game
            if ((char) buf[0] == 'F') {
                mFinishedParticipants.add(rtm.getSenderParticipantId());
            }
        }
        */
    }


    //TODO write code for onInvitationReceived
    // Called when we get an invitation to play a game. We react by showing that to the user.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        // We got an invitation to play a game! So, store it in
        // mIncomingInvitationId
        // and show the popup on the screen.
        /*
        mIncomingInvitationId = invitation.getInvitationId();
        ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
                invitation.getInviter().getDisplayName() + " " +
                        getString(R.string.is_inviting_you));
        switchToScreen(mCurScreen); // This will show the invitation popup
        */
    }

    //TODO write code for onInvitationRemoved
    @Override
    public void onInvitationRemoved(String invitationId) {

        /*
        if (mIncomingInvitationId.equals(invitationId)&&mIncomingInvitationId!=null) {
            mIncomingInvitationId = null;
            switchToScreen(mCurScreen); // This will hide the invitation popup
        }
        */

    }

    /*
     * CALLBACKS SECTION. This section shows how we implement the several games
     * API callbacks.
     */

    //TODO wite code for onConnected
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected() called. Sign in successful!");

        Log.d(TAG, "Sign-in succeeded.");

        // register listener so we are notified if we receive an invitation to play
        // while we are in the game
        /*
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        if (connectionHint != null) {
            Log.d(TAG, "onConnected: connection hint provided. Checking for invite.");
            Invitation inv = connectionHint
                    .getParcelable(Multiplayer.EXTRA_INVITATION);
            if (inv != null && inv.getInvitationId() != null) {
                // retrieve and cache the invitation ID
                Log.d(TAG,"onConnected: connection hint has a room invite!");
                acceptInviteToRoom(inv.getInvitationId());
                return;
            }
        }
        switchToMainScreen();
        */
    }

    //TODO onConnectionSuspended
    @Override
    public void onConnectionSuspended(int i) {
        /*
        Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        mGoogleApiClient.connect();
        */
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() called, result: " + connectionResult);
        /*
        if (mResolvingConnectionFailure) {
            Log.d(TAG, "onConnectionFailed() ignoring connection failure; already resolving.");
            return;
        }

        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;
            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient,
                    connectionResult, RC_SIGN_IN, getString(R.string.signin_other_error));
        }

        switchToScreen(R.id.screen_sign_in);
        */
    }


    //TODO write code for onConnectedToRoom
    // Called when we are connected to the room. We're not ready to play yet! (maybe not everybody
    // is connected yet).
    @Override
    public void onConnectedToRoom(Room room) {
        Log.d(TAG, "onConnectedToRoom.");
        /*
        //get participants and my ID:
        mParticipants = room.getParticipants();
        mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

        // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
        if(mRoomId==null)
            mRoomId = room.getRoomId();

        // print out the list of participants (for debug purposes)
        Log.d(TAG, "Room ID: " + mRoomId);
        Log.d(TAG, "My ID " + mMyId);
        Log.d(TAG, "<< CONNECTED TO ROOM>>");
        */
    }

    //TODO onLeftRoom
    // Called when we've successfully left the room (this happens a result of voluntarily leaving
    // via a call to leaveRoom(). If we get disconnected, we get onDisconnectedFromRoom()).
    @Override
    public void onLeftRoom(int statusCode, String roomId) {
        // we have left the room; return to main screen.
        Log.d(TAG, "onLeftRoom, code " + statusCode);
        /*
        switchToMainScreen();
        */
    }

    //TODO onDisconnectedFromRoom
    // Called when we get disconnected from the room. We return to the main screen.
    @Override
    public void onDisconnectedFromRoom(Room room) {
        /*
        mRoomId = null;
        showGameError();
        */
    }

    //TODO showGameError
    // Show error message about game being cancelled and return to main screen.
    void showGameError() {
        /*
        BaseGameUtils.makeSimpleDialog(this, getString(R.string.game_problem));
        switchToMainScreen();
        */
    }

    // Called when room has been created
    @Override
    public void onRoomCreated(int statusCode, Room room) {
        Log.d(TAG, "onRoomCreated(" + statusCode + ", " + room + ")");
        /*
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomCreated, status " + statusCode);
            showGameError();
            return;
        }

        // save room ID so we can leave cleanly before the game starts.
        mRoomId = room.getRoomId();

        // show the waiting room UI
        showWaitingRoom(room);
        */
    }

    // Called when room is fully connected.
    @Override
    public void onRoomConnected(int statusCode, Room room) {
        Log.d(TAG, "onRoomConnected(" + statusCode + ", " + room + ")");
        /*
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }
        updateRoom(room);
        */
    }

    //TODO onJoinedRoom
    @Override
    public void onJoinedRoom(int statusCode, Room room) {
        Log.d(TAG, "onJoinedRoom(" + statusCode + ", " + room + ")");
        /*
        if (statusCode != GamesStatusCodes.STATUS_OK) {
            Log.e(TAG, "*** Error: onRoomConnected, status " + statusCode);
            showGameError();
            return;
        }

        // show the waiting room UI
        showWaitingRoom(room);
        */
    }




    @Override
    public void onPeerDeclined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerInvitedToRoom(Room room, List<String> arg1) {
        updateRoom(room);
    }



    @Override
    public void onP2PDisconnected(String participant) {
    }

    @Override
    public void onP2PConnected(String participant) {
    }

    @Override
    public void onPeerJoined(Room room, List<String> arg1) {
        updateRoom(room);
    }

    @Override
    public void onPeerLeft(Room room, List<String> peersWhoLeft) {
        updateRoom(room);
    }

    @Override
    public void onRoomAutoMatching(Room room) {
        updateRoom(room);
    }

    @Override
    public void onRoomConnecting(Room room) {
        updateRoom(room);
    }

    @Override
    public void onPeersConnected(Room room, List<String> peers) {
        updateRoom(room);
    }

    @Override
    public void onPeersDisconnected(Room room, List<String> peers) {
        updateRoom(room);
    }


    void updateRoom(Room room) {
        /*
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
            updatePeerScoresDisplay();
        }
        */
    }


    /****************************************************
     * Game Class Object Functions
     ****************************************************/

    public int getImageNumber() {
        return mGame.getImageNumber();
    }

    public void setImageNumber(int imageNumber) {
        mGame.setImageNumber(imageNumber);
    }

    //imageNumber
    private int imageNumber;
    //time in seconds
    private long runningTime = 0;


    //sets value of square given row and col
    //returns empty stack if successful
    //returns ids of problem squares in stack if failed
    public Set<Integer> setBoardValueAt(int row, int col, int value) {
        return mGame.setBoardValueAt(row, col, value);
    }

    //sets value of square given the index
    //returns empty stack if successful
    //returns ids of problem squares in stack if failed
    public Set<Integer> setBoardValueAt(int idx, int value) {
        return mGame.setBoardValueAt(idx, value);
    }

    //gets value of square given row and col
    public int getBoardValueAt(int row, int col) {
        return mGame.getBoardValueAt(row, col);
    }

    //gets board number
    public int getBoardNumber(){return mGame.getBoardNumber();}

    //gets value of square given the index
    public int getBoardValueAt(int idx) {
        return mGame.getBoardValueAt(idx);
    }

    //gets number of 1's, 2's, etc
    public int[] getValueTotals(){
        return mGame.getValueTotals();
    }

    //return if a square at the index is a starting value
    public boolean isStartingValue(int idx)
    {
        return mGame.isStartingValue(idx);
    }


    //return if a square at row, col is a starting value
    public boolean isStartingValue(int row, int col)
    {
        return mGame.isStartingValue(row, col);
    }

    //set cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    public void setBoardNotepadAt(int idx, int num, boolean value) {
        mGame.setBoardNotepadAt(idx, num, value);
    }

    //set cell notepad value for given num
    public void setBoardNotepadAt(int row, int col, int num, boolean value) {
        mGame.setBoardNotepadAt(row, col, num, value);
    }

    //toggles cell notepad value for given num
    //for a square given its index for a single dimensional array of the board
    public void toggleBoardNotepadAt(int idx, int num) {
        mGame.toggleBoardNotepadAt(idx, num);
    }

    //toggles cell notepad value for given num
    public void toggleBoardNotepadAt(int row, int col, int num) {
        mGame.toggleBoardNotepadAt(row, col, num);
    }

    //gets the notepad value for boolean num
    // for a square given its index for a single dimensional array of the board
    public boolean getBoardNotepadAt(int idx, int num) {
        return mGame.getBoardNotepadAt(idx, num);
    }

    //get cell notepad value for given num
    public boolean getBoardNotepadAt(int row, int col, int num) {
        return mGame.getBoardNotepadAt(row, col, num);
    }

    //returns index of a altered cell after an undo
    //returns -1 if no undo occurred
    public int undo()
    {
        return mGame.undo();
    }

    //returns true if a undo can occur
    public boolean canUndo()
    {
        return mGame.canUndo();
    }

    //returns index of a altered cell after an redo
    //returns -1 if no redo occurred
    public int redo()
    {
        return mGame.redo();
    }

    //returns true if a redo can occur
    public boolean canRedo()
    {
        return mGame.canRedo();
    }

    //pauses timer
    public void pauseTimer()
    {
        mGame.pauseTimer();

    }

    public void unpauseTimer()
    {
        mGame.unpauseTimer();

        if(isBotGame)
        {
            //let the games begin
            for(int i = 0; i < allBots.length; i++)
            {
                allBots[i].startPlayingGame();
            }
        }

    }

    //returns running time in seconds
    public long getRunningTime()
    {
        return mGame.getRunningTime();
    }

    //gets the total score for the game
    public long getScore()
    {
        return mGame.getScore();
    }

    public long getLevelBonus()
    {
        return mGame.getLevelBonus();
    }

    public long getTotalOneTimeGuesses()
    {
        return mGame.getTotalOneTimeGuesses();
    }

    public long getOneTimeGuessSquareBonus()
    {
        return mGame.getOneTimeGuessSquareBonus();
    }

    //in points per seconds
    public long getTimePenalty() { return mGame.getTimePenalty(); }

    //in points per seconds
    public double getIncorrectGuessesPenalty()
    {
        return mGame.getIncorrectGuessesPenalty();
    }

    public long getRunningTimeRemovalInterval() {return mGame.getRunningTimeRemovalInterval();}

    // Ensures that points are only removed when running time hit an interval set by
    // getRunningTimeRemovalInterval
    // Example: getRunningTimeRemovalInterval = 25 secs and getTimePenalty = 10 pts/secs
    // At 25 secs total penalty is 10 pts and this function would return 1 intervals
    // At 49 secs total penalty is 10 pts and this function would return 1 intervals
    // At 50 secs total penalty is 20 pts and this function would return 2 intervals
    public long totalPenaltyTimeIntervals()
    {
        return mGame.totalPenaltyTimeIntervals();
    }

    public long totalIncorrectGuesses()
    {
        return mGame.totalIncorrectGuesses();
    }

    public double getAccuracy() {
        return mGame.getAccuracy();
    }

    public long getEarlyFinishBonus() {return  mGame.getEarlyFinishBonus();}

    public long getEarlyFinishTime() {return  mGame.getEarlyFinishTime();}


    //passes current state of game to new solver
    public void updateSolver() {
        mGame.updateSolver();
    }

    //returns the steps required to solve from a blank board
    public Queue<Square> getSolutionSteps()
    {
        return mGame.getSolutionSteps();
    }

    public void updateAllNotepads(boolean removeOnly) {
        mGame.updateAllNotepads(removeOnly);
    }

    public int getNakedSingle() {
        return mGame.getNakedSingle();
    }

    public Strategies.Hint getHint(int hintType) {
        return mGame.getHint(hintType);
    }

    public void removeNakedDoubles(Strategies.Hint hint) {
        mGame.removeNakedDoubles(hint);
    }

    //returns the total number of elements in the board

    public  int totalBoardElements() {return mGame.totalBoardElements(); }

    //returns the total number of rows in the board
    public  int totalBoardRows() {return mGame.totalBoardRows(); }

    //returns the total number of columns in the board
    public  int totalBoardCols() {return mGame.totalBoardCols(); }

    //returns the column/row size of a cluster
    public  int getClusterSize() {return mGame.getClusterSize(); }

    //true if the board is complete
    public boolean isGameFinished()
    {
        return mGame.isGameFinished();
    }

    public void resetGame()
    {
        mGame.resetGame();
    }

    public int getNumberOpenSquares(){
        return mGame.getNumberOpenSquares();
    }

    /************************************************************************
     * Solver object solution to the game
     * @return Solver object solution to the game
     */
    public Solver getSolutionSolver() {return mGame.getSolutionSolver();}

}
