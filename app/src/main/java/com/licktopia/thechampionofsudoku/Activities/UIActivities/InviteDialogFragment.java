package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.licktopia.thechampionofsudoku.R;

/**
 * Created by Matt on 8/5/2017.
 */

public class InviteDialogFragment extends DialogFragment {

    public static InviteDialogFragment newInstance() {
        return new InviteDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        super.onCreateDialog(bundle);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View inviteDialogFragment = getActivity().getLayoutInflater()
                .inflate(R.layout.activity_invite, null);
        builder.setView(inviteDialogFragment);
        builder.setTitle(getString(R.string.invite_dialog_title));

        return builder.create();
    }
}
