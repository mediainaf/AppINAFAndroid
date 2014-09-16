/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class VideoDetailFragment extends Fragment {
    private String mTitle;
    private String mDate;
    private String mThumbnailUrl;
    private String mVideoUrl;
    private String mvisCounter;
    private String mDescription;

    public static final String ARG_ITEM_ID = "item_id";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if (savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        mTitle = bundle.getString("title");
        mDate = bundle.getString("date");
        mThumbnailUrl = bundle.getString("thumbnailUrl");
        mVideoUrl = bundle.getString("videoUrl");
        mvisCounter = bundle.getString("visCounter");
        mDescription = bundle.getString("description");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView scroll = (ScrollView) inflater.inflate(R.layout.fragment_video_detail, null);
        TextView title = (TextView) scroll.findViewById(R.id.video_detail_title);
        TextView date = (TextView) scroll.findViewById(R.id.video_detail_date);
        WebView video = (WebView) scroll.findViewById(R.id.video_detail_video);
        video.getSettings().setJavaScriptEnabled(true);
        video.setPadding(0, 0, 0, 0);
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        video.setLayoutParams(new LinearLayout.LayoutParams(width, (int)(width/1.77)));
        TextView visualCounter = (TextView) scroll.findViewById(R.id.video_detail_viscounter);
        TextView description = (TextView) scroll.findViewById(R.id.video_detail_description);

        title.setText(mTitle);
        date.setText("Pubblicato il "+mDate);
        String iframe="<html><style>.embed-container { position: relative; padding-bottom: 56.25%; left: 0%; height: 0; overflow: hidden; max-width: 100%; height: auto; } .embed-container iframe, .embed-container object, .embed-container embed { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }</style>"+mVideoUrl+"</html>";
        video.loadData(iframe, "text/html", "UTF-8");
        visualCounter.setText("Visualizzazioni: "+mvisCounter);
        description.setText(mDescription);

        return scroll;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("title", mTitle);
        outState.putString("date", mDate);
        outState.putString("thumbnailUrl", mThumbnailUrl);
        outState.putString("videoUrl", mVideoUrl);
        outState.putString("visCounter", mvisCounter);
        outState.putString("description", mDescription);
    }

    @Override
    public void onPause() {
        super.onPause();
        WebView contentView = (WebView) getActivity().findViewById(R.id.video_detail_video);
        contentView.onPause();
    }
}