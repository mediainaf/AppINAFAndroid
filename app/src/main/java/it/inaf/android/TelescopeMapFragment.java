/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class TelescopeMapFragment extends Fragment {

    Bundle mArgs;
    ArrayList<TelescopeItem> mItemList;
    HashMap<String, Integer> idToTelIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idToTelIndex = new HashMap<String, Integer>();

        if(savedInstanceState != null)
            mArgs = savedInstanceState;
        else
            mArgs = getArguments();

        mItemList = (ArrayList<TelescopeItem>) mArgs.getSerializable("item_list");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_locations, container, false);
        SupportMapFragment fragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.map, fragment)
                .commit();
        return layout;
    }

    private class WindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            TextView textview = (TextView) getActivity().getLayoutInflater().inflate(R.layout.satellite_info, null);
            final TelescopeItem t = mItemList.get(idToTelIndex.get(marker.getId()));
            textview.setText(t.name);
            return textview;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleMap map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setInfoWindowAdapter(new WindowAdapter());
        for (int i = 0; i < mItemList.size(); i++) {
            TelescopeItem item = mItemList.get(i);
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(item.latitude, item.longitude));
            Marker m = map.addMarker(markerOptions);
            idToTelIndex.put(m.getId(), i);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("item_list", mItemList);
    }
}