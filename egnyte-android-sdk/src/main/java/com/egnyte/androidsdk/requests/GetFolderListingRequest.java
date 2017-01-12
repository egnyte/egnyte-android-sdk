package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.FolderListing;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *  * Represents getting folder listing request
 */
public class GetFolderListingRequest extends JSONResponseRequest<FolderListing> {

    public enum SortDirection {
        ASCENDING, DESCENDING
    }

    public enum SortBy {
        NAME, LAST_MODIFIED, UPLOADED_BY
    }

    /**
     * @param cloudPath path to folder
     */
    public GetFolderListingRequest(String cloudPath) {
        super("GET", "/pubapi/v1/fs", cloudPath, createQueryParamsMap(null, null, null, null), null, null);
    }

    /**
     * @param cloudPath path to folder
     * @param count the maximum number of items to return
     * @param offset the zero-based index from which to start returning items.
     * @param sortBy value describing how to sort results
     * @param sortDirection the direction of the sort
     */
    public GetFolderListingRequest(String cloudPath,
                                   Integer count,
                                   Integer offset,
                                   SortBy sortBy,
                                   SortDirection sortDirection) {
        super("GET", "/pubapi/v1/fs", cloudPath, createQueryParamsMap(count, offset, sortBy, sortDirection), null, null);
    }


    private static HashMap<String, Object> createQueryParamsMap(Integer count,
                                                                Integer offset,
                                                                SortBy sortBy,
                                                                SortDirection sortDirection) {
        HashMap<String, Object> queryParamsMap = new HashMap<>();
        QueryUtils.addIfNotNull("list_content", true, queryParamsMap);
        QueryUtils.addIfNotNull("count", count, queryParamsMap);
        QueryUtils.addIfNotNull("offset", offset, queryParamsMap);
        QueryUtils.addIfNotNull("sort_by", sortBy, queryParamsMap);
        QueryUtils.addIfNotNull("sort_direction", sortDirection, queryParamsMap);
        return queryParamsMap;
    }

    @Override
    protected FolderListing parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        if (!jsonObject.getBoolean("is_folder")) {
            throw new ResponseParsingException("Expected folder, but received file");
        }
        return FolderListing.parse(jsonObject);
    }
}
