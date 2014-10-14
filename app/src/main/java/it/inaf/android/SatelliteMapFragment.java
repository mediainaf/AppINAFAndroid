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

public class SatelliteMapFragment extends Fragment {

    ArrayList<Satellite> satellites;
    HashMap<String, Integer> idToSatIndex;
    boolean mInitialized = false;
    boolean mZooming = false;
    boolean mZoomOK = false;
    String mFollowSatellite = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        satellites = new ArrayList<Satellite>();

        Satellite agile = new Satellite("Agile");
        agile.readTLE("1 31135U 07013A   14163.34680237  .00005153  00000-0  18593-3 0  9849",
                "2 31135 002.4637 352.3052 0016887 246.2767 113.5792 15.19897183395266");
        satellites.add(agile);

        Satellite fermi = new Satellite("Fermi");
        fermi.readTLE("1 33053U 08029A   14161.76628270  .00001306  00000-0  63609-4 0  5886",
                "2 33053 025.5822 296.4061 0013102 145.0912 215.0483 15.08435056330496");
        satellites.add(fermi);

        Satellite integral = new Satellite("Integral");
        integral.readTLE("1 27540U 02048A   14162.82361356  .00001041  00000-0  00000+0 0  5025",
                "2 27540 056.2995 239.1140 8397945 257.4318 000.3039 00.33349459 10388");
        satellites.add(integral);

        Satellite chandra = new Satellite("Chandra");
        chandra.readTLE("1 25867U 99040B   14165.24932237 -.00000272  00000-0  00000+0 0   283",
                "2 25867 076.8702 320.8309 8118375 274.2524 359.9080 00.37800584 11904");
        satellites.add(chandra);

        Satellite swift = new Satellite("Swift");
        swift.readTLE("1 28485U 04047A   14162.46649644  .00002050  00000-0  13073-3 0  7047",
                "2 28485 020.5562 256.8927 0012973 038.0784 322.0564 14.98862278522332");
        satellites.add(swift);

        Satellite xmm_newton = new Satellite("XMM Newton");
        xmm_newton.readTLE("1 25989U 99066A   14162.83713824  .00000041  00000-0  00000+0 0  6411",
                "2 25989 065.2736 056.8723 7695797 093.6205 359.6003 00.50127112 15360");
        satellites.add(xmm_newton);

        Satellite corot = new Satellite("Corot");
        corot.readTLE("1 29678U 06063A   14162.19373516  .00000425  00000-0  96034-4 0  4369",
                "2 29678 090.0189 011.6656 0203110 239.5882 262.3331 14.42990413381076");
        satellites.add(corot);

        idToSatIndex = new HashMap<String, Integer>();
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
            final Satellite s = satellites.get(idToSatIndex.get(marker.getId()));
            textview.setText(s.name);
            return textview;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(mInitialized)
            return;

        final GoogleMap map = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        // center on Italy
//
        // TODO zoom out
        map.setInfoWindowAdapter(new WindowAdapter());
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(mFollowSatellite.isEmpty())
                    mFollowSatellite = marker.getId();
                else {
                    mFollowSatellite = "";
                    mZooming = false;
                    mZoomOK = false;
                }
                return false;
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mFollowSatellite = "";
                mZooming = false;
                mZoomOK = false;
            }
        });

        for(int i=0; i<satellites.size(); i++) {
            Satellite.GeoCoordinates coords = satellites.get(i).getLatLong();
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(coords.latitude, coords.longitude));
            final Marker m = map.addMarker(markerOptions);
            int resid = getResources().getIdentifier("sat"+(i+1), "drawable", getActivity().getPackageName());
            m.setIcon(BitmapDescriptorFactory.fromResource(resid));
            idToSatIndex.put(m.getId(), i);

            final Handler handler = new Handler();
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Satellite s = satellites.get(idToSatIndex.get(m.getId()));
                    Satellite.GeoCoordinates coords = s.getLatLong();
                    m.setPosition(new LatLng(coords.latitude, coords.longitude));

                    if(m.getId().compareTo(mFollowSatellite) == 0) {
                        if(mZoomOK) {
                            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(coords.latitude, coords.longitude)));
                        }
                        else if(!mZooming) {
                            mZooming = true;
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coords.latitude, coords.longitude), 5), 2000, new GoogleMap.CancelableCallback() {
                                @Override
                                public void onFinish() {
                                    mZooming = false;
                                    mZoomOK = true;
                                }

                                @Override
                                public void onCancel() {
                                }
                            });
                        }
                    }

                    handler.postDelayed(this, 1000);
                }
            });
        }

        mInitialized = true;
    }
}