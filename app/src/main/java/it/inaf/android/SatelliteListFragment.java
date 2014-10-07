/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class SatelliteListFragment extends ListFragment {

    private HashSet<String> mNoImageSet = new HashSet<String>();

    static class ViewHolder
    {
        ImageView image;
        TextView name;
    }

    private class SatelliteImageListener extends ImageListener {
        private HashSet<String> mNoImageSet;

        SatelliteImageListener(ImageView image, HashSet<String> noImageSet) {
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

                mImage.setImageResource(R.drawable.satellite);
                int padding = (int) getResources().getDimension(R.dimen.telescope_padding);
                mImage.setPadding(padding, padding, padding, padding);
            }
        }
    }

    private class SatelliteListAdapter extends ArrayAdapter<SatelliteItem>
    {
        private Context mContext;
        private ArrayList<SatelliteItem> objects = null;

        public SatelliteListAdapter(Context context, int textviewid, ArrayList<SatelliteItem> objects)
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
        public SatelliteItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                LayoutInflater li = LayoutInflater.from(mContext);
                convertView = li.inflate(R.layout.satellite_item, parent, false);

                holder = new ViewHolder();
                holder.image = (ImageView)convertView.findViewById(R.id.satellite_list_image);
                holder.name = (TextView)convertView.findViewById(R.id.satellite_list_name);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            SatelliteItem data = objects.get(position);

            if(data == null)
                return convertView;

            holder.name.setText(data.name);

            if(mNoImageSet.contains(data.imgUrl)) {
                holder.image.setImageResource(R.drawable.satellite);
                int padding = (int) getResources().getDimension(R.dimen.telescope_padding);
                holder.image.setPadding(padding, padding, padding, padding);
            }
            else {
                holder.image.setTag(data.imgUrl);
                holder.image.setImageResource(R.drawable.empty);
                holder.image.setPadding(0, 0, 0, 0);
                INAF.imageLoader.get(data.imgUrl, new SatelliteImageListener(holder.image, mNoImageSet));
            }

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ArrayList<SatelliteItem> itemList = new ArrayList<SatelliteItem>();
        int length = INAF.jsonSatellites.length();
        for (int i = 0; i < length; i++) {
            try {
                JSONObject obj = INAF.jsonSatellites.getJSONObject(i);

                boolean show = obj.getString("showonapp").equals("1");
                if (!show)
                    continue;

                SatelliteItem satItem = new SatelliteItem();
                satItem.id = obj.getString("id");
                satItem.name = obj.getString("name");
                satItem.label = obj.getString("label");
                satItem.tag = obj.getString("tag");
                satItem.imgUrl = INAF.satelliteImagePrefixUrl + obj.getString("imgbase") + ".jpg";
                satItem.coordx = obj.getInt("coordx");
                satItem.coordy = obj.getInt("coordy");
                satItem.phase = obj.getInt("phase");
                satItem.scope = obj.getInt("scope");
                satItem.srow = obj.getInt("srow");
                satItem.scol = obj.getInt("scol");
                satItem.showonweb = obj.getString("showonweb").equals("1");
                satItem.showonapp = true;

                itemList.add(satItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SatelliteListAdapter adapter = new SatelliteListAdapter(getActivity(), R.layout.satellite_item, itemList);
        setListAdapter(adapter);
    }
}
