/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CreditsDialogFragment extends DialogFragment {
    private String mText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mText = getArguments().getString("text");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mText)
                .setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }
}
