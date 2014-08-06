/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeDetailFragment extends Fragment {

    private static final String mDetailUrl = "http://app.media.inaf.it/GetAbout.php";

    private String mDescription = "aaa";

    JSONRequestFragment mJSONFragmentRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        mJSONFragmentRequest = (JSONRequestFragment) fm.findFragmentByTag("json_request");

        if (mJSONFragmentRequest  == null) {
            mJSONFragmentRequest = new JSONRequestFragment();
            fm.beginTransaction().add(mJSONFragmentRequest, "json_request").commit();
        }
        mJSONFragmentRequest.start(mDetailUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.home_detail_fragment, container, false);

        return layout;
    }
}