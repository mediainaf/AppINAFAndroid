/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedListActivity extends NavigationDrawerActivity
        implements StringRequestFragment.Callbacks, FeedListFragment.Callbacks {

    Bundle mArgs;
    ArrayList<RSSItem> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        StringRequestFragment request = (StringRequestFragment) fm.findFragmentByTag("feed_list_request");

        if(savedInstanceState != null) {
            mArgs = savedInstanceState.getBundle("args");
        }
        else {
            mArgs = getIntent().getExtras();
        }

        mItemList = (ArrayList<RSSItem>) mArgs.getSerializable("item_list");

        // if there is no request ongoing and no previous request results
        if(request == null && mItemList == null)
        {
            request = new StringRequestFragment();
            fm.beginTransaction().add(request, "feed_list_request").commit();
            request.start(Request.Method.GET, mArgs.getString("feed_url"));
            ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
            pb.setVisibility(ProgressBar.VISIBLE);
        }
        else {
            replaceFragment();
        }
    }

    @Override
    public void onResponse(String xmlString) {
        mItemList = new ArrayList<RSSItem>();

        // parse xml
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new InputSource(new StringReader(xmlString)));

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
                String descClean = descriptionCDATA.replaceAll("<(.*?)\\>"," "); //Removes all items in brackets
                descClean = descClean.replaceAll("<(.*?)\\\n"," "); //Must be undeneath
                descClean = descClean.replaceFirst("(.*?)\\>", " "); //Removes any connected item to the last bracket
                descClean = descClean.replaceAll("&nbsp;"," ");
                descClean = descClean.replaceAll("&amp;"," ");
                descClean = descClean.replaceAll("&amp;"," ");
                rssItem.description = descClean.trim();
                // find the image url inside the description
                Pattern p = Pattern.compile(".*<img[^>]*src=\"([^\"]*)", Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(descriptionCDATA);
                m.find();
                rssItem.imageUrl = m.group(1);
                Element contentElement = item.getChild("encoded", Namespace.getNamespace("http://purl.org/rss/1.0/modules/content/"));
                String contentCDATA = contentElement.getText();
                rssItem.content = contentCDATA.replaceAll("[<](/)?div[^>]*[>]", "");

                mItemList.add(rssItem);
            }
        } catch (JDOMException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        ProgressBar pb = (ProgressBar) findViewById(R.id.preloader);
        pb.setVisibility(ProgressBar.INVISIBLE);
        replaceFragment();

/*        if (findViewById(R.id.item_detail_container) != null) { TODO handle two-panel
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FeedListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }*/
    }

    @Override
    public void onError(VolleyError error) {
        Log.e("aaa", "TODO Handle Error!!!!!!!");
        Log.e("aaa", error.getMessage());
    }

    @Override
    public void onFeedItemSelected(Bundle args) {
/*      if (mTwoPane) { TODO handle item selection
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(FeedDetailFragment.ARG_ITEM_ID, args.getString(ITEM_ID));
            FeedDetailFragment fragment = new FeedDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }*/
        Intent detailIntent = new Intent(this, FeedDetailActivity.class);
        detailIntent.putExtras(args);
        startActivity(detailIntent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mItemList != null)
            outState.putSerializable("item_list", mItemList);
    }

    void replaceFragment() {
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putSerializable("item_list", mItemList);
        FeedListFragment fragment = new FeedListFragment();
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.container, fragment, "feedlist" + mTitle).commit();
    }
}
