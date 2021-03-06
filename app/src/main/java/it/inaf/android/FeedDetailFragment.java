/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class FeedDetailFragment extends Fragment
{
    private String mTitle;
    private String mAuthor;
    private String mDate;
    private String mDescription;
    private String mContent;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if(savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        mTitle = bundle.getString("title");
        mAuthor = bundle.getString("author");
        mDate = bundle.getString("date");
        mDescription = bundle.getString("description");
        mContent = bundle.getString("content");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.fragment_feed_detail, container, false);
        ScrollView parent = (ScrollView) root.findViewById(R.id.feed_detail_container);
        TextView titleView = (TextView) parent.findViewById(R.id.txtTitle);
        titleView.setText(mTitle);
        TextView subtitleView = (TextView)parent.findViewById(R.id.txtSubtitle);
        subtitleView.setText("di "+mAuthor+" / "+mDate);
        TextView summaryView = (TextView)parent.findViewById(R.id.txtSummary);
        summaryView.setText(mDescription);
        VideoEnabledWebView contentView = (VideoEnabledWebView)parent.findViewById(R.id.txtContent);
        RelativeLayout videoLayout = (RelativeLayout) root.findViewById(R.id.feed_detail_fs_layout);
        View progress = root.findViewById(R.id.feed_detail_loading);
        // Remove fixed widths
        String replacedString = "<html><body>";
        replacedString += mContent.replaceAll("width=\"[a-zA-Z0-9 ]*\"", "width=\"100%\"").replaceAll("height=\"[a-zA-Z0-9 ]*\"", "");
        replacedString += "</body></html>";
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.setBackgroundColor(getResources().getColor(R.color.transparent));

        VideoEnabledWebChromeClient webChromeClient;
        webChromeClient = new VideoEnabledWebChromeClient(parent, videoLayout, progress, contentView)
        {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
            }
        };

        webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                Window w = getActivity().getWindow();
                if (fullscreen)
                {
                    getActivity().getActionBar().hide();
                    WindowManager.LayoutParams attrs = w.getAttributes();
                    attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    w.setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                    }
                }
                else
                {
                    getActivity().getActionBar().show();
                    WindowManager.LayoutParams attrs = w.getAttributes();
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                    attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                    w.setAttributes(attrs);
                    if (android.os.Build.VERSION.SDK_INT >= 14)
                    {
                        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    }
                }
            }
        });
        contentView.setWebChromeClient(webChromeClient);

        contentView.loadDataWithBaseURL("", replacedString, "text/html", "UTF-8", null);

        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("title", mTitle);
        outState.putString("author", mAuthor);
        outState.putString("date", mDate);
        outState.putString("description", mDescription);
        outState.putString("content", mContent);
    }

    @Override
    public void onPause() {
        super.onPause();
        WebView contentView = (WebView)getActivity().findViewById(R.id.txtContent);
        contentView.onPause();
    }
}