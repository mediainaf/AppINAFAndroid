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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LocationsFragment extends Fragment {

    private class LocationInfo
    {
        String name;
        String descr;
        String website;
        String address;
        String phone;
        String coordinates;
    }

    LocationInfo[] mLocations;
    HashMap<String, Integer> idToLocationInfoIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int length = INAF.jsonLocations.length();

        mLocations = new LocationInfo[length];
        idToLocationInfoIndex = new HashMap<String, Integer>();

        for(int i=0; i< length; i++) {
            try {
                JSONObject obj = INAF.jsonLocations.getJSONObject(i);
                mLocations[i] = new LocationInfo();
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

            final LocationInfo mLocation = mLocations[idToLocationInfoIndex.get(marker.getId())];
            titleView.setText(mLocation.name);
            addressView.setText(mLocation.address);
            phoneView.setText("Telefono: "+mLocation.phone);
            websiteView.setText(mLocation.website);
/*            phoneView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+mLocation.phone));
                    startActivity(callIntent);
                }
            });*/
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

        for(int i=0; i<mLocations.length; i++) {
            String coordinateArray[] = mLocations[i].coordinates.split(",");
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(coordinateArray[1]), Double.parseDouble(coordinateArray[0])));
            Marker m = map.addMarker(markerOptions);
            idToLocationInfoIndex.put(m.getId(), i);
        }
    }
}