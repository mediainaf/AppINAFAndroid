/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

public class ImageListener implements ImageLoader.ImageListener
{
    private ImageView mImage;
    private String mUrl;

    ImageListener(ImageView image) {
        mImage = image;
        mUrl = image.getTag().toString();
    }

    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        if (response.getBitmap() != null) {
            if (mImage.getTag().toString().equals(mUrl))
                mImage.setImageBitmap(response.getBitmap());
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // TODO handle volley errors
    }
}