package com.licktopia.championsofsudoku.Sudoku;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.licktopia.championsofsudoku.R;
import com.licktopia.championsofsudoku.SudokuFacts;

public class GamerProfileActivity extends AppCompatActivity {

    SudokuFacts sudokuFacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamer_profile);


        updateUI();
    }

    private void updateUI() {
        String gamerName = getIntent().getExtras().getString("GAMERNAME");
        String gamerId = getIntent().getExtras().getString("GAMERID");
        TextView mTitleName = (TextView) findViewById(R.id.title_name);
        TextView mFactText = (TextView) findViewById(R.id.fact_text);
        TextView mDidYouKnow = (TextView) findViewById(R.id.did_you_know);
        LinearLayout mFactBackground = (LinearLayout) findViewById(R.id.fact_background);

        sudokuFacts = new SudokuFacts();
        mFactText.setText(sudokuFacts.getFunFact());
        String color = sudokuFacts.getColorHex();
        mDidYouKnow.setBackgroundColor(Color.parseColor(color));
        mFactBackground.setBackgroundColor(Color.parseColor(color));
        mTitleName.setBackgroundColor(Color.parseColor(color));
        mTitleName.setText(gamerName);
    }
}
