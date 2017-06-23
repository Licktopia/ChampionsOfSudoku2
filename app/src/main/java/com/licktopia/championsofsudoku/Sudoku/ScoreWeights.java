package com.licktopia.championsofsudoku.Sudoku;

/**
 * Created by John Konecny on 6/23/2017.
 */

class ScoreWeights {
    private long basePoints = -1,
    oneTimeGuessBonus = -1;
    private double runningTimePenalty = -1,
    incorrectGuessPenalty = -1;

    protected ScoreWeights(long basePoints,
                           long oneTimeGuessBonus,
                           double runningTimePenalty,
                           double incorrectGuessPenalty)
    {
        this.basePoints = basePoints;
        this.oneTimeGuessBonus = oneTimeGuessBonus;
        this.runningTimePenalty = runningTimePenalty;
        this.incorrectGuessPenalty = incorrectGuessPenalty;
    }

    protected long getBasePoints() {
        return basePoints;
    }

    protected long getOneTimeGuessBonus() {
        return oneTimeGuessBonus;
    }

    protected double getRunningTimePenalty() {
        return runningTimePenalty;
    }

    protected double getIncorrectGuessPenalty() {
        return incorrectGuessPenalty;
    }
}
