/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FeedListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FeedDetailFragment}.
 */
public class HomeActivity extends NavigationDrawerActivity implements JSONRequestFragment.Callbacks {

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
    private AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mNavigationDrawerFragment.setUpCaretIndicatorEnabled(false);

        if (savedInstanceState == null) {
            HomeDetailFragment fragment = new HomeDetailFragment();

            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, upIntent);
            overridePendingTransition(0, 0);
//            NavUtils.navigateUpFromSameTask(this);

            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONArray response) {
        String description = null;
        try {
            JSONObject obj = response.getJSONObject(0);
            description = obj.getString("descr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view = findViewById(R.id.homeTextContainer);
        TextView textview = (TextView) view.findViewById(R.id.homeText);
        textview.setText(description);
        textview.setWidth((int) (view.getWidth() * 0.8));
        textview.setHeight((int) (view.getHeight() * 0.8));

        textview.startAnimation(fadeIn);
        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
    }

    @Override
    public void onError(VolleyError error) {

    }
}
