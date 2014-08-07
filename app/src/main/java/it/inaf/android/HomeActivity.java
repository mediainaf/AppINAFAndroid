/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends NavigationDrawerActivity implements JSONRequestFragment.Callbacks {

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            HomeDetailFragment fragment = new HomeDetailFragment();

            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
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
        // TODO handle volley errors
    }
}
