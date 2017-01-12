package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.JSONParser;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.EgnyteSearchedFolder;
import com.egnyte.androidsdk.entities.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Request for searching folders
 */
public class SearchFoldersRequest extends SearchRequest<EgnyteSearchedFolder> {

    public SearchFoldersRequest(String query) {
        this(query, null, null, null, null, null);
    }

    /**
     *
     * @param query phrase to look up
     * @param offset the zero-based index from which to start returning items.
     * @param count the maximum number of items to return
     * @param folderPath limit search only to contents of given folder path, may be null
     * @param modifiedBefore limit search only to folders modified before given date
     * @param modifiedAfter limit search only to folders modified after given date
     */
    public SearchFoldersRequest(String query, Integer offset, Integer count, String folderPath, Date modifiedBefore, Date modifiedAfter) {
        super(query, offset, count, folderPath, modifiedBefore, modifiedAfter, Type.FOLDER);
    }

    @Override
    protected SearchResult<EgnyteSearchedFolder> parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        return SearchResult.parse(jsonObject, new JSONParser<EgnyteSearchedFolder>() {
            @Override
            public EgnyteSearchedFolder parse(JSONObject jsonObject) throws JSONException {
                return EgnyteSearchedFolder.parse(jsonObject);
            }
        });
    }
}