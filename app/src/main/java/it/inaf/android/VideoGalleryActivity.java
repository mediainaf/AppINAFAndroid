/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoGalleryActivity extends NavigationDrawerActivity
        implements JSONRequestFragment.Callbacks, VideoGalleryFragment.Callbacks {

    private static final String mYoutubeFeedUrl = "http://app.media.inaf.it/GetYoutubeVideoList.php";
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
            startLoading();
        }
        else {
            addFragment();
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
                JSONArray items = response.getJSONArray("items");

                for(int i=0; i<items.length(); i++)
                {
                    VideoItem item = new VideoItem();

                    JSONObject joItem = items.getJSONObject(i);
                    JSONObject joSnippet = joItem.getJSONObject("snippet");
                    item.title = joSnippet.getString("title");
                    String youtubeId = joItem.getString("id");
                    item.videoUrl = "<body style='margin:0;padding:0;'><div class='embed-container'><iframe src=\"http://www.youtube.com/embed/" + youtubeId + "?modestbranding=1&showinfo=0\" frameborder=\"0\" allowfullscreen></iframe></div></body>";
                    item.date = DateFormatter.formatType2(joSnippet.getString("publishedAt"));
                    item.thumbnailUrl = joSnippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
                    item.visualizationCounter = joItem.getJSONObject("statistics").getString("viewCount");
                    item.description = joSnippet.getString("description");

                    mItemList.add(item);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            addFragment();
        }
    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(this, "Connessione lenta o assente. Controllare le impostazioni di connessione e ritentare.", Toast.LENGTH_LONG).show();
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
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }*/
        Intent intent = new Intent(this, VideoDetailActivity.class);
        intent.putExtras(args);
        intent.putExtra("top_activity", false);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    void addFragment() {
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putSerializable("item_list", mItemList);
        VideoGalleryFragment fragment = new VideoGalleryFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.container, fragment, "fragment_container").commit();
        stopLoading();
    }
}
