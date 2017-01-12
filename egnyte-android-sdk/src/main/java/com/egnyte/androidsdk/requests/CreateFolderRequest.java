package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.body.JSONBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/File_System_Management_API_Documentation#Create-a-Folder">Create a Folder</a> request
 */
public class CreateFolderRequest extends BaseRequest<Void> {

    public CreateFolderRequest(String fullCloudPath) {
        super("POST", "/pubapi/v1/fs", fullCloudPath, null, null, createBody());
    }

    private static RequestBody createBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "add_folder");
        } catch (JSONException ignore) {
        }
        return new JSONBody(jsonObject);
    }

    @Override
    protected Void parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) {
        return null;
    }
}