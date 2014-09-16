/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoGalleryActivity extends NavigationDrawerActivity
        implements JSONRequestFragment.Callbacks, VideoGalleryFragment.Callbacks {

//    private boolean mTwoPane;

    boolean mLoading = true;

    public static final int JSON_VIDEO_YOUTUBE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            VideoGalleryFragment fragment = new VideoGalleryFragment();

            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        if(savedInstanceState != null) {
            mLoading = savedInstanceState.getBoolean("loading");
        }

        if(mLoading) {
            ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.VISIBLE);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        VideoGalleryFragment fragment = (VideoGalleryFragment) fragmentManager.findFragmentByTag("video_gallery_fragment");
        if(fragment == null) {
            fragment = new VideoGalleryFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, "video_gallery_fragment").commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", mLoading);
    }

    @Override
    public void onResponseArray(int id, JSONArray response) {
    }

    @Override
    public void onResponse(int id, JSONObject response) {
        if (id == JSON_VIDEO_YOUTUBE) {

            ArrayList<VideoItem> list = new ArrayList<VideoItem>();

            try {
                JSONArray entries = response.getJSONObject("feed").getJSONArray("entry");

                for(int i=0; i<entries.length(); i++)
                {
                    VideoItem item = new VideoItem();

                    JSONObject entry = entries.getJSONObject(i);
                    item.title = entry.getJSONObject("title").getString("$t");
                    String url = entry.getJSONObject("id").getString("$t");
                    String youtubeId = url.substring(url.replaceAll("\\\\", "/").lastIndexOf("/"));
                    item.videoUrl = "<body style='margin:0;padding:0;'><div class='embed-container'><iframe src=\"http://www.youtube.com/embed/" + youtubeId + "?modestbranding=1&showinfo=0\" frameborder=\"0\" allowfullscreen></iframe></div></body>";
                    item.date = DateFormatter.format(entry.getJSONObject("published").getString("$t"));
                    item.thumbnailUrl = entry.getJSONObject("media$group").getJSONArray("media$thumbnail").getJSONObject(0).getString("url");
                    item.visualizationCounter = entry.getJSONObject("yt$statistics").getString("viewCount");
                    item.description = entry.getJSONObject("content").getString("$t");

                    list.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            VideoGalleryFragment frag = (VideoGalleryFragment)
                    getSupportFragmentManager().findFragmentByTag("video_gallery_fragment");

            frag.setArrayList(list);
            mLoading = false;
        }
    }

    @Override
    public void onError(VolleyError error) {
        Log.e("Error", "Error on loading video feed");
    }

    @Override
    public void onItemSelected(Bundle args) {
/*      if (mTwoPane) { TODO handle item selection
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FeedDetailFragment.ARG_ITEM_ID, args.getString(ITEM_ID));
            FeedDetailFragment fragment = new FeedDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }*/
        Intent detailIntent = new Intent(this, VideoDetailActivity.class);
        detailIntent.putExtras(args);
        startActivity(detailIntent);
        overridePendingTransition(0, 0);
    }
}
