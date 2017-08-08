package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licktopia.thechampionofsudoku.R;

public class AboutFragment extends DialogFragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreateDialog(bundle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View aboutDialogFragment = getActivity().getLayoutInflater()
                .inflate(R.layout.activity_about, null);
        builder.setView(aboutDialogFragment);
        builder.setTitle(getString(R.string.about_dialog_title));

        return builder.create();
    }
}
