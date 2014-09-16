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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedListRequestFragment extends Fragment {

    static interface Callbacks {
        void onResponse(ArrayList<RSSItem> itemList);
        void onError(VolleyError error);
    }

    private static final boolean DEBUG = true;
    private static final String TAG = FeedListRequestFragment.class.getSimpleName();

    private Callbacks mCallbacks;
    private boolean mRunning;

    @Override
    public void onAttach(Activity activity) {
        if (DEBUG) Log.i(TAG, "onAttach(Activity)");
        super.onAttach(activity);

        if (!(activity instanceof FeedListRequestFragment.Callbacks)) {
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

    protected static final String TYPE_UTF8_CHARSET = "charset=UTF-8";


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

        SAXBuilder mBuilder = new SAXBuilder();



        @Override
        public void onResponse(String response) {
            ArrayList<RSSItem> itemList = new ArrayList<RSSItem>();

            try {
                Document doc = mBuilder.build(new InputSource(new StringReader(response)));

                List<Element> items = doc.getContent(Filters.element()).get(0).getChild("channel").getChildren("item");

                for (int i=0; i < items.size(); i++) {
                    RSSItem rssItem = new RSSItem();
                    Element item = items.get(i);

                    rssItem.title = item.getChild("title").getText();
                    rssItem.date = DateFormatter.format(item.getChild("pubDate").getText());
                    rssItem.link = item.getChild("link").getText();
                    Element authorElement = item.getChild("creator", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
                    rssItem.author = authorElement.getText();
                    String descriptionCDATA = item.getChild("description").getText();
                    rssItem.description = descriptionCDATA.replaceAll("\\<.*?>","");
                    // find the image url inside the description
                    Pattern p = Pattern.compile(".*<img[^>]*src=\"([^\"]*)", Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(descriptionCDATA);
                    m.find();
                    rssItem.imageUrl = m.group(1);
                    Element contentElement = item.getChild("encoded", Namespace.getNamespace("http://purl.org/rss/1.0/modules/content/"));
                    String contentCDATA = contentElement.getText();
                    rssItem.content = contentCDATA.replaceAll("[<](/)?div[^>]*[>]", "");

                    itemList.add(rssItem);
                }
            } catch (JDOMException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if(mCallbacks != null)
                mCallbacks.onResponse(itemList);
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

    public void start(int method, String url) {
        StringUTF8Request request = new StringUTF8Request(method, url, new ResponseListener(), new ErrorListener());
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

    public boolean isRunning() {
        return mRunning;
    }
}
