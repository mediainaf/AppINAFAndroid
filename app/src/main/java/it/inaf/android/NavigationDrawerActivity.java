/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

public class NavigationDrawerActivity extends ActionBarActivity
        implements NavigationDrawerFragment.Callbacks {

    protected NavigationDrawerFragment mNavigationDrawerFragment;

    protected String mTitle;
    protected int mPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation_drawer);

        FragmentManager fm = getSupportFragmentManager();
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fm.findFragmentById(R.id.navigation_drawer);

        if(savedInstanceState != null) {
            mPosition = savedInstanceState.getInt("nav_position");
        }
        else {
            Bundle extras = getIntent().getExtras();
            if(extras != null)
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
            case 6: {
                mTitle = getString(R.string.title_section7);
                Intent telescopesIntent = new Intent(this, TelescopeListActivity.class);
                telescopesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                telescopesIntent.putExtra("nav_position", position);
                startActivity(telescopesIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 7: {
                mTitle = getString(R.string.title_section8);
                Intent satellitesIntent = new Intent(this, SatelliteListActivity.class);
                satellitesIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                satellitesIntent.putExtra("nav_position", position);
                startActivity(satellitesIntent);
                finish();
                overridePendingTransition(0, 0);
                break;
            }
            case 8: {
                mTitle = getString(R.string.title_section9);
                Intent jobsIntent = new Intent(this, JobListActivity.class);
                jobsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                jobsIntent.putExtra("nav_position", position);
                startActivity(jobsIntent);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("nav_pos", mPosition);
    }

    public void startLoading() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag("fragment_container");
        if(f != null) {
            if (f.isVisible()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.hide(f);
                ft.commit();
            }
        }

        ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    void stopLoading() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag("fragment_container");
        if(f != null) {
            if (f.isHidden()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.show(f);
                ft.commit();
            }
        }

        ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.INVISIBLE);
    }
}
