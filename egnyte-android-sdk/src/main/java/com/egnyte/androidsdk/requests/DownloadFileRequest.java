package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/File_System_Management_API_Documentation#Download-File">Download File</a> request
 */
public class DownloadFileRequest extends BaseRequest<Void> {

    final ProgressListener progressListener;
    final OutputStreamProvider outputStreamProvider;

    /**
     * @param cloudPath path to download
     * @param entryId entryId of a file, if you want to download a specyfic version. If null, will download most recent version.
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @param destinationFile local {@link File} to write into
     */
    public DownloadFileRequest(String cloudPath, String entryId, ProgressListener progressListener, final File destinationFile) {
        super("GET", "/pubapi/v1/fs-content", cloudPath, createQueryParamsMap(entryId), null, null);
        this.progressListener = progressListener;
        this.outputStreamProvider = new OutputStreamProvider() {
            @Override
            public OutputStream provideOutputStream() throws Exception {
                return new FileOutputStream(destinationFile);
            }
        };
    }

    /**
     * @param cloudPath path to download
     * @param entryId entryId of a file, if you want to download a specyfic version. If null, will download most recent version.
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @param outputStreamProvider {@link OutputStreamProvider} that will provide {@link OutputStream} to write into
     */
    public DownloadFileRequest(String cloudPath, String entryId, ProgressListener progressListener, OutputStreamProvider outputStreamProvider) {
        super("GET", "/pubapi/v1/fs-content", cloudPath, createQueryParamsMap(entryId), null, null);
        this.progressListener = progressListener;
        this.outputStreamProvider = outputStreamProvider;
    }

    private static HashMap<String, Object> createQueryParamsMap(String entryId) {
        HashMap<String, Object> queryParamsMap = new HashMap<>();
        QueryUtils.addIfNotNull("entry_id", entryId, queryParamsMap);
        return queryParamsMap;
    }

    @Override
    protected Void parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) throws ResponseParsingException {
        OutputStream outputStream = null;
        try {
            outputStream = outputStreamProvider.provideOutputStream();
            int n;
            long total = 0;
            byte[] buffer = new byte[2048];
            while ((n = inputStream.read(buffer)) != -1) {
                cancelledState.throwIfCancelled();
                total += n;
                outputStream.write(buffer, 0, n);
                if (progressListener != null) {
                    progressListener.onProgress(total);
                }
            }
            return null;
        } catch (Exception exception) {
            throw new ResponseParsingException(exception);
        } finally {
            CloseableUtils.closeSilently(outputStream);
            CloseableUtils.closeSilently(inputStream);
        }
    }
}
