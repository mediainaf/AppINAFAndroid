/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.io.Serializable;

public class AppItem implements Serializable {
    String id;
    String name;
    String descr;
    String authors;
    String iconurl;
    String infourl;
    String iosurl;
    String androidurl;
    String price;
    String lang;
    String notes;
}