/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TelescopeDetailFragment extends Fragment
{
    private String mLink;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if(savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        mLink = bundle.getString("link");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        WebView view = (WebView) inflater.inflate(R.layout.fragment_telescope_detail, container, false);
        view.setBackgroundColor(getResources().getColor(R.color.transparent));
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        view.setWebViewClient(new WebViewClient());
        view.loadUrl(mLink);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("link", mLink);
    }
}