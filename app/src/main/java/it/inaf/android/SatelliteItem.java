/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.io.Serializable;

public class SatelliteItem implements Serializable {
    String id;
    String name;
    String label;
    String tag;
    String imgUrl;
    int coordx;
    int coordy;
    int phase;
    int scope;
    int srow;
    int scol;
    boolean showonweb;
    boolean showonapp;
}