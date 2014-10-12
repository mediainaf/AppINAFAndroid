/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoGalleryActivity extends NavigationDrawerActivity
        implements JSONRequestFragment.Callbacks, VideoGalleryFragment.Callbacks {

    private static final String mYoutubeFeedUrl = "http://gdata.youtube.com/feeds/api/users/inaftv/uploads?alt=json&max-results=50";
    private Bundle mArgs;
    private ArrayList<VideoItem> mItemList = null;
    public static final int JSON_VIDEO_YOUTUBE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        JSONRequestFragment requestYoutubeJson = (JSONRequestFragment) fm.findFragmentByTag("json_request_youtube");

        if(savedInstanceState != null) {
            mArgs = savedInstanceState.getBundle("args");
        }
        else {
            mArgs = getIntent().getExtras();
        }

        mItemList = (ArrayList<VideoItem>) mArgs.getSerializable("item_list");

        // if there is no request ongoing and no previous request results
        if(requestYoutubeJson == null && mItemList == null)
        {
            requestYoutubeJson = new JSONRequestFragment();
            fm.beginTransaction().add(requestYoutubeJson, "json_request_youtube").commit();
            requestYoutubeJson.start(VideoGalleryActivity.JSON_VIDEO_YOUTUBE, mYoutubeFeedUrl, false);
            ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.VISIBLE);
        }
        else {
            replaceFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("args", mArgs);
        if(mItemList != null)
            outState.putSerializable("item_list", mItemList);
    }

    @Override
    public void onResponseArray(int id, JSONArray response) {
    }

    @Override
    public void onResponse(int id, JSONObject response) {
        if (id == JSON_VIDEO_YOUTUBE) {

            mItemList = new ArrayList<VideoItem>();

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

                    mItemList.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.INVISIBLE);
            replaceFragment();
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

    void replaceFragment() {
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putSerializable("item_list", mItemList);
        VideoGalleryFragment fragment = new VideoGalleryFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment, "video_gallery_fragment" + mTitle).commit();
    }
}
