/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.VolleyError;

public class JobDetailActivity extends NavigationDrawerActivity
        implements StringRequestFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigationDrawerFragment.setUpCaretIndicatorEnabled(false);

        getActionBar().setTitle("");

        FragmentManager fm = getSupportFragmentManager();
        StringRequestFragment jobsRequest = new StringRequestFragment();
        fm.beginTransaction().add(jobsRequest, "job_detail_request").commit();

        JobItem item = (JobItem) getIntent().getExtras().getSerializable("item");
        jobsRequest.start(Request.Method.GET, item.link);
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
    public void onResponse(String string) {
        JobDetailFragment fragment = new JobDetailFragment();

        Bundle bundle = getIntent().getExtras();
        bundle.putString("webpage", string);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onError(VolleyError error) {

    }
}