package com.licktopia.championsofsudoku.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.licktopia.championsofsudoku.R;

/**
 * Created by jeff on 1/15/17.
 */

public class SudokuSettingsFragment extends PreferenceFragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
