package com.licktopia.thechampionofsudoku.Sudoku;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategies class contains static methods to find squares on which moves can be made.
 */

public class Strategies {

    public static class Hint {
        public int[][] hintInfo;

        public Hint(int[][] hintInfo) {
            this.hintInfo = hintInfo;
        }

        public int getRemoveFrom() {
            return hintInfo[REMOVE_FROM][0];
        }

    }

    public static final int REMOVE_FROM = 0;
    public static final int SELECTED = 1;
    public static final int CONNECTED = 2;
    public static final int NOTEPAD = 3;
    
    public static final int NUM_CATEGORIES = 4;

    public static final int NAKED_SINGLE = 1;
    public static final int NAKED_DOUBLE = 2;
    public static final int HIDDEN_SINGLE = 3;
    public static final int HIDDEN_PAIR = 4;
    public static final int LOCKED_CANDIDATE = 5;
    public static final int XWING = 6;

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

    /* Executes the strategy provided by the given Hint object */
    public static void executeStrategy(Board board, Hint hint) {
        // Get indices to remove notepad values from
        ArrayList<Integer> indicesToEdit = new ArrayList<>();
        if (hint.getRemoveFrom() == CONNECTED) {
            for (int idx : hint.hintInfo[CONNECTED])
                indicesToEdit.add(idx);
            for (int idx : hint.hintInfo[SELECTED])
                indicesToEdit.remove(idx);
        }
        else
            for (int idx : hint.hintInfo[SELECTED])
                indicesToEdit.add(idx);

        for (int idx : indicesToEdit) {
            for (int np : hint.hintInfo[NOTEPAD])
                board.getSquare(idx).setNotepad(np, false);
        }
    }


    /****** HIDDEN SINGLES *******/

    /* Returns a list of Pairs (index - notepad num) of squares that have the sole occurrence of num in a group */
    public static List<Hint> getHiddenSingles(Board board) {

        ArrayList<Hint> hiddenSingles = new ArrayList<>();
        for (int[] group : board.getAllIndexGroups()) {
            for (int i = 1; i <= board.getSize(); i++) {
                // Find notepad values that only appear once in a group
                if (board.getTotalNotepadOccurrences(group, i) == 1) {
                    // Find square in group that has single appearance of value
                    for (int j = 0; j < group.length; j++) {
                        if (board.getNotepadAt(group[j], i)) {
                            int[][] hintInfo = new int[NUM_CATEGORIES][];
                            hintInfo[REMOVE_FROM] = new int[] {SELECTED};
                            hintInfo[SELECTED] = new int[] {group[j]};
                            hintInfo[CONNECTED] = group;
                            int[] notepad = new int[board.getSize() - 1];
                            for (int x = 0, y = 1; x < notepad.length; x++, y++) {
                                if (y == i) y++;
                                notepad[x] = y;
                            }
                            hintInfo[NOTEPAD] = notepad;
                            hiddenSingles.add(new Hint(hintInfo));
                        }
                    }
                }
            }
        }
        return hiddenSingles;
    }

    /****** NAKED PAIRS *******/

