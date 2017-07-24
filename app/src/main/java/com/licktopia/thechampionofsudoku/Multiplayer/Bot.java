package com.licktopia.thechampionofsudoku.Multiplayer;

import android.os.Handler;

import com.licktopia.thechampionofsudoku.Sudoku.Board;
import com.licktopia.thechampionofsudoku.Sudoku.Solver;
import com.licktopia.thechampionofsudoku.Sudoku.Square;

import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Matt on 7/23/2017.
 */

public class Bot {

    // Average time delay (in ms) it takes for bot to make a move
    private static final int[] AVG_DELAY = {25000, 20000, 15000, 12500, 10000, 9000, 8000, 7000, 6000, 5000};
    // Range that speed of finding next square can vary
    private static final int[] CONSISTENCY_RANGE = {100, 90, 80, 70, 60, 50, 40, 30, 15, 0};
    // Percentage chance that bot "can't find" answer, waits another cycle
    private static final int[] ACCURACY = {30, 25, 20, 15, 13, 10, 8, 5, 2, 0};

    private String name;
    private int skill;
    private int consistency;
    private int accuracy;
    private Board board;
    private Solver solver;
    private Queue<Square> steps;
    private Handler moveHandler;
    private Runnable makeMove = new Runnable() {
        public void run() {
            makeNextMove();
        }
    };

    // Constructor
    public Bot(Board board, String name, int skill) {
        this.board = board;
        this.name = name;
        setSkill(skill);
        setConsistency(5);   // Default value
        setAccuracy(7);      // Default value
        solver = new Solver(board);
        solver.solveWithStrategies();
        steps = solver.getExactSolution();
    }

    public Bot(Board board) {
        this(board, "SudokuBot3000", 5);
    }


    // Set bot skill level (speed)
    public void setSkill(int skill) {
        this.skill = skill;
    }

    // Set bot consistency (range that answer speed varies)
    public void setConsistency(int consistency) {
        this.consistency = consistency;
    }

    // Set bot accuracy (percentage change no answer entered; "incorrect")
    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    // Fills in board over time based on bot skills and attributes
    private void startPlayingGame() {
        moveHandler.postDelayed(makeMove, getRandomizedWaitTime());
    }

    // Execute next move in the solving steps
    private void makeNextMove() {
        // Make moves while steps remain
        if (!steps.isEmpty()) {

            // Executes move based on accuracy probability
            int randomNum = ThreadLocalRandom.current().nextInt(0, 100);
            if (randomNum > ACCURACY[accuracy]) {
                Square square = steps.poll();
                board.setValueAt(square.getIdx(), square.getValue());
            }

            moveHandler.postDelayed(makeMove, getRandomizedWaitTime());
        }
    }

    // Gets a randomly adjusted move time (+/- 25%) based on skill level
    private int getRandomizedWaitTime() {
        int randomNum = ThreadLocalRandom.current().nextInt(0, CONSISTENCY_RANGE[consistency]);
        int percentageAdjusted = 75 + randomNum;
        int randomizedTime = AVG_DELAY[skill] / 100 * percentageAdjusted;
        return randomizedTime;
    }
}
