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

    private static int[] getGroup(Board board, int idx1, int idx2) {
        int group[];
        if (board.isSameRow(idx1, idx2)) group = board.getRowIndicesByIndex(idx1);
        else if (board.isSameCol(idx1, idx2)) group = board.getColIndicesByIndex(idx1);
        else group = board.getClusterIndicesByIndex(idx1);
        return group;
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

    /* Removes a random instance of a single */
    public static int removeRandomNakedSingle(Board board) {
        List<Integer> singles = getNakedSingles(board);
        if (!singles.isEmpty()) {
            int index = getRandomElement(singles);
            board.setOnlyPossibleValueAt(index);
            return index;
        }
        // No singles to remove
        return -1;
    }


    /****** HIDDEN SINGLES *******/

    /* Returns a list of Pairs (index - notepad num) of squares that have the sole occurrence of num in a group */
    public static List<Pair> getHiddenSingles(Board board) {

        ArrayList<Pair> hiddenSingles = new ArrayList<>();
        for (int[] group : board.getAllIndexGroups()) {
            for (int i = 1; i <= board.getSize(); i++) {
                // Find notepad values that only appear once in a group
                if (board.getTotalNotepadOccurrences(group, i) == 1) {
                    // Find square in group that has single appearance of value
                    for (int j = 0; j < group.length; j++) {
                        if (board.getNotepadAt(group[j], i)) hiddenSingles.add(new Pair(group[j], i));
                    }
                }
            }
        }
        return hiddenSingles;
    }

    // Updates the notepad of a random hidden single, if one exists
    public static void removeRandomHiddenSingle(Board board) {
        List<Pair> hiddenSingles = getHiddenSingles(board);
        if (!hiddenSingles.isEmpty()) {
            Pair indexNumPair = getRandomElement(hiddenSingles);
            board.setOnlyNotepadValue((int)indexNumPair.first, (int)indexNumPair.second);
        }
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
                if ((board.isSameRow(i, j) || board.isSameCol(i, j) || board.isSameCluster(i, j)) && board.hasSameNotepad(i, j)) {
                    // Check to make sure there are values to be removed in other squares
                    List<Integer> values = board.getNotepadValues(i);
                    int[] group = getGroup(board, i, j);
                    if (board.getTotalNotepadOccurrences(group, values.get(0)) > 2 ||
                            board.getTotalNotepadOccurrences(group, values.get(1)) > 2)
                        nakedPairs.add(new Pair(i, j));
                }
            }
        }

        return nakedPairs;
    }

    /* Execute naked pair strategy on board given a Pair instance */
    public static void removeNakedPairValues(Board board, Pair pair) {

        // Get the two notepad values in the naked pair
        int idx1 = (int)pair.first;
        int idx2 = (int)pair.second;
        List<Integer> values = board.getNotepadValues(idx1);
        int notepad1 = values.get(0);
        int notepad2 = values.get(1);

        int[] group = getGroup(board, idx1, idx2);

        // Remove notepad values from all squares in indices that are open and not part of the pair
        for (int i : group) {
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
    public static void removeRandomNakedPairValues(Board board) {
        List<Pair> pairs = getNakedPairs(board);
        if (!pairs.isEmpty()) {
            Pair pair = getRandomElement(pairs);
            removeNakedPairValues(board, pair);
        }
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
                            // Check there are other notepad numbers to remove
                            if (board.getTotalNotepadValues(idx1) > 2 || board.getTotalNotepadValues(idx2) > 2)
                                hiddenPairs.add(new Pair(idx1, idx2));
                        }
                    }
                    twoSquareNotepadNums.add(j);
                }
            }
        }

        return hiddenPairs;
    }

    public static void removeHiddenPairValues(Board board, Pair pair) {

        // Get the notepad values in the naked pair
        int idx1 = (int)pair.first;
        int idx2 = (int)pair.second;

        // Get the indices of the squares in the group
        int group[] = getGroup(board, idx1, idx2);

        // Set all notepad nums in the two squares that are not exclusive to false
        for (int i = 1; i <= board.getSize(); i++) {
            if (!(board.getTotalNotepadOccurrences(group, i) == 2) || !board.getNotepadAt(idx1, i) || !board.getNotepadAt(idx2, i)) {
                board.setNotepadAt(idx1, i, false);
                board.setNotepadAt(idx2, i, false);
            }
        }
    }

    /* Finds, selects, and executes hidden pair strategy on one random existing hidden pair */
    public static void removeRandomHiddenPairValues(Board board) {
        List<Pair> pairs = getHiddenPairs(board);
        if (!pairs.isEmpty()) {
            Pair pair = getRandomElement(pairs);
            removeHiddenPairValues(board, pair);
        }
    }


    /****** LOCKED CANDIDATE *******/

    // Search CLUSTERS for notepad candidates that are restricted to single row or column
    // Returns a Pair object containing the cluster number and the locked notepad number
    public static List<Pair> getLockedCandidates(Board board) {

        ArrayList<Pair> lockedCandidates = new ArrayList<>();

        // Iterate over each cluster to find possible restricted candidates
        for (int i = 0; i < board.getSize(); i++) {
            // Get cluster indices
            int[] indices = board.getClusterIndicesByIndex(i);
            // Iterate through each notepad number
            for (int np = 1; np <= board.getSize(); np++) {
                // Check if a number appears only two or three times in a cluster
                int occurrences = board.getTotalNotepadOccurrences(indices, np);
                if (occurrences == 2 || occurrences == 3) {
                    int first, second, third;
                    first = second = third = -1;
                    // Get indices of occurrences
                    for (int index : indices) {
                        if (board.getNotepadAt(index, np)) {
                            if (first == -1) first = index;
                            else if (second == - 1) second = index;
                            else third = index;
                        }
                    }
                    // See if occurrences are all in same row or col
                    if (board.isSameRow(first, second) && (third == -1 || board.isSameRow(first, third))) {
                        // If there are values in the row to be removed, add to list
                        int[] rowIndices = board.getRowIndicesByIndex(first);
                        int rowOccurrences = board.getTotalNotepadOccurrences(rowIndices, np);
                        if (rowOccurrences > occurrences) lockedCandidates.add(new Pair(i, np));
                    }
                    else if (board.isSameCol(first, second) && (third == -1 || board.isSameCol(first, third))) {
                        // If there are values in the column to be removed, add to list
                        int[] colIndices = board.getColIndicesByIndex(first);
                        int colOccurrences = board.getTotalNotepadOccurrences(colIndices, np);
                        if (colOccurrences > occurrences) lockedCandidates.add(new Pair(i, np));
                    }
                }
            }
        }

        return lockedCandidates;
    }

    public static void removeLockedCandidateValues(Board board, Pair pair) {

        int cluster = (int)pair.first;
        int np = (int)pair.second;
        int[] indices = board.getClusterIndices(cluster);

        // Get indices containing np
        int idx1 = -1;
        int idx2 = -1;
        int idx3 = -1;
        for (int i = 0; i < indices.length; i++) {
            if (board.getNotepadAt(indices[i], np)) {
                if (idx1 == -1) idx1 = indices[i];
                else if (idx2 == -1) idx2 = indices[i];
                else idx3 = indices[i];
            }
        }

        // Get indices of row or column to be edited
        int[] indicesToEdit;
        if (board.isSameRow(idx1, idx2))
            indicesToEdit = board.getRowIndicesByIndex(idx1);
        else
            indicesToEdit = board.getColIndicesByIndex(idx1);

        // Set notepad for other indices in row/col to false
        for (int i : indicesToEdit) {
            if (i != idx1 && i != idx2 && i != idx3)
                board.setNotepadAt(i, np, false);
        }
    }

    public static void removeRandomLockedCandidateValues(Board board) {
        List<Pair> pairs = getLockedCandidates(board);
        if (!pairs.isEmpty()) {
            Pair pair = getRandomElement(pairs);
            removeLockedCandidateValues(board, pair);
        }
    }


    /****** X WING *******/

    public static void xWing(Board board) {
        //  Two rows (or two columns) each contain only two squares with a given candidate
        //  Same candidate in both rows (or columns) and share the same two columns (or rows)
        //  Eliminate that candidate from the other squares in that column (or row) since it must appear
        //  in one of the two spots in that column
    }


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
