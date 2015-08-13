/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JobListActivity extends NavigationDrawerActivity
        implements StringRequestFragment.Callbacks, JobListFragment.Callbacks {

    private String jobsUrl = "http://www.inaf.it/it/lavora-con-noi/concorsi-inaf/rss";

    Bundle mArgs;
    ArrayList<JobItem> mItemList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getSupportFragmentManager();
        StringRequestFragment request = (StringRequestFragment) fm.findFragmentByTag("feed_list_request");

        if(savedInstanceState != null)
            mArgs = savedInstanceState.getBundle("args");
        else
            mArgs = getIntent().getExtras();

        mItemList = (ArrayList<JobItem>) mArgs.getSerializable("item_list");

        // if there is no request ongoing and no previous request results
        if(request == null && mItemList == null) {
            StringRequestFragment jobsRequest = new StringRequestFragment();
            fm.beginTransaction().add(jobsRequest, "job_request").commit();
            jobsRequest.start(Request.Method.GET, jobsUrl);
            startLoading();
        }
        else {
            addFragment();
        }
    }

    @Override
    public void onResponse(String xmlString, String url) {
        ArrayList<JobItem> itemList = new ArrayList<JobItem>();

        // parse xml
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new InputSource(new StringReader(xmlString)));

            Namespace rssNS = Namespace.getNamespace("http://purl.org/rss/1.0/");
            List<Element> items = doc.getContent(Filters.element()).get(0).getChildren("item", rssNS);

            for (int i=0; i < items.size(); i++) {
                JobItem jobItem = new JobItem();
                Element item = items.get(i);

                jobItem.title = item.getChild("title", rssNS).getText();
                jobItem.link = item.getChild("link", rssNS).getText();
                String descriptionCDATA = item.getChild("description", rssNS).getText();
                String descClean = descriptionCDATA.replaceAll("<(.*?)\\>"," "); //Removes all items in brackets
                descClean = descClean.replaceAll("<(.*?)\\\n"," "); //Must be undeneath
                descClean = descClean.replaceFirst("(.*?)\\>", " "); //Removes any connected item to the last bracket
                descClean = descClean.replaceAll("&nbsp;"," ");
                descClean = descClean.replaceAll("&amp;"," ");
                descClean = descClean.replaceAll("&amp;"," ");
                jobItem.description = descClean.trim();
                Element dateElement = item.getChild("date", Namespace.getNamespace("http://purl.org/dc/elements/1.1/"));
                jobItem.plainDate = dateElement.getText();
                jobItem.date = DateFormatter.formatType3(dateElement.getText());
                Element contentElement = item.getChild("encoded", Namespace.getNamespace("http://purl.org/rss/1.0/modules/content/"));
                String contentCDATA = contentElement.getText();
                jobItem.content = contentCDATA.replaceAll("[<](/)?div[^>]*[>]", "");

                itemList.add(jobItem);
            }
        } catch (JDOMException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // date sorting
        Collections.sort(itemList, new Comparator<JobItem>() {
            @Override
            public int compare(JobItem job1, JobItem job2) {
            return job2.plainDate.compareTo(job1.plainDate);
            }
        });

        mItemList = itemList;
        addFragment();
    }

    @Override
    public void onError(VolleyError error, String url) {
        Toast.makeText(this, "Connessione lenta o assente. Controllare le impostazioni di connessione e ritentare.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemSelected(Bundle args) {
        Intent intent = new Intent(this, JobDetailActivity.class);
        intent.putExtras(args);
        intent.putExtra("top_activity", false);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("args", mArgs);
        if(mItemList != null)
            outState.putSerializable("item_list", mItemList);
    }

    void addFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putSerializable("item_list", mItemList);
        JobListFragment fragment = new JobListFragment();
        fragment.setArguments(args);
        fm.beginTransaction()
                .add(R.id.container, fragment, "fragment_container").commit();
        stopLoading();
    }
}
