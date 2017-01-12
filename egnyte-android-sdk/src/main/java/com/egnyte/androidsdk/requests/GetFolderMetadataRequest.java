package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.EgnyteFolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Represents getting folder metadata request
 */
public class GetFolderMetadataRequest extends JSONResponseRequest<EgnyteFolder> {

    public GetFolderMetadataRequest(String cloudPath) {
        super("GET", "/pubapi/v1/fs", cloudPath, createQueryParamsMap(), null, null);
    }

    private static HashMap<String, Object> createQueryParamsMap() {
        HashMap<String, Object> queryParamsMap = new HashMap<>();
        QueryUtils.addIfNotNull("list_content", false, queryParamsMap);
        return queryParamsMap;
    }

    @Override
    protected EgnyteFolder parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        if (!jsonObject.getBoolean("is_folder")) {
            throw new ResponseParsingException("Expected folder, but received file");
        }
        return EgnyteFolder.parse(jsonObject);
    }
}
