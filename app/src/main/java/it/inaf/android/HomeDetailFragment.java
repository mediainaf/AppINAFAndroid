/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeDetailFragment extends Fragment {
    String mDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {
            mDescription = savedInstanceState.getString("descr");
        }
        else {
            try {
                JSONObject obj = INAF.jsonAbout.getJSONObject(0);
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
        double ratio = 0.7;
        textView.setWidth((int) (INAF.width * ratio));

        imgView.setImageDrawable(INAF.homeBackground);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("descr", mDescription);
    }
}
