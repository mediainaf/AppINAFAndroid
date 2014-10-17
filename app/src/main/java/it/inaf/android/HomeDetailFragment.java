/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class HomeDetailFragment extends Fragment {
    String mDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mDescription = savedInstanceState.getString("descr");
        }
        else {
            JSONArray json = INAF.loadJson(getActivity(), "json_about");
            try {
                JSONObject obj = json.getJSONObject(0);
                mDescription = obj.getString("descr");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.home_detail_fragment, container, false);

        TextView textView = (TextView) layout.findViewById(R.id.home_text);
        ImageView imgView = (ImageView) layout.findViewById(R.id.home_background);

        textView.setText(mDescription);
        int width;
        if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE)
            width = (int) (INAF.height * 0.75);
        else
            width = (int) (INAF.width * 0.75);
        textView.setWidth(width);

        SharedPreferences settings = getActivity().getSharedPreferences("global_data", 0);
        String dir = settings.getString("image_dir", null);

        Bitmap bmp = null;
        try {
            File f = new File(dir, "home_background.png");
            bmp = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        imgView.setImageBitmap(bmp);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("descr", mDescription);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        TextView textView = (TextView) getActivity().findViewById(R.id.home_text);
        int width;
        if(getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE)
            width = (int) (INAF.height * 0.7);
        else
            width = (int) (INAF.width * 0.7);
        textView.setWidth(width);
    }
}
