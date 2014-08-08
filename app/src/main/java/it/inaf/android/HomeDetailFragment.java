/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeDetailFragment extends Fragment {

    private static final String mDetailUrl = "http://app.media.inaf.it/GetAbout.php";
    private static final String mImageUrl = "http://app.media.inaf.it/GetSplashImage.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        JSONRequestFragment requestDetails = (JSONRequestFragment) fm.findFragmentByTag("json_request1");
        if (requestDetails == null) {
            requestDetails = new JSONRequestFragment();
            fm.beginTransaction().add(requestDetails, "json_request1").commit();
        }
        requestDetails.start(HomeActivity.JSON_HOME_DETAILS, mDetailUrl, true);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        String imageUrl = mImageUrl + "?width=" + width + "&height=" + height + "&deviceName=android";

        JSONRequestFragment requestImage = (JSONRequestFragment) fm.findFragmentByTag("json_request2");
        if (requestImage == null) {
            requestImage = new JSONRequestFragment();
            fm.beginTransaction().add(requestImage, "json_request2").commit();
        }
        requestImage.start(HomeActivity.JSON_HOME_SPLASH_IMAGE, imageUrl, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.home_detail_fragment, container, false);

        return layout;
    }
}