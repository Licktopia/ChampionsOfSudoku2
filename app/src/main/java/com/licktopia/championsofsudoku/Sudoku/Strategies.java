package com.licktopia.championsofsudoku.Sudoku;

import android.support.v4.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategies class contains static methods to find squares on which moves can be made.
 */

public class Strategies {

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

    /* Returns a list of indices of squares that have the sole occurrence of a notepad number in a group */
    public static List<Integer> getHiddenSingles(Board board) {

        ArrayList<Integer> hiddenSingles = new ArrayList<>();
        for (int[] group : board.getAllIndexGroups()) {
            for (int i = 1; i <= board.getSize(); i++) {
                // Find notepad values that only appear once in a group
                if (board.getTotalNotepadOccurrences(group, i) == 1) {
                    // Find square in group that has single appearance of value
                    for (int j = 0; j < group.length; j++) {
                        if (board.getNotepadAt(group[j], i)) hiddenSingles.add(group[j]);
                    }
                }
            }
        }
        return hiddenSingles;
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
        int idx1 = (int)pair.first;
        int idx2 = (int)pair.second;
        int notepad1 = 0;
        int notepad2 = 0;
        for (int i = 1; i <= board.getSize(); i++) {
            if (board.getNotepadAt(idx1, i)) {
                if (notepad1 == 0) notepad1 = i;
                else notepad2 = i;
            }
        }

        int[] indices;
        if (board.isSameRow(idx1, idx2)) indices = board.getRowIndicesByIndex(idx1);
        else if (board.isSameCol(idx1, idx2)) indices = board.getColIndicesByIndex(idx1);
        else indices = board.getClusterIndicesByIndex(idx1);

        // Remove notepad values from all squares in indices that are open and not part of the pair
        for (int i : indices) {
            // Skip over squares in pair
            if (i == idx1 || i == idx2) continue;
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
    public static List<Pair> getHiddenPairs(Board board) {

        ArrayList<Pair> hiddenPairs = new ArrayList<>();

        int numGroups = board.getSize() * 3;
        int[][] groups = board.getAllIndexGroups();
        for (int[] group: groups) {
            // Iterate over each group
            List<Integer> twoSquareNotepadNums = new ArrayList<>();
            // Find notepad numbers in group that appear in only two squares
            for (int j = 1; j <= board.getSize(); j++){
                if (board.getTotalNotepadOccurrences(group, j) == 2) {
                    // Check number against previously found numbers
                    for (int k = 0; k < twoSquareNotepadNums.size(); k++) {
                        if (board.hasSameNotepadPositions(group, twoSquareNotepadNums.get(k), j)) {
                            // Found hidden pair
                            int idx1 = -1;
                            int idx2 = -1;
                            for (int l = 0; l < board.getSize(); l++) {
                                if (board.getNotepadAt(group[l], j)) {
                                    if (idx1 == -1) idx1 = group[l];
                                    else idx2 = group[l];
                                }
                            }
                            hiddenPairs.add(new Pair(idx1, idx2));
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
            int[] indices = board.getClusterIndicesByIndex(i);

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


    //updates all squares' notepads so that only valid moves are true
    public static void updateAllNotepads(Board board, boolean removeOnly)
    {
        for(int i = 0; i < board.totalBoardRows(); i++)
        {
            for(int j = 0; j < board.totalBoardCols(); j++)
            {
                updateNotepad(board, i, j, removeOnly);
            }
        }
    }

    // updates notepad for square at row, col so that only valid moves are true
    // if removeOnly is set, update will only remove notepad values, not add any
    public static void updateNotepad(Board board, int row, int col, boolean removeOnly)
    {
        if(!board.isStartingValue(row, col) && board.getValueAt(row, col) == 0)
        {
            for(int i = 1; i <= board.getSize(); i++)
            {
                Set<Integer> testStk = board.setValueAt(row, col, i);
                if(testStk.isEmpty())
                {
                    if (!removeOnly) board.setNotepadAt(row, col, i, true);
                }
                else
                {
                    board.setNotepadAt(row, col, i, false);
                }
                board.setValueAt(row, col, 0);
            }
        }
    }
}
