/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends FragmentActivity implements JSONRequestFragment.Callbacks {

    public static final int JSON_ABOUT = 0;
    public static final int JSON_HOME_SPLASH_IMAGE = 1;
    public static final int JSON_LOCATIONS = 2;
    public int responseCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView image = (ImageView) findViewById(R.id.splash_image);
        if (INAF.aspectRatio > 1.5)
            image.setImageResource(R.drawable.launch_port_16_9);
        else
            image.setImageResource(R.drawable.launch_port_4_3);

        FragmentManager fm = getSupportFragmentManager();
        JSONRequestFragment requestDetails = (JSONRequestFragment) fm.findFragmentByTag("json_request1");
        if (requestDetails == null) {
            requestDetails = new JSONRequestFragment();
            fm.beginTransaction().add(requestDetails, "json_request1").commit();
        }
        requestDetails.start(JSON_ABOUT, INAF.aboutUrl, true);

        String imageJsonUrl = INAF.homeImageUrl + "?width=" + INAF.width + "&height=" + INAF.height + "&deviceName=android";

        JSONRequestFragment requestImage = (JSONRequestFragment) fm.findFragmentByTag("json_request2");
        if (requestImage == null) {
            requestImage = new JSONRequestFragment();
            fm.beginTransaction().add(requestImage, "json_request2").commit();
        }
        requestImage.start(JSON_HOME_SPLASH_IMAGE, imageJsonUrl, false);

        JSONRequestFragment requestLocations = (JSONRequestFragment) fm.findFragmentByTag("json_request3");
        if (requestLocations == null) {
            requestLocations = new JSONRequestFragment();
            fm.beginTransaction().add(requestLocations, "json_request3").commit();
        }
        requestDetails.start(JSON_LOCATIONS, INAF.locationsUrl, true);
    }

    @Override
    public void onResponseArray(int id, JSONArray response) {
        if (id == JSON_ABOUT) {
            INAF.jsonAbout = response;
        }
        else if(id == JSON_LOCATIONS) {
            INAF.jsonLocations = response;
        }
        checkStart();
    }

    @Override
    public void onResponse(int id, JSONObject response) {
        if(id == JSON_HOME_SPLASH_IMAGE) {
            INAF.jsonHomeImage = response;
            String imageUrl = null;
            try {
                if(!INAF.jsonHomeImage.getBoolean("error"))
                {
                    JSONObject responseObj = (JSONObject) INAF.jsonHomeImage.get("response");
                    imageUrl = responseObj.getString("urlMainSplashScreen");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            INAF.imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        INAF.homeBackground = new BitmapDrawable(getResources(), response.getBitmap());
                        checkStart();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO handle volley errors
                }
            });
        }
    }

    @Override
    public void onError(VolleyError error) {
        Log.i("unhandled errors", "volley error: "+error.toString());
        // TODO handle error!
    }

    void checkStart()
    {
        responseCounter++;
        Log.d("SplashActivity::onResponseArray()", "counter increased to " + responseCounter);

        if(responseCounter == 3) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }
}
