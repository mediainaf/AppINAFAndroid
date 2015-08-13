/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class NavigationDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    protected String mTitle;
    protected boolean mGoogleServicesAvailable;
    protected int mItemId;
    protected boolean mTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mGoogleServicesAvailable = savedInstanceState.getBoolean("google_services");
            mItemId = savedInstanceState.getInt("nav_position");
            mTop = savedInstanceState.getBoolean("top_activity");
        }
        else {
            mGoogleServicesAvailable = checkPlayServices();
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mItemId = extras.getInt("nav_position");
                mTop = extras.getBoolean("top_activity");
            }
            else {
                mItemId = R.id.drawer_section_1;
                mTop = true;
            }
        }

        // inflate the the navigation drawer or not based on top/detail activity
        if(mTop)
            setContentView(R.layout.activity_navigation_drawer);
        else
            setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.action_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        // set the navigation drawer only if this is a top activity
        if(mTop) {
            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }
            };
            drawerLayout.setDrawerListener(actionBarDrawerToggle);
            actionBarDrawerToggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);
            navigationView.setNavigationItemSelectedListener(this);
            navigationView.getMenu().findItem(mItemId).setChecked(true);

        }


        if(mGoogleServicesAvailable) {
            // TODO handle push registration fail
            /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(getApplicationContext(), "Registered!", Toast.LENGTH_LONG).show();
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context);
                    boolean sentToken = sharedPreferences
                            .getBoolean(INAF.SENT_TOKEN_TO_SERVER, false);
                    if (sentToken) {
                        Toast.makeText(getApplicationContext(), "All ok!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                    }
                }
            };*/

            Intent intent = new Intent(this, PushRegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(INAF.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mItemId = menuItem.getItemId();
        menuItem.setChecked(true);

        Intent intent = null;
        switch (mItemId) {
            case R.id.drawer_section_1: {
                mTitle = getString(R.string.title_section1);
                intent = new Intent(this, HomeActivity.class);
                break;
            }
            case R.id.drawer_section_2: {
                mTitle = getString(R.string.title_section2);
                intent = new Intent(this, FeedListActivity.class);
                intent.putExtra("feed_type", "news");
                intent.putExtra("feed_url", "http://www.media.inaf.it/category/news/feed");
                break;
            }
            case R.id.drawer_section_3: {
                mTitle = getString(R.string.title_section3);
                intent = new Intent(this, FeedListActivity.class);
                intent.putExtra("feed_type", "events");
                intent.putExtra("feed_url", "http://www.media.inaf.it/category/eventi/feed");
                break;
            }
            case R.id.drawer_section_4: {
                mTitle = getString(R.string.title_section4);
                intent = new Intent(this, VideoGalleryActivity.class);
                break;
            }
            case R.id.drawer_section_5: {
                mTitle = getString(R.string.title_section5);
                intent = new Intent(this, AppsActivity.class);
                break;
            }
            case R.id.drawer_section_6: {
                mTitle = getString(R.string.title_section6);
                intent = new Intent(this, LocationsActivity.class);
                break;
            }
            case R.id.drawer_section_7: {
                mTitle = getString(R.string.title_section7);
                intent = new Intent(this, TelescopeListActivity.class);
                break;
            }
            case R.id.drawer_section_8: {
                mTitle = getString(R.string.title_section8);
                intent = new Intent(this, SatelliteListActivity.class);
                break;
            }
            case R.id.drawer_section_9: {
                mTitle = getString(R.string.title_section9);
                intent = new Intent(this, JobListActivity.class);
                break;
            }
            case R.id.drawer_section_10: {
                mTitle = getString(R.string.title_section10);
                intent = new Intent(this, ShareTweetActivity.class);
                break;
            }
        }

        intent.putExtra("nav_position", mItemId);
        intent.putExtra("top_activity", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("nav_pos", mItemId);
        outState.putBoolean("google_services", mGoogleServicesAvailable);
        outState.putBoolean("top_activity", mTop);
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

    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                GooglePlayServicesUtil.getErrorDialog(status, this,
                        INAF.REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
            } else {
                Toast.makeText(this, "This device cannot handle notifications and maps features.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }
}
