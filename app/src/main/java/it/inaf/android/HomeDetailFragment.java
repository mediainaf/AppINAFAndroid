/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class HomeDetailFragment extends Fragment {
    String mDescription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            JSONObject obj = INAF.jsonAbout.getJSONObject(0);
            mDescription = obj.getString("descr");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.home_detail_fragment, container, false);

        TextView textView = (TextView) layout.findViewById(R.id.homeText);
        textView.setText(mDescription);

        double ratio = 0.7;
        textView.setWidth((int) (INAF.width * ratio));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            layout.setBackground(INAF.homeBackground);
        else
            layout.setBackgroundDrawable(INAF.homeBackground);

        return layout;
    }
}
