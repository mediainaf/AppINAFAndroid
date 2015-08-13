/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationsActivity extends NavigationDrawerActivity {
    Bundle mArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!mGoogleServicesAvailable)
            return;

        if(savedInstanceState != null)
            mArgs = savedInstanceState.getBundle("args");
        else
            mArgs = getIntent().getExtras();

        LocationsFragment fragment = new LocationsFragment();

        fragment.setArguments(mArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "fragment_container")
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("args", mArgs);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INAF.REQUEST_CODE_RECOVER_PLAY_SERVICES:
                int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
                if (status != ConnectionResult.SUCCESS) {
                    Toast.makeText(this, "Google Play Services must be installed.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }

        // TODO restart the activity

        super.onActivityResult(requestCode, resultCode, data);
    }
}