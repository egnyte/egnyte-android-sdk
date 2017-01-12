package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.EgnyteFile;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents getting file metadata request
 */
public class GetFileMetadataRequest extends JSONResponseRequest<EgnyteFile> {

    public GetFileMetadataRequest(String cloudPath) {
        super("GET", "/pubapi/v1/fs", cloudPath, null, null, null);
    }

    @Override
    protected EgnyteFile parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        if (jsonObject.getBoolean("is_folder")) {
            throw new ResponseParsingException("Expected file, but received folder");
        }
        return EgnyteFile.parse(jsonObject);
    }
}
