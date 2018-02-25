package com.filters.shades;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.DialogFragment;
import android.widget.EditText;
import android.widget.LinearLayout;


/**
 * Created by Federica on 24/02/2018.
 */

public class URLDialogFragment extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CameraActivity callingActivity = (CameraActivity) getActivity();

        final EditText input = new EditText(getActivity());
        input.setTextIsSelectable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setMessage(R.string.url_dialog)
                .setPositiveButton(R.string.positive_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callingActivity.onUserSelectValue(input.getText().toString());
                        dismiss();
                    }
                })
                .setNegativeButton(R.string.negative_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        URLDialogFragment.this.getDialog().cancel();
                    }
                });
        builder.setView(input);

        return builder.create();
    }


}
