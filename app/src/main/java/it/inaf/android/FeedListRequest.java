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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * VolleyStringRequest manages a single background volley string request and retains itself across
 * configuration changes.
 */
public class FeedListRequest extends Fragment {

    /**
     * Callback interface through which the fragment can report the task's
     * results back to the Activity.
     */
    static interface VolleyCallbacks {
        void onResponse(ArrayList<RSSItem> itemList);
        void onError(VolleyError error);
    }

    private static final boolean DEBUG = true;
    private static final String TAG = FeedListRequest.class.getSimpleName();

    private VolleyCallbacks mCallbacks;
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
        if (!(activity instanceof VolleyCallbacks)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }

        // Hold a reference to the parent Activity so we can report back the task's
        // current progress and results.
        mCallbacks = (VolleyCallbacks) activity;
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

        private String formatDate(String date)
        {
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
            java.util.Date tmpDate = null;
            try
            {
                tmpDate = format.parse(date);
            }
            catch(ParseException e)
            {
                e.printStackTrace();
            }

            SimpleDateFormat postFormater = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);
            return postFormater.format(tmpDate);
        }

        @Override
        public void onResponse(String response) {
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            ArrayList<RSSItem> itemList = new ArrayList<RSSItem>();

            try {
                String query = "//rss/channel/item";
                InputSource source = new InputSource(new StringReader(response));
                NodeList items = (NodeList) xpath.evaluate(query, source, XPathConstants.NODESET);

                for(int i=0; i < items.getLength(); i++)
                {
                    RSSItem rssItem = new RSSItem();
                    Element item = (Element) items.item(i);
                    rssItem.title = xpath.evaluate("title/text()", item);
                    rssItem.date = formatDate(xpath.evaluate("pubDate/text()", item));
                    rssItem.link = xpath.evaluate("link/text()", item);
                    NodeList nlist = item.getElementsByTagNameNS("http://purl.org/rss/1.0/modules/content/", "encoded");
                    String contentCDATA = nlist.item(0).getTextContent();
                    rssItem.content = contentCDATA.replaceAll("[<](/)?div[^>]*[>]", "");
                    nlist = item.getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "creator");
                    rssItem.author = nlist.item(0).getTextContent();
                    String descriptionCDATA = xpath.evaluate("description/text()", item);
                    rssItem.description = descriptionCDATA.replaceAll("\\<.*?>","") + ".";
                    // find the image url inside the description
                    Pattern p = Pattern.compile(".*<img[^>]*src=\"([^\"]*)",Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(descriptionCDATA);
                    m.find();
                    rssItem.imageUrl = m.group(1);
                    itemList.add(rssItem);
                }
            }
            catch(XPathExpressionException e)
            {
                e.printStackTrace();
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

    /**
     * Start the volley request.
     */
    public void start(int method, String url) {
        StringUTF8Request request = new StringUTF8Request(method, url, new ResponseListener(), new ErrorListener());
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
