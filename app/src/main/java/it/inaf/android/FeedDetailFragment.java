/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
        ScrollView parent = (ScrollView) inflater.inflate(R.layout.fragment_feed_detail, container, false);
        TextView titleView = (TextView)parent.findViewById(R.id.txtTitle);
        titleView.setText(mTitle);
        TextView subtitleView = (TextView)parent.findViewById(R.id.txtSubtitle);
        subtitleView.setText("di "+mAuthor+" / "+mDate);
        TextView summaryView = (TextView)parent.findViewById(R.id.txtSummary);
        summaryView.setText(mDescription);
        WebView contentView = (WebView)parent.findViewById(R.id.txtContent);
        // Remove fixed widths
        String replacedString = "<html><body>";
        replacedString += mContent.replaceAll("width=\"[a-zA-Z0-9 ]*\"", "width=\"100%\"").replaceAll("height=\"[a-zA-Z0-9 ]*\"", "");
        replacedString += "</body></html>";
        contentView.getSettings().setJavaScriptEnabled(true);
        contentView.setBackgroundColor(getResources().getColor(R.color.transparent));
        contentView.loadDataWithBaseURL("", replacedString, "text/html", "UTF-8", null);

        return parent;
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