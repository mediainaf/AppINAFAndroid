/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

public class JobDetailActivity extends NavigationDrawerActivity
        implements StringRequestFragment.Callbacks {
    Bundle mArgs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState != null)
            mArgs = savedInstanceState.getBundle("args");
        else
            mArgs = getIntent().getExtras();

        if(mArgs.getString("webpage") != null) {
            addFragment();
        }
        else {
            FragmentManager fm = getSupportFragmentManager();
            StringRequestFragment jobsRequest = new StringRequestFragment();
            fm.beginTransaction().add(jobsRequest, "job_detail_request").commit();

            JobItem item = (JobItem) mArgs.getSerializable("item");
            jobsRequest.start(Request.Method.GET, item.link);
            startLoading();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, upIntent);
            overridePendingTransition(0, 0);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(String string, String url) {
        mArgs.putString("webpage", string);
        addFragment();
    }

    @Override
    public void onError(VolleyError error, String url) {
        Toast.makeText(this, "Connessione lenta o assente. Controllare le impostazioni di connessione e ritentare.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("args", mArgs);
    }

    void addFragment() {
        JobDetailFragment fragment = new JobDetailFragment();
        fragment.setArguments(mArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "fragment_container")
                .commit();
        stopLoading();
    }
}