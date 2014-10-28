/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WebcamFragment extends Fragment {

    private WebcamAdapter mWebcamAdapter = null;
    private ArrayList<WebcamItem> mItemList = null;
    int mWebcamSize = 0;
    int mWebcamSpacing = 0;
    GridView mGridView;
    final Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args;
        if(savedInstanceState != null)
            args = savedInstanceState;
        else
            args = getArguments();

        mItemList = (ArrayList<WebcamItem>) args.getSerializable("item_list");

        mWebcamAdapter = new WebcamAdapter(getActivity(), R.layout.webcam_item, mItemList);

        // update images every minute
        handler.postDelayed( new Runnable() {
            @Override
            public void run() {
                mWebcamAdapter.notifyDataSetChanged();
                handler.postDelayed( this, 60 * 1000 );
            }
        }, 60 * 1000 );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mGridView = (GridView) inflater.inflate(R.layout.webcam_fragment, container, false);

        mWebcamSize = getResources().getDimensionPixelSize(R.dimen.webcam_size);
        mWebcamSpacing = getResources().getDimensionPixelSize(R.dimen.webcam_spacing);

        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mWebcamAdapter.getNumColumns() == 0) {
                    // first pass
                    int numColumns = (int) Math.floor(INAF.width / (mWebcamSize + mWebcamSpacing));
                    if (numColumns == 0)
                        numColumns = 1;
                     final int columnWidth = (INAF.width / numColumns) - mWebcamSpacing;
                     mWebcamAdapter.setNumColumns(numColumns);
                     mWebcamAdapter.setItemHeight((int)(0.75*columnWidth));
                } else {
                    // second pass
                    mGridView.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
//                    ((NavigationDrawerActivity)getActivity()).stopLoading();
                }
            }
        });

        mGridView.setAdapter(mWebcamAdapter);

        return mGridView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item_list", mItemList);
    }

    static class ViewHolder
    {
        TextView title;
        ImageView webcam;
    }

    private class WebcamAdapter extends ArrayAdapter<WebcamItem> {
        private ArrayList<WebcamItem> objects = null;
        private LayoutInflater mInflater;
        private LinearLayout.LayoutParams mImageViewLayoutParams;
        private int mItemHeight = 0;
        private int mNumColumns = 0;

        public WebcamAdapter(Context context, int id, ArrayList<WebcamItem> objects)
        {
            super(context, id, objects);
            this.objects = objects;

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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
            mImageViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
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
        public WebcamItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.webcam_item, parent, false);
                holder = new ViewHolder();
                holder.webcam = (ImageView) convertView.findViewById(R.id.webcam_image);
                holder.title = (TextView) convertView.findViewById(R.id.webcam_title);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.webcam.setLayoutParams(mImageViewLayoutParams);

            if (holder.webcam.getLayoutParams().height != mItemHeight) {
                holder.webcam.setLayoutParams(mImageViewLayoutParams);
            }

            WebcamItem item = getItem(position);

            holder.webcam.setTag(item.imageUrl);
            holder.webcam.setImageResource(R.drawable.empty);
            holder.title.setText(item.title);

            // remove picture from the cache if present
            INAF.mBitmapCache.remove("#W0#H0"+item.imageUrl);
            INAF.imageLoader.get(item.imageUrl, new ImageListener(holder.webcam));

            return convertView;
        }
    }
}