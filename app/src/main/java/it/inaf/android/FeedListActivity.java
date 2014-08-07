/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import java.util.ArrayList;

public class FeedListActivity extends NavigationDrawerActivity
        implements FeedListRequestFragment.Callbacks, FeedListFragment.Callbacks {

//    private boolean mTwoPane;

    FeedListRequestFragment mFeedListRequest;

    boolean mLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        mFeedListRequest = (FeedListRequestFragment) fm.findFragmentByTag("feed_list_request");

        if (mFeedListRequest == null) {
            mFeedListRequest = new FeedListRequestFragment();
            fm.beginTransaction().add(mFeedListRequest, "feed_list_request").commit();
        }

        if(savedInstanceState != null) {
            mLoading = savedInstanceState.getBoolean("loading");
        }
        else {
            mLoading = true;
            Intent intent = getIntent();
            mFeedListRequest.start(Request.Method.GET, intent.getStringExtra("feed_url"));
        }

        if(mLoading) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    public void onResponse(ArrayList<RSSItem> itemList) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FeedListFragment target = (FeedListFragment) fragmentManager.findFragmentByTag("feedlist" + mTitle);
        if(target != null) {
            target.setArrayList(itemList);
        }
        else {
            Bundle args = new Bundle();
            args.putString("title", mTitle);
            args.putSerializable("item_list", itemList);
            FeedListFragment fragment = new FeedListFragment();
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, "feedlist" + mTitle).commit();
        }
        ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.INVISIBLE);
        mLoading = false;
/*        if (findViewById(R.id.item_detail_container) != null) { TODO handle two-panel
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FeedListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }*/
    }

    @Override
    public void onError(VolleyError error) {
        Log.e("aaa", "TODO Handle Error!!!!!!!");
        Log.e("aaa", error.getMessage());
    }

    @Override
    public void onFeedItemSelected(Bundle args) {
/*      if (mTwoPane) { TODO handle item selection
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FeedDetailFragment.ARG_ITEM_ID, args.getString(ITEM_ID));
            FeedDetailFragment fragment = new FeedDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }*/
        Intent detailIntent = new Intent(this, FeedDetailActivity.class);
        detailIntent.putExtras(args);
        startActivity(detailIntent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", mLoading);
    }
}
