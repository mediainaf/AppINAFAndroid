/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class VideoGalleryFragment extends Fragment {

    private static final String mYoutubeFeedUrl = "http://gdata.youtube.com/feeds/api/users/inaftv/uploads?alt=json&max-results=50";
    private VideoListAdapter mVideoAdapter = null;
    private ArrayList<VideoItem> mItemList = null;
    int mThumbSize = 0;
    int mThumbSpacing = 0;
    GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        JSONRequestFragment requestYoutubeJson = (JSONRequestFragment) fm.findFragmentByTag("json_request_youtube");
        if (requestYoutubeJson == null) {
            requestYoutubeJson = new JSONRequestFragment();
            fm.beginTransaction().add(requestYoutubeJson, "json_request_youtube").commit();
        }
        requestYoutubeJson.start(VideoGalleryActivity.JSON_VIDEO_YOUTUBE, mYoutubeFeedUrl, false);
        ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.video_gallery_fragment, container, false);

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setArrayList(ArrayList<VideoItem> itemList) {

        if(mVideoAdapter != null)
            return;

        mItemList = itemList;

        mVideoAdapter = new VideoListAdapter(getActivity(), R.layout.video_gallery_fragment, mItemList);
        mGridView = (GridView) getActivity().findViewById(R.id.video_gallery);
        mGridView.setAdapter(mVideoAdapter);

        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);
        mThumbSpacing = 0;//getResources().getDimensionPixelSize(R.dimen.thumb_spacing);

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mVideoAdapter.getNumColumns() == 0) {
                    // first pass
                    final int numColumns = (int) Math.floor(mGridView.getWidth() / (mThumbSize + mThumbSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (mGridView.getWidth() / numColumns) - mThumbSpacing;
                        mVideoAdapter.setNumColumns(numColumns);
                        mVideoAdapter.setItemHeight(columnWidth);

                    }
                }
                else
                {
                    // second pass
                    mGridView.setVisibility(View.VISIBLE);
                    // TODO fix the api level
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.preloader);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }

    static class ViewHolder
    {
        ImageView thumbnail;
        TextView title;
        TextView visCounter;
        TextView date;
    }

    private class VideoListAdapter extends ArrayAdapter<VideoItem> {
        private Context mContext;
        private ArrayList<VideoItem> objects = null;
        private LayoutInflater mInflater;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;
        private int mItemHeight = 0;
        private int mNumColumns = 0;

        public VideoListAdapter(Context context, int id, ArrayList<VideoItem> objects)
        {
            super(context, id, objects);
            mContext = context;
            this.objects = objects;

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }

        @Override
        public int getCount()
        {
            return ((null != objects) ? objects.size() : 0);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public VideoItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.video_item, null);
                holder = new ViewHolder();
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.video_item_image);
                holder.title = (TextView) convertView.findViewById(R.id.video_item_title);
                holder.visCounter = (TextView) convertView.findViewById(R.id.video_item_vis_counter);
                holder.date = (TextView) convertView.findViewById(R.id.video_item_date);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.thumbnail.setLayoutParams(mImageViewLayoutParams);

            if (holder.thumbnail.getLayoutParams().height != mItemHeight) {
                holder.thumbnail.setLayoutParams(mImageViewLayoutParams);
            }

            VideoItem item = getItem(position);

            holder.thumbnail.setTag(item.thumbnailUrl);
            holder.thumbnail.setImageResource(R.drawable.empty);
            holder.title.setText(item.title);
            holder.visCounter.setText(item.visualizationCounter);
            holder.date.setText("today");

            INAF.imageLoader.get(item.thumbnailUrl, new ImageListener(holder.thumbnail));

            return convertView;
        }
    }
}