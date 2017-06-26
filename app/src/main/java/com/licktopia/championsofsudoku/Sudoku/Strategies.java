package com.licktopia.championsofsudoku.Sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategies class contains static methods to find squares on which moves can be made.
 */

public class Strategies {

    static class Pair {
        final int idx1;
        final int idx2;

        Pair(int idx1, int idx2) {
            this.idx1 = idx1;
            this.idx2 = idx2;
        }
    }

    /* Returns a single random element of type <T> from a list */
    public static <T> T getRandomElement(List<T> list) {
        int rand = ThreadLocalRandom.current().nextInt(0, list.size());
        return list.get(rand);
    }

    /****** NAKED SINGLES *******/

    /* Return a list of square indices that have only one possible value */
    public static List<Integer> getNakedSingles(Board board) {

        ArrayList<Integer> nakedSquares = new ArrayList<>();
        for (int i = 0; i < board.totalBoardElements(); i++) {
            if (!board.isStartingValue(i) && board.getValueAt(i) == 0 && board.getTotalNotepadValues(i) == 1)
                nakedSquares.add(i);
        }
        return nakedSquares;
    }


    /****** NAKED PAIRS *******/


    /* Find all instances of naked pairs */
    public static List<Pair> getNakedPairs(Board board) {

        ArrayList<Pair> nakedPairs = new ArrayList<>();

        // Loop over every pair of squares
        for (int i = 0; i < board.totalBoardElements(); i++) {
            if (board.getValueAt(i) != 0 || board.getTotalNotepadValues(i) != 2) continue; // Skip non-empty squares
            for (int j = i + 1; j < board.totalBoardElements(); j++) {
                if (board.getValueAt(j) != 0) continue; // Skip non-empty squares
                // Only check pairs that are connected
                if ((board.isSameRow(i, j) || board.isSameCol(i, j) || board.isSameCluster(i, j)) && board.hasSameNotepad(i, j))
                    nakedPairs.add(new Pair(i, j));
            }
        }

        return nakedPairs;
    }

    /* Execute naked pair strategy on board given a Pair instance */
    public static void removeNakedPairValues(Board board, Pair pair) {

        // Get the two notepad values in the naked pair
        int notepad1 = 0;
        int notepad2 = 0;
        for (int i = 0; i < board.getSize(); i++) {
            if (board.getNotepadAt(pair.idx1, i)) {
                if (notepad1 == 0) notepad1 = i;
                else notepad2 = i;
            }
        }

        int[] indices;
        if (board.isSameRow(pair.idx1, pair.idx2)) indices = board.getRowIndices(pair.idx1);
        else if (board.isSameCol(pair.idx1, pair.idx2)) indices = board.getColIndices(pair.idx1);
        else indices = board.getClusterIndices(pair.idx1);

        // Remove notepad values from all squares in indices that are open and not part of the pair
        for (int i : indices) {
            // Skip over squares in pair
            if (i == pair.idx1 || i == pair.idx2) continue;
            // Remove values from open squares
            if (!board.isStartingValue(i) && board.getValueAt(i) == 0) {
                board.setNotepadAt(i, notepad1, false);
                board.setNotepadAt(i, notepad2, false);
            }
        }
    }

    /* Finds, selects, and executes naked pair strategy on one random existing naked pair */
    public static boolean removeRandomNakedPairValues(Board board) {
        List<Pair> pairs = getNakedPairs(board);
        if (!pairs.isEmpty()) {
            Pair pair = getRandomElement(pairs);
            removeNakedPairValues(board, pair);
            return true;
        }
        return false;
    }



    /****** HIDDEN PAIRS ********/

    // Hidden pairs are pairs of squares within a row/col/cluster that both contain the only two
    // appearances of a notepad number within that row/col/cluster
    public static List<Pair> findHiddenPairs(Board board) {

        ArrayList<Pair> hiddenPairs = new ArrayList<>();

        for (int i = 0; i < board.getSize(); i++) {
            /* Iterate over each row */
            int[] row = board.getRowIndices(i * board.getSize());
            List<Integer> twoSquareNotepadNums = new ArrayList<>();
            /* Find notepad numbers in row that appear in only two squares */
            for (int j = 0; j < board.getSize(); j++){
                if (board.getTotalNotepadOccurrences(row, j) == 2) {
                    /* Check number against previously found numbers */
                    for (int k = 0; k < twoSquareNotepadNums.size(); k++) {
                        if (board.hasSameNotepadPositions(row, twoSquareNotepadNums.get(k), j)) {

                        }
                    }
                    twoSquareNotepadNums.add(j);
                }
            }
        }

        return hiddenPairs;
    }

    /****** LOCKED CANDIDATE *******/

    // Search CLUSTERS for notepad candidates that are restricted to single row or column
    public static void lockedCandidate(Board board) {

        // Iterate over each cluster to find possible restricted candidates
        for (int i = 0; i < board.getSize(); i++) {
            int[] indices = board.getClusterIndices(i);

        }
    }



    /****** X WING *******/

    public static void xWing(Board board) {
        //  Two rows (or two columns) each contain only two squares with a given candidate
        //  Same candidate in both rows (or columns) and share the same two columns (or rows)
        //  Eliminate that candidate from the other squares in that column (or row) since it must appear
        //  in one of the two spots in that column
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
