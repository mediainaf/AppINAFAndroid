/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

/**
 * VolleyStringRequest manages a single background volley string request and retains itself across
 * configuration changes.
 */
public class JSONRequestFragment extends Fragment {

    /**
     * Callback interface through which the fragment can report the task's
     * results back to the Activity.
     */
    static interface Callbacks {
        void onResponse(JSONArray response);
        void onError(VolleyError error);
    }

    private static final boolean DEBUG = true;
    private static final String TAG = JSONRequestFragment.class.getSimpleName();

    private Callbacks mCallbacks;
    private boolean mRunning;

    /**
     * Hold a reference to the parent Activity so we can report the task's current
     * progress and results. The Android framework will pass us a reference to the
     * newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);
        if (!(activity instanceof JSONRequestFragment.Callbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }

        // Hold a reference to the parent Activity so we can report back the task's
        // current progress and results.
        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy()");
        super.onDestroy();
        cancel();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG) Log.i(TAG, "onActivityCreated(Bundle)");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (DEBUG) Log.i(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.i(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.i(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.i(TAG, "onStop()");
        super.onStop();
    }

    private class ResponseListener implements Response.Listener<JSONArray> {

        @Override
        public void onResponse(JSONArray response) {
            if(mCallbacks != null)
                mCallbacks.onResponse(response);
            mRunning = false;
        }
    }

    private class ErrorListener implements Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            mCallbacks.onError(error);
            mRunning = false;
        }
    }

    /**
     * Start the volley request.
     */
    public void start(String url) {
        JsonArrayRequest request = new JsonArrayRequest(url, new ResponseListener(), new ErrorListener());
        INAF.requestQueue.add(request);
        mRunning = true;
    }

    /**
     * Cancel the volley request.
     */
    public void cancel() {
        if (mRunning) {
            // TODO cancel volley request
            Log.e("errors", "TODO: cancel volley request");
            mRunning = false;
        }
    }

    /**
     * Returns the current state of the request.
     */
    public boolean isRunning() {
        return mRunning;
    }
}