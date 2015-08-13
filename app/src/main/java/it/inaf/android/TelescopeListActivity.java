/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TelescopeListActivity extends NavigationDrawerActivity
        implements TelescopeListFragment.Callbacks {
    private Bundle mArgs;
    private ArrayList<TelescopeItem> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mArgs = savedInstanceState.getBundle("args");
            mItemList = (ArrayList<TelescopeItem>) mArgs.getSerializable("item_list");
        }
        else {
            mArgs = getIntent().getExtras();
            mItemList = new ArrayList<TelescopeItem>();

            JSONArray json = INAF.loadJson(this, "json_telescopes");

            int length = json.length();

            for (int i = 0; i < length; i++) {
                try {
                    JSONObject obj = json.getJSONObject(i);

                    boolean show = obj.getString("showonapp").equals("1");
                    if (!show)
                        continue;

                    TelescopeItem telItem = new TelescopeItem();
                    telItem.id = obj.getString("id");
                    telItem.name = obj.getString("name");
                    telItem.label = obj.getString("label");
                    telItem.tag = obj.getString("tag");
                    telItem.imgUrl = INAF.telescopeImagePrefixUrl + obj.getString("imgbase") + ".jpg";
                    telItem.coordx = obj.getInt("coordx");
                    telItem.coordy = obj.getInt("coordy");
                    telItem.latitude = obj.getDouble("latitude");
                    telItem.longitude = obj.getDouble("longitude");
                    telItem.phase = obj.getInt("phase");
                    telItem.scope = obj.getInt("scope");
                    telItem.showonweb = obj.getString("showonweb").equals("1");
                    telItem.showonapp = true;

                    mItemList.add(telItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            mArgs.putSerializable("item_list", mItemList);
        }

        TelescopeListFragment fragment = new TelescopeListFragment();
        fragment.setArguments(mArgs);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "fragment_container")
                .commit();
    }

    @Override
    public void onItemSelected(Bundle args) {
        Intent intent = new Intent(this, TelescopeDetailActivity.class);
        intent.putExtras(args);
        intent.putExtra("top_activity", false);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.telescope_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_map: {
                Intent intent = new Intent(this, TelescopeMapActivity.class);
                intent.putExtras(mArgs);
                intent.putExtra("top_activity", false);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            case R.id.action_open_webcam: {
                Intent intent = new Intent(this, WebcamActivity.class);
                intent.putExtra("top_activity", false);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
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
