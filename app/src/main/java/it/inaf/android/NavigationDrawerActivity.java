/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class NavigationDrawerActivity extends ActionBarActivity
        implements NavigationDrawerFragment.Callbacks {

    protected NavigationDrawerFragment mNavigationDrawerFragment;

    protected String mTitle;
    protected int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();

        setContentView(R.layout.activity_navigation_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fm.findFragmentById(R.id.navigation_drawer);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            mPosition = extras.getInt("nav_position");
        }

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mPosition);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mPosition = position;

        switch (position) {
            case 0: {
                mTitle = getString(R.string.title_section1);
                Intent feedListIntent = new Intent(this, HomeActivity.class);
                feedListIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                feedListIntent.putExtra("nav_position", position);
                startActivity(feedListIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 1: {
                mTitle = getString(R.string.title_section2);
                Intent feedListIntent = new Intent(this, FeedListActivity.class);
                feedListIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                feedListIntent.putExtra("feed_url", "http://www.media.inaf.it/category/news/feed");
                feedListIntent.putExtra("nav_position", position);
                startActivity(feedListIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 2: {
                mTitle = getString(R.string.title_section3);
                Intent feedListIntent = new Intent(this, FeedListActivity.class);
                feedListIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                feedListIntent.putExtra("feed_url", "http://www.media.inaf.it/category/eventi/feed");
                feedListIntent.putExtra("nav_position", position);
                startActivity(feedListIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 3: {
                mTitle = getString(R.string.title_section4);
                Intent videoGalleryIntent = new Intent(this, VideoGalleryActivity.class);
                videoGalleryIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                videoGalleryIntent.putExtra("nav_position", position);
                startActivity(videoGalleryIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 4: {
                mTitle = getString(R.string.title_section5);
                Intent appsIntent = new Intent(this, AppsActivity.class);
                appsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                appsIntent.putExtra("nav_position", position);
                startActivity(appsIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 5: {
                mTitle = getString(R.string.title_section6);
                Intent placesIntent = new Intent(this, LocationsActivity.class);
                placesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                placesIntent.putExtra("nav_position", position);
                startActivity(placesIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.my, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
