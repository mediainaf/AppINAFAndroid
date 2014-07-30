/* Copyright (c) 2014 Andrea Zoli. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file. */

package it.inaf.android;

import java.io.Serializable;

public class RSSItem implements Serializable
{
    private static final long serialVersionUID = -4816726307049056150L;

    public String title;
    public String date;
    public String author;
    public String description;
    public String link;
    public String content;
}