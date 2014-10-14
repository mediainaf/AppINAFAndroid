/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;

public class HomeActivity extends NavigationDrawerActivity {
    Bundle mArgs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null)
            mArgs = savedInstanceState.getBundle("args");
        else
            mArgs = getIntent().getExtras();

        HomeDetailFragment fragment = new HomeDetailFragment();
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
}
