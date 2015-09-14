//    JsonObjectFormat.java
//    ~~~~~~~~~
//    This module implements the JsonObjectFormat class.
//    :authors: Konstantin Bokarius.
//    :copyright: (c) 2015 by Fanout, Inc.
//    :license: MIT, see LICENSE for more details.

package org.fanout.fanout;

import java.pubcontrol.Format;

/**
 * The JSON object format used for publishing messages to Fanout.io.
 */
public class JsonObjectFormat implements Format {
    private String value;

    /**
     * Initialize with a response value.
     */
    public JsonObjectFormat(String value) {
        this.value = value;
    }

    /**
     * The name of the format.
     */
    public String name() {
        return "json-object";
    }

    /**
     * The method used to export the format data.
     */
    public Object export() {
        return this.value;
    }
}
