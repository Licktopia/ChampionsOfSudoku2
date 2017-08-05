package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licktopia.thechampionofsudoku.R;

public class InstructionsFragment extends Fragment {

    public static InstructionsFragment newInstance() {
        return new InstructionsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_instructions, container, false);
        return view;
    }
}


