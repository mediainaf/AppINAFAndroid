/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.io.Serializable;

public class JobItem implements Serializable {
    public String title;
    public String link;
    public String description;
    public String content;
    public String plainDate;
    public String date;
}