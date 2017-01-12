package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.JSONParser;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.EgnyteSearchedFile;
import com.egnyte.androidsdk.entities.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

/**
 * Request for searching files
 */
public class SearchFilesRequest extends SearchRequest<EgnyteSearchedFile> {

    public SearchFilesRequest(String query) {
        this(query, null, null, null, null, null);
    }

    /**
     *
     * @param query phrase to look up
     * @param offset the zero-based index from which to start returning items.
     * @param count the maximum number of items to return
     * @param folderPath limit search only to contents of given folder path, may be null
     * @param modifiedBefore limit search only to files modified before given date
     * @param modifiedAfter limit search only to files modified after given date
     */
    public SearchFilesRequest(String query, Integer offset, Integer count, String folderPath, Date modifiedBefore, Date modifiedAfter) {
        super(query, offset, count, folderPath, modifiedBefore, modifiedAfter, Type.FILE);
    }

    @Override
    protected SearchResult<EgnyteSearchedFile> parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        return SearchResult.parse(jsonObject, new JSONParser<EgnyteSearchedFile>() {
            @Override
            public EgnyteSearchedFile parse(JSONObject jsonObject) throws JSONException, ResponseParsingException {
                try {
                    return EgnyteSearchedFile.parse(jsonObject, getDateFormat());
                } catch (ParseException parseException) {
                    throw new ResponseParsingException(parseException);
                }
            }
        });
    }
}
