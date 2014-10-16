/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

public class StringRequestFragment extends Fragment {

    static interface Callbacks {
        void onResponse(String xmlString, String url);
        void onError(VolleyError error, String url);
    }

    private static final boolean DEBUG = true;
    private static final String TAG = StringRequestFragment.class.getSimpleName();

    private Callbacks mCallbacks;
    private boolean mRunning;

    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);

        if (!(activity instanceof StringRequestFragment.Callbacks)) {
            throw new IllegalStateException("Activity must implement the FeedListRequestFragment interface.");
        }

        // Update the activity reference at start and on configuration changes.
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

    private class StringUTF8Request extends StringRequest {

        public StringUTF8Request(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            try {
                // set HTML header to utf8 charset as default
                String contentType = response.headers.get("content-type");
                if(contentType == null)
                    contentType = "text/html; charset=utf-8";
                response.headers.put(HTTP.CONTENT_TYPE,contentType);
                String s = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                //
                return Response.success(s, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            }
        }
    }

    private class ResponseListener implements Response.Listener<String> {
        private String mUrl;

        ResponseListener(String url) {
            mUrl = url;
        }

        @Override
        public void onResponse(String response) {
            if(mCallbacks != null)
                mCallbacks.onResponse(response, mUrl);
            mRunning = false;
        }
    }

    private class ErrorListener implements Response.ErrorListener {
        private String mUrl;

        ErrorListener(String url) {
            mUrl = url;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            mCallbacks.onError(error, mUrl);
            mRunning = false;
        }
    }

    public void start(int method, String url) {
        StringUTF8Request request = new StringUTF8Request(method, url, new ResponseListener(url), new ErrorListener(url));
        INAF.requestQueue.add(request);
        mRunning = true;
    }

    public void cancel() {
        if (mRunning) {
            // TODO cancel volley request
            Log.e("errors", "TODO: cancel volley request");
            mRunning = false;
        }
    }
}
