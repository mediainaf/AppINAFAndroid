/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LocationsFragment extends Fragment {

    LocationItem[] mLocations;
    HashMap<String, Integer> idToLocationInfoIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JSONArray json = INAF.loadJson(getActivity(), "json_locations");

        int length = json.length();

        mLocations = new LocationItem[length];
        idToLocationInfoIndex = new HashMap<String, Integer>();

        for(int i=0; i< length; i++) {
            try {
                JSONObject obj = json.getJSONObject(i);
                mLocations[i] = new LocationItem();
                mLocations[i].name = obj.getString("name");
                mLocations[i].descr = obj.getString("descr");
                mLocations[i].website = obj.getString("website");
                mLocations[i].address = obj.getString("address");
                mLocations[i].phone = obj.getString("phone");
                mLocations[i].coordinates = obj.getString("coordinates");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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

    private class LocationWindowAdapter implements GoogleMap.InfoWindowAdapter {

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View layout = getActivity().getLayoutInflater().inflate(R.layout.location_info, null);
            TextView titleView = (TextView) layout.findViewById(R.id.location_info_title);
            TextView phoneView = (TextView) layout.findViewById(R.id.location_info_phone);
            TextView addressView = (TextView) layout.findViewById(R.id.location_info_address);
            TextView websiteView = (TextView) layout.findViewById(R.id.location_info_website);

            final LocationItem mLocation = mLocations[idToLocationInfoIndex.get(marker.getId())];
            titleView.setText(mLocation.name);
            addressView.setText(mLocation.address);
            phoneView.setText("Telefono: "+mLocation.phone);
            websiteView.setText(mLocation.website);

            return layout;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        GoogleMap map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        // center on Italy
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.2924601, 12.5736108), 5));
        map.setInfoWindowAdapter(new LocationWindowAdapter());
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LocationItem location = mLocations[idToLocationInfoIndex.get(marker.getId())];

                LocationDialogFragment dialog = new LocationDialogFragment();
                Bundle args = new Bundle();
                args.putSerializable("item", location);
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(), "blablabla");
            }
        });

        for(int i=0; i<mLocations.length; i++) {
            String coordinateArray[] = mLocations[i].coordinates.split(",");
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(coordinateArray[1]), Double.parseDouble(coordinateArray[0])));
            Marker m = map.addMarker(markerOptions);
            idToLocationInfoIndex.put(m.getId(), i);
        }
    }
}