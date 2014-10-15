/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.ArrayList;

public class WebcamActivity extends NavigationDrawerActivity {

    private static final String[] urls = { "http://www.med.ira.inaf.it/webcam.jpg",
    "http://abell.as.arizona.edu/~hill/lbtmc02/image.jpg",
    "http://abell.as.arizona.edu/~hill/lbtcam/hugesize.jpg",
    "http://www.tt1obs.org/slideshow/p013_1_1.jpg",
    "http://www.srt.inaf.it/static/img/web-image-last-ts.jpg?1402669975",
    "http://www.tng.iac.es/webcam/get.html?resolution=640x480&compression=30&clock=1&date=1&dummy=1402670070248",
    "http://www.noto.ira.inaf.it/cams/cam1.jpg",
    "http://www.magic.iac.es/webcams/webcam/2014/06/13/1530_la.jpg",
    "http://www.magic.iac.es/webcams/webcam2/2014/06/13/1530_la.jpg",
    "http://archive.oapd.inaf.it/meteo/webcam_high.jpg",
    "http://archive.oapd.inaf.it/meteo/videocam_low.jpg",
    "http://polaris.me.oa-brera.inaf.it/remwbc/remwbc1.jpg",
    "http://polaris.me.oa-brera.inaf.it/remwbc/remwbc2.jpg",
    "http://polaris.me.oa-brera.inaf.it/remwbc/remwbcx.jpg",
    "http://www.media.inaf.it/wp-content/uploads/webcams/img_6.jpg" };

    private static final String[] names = { "Radiotelescopio di Medicina (Bologna)",
    "Large Binocular Telescope (Arizona,US) - Interno sx fronte",
    "Large Binocular Telescope (Arizona,US) - Esterno",
    " Il telescopio TT1 di Toppo Castel-grande (PZ)",
    "Sardinia Radio Telescope (Cagliari)",
    "Telescopio Nazionale Galileo (Isole Canarie)",
    "Radiotelescopio di Noto (SR)",
    "Il telescopio Cherenkov MAGIC-I (Isole Canarie)",
    "Il telescopio Cherenkov MAGIC-II (Isole Canarie)",
    "Stazione Osservativa di Cima Ekar (Asiago) - High",
    "Stazione Osservativa di Cima Ekar (Asiago) - Low",
    "Telescopio REM- Rapid Ey Mount (La Silla, Cile)",
    "Telescopio REM- Rapid Ey Mount (La Silla, Cile)",
    "Telescopio REM- Rapid Ey Mount (La Silla, Cile)",
    "Il telescopio di Loiano (Bologna)" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNavigationDrawerFragment.setUpCaretIndicatorEnabled(false);

        ArrayList<WebcamItem> itemList = new ArrayList<WebcamItem>();

        for(int i=0; i<urls.length; i++) {
            WebcamItem item = new WebcamItem();
            item.imageUrl = urls[i];
            item.title = names[i];
            itemList.add(item);
        }

        Bundle args = new Bundle();
        WebcamFragment fragment = new WebcamFragment();
        args.putSerializable("item_list", itemList);
        fragment.setArguments(args);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.container, fragment, "fragment_container").commit();

        getActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent upIntent = NavUtils.getParentActivityIntent(this);
            upIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            NavUtils.navigateUpTo(this, upIntent);
            overridePendingTransition(0, 0);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
