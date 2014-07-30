/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class FeedListFragment extends ListFragment
{
    private ArrayList<RSSItem> mItemList = null;
    private RSSListAdapter mRssAdapter = null;

    private String mTitle;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface Callbacks
    {
        // Called when a feed in the list is selected.
        void onFeedItemSelected(Bundle args);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onFeedItemSelected(Bundle args) {
        }
    };

    public FeedListFragment()
    {
    }

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

        Bundle args = getArguments();
        if(savedInstanceState != null)
            args = savedInstanceState;

        mTitle = args.getString("title");
        mItemList = (ArrayList<RSSItem>) args.getSerializable("item_list");
        mRssAdapter = new RSSListAdapter(getActivity(), R.layout.feed_item, mItemList);
        setListAdapter(mRssAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_feed_list, container, false);
        getActivity().setTitle(mTitle);

        if(savedInstanceState != null && mItemList != null) {
            ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        RSSItem data = mItemList.get(position);

        Bundle args = new Bundle();
        args.putString("title", data.title);
        args.putString("author", data.author);
        args.putString("date", data.date);
        args.putString("description", data.description);
        args.putString("content", data.content);
        args.putInt("position", position);
        mCallbacks.onFeedItemSelected(args);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("title", mTitle);
        outState.putSerializable("item_list", mItemList);

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    static class ViewHolder
    {
        ImageView image;
        TextView title;
        TextView subtitle;
        TextView description;
    }

    public void setArrayList(ArrayList<RSSItem> itemList) {
        mItemList = itemList;
        ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.INVISIBLE);
        mRssAdapter = new RSSListAdapter(getActivity(), R.layout.feed_item, mItemList);
        setListAdapter(mRssAdapter);
    }

    private class RSSListAdapter extends ArrayAdapter<RSSItem>
    {
        private Context mContext;
        private ArrayList<RSSItem> objects = null;

        public RSSListAdapter(Context context, int textviewid, ArrayList<RSSItem> objects)
        {
            super(context, textviewid, objects);
            mContext = context;
            this.objects = objects;
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
        public RSSItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;

            if(convertView == null)
            {
                LayoutInflater li = LayoutInflater.from(mContext);
                convertView = li.inflate(R.layout.feed_item, null);

                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.imageView1);
                holder.title = (TextView)convertView.findViewById(R.id.txtTitle);
                holder.subtitle = (TextView)convertView.findViewById(R.id.txtSubtitle);
                holder.description = (TextView)convertView.findViewById(R.id.txtDescription);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            RSSItem data = objects.get(position);

            if(data != null)
            {
                // TODO handle image
/*                Picasso.with(getActivity())
                    .load(data.imageUrl)
                    .into(holder.image);*/

                holder.title.setText(data.title);
                holder.subtitle.setText(data.author+" / "+data.date);
                holder.description.setText(data.description);

                // set description number of lines based on the title and data heights.
                Rect bounds = new Rect();
                Paint textPaint = holder.title.getPaint();
                textPaint.getTextBounds(data.title, 0, data.title.length(), bounds);
                double textWidth = bounds.width();
                holder.title.measure(MeasureSpec.UNSPECIFIED,MeasureSpec.UNSPECIFIED);
                double titleViewWidth = holder.title.getMeasuredWidth();
                int titleNLines = (int) Math.ceil( textWidth / titleViewWidth);
                int heightTitle = titleNLines * holder.title.getLineHeight();
                int heightDate = holder.subtitle.getLineHeight();
                int heightContainer = convertView.findViewById(R.id.vlayout1).getLayoutParams().height; // this has to be fixed
                int heightDescription = heightContainer - heightTitle - heightDate;
                int descNLines = (int) heightDescription / holder.description.getLineHeight();
                holder.description.setLines(descNLines);
            }
            return convertView;
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

}