    /* Find all instances of naked pairs */
    public static List<Hint> getNakedPairs(Board board) {

        ArrayList<Hint> nakedPairs = new ArrayList<>();

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
                        board.getTotalNotepadOccurrences(group, values.get(1)) > 2) {

                        // Create hint object
                        int[][] hintInfo = new int[NUM_CATEGORIES][];
                        hintInfo[REMOVE_FROM] = new int[] {CONNECTED};
                        hintInfo[SELECTED] = new int[] {i, j};
                        hintInfo[CONNECTED] = group;
                        hintInfo[NOTEPAD] = new int[] {values.get(0), values.get(1)};
                        nakedPairs.add(new Hint(hintInfo));
                    }
                }
            }
        }

        return nakedPairs;
    }

    /****** HIDDEN PAIRS ********/

    // Hidden pairs are pairs of squares within a row/col/cluster that both contain the only two
    // appearances of a notepad number within that row/col/cluster
    public static List<Hint> getHiddenPairs(Board board) {

        ArrayList<Hint> hiddenPairs = new ArrayList<>();

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
                        if (board.hasTwoNumbersInSamePositions(group, twoSquareNotepadNums.get(k), j)) {
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
                            if (board.getTotalNotepadValues(idx1) > 2 || board.getTotalNotepadValues(idx2) > 2) {

                                // Create hint object
                                int[][] hintInfo = new int[NUM_CATEGORIES][];
                                hintInfo[REMOVE_FROM] = new int[] {SELECTED};
                                hintInfo[SELECTED] = new int[] {idx1, idx2};
                                hintInfo[CONNECTED] = group;
                                int[] notepad = new int[board.getSize() - 2];
                                for (int x = 0, y = 1; x < notepad.length; x++, y++) {
                                    if (y == j || y == twoSquareNotepadNums.get(k)) y++;
                                    notepad[x] = y;
                                }
                                hintInfo[NOTEPAD] = notepad;
                                hiddenPairs.add(new Hint(hintInfo));
                            }
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
    // Returns a Pair object containing the cluster number and the locked notepad number
    public static List<Hint> getLockedCandidates(Board board) {

        ArrayList<Hint> lockedCandidates = new ArrayList<>();

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
                        if (rowOccurrences > occurrences) {

                            // Create hint object
                            int[][] hintInfo = new int[NUM_CATEGORIES][];
                            hintInfo[REMOVE_FROM] = new int[] {CONNECTED};
                            int[] selected;
                            if (board.isSameCluster(first, first - 1)) selected = new int[] {first - 1, first, second};
                            else if  (board.isSameCluster(second, second + 1)) selected = new int[] {first, second, second + 1};
                            else selected = new int[] {first, first + 1, second};
                            hintInfo[SELECTED] = selected;
                            hintInfo[CONNECTED] = board.getRowIndicesByIndex(first);
                            hintInfo[NOTEPAD] = new int[] {np};
                            lockedCandidates.add(new Hint(hintInfo));
                        }
                    }
                    else if (board.isSameCol(first, second) && (third == -1 || board.isSameCol(first, third))) {
                        // If there are values in the column to be removed, add to list
                        int[] colIndices = board.getColIndicesByIndex(first);
                        int colOccurrences = board.getTotalNotepadOccurrences(colIndices, np);
                        if (colOccurrences > occurrences) {

                            // Create hint object
                            int[][] hintInfo = new int[NUM_CATEGORIES][];
                            hintInfo[REMOVE_FROM] = new int[] {CONNECTED};
                            int[] selected;
                            int size = board.getSize();
                            if (board.isSameCluster(first, first - board.getSize()) && first >= size)
                                selected = new int[] {first - board.getSize(), first, second};
                            else if (board.isSameCluster(second, second + board.getSize()) && second < (size * size) - size)
                                selected = new int[] {first, second, second + board.getSize()};
                            else selected = new int[] {first, first + board.getSize(), second};
                            hintInfo[SELECTED] = selected;
                            hintInfo[CONNECTED] = board.getColIndicesByIndex(first);
                            hintInfo[NOTEPAD] = new int[] {np};
                            lockedCandidates.add(new Hint(hintInfo));
                        }
                    }
                }
            }
        }

        return lockedCandidates;
    }

    /****** X WING *******/
    //  Two rows (or two columns) each contain only two squares with a given candidate
    //  Same candidate in both rows (or columns) and share the same two columns (or rows)
    //  Eliminate that candidate from the other squares in that column (or row) since it must appear
    //  in one of the two spots in that column
    public static List<Hint> getXWings(Board board) {

        ArrayList<Hint> xWings = new ArrayList<>();

        // Get all index groups of rows and columns
        int[][] rows = board.getEveryRowIndices();
        int[][] cols = board.getEveryColIndices();
        int[][] groups = new int[rows.length + cols.length][];
        for (int i = 0; i < groups.length; i++) {
            if (i < rows.length) groups[i] = rows[i];
            else groups[i] = cols[i % cols.length];
        }
        // Iterate through every notepad number
        for (int num = 1; num <= board.getSize(); num++) {
            // Iterate through each group
            for (int i = 0; i < groups.length; i++) {
                // If group has exactly two occurrences of current notepad num
                if (board.getTotalNotepadOccurrences(groups[i], num) == 2) {
                    // Iterate through remaining rows if row or remaining columns if column
                    int iterateTo = i < rows.length ?  rows.length : groups.length;
                    for (int j = i + 1; j < iterateTo; j++) {
                        // If second group has matching position as first group
                        if (board.hasNumberInSamePositions(groups[i], groups[j], num)) {
                            int idx1 = board.getFirstOccurrence(groups[i], num);
                            int idx2 = board.getLastOccurrence(groups[i], num);
                            // Create hint object
                            int[][] hintInfo = new int[NUM_CATEGORIES][];
                            hintInfo[REMOVE_FROM] = new int[] {CONNECTED};
                            hintInfo[SELECTED] = new int[] {groups[i][idx1], groups[i][idx2], groups[j][idx1], groups[j][idx2]};
                            int[] conn1, conn2;
                            if (i < rows.length) {
                                // If instance found in two rows, get corresponding columns
                                conn1 = board.getColIndices(idx1);
                                conn2 = board.getColIndices(idx2);
                            }
                            else { // Get corresponding rows
                                conn1 = board.getRowIndices(idx1);
                                conn2 = board.getRowIndices(idx2);
                            }
                            // Check if xWing instance would remove any notepad numbers
                            if (board.getTotalNotepadOccurrences(conn1, num) > 2 || board.getTotalNotepadOccurrences(conn2, num) > 2) {
                                // Merge groups and add to Hint
                                int[] connected = new int[conn1.length + conn2.length];
                                for (int k = 0; k < connected.length; k++) {
                                    if (k < conn1.length) connected[k] = conn1[k];
                                    else connected[k] = conn2[k % conn2.length];
                                }
                                hintInfo[CONNECTED] = connected;
                                hintInfo[NOTEPAD] = new int[]{num};
                                xWings.add(new Hint(hintInfo));
                            }
                        }
                    }
                }
            }
        }

        return xWings;
    }

    public static void executeStrategyFromList(Board board, List<Hint> hints) {
        if (!hints.isEmpty()) {
            Hint hint = getRandomElement(hints);
            executeStrategy(board, hint);
        }
    }

    /* Finds, selects, and executes naked pair strategy on one random existing hidden single */
    public static void removeRandomHiddenSingle(Board board) {
        executeStrategyFromList(board, getHiddenSingles(board));
    }
    /* Finds, selects, and executes naked pair strategy on one random existing naked pair */
    public static void removeRandomNakedPairValues(Board board) {
        executeStrategyFromList(board, getNakedPairs(board));
    }

    /* Finds, selects, and executes hidden pair strategy on one random existing hidden pair */
    public static void removeRandomHiddenPairValues(Board board) {
        executeStrategyFromList(board, getHiddenPairs(board));
    }

    public static void removeRandomLockedCandidateValues(Board board) {
        executeStrategyFromList(board, getLockedCandidates(board));
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
            //used to prevent the score from being impacted
            Board tempBoard = new Board(board);
            for(int i = 1; i <= board.getSize(); i++)
            {
                Set<Integer> testStk = tempBoard.setValueAt(row, col, i);
                if(testStk.isEmpty())
                {
                    if (!removeOnly) board.setNotepadAt(row, col, i, true);
                }
                else
                {
                    board.setNotepadAt(row, col, i, false);
                }
                //commented out because tempBoard was created
                //board.setValueAt(row, col, 0);
            }
        }
    }
}
