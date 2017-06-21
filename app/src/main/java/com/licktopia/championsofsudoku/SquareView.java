package com.licktopia.championsofsudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Handles state changes for the display of an individual Sudoku square
 */

public class SquareView {

    private Context context;
    private RelativeLayout squareHolder;
    private RelativeLayout highlight;
    private ImageView number;
    private RelativeLayout color;
    private ImageView art;
    private LinearLayout notepad;
    private ImageView[] notepadNums;


    public RelativeLayout getSquareHolder() {
        return squareHolder;
    }

    /* Constructor */
    public SquareView(Context context, RelativeLayout squareHolder) {

        this.context = context;
        this.squareHolder = squareHolder;
        this.notepadNums = new ImageView[9];

        inflateSquareHolder();
    }


    /* Inflate view into square layout */
    private void inflateSquareHolder() {
        LayoutInflater li = LayoutInflater.from(context);

        View v = li.inflate(R.layout.square_view, null);
        highlight = (RelativeLayout) v.findViewById(R.id.highlight);
        number = (ImageView) v.findViewById(R.id.squareNumber);
        color = (RelativeLayout) v.findViewById(R.id.squareColor);
        art = (ImageView) v.findViewById(R.id.squareArt);
        notepad = (LinearLayout) v.findViewById(R.id.squareNotepad);
        notepadNums[0] = (ImageView) v.findViewById(R.id.squareNotepad1);
        notepadNums[1] = (ImageView) v.findViewById(R.id.squareNotepad2);
        notepadNums[2] = (ImageView) v.findViewById(R.id.squareNotepad3);
        notepadNums[3] = (ImageView) v.findViewById(R.id.squareNotepad4);
        notepadNums[4] = (ImageView) v.findViewById(R.id.squareNotepad5);
        notepadNums[5] = (ImageView) v.findViewById(R.id.squareNotepad6);
        notepadNums[6] = (ImageView) v.findViewById(R.id.squareNotepad7);
        notepadNums[7] = (ImageView) v.findViewById(R.id.squareNotepad8);
        notepadNums[8] = (ImageView) v.findViewById(R.id.squareNotepad9);

        squareHolder.addView(v);
    }


    /* Set number displayed in square */
    public void setBigNumber(int n) {
        if (n == 0)
            number.setImageDrawable(null);   // Remove display object
        else
            number.setImageDrawable(context.getDrawable(Tags.numberImageTags[n - 1])); // Set background image
    }

    /* Set color displayed in square */
    public void setSquareColor(int n) {
        if (n == 0)
            squareHolder.setBackground(null);   // Remove display object
        else
            color.setBackground(context.getDrawable(Tags.colors[n - 1])); // Set background image
    }


    /* Set visibility of notepad */
    public void setNotepadDisplay(boolean visible) {
        if (visible) notepad.setVisibility(View.VISIBLE);
        else notepad.setVisibility(View.INVISIBLE);
    }


    /* Set display of notepad number (1 - 9)*/
    public void toggleNotepadNumber(int number) {
        if (notepadNums[number - 1].getVisibility() == View.INVISIBLE)
            notepadNums[number - 1].setVisibility(View.VISIBLE);
        else notepadNums[number - 1].setVisibility(View.INVISIBLE);
    }


    /* Set art displayed in square*/
    public void setArt() {
        //TODO
    }


    /* Sets highlight state of current square */
    public void setHighlighted(boolean highlighted) {
        if (highlighted)
            highlight.setBackgroundColor(context.getColor(R.color.colorHighLight)); // Set to highlighted
        else
            highlight.setBackgroundColor(context.getColor(R.color.white)); // Remove highlight
    }
}
