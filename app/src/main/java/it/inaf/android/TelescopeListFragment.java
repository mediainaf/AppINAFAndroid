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

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class TelescopeListFragment extends ListFragment {

    private HashSet<String> mNoImageSet = new HashSet<String>();
    ArrayList<TelescopeItem> mItemList;
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

    private class TelescopeImageListener extends ImageListener {
        private HashSet<String> mNoImageSet;

        TelescopeImageListener(ImageView image, HashSet<String> noImageSet) {
            super(image);
            mNoImageSet = noImageSet;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            super.onErrorResponse(error);

            if (error.networkResponse.statusCode == 404) {
                mNoImageSet.add(mUrl);

                if (!mImage.getTag().toString().equals(mUrl))
                    return;

                mImage.setImageResource(R.drawable.telescope);
                int padding = (int) getResources().getDimension(R.dimen.telescope_padding);
                mImage.setPadding(padding, padding, padding, padding);
            }
        }
    }

    private class TelescopeListAdapter extends ArrayAdapter<TelescopeItem>
    {
        private Context mContext;
        private ArrayList<TelescopeItem> objects = null;

        public TelescopeListAdapter(Context context, int textviewid, ArrayList<TelescopeItem> objects)
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
        public TelescopeItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                LayoutInflater li = LayoutInflater.from(mContext);
                convertView = li.inflate(R.layout.telescope_item, null);

                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.telescope_list_image);
                holder.name = (TextView)convertView.findViewById(R.id.telescope_list_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            TelescopeItem data = objects.get(position);

            if(data == null)
                return convertView;

            holder.name.setText(data.name);

            if(mNoImageSet.contains(data.imgUrl)) {
                holder.image.setImageResource(R.drawable.telescope);
                int padding = (int) getResources().getDimension(R.dimen.telescope_padding);
                holder.image.setPadding(padding, padding, padding, padding);
            }
            else {
                holder.image.setTag(data.imgUrl);
                holder.image.setImageResource(R.drawable.empty);
                holder.image.setPadding(0, 0, 0, 0);
                INAF.imageLoader.get(data.imgUrl, new TelescopeImageListener(holder.image, mNoImageSet));
            }

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

        mItemList = new ArrayList<TelescopeItem>();

        int length = INAF.jsonTelescopes.length();

        for (int i = 0; i < length; i++) {
            try {
                JSONObject obj = INAF.jsonTelescopes.getJSONObject(i);

                boolean show = obj.getString("showonapp").equals("1");
                if (!show)
                    continue;

                TelescopeItem telItem = new TelescopeItem();
                telItem.id = obj.getString("id");
                telItem.name = obj.getString("name");
                telItem.label = obj.getString("label");
                telItem.tag = obj.getString("tag");
                telItem.imgUrl = INAF.telescopeImagePrefixUrl + obj.getString("imgbase") + ".jpg";
                telItem.coordx = obj.getInt("coordx");
                telItem.coordy = obj.getInt("coordy");
                telItem.latitude = obj.getDouble("latitude");
                telItem.longitude = obj.getDouble("longitude");
                telItem.phase = obj.getInt("phase");
                telItem.scope = obj.getInt("scope");
                telItem.showonweb = obj.getString("showonweb").equals("1");
                telItem.showonapp = true;

                mItemList.add(telItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        TelescopeListAdapter adapter = new TelescopeListAdapter(getActivity(), R.layout.telescope_item, mItemList);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        TelescopeItem item = mItemList.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("link", INAF.telescopeDetailPrefixUrl+item.tag);
        mCallbacks.onItemSelected(bundle);
    }
}
