/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;

public class TelescopeListActivity extends NavigationDrawerActivity
        implements TelescopeListFragment.Callbacks {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            TelescopeListFragment fragment = new TelescopeListFragment();

            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public void onItemSelected(Bundle args) {
        Intent detailIntent = new Intent(this, TelescopeDetailActivity.class);
        detailIntent.putExtras(args);
        startActivity(detailIntent);
        overridePendingTransition(0, 0);
    }
}
