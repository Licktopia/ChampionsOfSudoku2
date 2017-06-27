package com.licktopia.thechampionofsudoku.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.licktopia.thechampionofsudoku.R;

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
