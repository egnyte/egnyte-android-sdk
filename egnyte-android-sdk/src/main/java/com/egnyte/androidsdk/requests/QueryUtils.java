package com.egnyte.androidsdk.requests;

import java.util.HashMap;
import java.util.Locale;

/**
 * Helper class for query parameters of requests
 */
public class QueryUtils {

    /**
     * Add lowercased value under key to given map if value is not null
     * @param key
     * @param value
     * @param map
     */
    public static void addIfNotNull(String key, Object value, HashMap<String, Object> map) {
        if (value != null) {
            map.put(key, String.valueOf(value).toLowerCase(Locale.US));
        }
    }
}
