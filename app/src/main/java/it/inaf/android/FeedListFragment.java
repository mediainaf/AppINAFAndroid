/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;

import java.util.ArrayList;

public class FeedListFragment extends ListFragment {

    private String mTitle;
    private ArrayList<RSSItem> mItemList = null;
    private String mFeedUrl;
    private RSSListAdapter mRssAdapter = null;
    private View mProgressBar;

    private static final String STATE_ACTIVATED_POSITION = "activated_position";
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private Callbacks mCallbacks = sDummyCallbacks;
    int mSelection = -1;

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

        Bundle bundle;
        if(savedInstanceState != null)
            bundle = savedInstanceState;
        else
            bundle = getArguments();

        mTitle = bundle.getString("title");
        mItemList = (ArrayList<RSSItem>) bundle.getSerializable("item_list");
        mFeedUrl = bundle.getString("feed_url");
        mRssAdapter = new RSSListAdapter(getActivity(), R.layout.feed_item, mItemList);

        mSelection = bundle.getInt("item_pos");
        if(mSelection >= 0) {
            openFeedDetail(mSelection);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_feed_list, container, false);
        getActivity().setTitle(mTitle);
        mProgressBar = View.inflate(getActivity(), R.layout.progress_bar, null);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSelection >= 0)
            return;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().show(this).commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
        getListView().setOnScrollListener(new EndlessScrollListener());
        getListView().addFooterView(mProgressBar);
        setListAdapter(mRssAdapter);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        openFeedDetail(position);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putString("title", mTitle);
        outState.putSerializable("item_list", mItemList);
        outState.putString("feed_url", mFeedUrl);

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
                convertView = li.inflate(R.layout.feed_item, parent, false);

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

            if(data == null)
                return convertView;

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
            int descNLines = (int) (heightDescription * 1.0 / holder.description.getLineHeight());
            holder.description.setLines(descNLines);

            if(data.imageUrl != null && !data.imageUrl.equals("")) {
                holder.image.setTag(data.imageUrl);
                holder.image.setImageResource(R.drawable.empty);
                INAF.imageLoader.get(data.imageUrl, new ImageListener(holder.image));
            }

            return convertView;
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class EndlessScrollListener implements AbsListView.OnScrollListener {
        private static final int pageSize = 14;
        private int pageNum = 1;
        private int lastItem = pageNum * pageSize;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {

            if (visibleItemCount > 0) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;

                if (lastVisibleItem >= lastItem) {
                    pageNum++;
                    lastItem = pageNum * pageSize;
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    StringRequestFragment request = new StringRequestFragment();
                    fm.beginTransaction().add(request, "feed_list_old_request").commit();
                    request.start(Request.Method.GET, mFeedUrl+"?paged="+String.valueOf(pageNum));
                    startBottomProgressBar();
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    void addItem(RSSItem item) {
        mRssAdapter.add(item);
    }

    void startBottomProgressBar(){
        ProgressBar loader = (ProgressBar) getActivity().findViewById(R.id.feed_endlist_loader);
        loader.setVisibility(View.VISIBLE);
    }

    void stopBottomProgressBar(){
        ProgressBar loader = (ProgressBar) getActivity().findViewById(R.id.feed_endlist_loader);
        loader.setVisibility(View.INVISIBLE);
    }

    void openFeedDetail(int position) {
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
}