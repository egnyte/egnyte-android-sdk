package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/File_System_Management_API_Documentation#Delete-a-File-or-Folder">Delete a File or Folder</a> request
 */
public class DeleteRequest extends BaseRequest<Void> {

    /**
     * @param cloudPath path to file or folder to be deleted
     */
    public DeleteRequest(String cloudPath) {
        super("DELETE", "/pubapi/v1/fs", cloudPath, null, null, null);
    }

    @Override
    protected Void parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) throws IOException, ResponseParsingException {
        return null;
    }
}
