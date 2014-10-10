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
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class JobDetailFragment extends Fragment
{
    private JobItem mItem;
    private String mDivOnlyHtml;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle;
        if(savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        mDivOnlyHtml = bundle.getString("divonly");
        mItem = (JobItem) bundle.getSerializable("item");

        if(mDivOnlyHtml == null) {
            mDivOnlyHtml = bundle.getString("webpage");
            Document doc = Jsoup.parse(mDivOnlyHtml);
            Elements contentDiv = doc.select("div#content");
            mDivOnlyHtml = "<html><body>" + contentDiv.get(0).getElementsByClass("contextualbody").html() + "</body></html>";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_job_detail, container, false);
        TextView title = (TextView) view.findViewById(R.id.job_detail_title);
        WebView webdetails = (WebView) view.findViewById(R.id.job_detail_webdetails);

        title.setText(mItem.title);
        webdetails.setBackgroundColor(getResources().getColor(R.color.transparent));

        webdetails.loadDataWithBaseURL("", mDivOnlyHtml, "text/html", "UTF-8", null);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item", mItem);
        outState.putString("divonly", mDivOnlyHtml);
    }
}