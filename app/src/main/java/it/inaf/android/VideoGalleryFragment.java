/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class VideoGalleryFragment extends Fragment {

    private VideoListAdapter mVideoAdapter = null;
    private ArrayList<VideoItem> mItemList = null;
    int mThumbSize = 0;
    int mThumbSpacing = 0;
    GridView mGridView;

    private Callbacks mCallbacks = sDummyCallbacks;

    public interface Callbacks
    {
        // Called when a video item in the list is selected.
        void onItemSelected(Bundle args);
    }

    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(Bundle args) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args;
        if(savedInstanceState != null)
            args = savedInstanceState;
        else
            args = getArguments();

        mItemList = (ArrayList<VideoItem>) args.getSerializable("item_list");
        mVideoAdapter = new VideoListAdapter(getActivity(), R.layout.video_item, mItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mGridView = (GridView) inflater.inflate(R.layout.video_gallery_fragment, container, false);

        mGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem item = mItemList.get(position);

                Bundle args = new Bundle();
                args.putString("title", item.title);
                args.putString("date", item.date);
                args.putString("thumbnailUrl", item.thumbnailUrl);
                args.putString("videoUrl", item.videoUrl);
                args.putString("visCounter", item.visualizationCounter);
                args.putString("description", item.description);
                args.putInt("position", position);
                mCallbacks.onItemSelected(args);
            }
        });

        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);
        mThumbSpacing = getResources().getDimensionPixelSize(R.dimen.thumb_spacing);

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mVideoAdapter.getNumColumns() == 0) {
                    // first pass
                    int numColumns = (int) Math.floor(INAF.width / (mThumbSize + mThumbSpacing));
                    if (numColumns == 0)
                        numColumns = 1;
                     final int columnWidth = (INAF.width / numColumns) - mThumbSpacing;
                     mVideoAdapter.setNumColumns(numColumns);
                     mVideoAdapter.setItemHeight((int)(0.75*columnWidth));
                } else {
                    // second pass
                    mGridView.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.preloader);
                    pb.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });

        mGridView.setAdapter(mVideoAdapter);

        return mGridView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item_list", mItemList);
    }

    static class ViewHolder
    {
        ImageView thumbnail;
        TextView title;
        TextView visCounter;
        TextView date;
    }

    private class VideoListAdapter extends ArrayAdapter<VideoItem> {
        private ArrayList<VideoItem> objects = null;
        private LayoutInflater mInflater;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;
        private int mItemHeight = 0;
        private int mNumColumns = 0;

        public VideoListAdapter(Context context, int id, ArrayList<VideoItem> objects)
        {
            super(context, id, objects);
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
                convertView = mInflater.inflate(R.layout.video_item, parent, false);
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
            holder.date.setText(item.date);

            INAF.imageLoader.get(item.thumbnailUrl, new ImageListener(holder.thumbnail));

            return convertView;
        }
    }
}