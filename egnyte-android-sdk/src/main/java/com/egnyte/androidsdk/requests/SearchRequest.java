package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.entities.SearchResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

abstract class SearchRequest<T> extends JSONResponseRequest<SearchResult<T>> {

    enum Type {
        FOLDER, FILE;

        @Override
        public String toString() {
            return name().toLowerCase(Locale.US);
        }
    }

    public SearchRequest(String query, Integer offset, Integer count, String folderPath, Date modifiedBefore, Date modifiedAfter, Type type) {
        super("GET", "/pubapi/v1/search", null, createQueryParamsMap(query, offset, count, folderPath, modifiedBefore, modifiedAfter, type), null, null);
    }

    private static HashMap<String, Object> createQueryParamsMap(String query, Integer offset, Integer count, String folderPath, Date modifiedBefore, Date modifiedAfter, Type type) {
        HashMap<String, Object> queryParamsMap = new HashMap<>();
        QueryUtils.addIfNotNull("query", encode(query), queryParamsMap);
        QueryUtils.addIfNotNull("offset", count, queryParamsMap);
        QueryUtils.addIfNotNull("count", offset, queryParamsMap);
        QueryUtils.addIfNotNull("folder", folderPath, queryParamsMap);
        QueryUtils.addIfNotNull("modified_before", parseDate(modifiedBefore), queryParamsMap);
        QueryUtils.addIfNotNull("modified_after", parseDate(modifiedAfter), queryParamsMap);
        QueryUtils.addIfNotNull("type", type, queryParamsMap);
        return queryParamsMap;
    }

    private static String encode(String query) {
        try {
            return URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            return null;
        }
    }

    private static Object parseDate(Date date) {
        if (date == null) {
            return null;
        }
        return getDateFormat().format(date);
    }

    static SimpleDateFormat getDateFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat;
    }
}
