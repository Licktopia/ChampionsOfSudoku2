package com.licktopia.thechampionofsudoku.Sudoku;

import android.content.Context;
import android.util.Log;

/**
 * Created by Matt on 7/5/2017.
 */

public class DifficultyClassifier {

    public static final int DIFFICULTY_LEVELS = 6;
    public static final int PUZZLES_PER_DIFFICULTY = 10000;

    public static void determineBoardDifficulties(Context context) {

        int[][] steps = new int[DIFFICULTY_LEVELS][PUZZLES_PER_DIFFICULTY];
        int[] maxLevel = new int[PUZZLES_PER_DIFFICULTY];
        int[] minLevel = new int[PUZZLES_PER_DIFFICULTY];
        double[] avgLevel = new double[PUZZLES_PER_DIFFICULTY];

        for (int level = 0; level < DIFFICULTY_LEVELS; level++) {
            int min = Integer.MAX_VALUE;
            int max = 0;
            double sum = 0;
            int solved = 0;
            for (int index = 0; index < PUZZLES_PER_DIFFICULTY; index++) {
                Board board = new Board(level, context, index);
                Solver solutionSolver = new Solver(board);
                int solvedSteps = solutionSolver.solveWithStrategies();
                if (solvedSteps < min) min = solvedSteps;
                if (solvedSteps > max) max = solvedSteps;
                if (solvedSteps != -1) {
                    solved++;
                    sum += solvedSteps;
                }
                steps[level][index] = solvedSteps;
                if (index % 50 == 0) Log.i("BoardResults", "Finished board #" + index + " at level " + level);
            }
            minLevel[level] = min;
            maxLevel[level] = max;
            avgLevel[level] = (double)sum / solved;
            Log.i("LevelResults", "Level Results: ");
            Log.i("LevelResults", "Min: " + minLevel[level]);
            Log.i("LevelResults", "Max: " + maxLevel[level]);
            Log.i("LevelResults", "Avg: " + avgLevel[level]);
            StringBuilder results = new StringBuilder();
            for (int i = 0; i < PUZZLES_PER_DIFFICULTY; i++) {
                if (i % 250 == 0) {
                    Log.i("IndResults", results.toString());
                    results = new StringBuilder();
                }
                results.append(steps[level][i]);
                results.append(',');
            }
        }
    }

    public static void getSolverEfficiency(Context context) {
        int[][] stepsToGo = new int[2][PUZZLES_PER_DIFFICULTY];
        int numCompleted = 0;

        for (int level = 1; level < 2; level++) {
            for (int index = 0; index < PUZZLES_PER_DIFFICULTY; index++) {
                Board board = new Board(level, context, index);
                Solver solutionSolver = new Solver(board);
                int solvedSteps = solutionSolver.solveWithStrategies();
                int squaresToGo = solutionSolver.getNumberOfSquaresUnfilled();
                if (squaresToGo == 0) numCompleted++;
                stepsToGo[level - 1][index] = squaresToGo;
                if (index % 50 == 0) Log.i("BoardResults", "Finished board #" + index + " at level " + level);
            }
            Log.i("LevelResults", "Level " + level + " Results: ");
            StringBuilder results = new StringBuilder();
            for (int i = 0; i < PUZZLES_PER_DIFFICULTY; i++) {
                if (i % 200 == 0) {
                    Log.i("IndResults", results.toString());
                    results = new StringBuilder();
                }
                results.append(stepsToGo[level - 1][i]);
                results.append(',');
            }
        }
        Log.i("LevelResults", "Number completed over 2 levels is: " + numCompleted);
    }
}
