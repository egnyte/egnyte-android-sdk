package com.egnyte.androidsdk.entities;

import com.egnyte.androidsdk.apiclient.egnyte.JSONParser;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class represents search result
 * @param Class representing result, {@link EgnyteSearchedFolder} or {@link EgnyteSearchedFile}
 */
public class SearchResult<T> {

    /**
     * Number of results
     */
    public final int count;
    /**
     * The zero-based index from which results were returned
     */
    public final int offset;
    /**
     * Results of search query
     */
    public final ArrayList<T> result;
    /**
     * Whether there are more results
     */
    public final boolean hasMore;
    /**
     * Total count of results for given query
     */
    public final int totalCount;

    public SearchResult(int count, int offset, ArrayList<T> result, boolean hasMore, int totalCount) {
        this.count = count;
        this.offset = offset;
        this.result = result;
        this.hasMore = hasMore;
        this.totalCount = totalCount;
    }

    public static <T> SearchResult parse(JSONObject jsonObject, JSONParser<T> parser) throws JSONException, ResponseParsingException {
        return new SearchResult(
                jsonObject.getInt("count"),
                jsonObject.getInt("offset"),
                parseResult(parser, jsonObject.getJSONArray("results")),
                jsonObject.getBoolean("hasMore"),
                jsonObject.getInt("total_count")
        );
    }

    private static <T> ArrayList<T> parseResult(JSONParser<T> parser, JSONArray jsonArray) throws JSONException, ResponseParsingException {
        ArrayList<T> result = new ArrayList();
        for (int i = 0; i < jsonArray.length(); ++i) {
            result.add(parser.parse(jsonArray.getJSONObject(i)));
        }
        return result;
    }
}
