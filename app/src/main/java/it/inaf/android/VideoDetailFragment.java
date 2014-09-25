/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class VideoDetailFragment extends Fragment {
    private String mTitle;
    private String mDate;
    private String mThumbnailUrl;
    private String mVideoUrl;
    private String mvisCounter;
    private String mDescription;
    private VideoEnabledWebView mWebView;
    private VideoEnabledWebChromeClient mWebChromeClient;

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
        RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.fragment_video_detail, null);
        ScrollView scroll = (ScrollView) root.findViewById(R.id.video_detail_container);
        TextView title = (TextView) root.findViewById(R.id.video_detail_title);
        TextView date = (TextView) root.findViewById(R.id.video_detail_date);
        WebView video = (WebView) root.findViewById(R.id.video_detail_video);
        RelativeLayout videoLayout = (RelativeLayout) root.findViewById(R.id.video_detail_fs_layout);
        View progress = root.findViewById(R.id.video_detail_loading);
        video.getSettings().setJavaScriptEnabled(true);

        mWebChromeClient = new VideoEnabledWebChromeClient(scroll, videoLayout, progress, (VideoEnabledWebView) video)
        {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
            }
        };

        mWebChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback()
        {
            @Override
            public void toggledFullscreen(boolean fullscreen)
            {
                Window w = getActivity().getWindow();
                if (fullscreen)
                {
                    // TODO remove actionbar
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
                    // TODO add actionbar
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

        video.setWebChromeClient(mWebChromeClient);
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

        return root;
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

    public boolean onBackPressed() {
        if (!mWebChromeClient.onBackPressed()) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                return false;
            }
        }
        return true;
    }
}