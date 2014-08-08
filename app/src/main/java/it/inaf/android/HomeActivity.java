/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends NavigationDrawerActivity implements JSONRequestFragment.Callbacks {

    private AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;

    public static final int JSON_HOME_DETAILS = 0;
    public static final int JSON_HOME_SPLASH_IMAGE = 1;

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
    public void onResponseArray(int id, JSONArray response) {
        if (id == JSON_HOME_DETAILS) {
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
            double ratio = 0.7;
            if(view.getWidth() > view.getHeight())
                ratio = 0.7;
            textview.setWidth((int) (view.getWidth() * ratio));

            textview.startAnimation(fadeIn);
            fadeIn.setDuration(1200);
            fadeIn.setFillAfter(true);
        }
    }

    @Override
    public void onResponse(int id, JSONObject response) {
        if(id == JSON_HOME_SPLASH_IMAGE) {
            try {
                if(!response.getBoolean("error"))
                {
                    JSONObject responseObj = (JSONObject) response.get("response");
                    String imageUrl = responseObj.getString("urlMainSplashScreen");
                    Log.d("aaa", imageUrl);

                    View view = findViewById(R.id.homeTextContainer);
                    view.setTag(imageUrl);
                    INAF.imageLoader.get(imageUrl, new BackgroundImageListener(view));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(VolleyError error) {
        // TODO handle volley errors
    }

    public class BackgroundImageListener implements ImageLoader.ImageListener
    {
        private View mView;
        private String mUrl;

        BackgroundImageListener(View view) {
            mView = view;
            mUrl = view.getTag().toString();
        }

        @Override
        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
            if (response.getBitmap() != null) {
                if (mView.getTag().toString().equals(mUrl))

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        mView.setBackground(new BitmapDrawable(getResources(),response.getBitmap()));
                    else
                        mView.setBackgroundDrawable(new BitmapDrawable(getResources(),response.getBitmap()));
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            // TODO handle volley errors
        }
    }
}
