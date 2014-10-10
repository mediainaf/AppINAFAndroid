/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppsFragment extends ListFragment {

    ArrayList<AppItem> mItemList;
    AppListAdapter mAdapter;
    Callbacks mCallbacks;

    public interface Callbacks
    {
        void onItemSelected(Bundle args);
    }

    static class ViewHolder
    {
        ImageView image;
        TextView name;
    }

    private class AppListAdapter extends ArrayAdapter<AppItem>
    {
        private Context mContext;
        private ArrayList<AppItem> objects = null;

        public AppListAdapter(Context context, int textviewid, ArrayList<AppItem> objects)
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
        public AppItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                LayoutInflater li = LayoutInflater.from(mContext);
                convertView = li.inflate(R.layout.app_item, parent, false);

                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.apps_list_image);
                holder.name = (TextView)convertView.findViewById(R.id.apps_list_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            AppItem data = objects.get(position);

            if(data == null)
                return convertView;

            holder.name.setText(data.name);

            holder.image.setTag(data.iconurl);
            holder.image.setImageResource(R.drawable.empty);
            INAF.imageLoader.get(data.iconurl, new ImageListener(holder.image));

            return convertView;
        }
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

        if(savedInstanceState != null)
        {
            mItemList = (ArrayList<AppItem>) savedInstanceState.getSerializable("item_list");
        }
        else {
            mItemList = new ArrayList<AppItem>();

            int length = INAF.jsonApps.length();

            for (int i = 0; i < length; i++) {
                try {
                    AppItem appItem = new AppItem();
                    JSONObject obj = INAF.jsonApps.getJSONObject(i);
                    appItem.id = obj.getString("id");
                    appItem.name = obj.getString("name");
                    appItem.descr = obj.getString("descr");
                    appItem.authors = obj.getString("authors");
                    appItem.iconurl = obj.getString("iconurl");
                    appItem.infourl = obj.getString("infourl");
                    appItem.iosurl = obj.getString("iosurl");
                    appItem.androidurl = obj.getString("androidurl");
                    appItem.price = obj.getString("price");
                    appItem.lang = obj.getString("lang");
                    appItem.notes = obj.getString("notes");

                    mItemList.add(appItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        mAdapter = new AppListAdapter(getActivity(), R.layout.feed_item, mItemList);
        setListAdapter(mAdapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        AppItem item = mItemList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("link", item.infourl);
        mCallbacks.onItemSelected(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item_list", mItemList);
    }
}
