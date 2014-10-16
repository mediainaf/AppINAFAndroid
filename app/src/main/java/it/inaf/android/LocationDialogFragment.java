/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LocationDialogFragment extends DialogFragment {
    private LocationItem mItem;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mItem = (LocationItem) getArguments().getSerializable("item");

        CharSequence[] menu = new String[3];
        menu[0] = "Apri " + mItem.website;
        menu[1] = "Chiama il " + mItem.phone;
        menu[2] = "Naviga all'indirizzo " + mItem.address;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(menu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent;
                switch(which)
                {
                    case 0:
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mItem.website));
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mItem.phone));
                        startActivity(intent);
                        break;
                    case 2:
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+mItem.address));
                        startActivity(i);
                        break;
                }
            }
        })
        .setNegativeButton("Chiudi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        return builder.create();
    }
}