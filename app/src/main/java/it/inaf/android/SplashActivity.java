/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class SplashActivity extends FragmentActivity implements JSONRequestFragment.Callbacks {

    public static final int JSON_ABOUT = 0;
    public static final int JSON_HOME_SPLASH_IMAGE = 1;
    public static final int JSON_LOCATIONS = 2;
    public static final int JSON_APPS = 3;
    public static final int JSON_TELESCOPES = 4;
    public static final int JSON_SATELLITES = 5;
    public int responseCounter = 0;

    private JSONArray mJsonAbout;
    private JSONArray mJsonLocations;
    private JSONArray mJsonApps;
    private JSONArray mJsonTelescopes;
    private JSONArray mJsonSatellites;

    private Bitmap mHomeBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView image = (ImageView) findViewById(R.id.splash_image);
        if (!INAF.landscape)
            image.setImageResource(R.drawable.galileo);
        else
            image.setImageResource(R.drawable.galileo_l);

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

        JSONRequestFragment requestApps = (JSONRequestFragment) fm.findFragmentByTag("json_request4");
        if (requestApps == null) {
            requestApps = new JSONRequestFragment();
            fm.beginTransaction().add(requestApps, "json_request4").commit();
        }
        requestApps.start(JSON_APPS, INAF.appsUrl, true);

        JSONRequestFragment requestTelescopes = (JSONRequestFragment) fm.findFragmentByTag("json_request5");
        if (requestTelescopes == null) {
            requestTelescopes = new JSONRequestFragment();
            fm.beginTransaction().add(requestTelescopes, "json_request5").commit();
        }
        requestApps.start(JSON_TELESCOPES, INAF.telescopesUrl, true);

        JSONRequestFragment requestSatellites = (JSONRequestFragment) fm.findFragmentByTag("json_request6");
        if (requestSatellites == null) {
            requestSatellites = new JSONRequestFragment();
            fm.beginTransaction().add(requestSatellites, "json_request6").commit();
        }
        requestApps.start(JSON_SATELLITES, INAF.satellitesUrl, true);
    }

    @Override
    public void onResponseArray(int id, JSONArray response) {
        if (id == JSON_ABOUT) {
            mJsonAbout = response;
        }
        else if(id == JSON_LOCATIONS) {
            mJsonLocations = response;
        }
        else if(id == JSON_APPS) {
            mJsonApps = response;
        }
        else if(id == JSON_TELESCOPES) {
            mJsonTelescopes = response;
        }
        else if(id == JSON_SATELLITES) {
            mJsonSatellites = response;
        }
        checkStart();
    }

    @Override
    public void onResponse(int id, JSONObject response) {
        if(id == JSON_HOME_SPLASH_IMAGE) {
            String imageUrl = null;
            try {
                if(!response.getBoolean("error"))
                {
                    JSONObject responseObj = (JSONObject) response.get("response");
                    imageUrl = responseObj.getString("urlMainSplashScreen");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            INAF.imageLoader.get(imageUrl, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        mHomeBackground = response.getBitmap();
                        checkStart();
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        }
    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(this, "Connessione lenta o assente. Controllare le impostazioni di connessione e ritentare.", Toast.LENGTH_LONG).show();
    }

    void checkStart()
    {
        responseCounter++;
        Log.d("SplashActivity::onResponseArray()", "counter increased to " + responseCounter);

        if(responseCounter == 6) {
/*            try {
                FileOutputStream fos = openFileOutput("home_background.bmp", Context.MODE_PRIVATE);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mHomeBackground.compress(Bitmap.CompressFormat.PNG, 100, stream);
                fos.write(stream.toByteArray());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File path = new File(directory,"home_background.png");
            try {
                FileOutputStream fos = new FileOutputStream(path);
                mHomeBackground.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences settings = getSharedPreferences("global_data", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("json_about", mJsonAbout.toString());
            editor.putString("json_apps", mJsonApps.toString());
            editor.putString("json_telescopes", mJsonTelescopes.toString());
            editor.putString("json_satellites", mJsonSatellites.toString());
            editor.putString("json_locations", mJsonLocations.toString());
            editor.putString("image_dir", directory.getAbsolutePath());
            editor.commit();

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ImageView image = (ImageView) findViewById(R.id.splash_image);

        // swap image
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            image.setImageResource(R.drawable.galileo_l);
        else
            image.setImageResource(R.drawable.galileo);
    }
}
