/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class INAF extends Application {

    public static RequestQueue requestQueue;
    public static ImageLoader imageLoader;

    private static Context mContext;
    private static BitmapLruCache mBitmapCache;

    public static int width;
    public static int height;
    public static float aspectRatio;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        requestQueue = Volley.newRequestQueue(mContext, null);
        imageLoader = new ImageLoader(requestQueue, getBitmapCache());
        VolleyLog.setTag("MyAppTag");
        // http://stackoverflow.com/a/17035814
        imageLoader.setBatchedResponseDelay(0);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        }
        else {
            width = display.getWidth();  // deprecated
            height = display.getHeight();  // deprecated
        }
        int r = display.getRotation();
        if(r == Surface.ROTATION_90 || r == Surface.ROTATION_270) {
            int tmp = width;
            width = height;
            height = tmp;
        }

        if(width > height)
            aspectRatio = width / (float)height;
        else
            aspectRatio = height / (float)width;

        Log.i("Info", "Width: " + width + " Height: " + height + "Aspect Ratio: " + aspectRatio);
    }

    public static BitmapLruCache getBitmapCache() {
        if (mBitmapCache == null) {
            // The cache size will be measured in kilobytes rather than
            // number of items. See http://developer.android.com/training/displaying-bitmaps/cache-bitmap.html
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int cacheSize = maxMemory / 8;  //Use 1/8th of the available memory for this memory cache.
            mBitmapCache = new BitmapLruCache(cacheSize);
        }
        return mBitmapCache;
    }
}
