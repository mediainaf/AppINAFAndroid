/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SatelliteListActivity extends NavigationDrawerActivity
        implements SatelliteListFragment.Callbacks {
    private Bundle mArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState != null)
            mArgs = savedInstanceState.getBundle("args");
        else
            mArgs = getIntent().getExtras();

        SatelliteListFragment fragment = new SatelliteListFragment();
        fragment.setArguments(mArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "fragment_container")
                .commit();
    }

    @Override
    public void onItemSelected(Bundle args) {
        Intent detailIntent = new Intent(this, SatelliteDetailActivity.class);
        detailIntent.putExtras(args);
        startActivity(detailIntent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.satellite_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_map:
                Intent mapIntent = new Intent(this, SatelliteMapActivity.class);
                mapIntent.putExtras(mArgs);
                startActivity(mapIntent);
                overridePendingTransition(0, 0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("args", mArgs);
    }
}
