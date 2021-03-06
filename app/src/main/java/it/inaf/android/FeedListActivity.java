/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONException;
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
    int mSpinnerCategoryPosition = 0;
    int mSpinnerCategoryId = 0;
    int mSpinnerDetailPosition = 0;
    boolean mLoading = true;
    FeedListFragment mFragment;

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
            JSONArray jsonAbout = INAF.loadJson(this, "json_about");
            String tweeturl = null;
            try {
                tweeturl = jsonAbout.getJSONObject(0).getString("tweventurl");
            } catch (JSONException e) {
            }
            if(tweeturl != null) {
                request = new StringRequestFragment();
                fm.beginTransaction().add(request, "feed_list_request").commit();
                request.start(Request.Method.GET, mArgs.getString("feed_url"));
                startLoading();
            }
            else {
                // TODO handle not existing tweet
            }
        }
        else {
            addFragment(-1);
        }

        if(mArgs.getString("feed_type").equals("news")) {
            mSpinnerCategoryPosition = mArgs.getInt("filter_category_pos", 0);
            mSpinnerDetailPosition = mArgs.getInt("filter_pos", 0);

            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.news_action_bar);
            Spinner spinner = (Spinner) findViewById(R.id.action_bar_spinner_collection);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                    R.array.category, android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            // setup initial spinner position
            spinner.setSelection(mSpinnerCategoryPosition);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Spinner spinner = (Spinner) findViewById(R.id.action_bar_spinner_collection_detail);

                    if(i == 0 && mSpinnerCategoryPosition != 0) {
                        spinner.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(getBaseContext(), FeedListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intent.putExtra("feed_type", "news");
                        intent.putExtra("feed_url", "http://www.media.inaf.it/category/news/feed");
                        intent.putExtra("nav_position", R.id.drawer_section_2);
                        intent.putExtra("top_activity", true);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                    else if(i == 0) {
                        mLoading = false;
                        return;
                    }

                    int resid;
                    mSpinnerCategoryPosition = i;
                    if(!mLoading)
                        mSpinnerDetailPosition = 0;
                    switch(i) {
                        case 1:
                            resid = R.array.sedi;
                            mSpinnerCategoryId = R.array.sedi_id;
                            break;
                        case 2:
                            resid = R.array.terra;
                            mSpinnerCategoryId = R.array.terra_id;
                            break;
                        case 3:
                            resid = R.array.spazio;
                            mSpinnerCategoryId = R.array.spazio_id;
                            break;
                        default:
                            spinner.setVisibility(View.INVISIBLE);
                            return;
                    }
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(),
                            resid, android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setVisibility(View.VISIBLE);
                    // setup initial spinner position
                    spinner.setSelection(mSpinnerDetailPosition);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(i == 0 || mSpinnerDetailPosition == i)
                                return;

                            mSpinnerDetailPosition = i;
                            String[] stringArray = getResources().getStringArray(mSpinnerCategoryId);
                            String tag = stringArray[i];
                            Intent intent = new Intent(getBaseContext(), FeedListActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("feed_type", "news");
                            intent.putExtra("feed_url", "http://www.media.inaf.it/tag/"+tag+"/feed");
                            intent.putExtra("nav_position", R.id.drawer_section_2);
                            intent.putExtra("filter_category_pos", mSpinnerCategoryPosition);
                            intent.putExtra("filter_pos", mSpinnerDetailPosition);
                            intent.putExtra("top_activity", true);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    mLoading = false;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }

    @Override
    public void onResponse(String xmlString, String url) {

        // search the selected item in the current page or go backward (triggered from push messages)
        boolean searchSelectedItem = false;
        int selectedItemPosition = -1;
        String selectedItemTitle = mArgs.getString("item_title");
        int pagenum = -1;
        if(selectedItemTitle != null) {
            searchSelectedItem = true;
            int qIndex = url.indexOf('?');
            if(qIndex < 0)
                pagenum = 0;
            else
                pagenum = Integer.valueOf(url.substring(qIndex + 7));
        }

        boolean paged = false;
        if(url.contains("?paged")) {
            paged = true;
        }
        else {
            mItemList = new ArrayList<RSSItem>();
        }

        // parse xml
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new InputSource(new StringReader(xmlString)));

            List<Element> items = doc.getContent(Filters.element()).get(0).getChild("channel").getChildren("item");

            for (int i=0; i < items.size(); i++) {
                RSSItem rssItem = new RSSItem();
                Element item = items.get(i);

                rssItem.title = item.getChild("title").getText();
                rssItem.date = DateFormatter.formatType1(item.getChild("pubDate").getText());
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
                boolean found = m.find();
                if(found)
                    rssItem.imageUrl = m.group(1);
                Element contentElement = item.getChild("encoded", Namespace.getNamespace("http://purl.org/rss/1.0/modules/content/"));
                String contentCDATA = contentElement.getText();
                rssItem.content = contentCDATA.replaceAll("[<](/)?div[^>]*[>]", "");

                if(paged && mFragment != null)
                    mFragment.addItem(rssItem);
                else
                    mItemList.add(rssItem);

                if(searchSelectedItem && selectedItemPosition < 0 && rssItem.title.compareTo(selectedItemTitle) == 0) {
                    selectedItemPosition = pagenum * 14 + i;
                    Log.d("debug", "selectedItemPosition" + selectedItemPosition);
                }
            }

            if(paged && mFragment != null)
                mFragment.stopBottomProgressBar();

        } catch (JDOMException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if(searchSelectedItem && selectedItemPosition < 0) {
            // Search backwards
            FragmentManager fm = getSupportFragmentManager();
            StringRequestFragment request = new StringRequestFragment();
            fm.beginTransaction().add(request, "feed_list_old_request").commit();
            request.start(Request.Method.GET, "http://www.media.inaf.it/category/eventi/feed?paged=" + (pagenum + 1));
            return;
        }

        if(!paged || searchSelectedItem)
            addFragment(selectedItemPosition);

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
    public void onError(VolleyError error, String url) {
        Toast.makeText(this, "Connessione lenta o assente. Controllare le impostazioni di connessione e ritentare.", Toast.LENGTH_LONG).show();
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
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }*/
        Intent intent = new Intent(this, FeedDetailActivity.class);
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
        outState.putInt("filter_category_pos", mSpinnerCategoryPosition);
        outState.putInt("filter_pos", mSpinnerDetailPosition);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mArgs.getString("item_title") != null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().show(mFragment).commit();
            mArgs.putString("item_title", null);
        }
    }

    void addFragment(int selectedPosition) {
        Bundle args = new Bundle();
        args.putString("title", mTitle);
        args.putSerializable("item_list", mItemList);
        args.putString("feed_url", mArgs.getString("feed_url"));
        args.putInt("item_pos", selectedPosition); // -1 no item selected, shows the list, otherwise open the item directly
        mFragment = new FeedListFragment();
        mFragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction tr = fm.beginTransaction();
        tr.add(R.id.container, mFragment, "fragment_container");
        tr.hide(mFragment);
        tr.commit();
        stopLoading();
    }
}
