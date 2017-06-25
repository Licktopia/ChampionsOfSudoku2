package com.licktopia.championsofsudoku.Sudoku;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Matt on 6/24/2017.
 */

public class Strategies {

    static class Pair {
        public final int idx1;
        public final int idx2;

        Pair(int idx1, int idx2) {
            this.idx1 = idx1;
            this.idx2 = idx2;
        }
    }

    /* Returns a single random element of type <T> from a list */
    public static <T> T getRandomFromSet(List<T> list) {
        int rand = ThreadLocalRandom.current().nextInt(0, list.size());
        return list.get(rand);
    }

    /* Return a list of square indices that have only one possible value */
    public static List<Integer> getNakedSingles(Board board) {

        ArrayList<Integer> nakedSquares = new ArrayList<>();
        for (int i = 0; i < board.totalBoardElements(); i++) {
            if (!board.isStartingValue(i) && board.getTotalNotepadValues(i) == 1)
                nakedSquares.add(i);
        }
        return nakedSquares;
    }

    /* Find instances of naked pairs, and adjust notepads accordingly */
    public static List<Pair> getNakedPairs(Board board) {

        ArrayList<Pair> nakedPairs = new ArrayList<>();

        // Loop over every pair of squares
        for (int i = 0; i < board.totalBoardElements(); i++) {
            for (int j = i + 1; j < board.totalBoardElements(); j++) {
                // Only check pairs that are connected
                if ((board.isSameRow(i, j) || board.isSameCol(i, j) || board.isSameCluster(i, j)) && board.hasSameNotepad(i, j))
                    nakedPairs.add(new Pair(i, j));
            }
        }

        return nakedPairs;
    }

    public static void removeNakedPairValues(Board board) {

    }


    /*
    public static Queue<Square> solveWithStrategies(Board board) {

        int numSteps = board.getOpenSquares();   // Number of squares to be filled
        int totalStrategiesUsed = 0;                // Track how many strategies performed to measure difficulty
        Queue<Square> steps = new LinkedList<>();   // To store steps taken

        while (steps.size() < numSteps) {
            updateAllNotepads();
            List<Square> singles = getNakedSingles(board);
            Square square = getRandomFromSet(singles);
            if (square != null)
                steps.add(square);
            else {
                // TODO Try next hardest strategy, and so on
            }
        }

        return steps;
    }
    */
}
