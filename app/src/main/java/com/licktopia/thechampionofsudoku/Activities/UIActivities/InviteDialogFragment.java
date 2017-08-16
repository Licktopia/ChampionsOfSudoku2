package com.licktopia.thechampionofsudoku.Activities.UIActivities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.licktopia.thechampionofsudoku.R;

import static com.licktopia.thechampionofsudoku.Activities.BaseActivity.phoneSize;

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
        View inviteDialogFragment;
        if(phoneSize==1) {
            inviteDialogFragment = getActivity().getLayoutInflater()
                    .inflate(R.layout.activity_invite_small, null);
        }
        else if(phoneSize == 3){
            inviteDialogFragment = getActivity().getLayoutInflater()
                    .inflate(R.layout.activity_invite_large, null);
        }
        else{
            inviteDialogFragment = getActivity().getLayoutInflater()
                    .inflate(R.layout.activity_invite, null);
        }

        builder.setView(inviteDialogFragment);
        builder.setTitle(getString(R.string.invite_dialog_title));

        return builder.create();
    }
}
