/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFormatter {
    public static String format(String date)
    {
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");
        java.util.Date tmpDate = null;
        try {
            tmpDate = format.parse(date);
        } catch(ParseException e) {
            format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            try {
                tmpDate = format.parse(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
        }

        SimpleDateFormat postFormater = new SimpleDateFormat("dd.MM.yyyy", Locale.ITALY);
        return postFormater.format(tmpDate);
    }
}