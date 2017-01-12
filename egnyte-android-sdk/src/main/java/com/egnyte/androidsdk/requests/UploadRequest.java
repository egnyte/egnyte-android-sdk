package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.UploadResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/File_System_Management_API_Documentation#Upload-a-File">Upload</a> request
 */
public class UploadRequest extends JSONResponseRequest<UploadResult> {

    /**
     * @param cloudPath path to upload to
     * @param sourceFile file to upload
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     */
    public UploadRequest(String cloudPath, File sourceFile, ProgressListener progressListener) {
        this(cloudPath, sourceFile, progressListener, null, null);
    }

    /**
     * @param cloudPath path to upload to
     * @param sourceFile file to upload
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @param checksum SHA512 hash of entire file that can be used for validating upload integrity, might be null
     * @param lastModified indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     */
    public UploadRequest(String cloudPath, final File sourceFile, final ProgressListener progressListener, String checksum, String lastModified) {
        this(cloudPath, new FileInputStreamProvider(sourceFile), progressListener, checksum, lastModified);
    }

    /**
     * @param cloudPath path to upload to
     * @param inputStreamProvider {@link InputStreamProvider} that will provide content to upload
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @param checksum SHA512 hash of entire file that can be used for validating upload integrity, might be null
     * @param lastModified indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     */
    public UploadRequest(String cloudPath, final InputStreamProvider inputStreamProvider, final ProgressListener progressListener, String checksum, String lastModified) {
        super("POST", "/pubapi/v1/fs-content", cloudPath, null, createHeaders(checksum, lastModified), new UploadRequestBody(inputStreamProvider, progressListener));
    }

    private static HashMap<String, String> createHeaders(String checksum, String lastModified) {
        HashMap<String, String> headers = new HashMap<>();
        if (checksum != null) {
            headers.put("X-Sha512-Checksum", checksum.toLowerCase(Locale.US));
        }
        if (lastModified != null) {
            headers.put("Last-Modified", lastModified);
        }
        return headers;
    }

    @Override
    protected UploadResult parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException {
        return UploadResult.parse(jsonObject);
    }
}
