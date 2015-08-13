/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_credits:
                CreditsDialogFragment dialog = new CreditsDialogFragment();
                Bundle args = new Bundle();
                JSONArray json = INAF.loadJson(this, "json_about");
                String text = "";
                try {
                    JSONObject obj = json.getJSONObject(0);
                    text = obj.getString("credits");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                args.putString("text", text);
                dialog.setArguments(args);
                dialog.show(getSupportFragmentManager(), "blablabla");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
