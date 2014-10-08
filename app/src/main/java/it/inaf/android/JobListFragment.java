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
import android.widget.TextView;

import java.util.ArrayList;

public class JobListFragment extends ListFragment {

    static class ViewHolder
    {
        TextView title;
        TextView date;
    }

    private class JobListAdapter extends ArrayAdapter<JobItem>
    {
        private Context mContext;
        private ArrayList<JobItem> objects = null;

        public JobListAdapter(Context context, int textviewid, ArrayList<JobItem> objects)
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
        public JobItem getItem(int position)
        {
            return ((null != objects) ? objects.get(position) : null);
        }

        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if(convertView == null)
            {
                LayoutInflater li = LayoutInflater.from(mContext);
                convertView = li.inflate(R.layout.job_item, parent, false);

                holder = new ViewHolder();
                holder.title = (TextView)convertView.findViewById(R.id.job_list_title);
                holder.date = (TextView)convertView.findViewById(R.id.job_list_date);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            JobItem data = objects.get(position);

            if(data == null)
                return convertView;

            holder.title.setText(data.title);
            holder.date.setText(data.date);

            return convertView;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if(savedInstanceState != null)
            args = savedInstanceState;

        String title = args.getString("title");
        getActivity().setTitle(title);
        ArrayList<JobItem> itemList = (ArrayList<JobItem>) args.getSerializable("item_list");

        JobListAdapter adapter = new JobListAdapter(getActivity(), R.layout.job_item, itemList);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_list, null);
        return view;
    }
}
