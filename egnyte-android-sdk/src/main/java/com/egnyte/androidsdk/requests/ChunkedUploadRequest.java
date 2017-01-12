package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.entities.ChunkedUploadResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This class represents <a href="https://developers.egnyte.com/docs/File_System_Management_API_Documentation#Chunked-Upload">Chunked Upload</a> request
 */
public class ChunkedUploadRequest extends BaseRequest<ChunkedUploadResult> {

    private final String cloudPath;
    private final File sourceFile;
    private final long chunkSize;

    /**
     * Returns object representing first chunk of upload
     *
     * @param cloudPath        path to upload to
     * @param sourceFile       file to upload
     * @param chunkSize        size of single chunk, must be between 10485760 (1MB) and 1073741824 (1GB)
     * @param checksum         SHA512 hash of the chunk being uploaded, might be null
     * @param lastModified     indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @return {@link ChunkedUploadRequest} representing first chunk
     */
    public static ChunkedUploadRequest firstChunk(String cloudPath, File sourceFile, long chunkSize, String checksum,
                                                  String lastModified, ProgressListener progressListener) {
        return new ChunkedUploadRequest(cloudPath, 1,
                sourceFile, chunkSize, null,
                checksum, lastModified, null, progressListener);
    }

    /**
     * Returns subsequent chunk of upload based on given {@link ChunkedUploadRequest}
     *
     * @param prevResult       result of uploading previous chunk
     * @param chunkNumber      chunk number, 1-indexed
     * @param checksum         SHA512 hash of the chunk being uploaded, might be null
     * @param lastModified     indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @return {@link ChunkedUploadRequest} representing subsequent chunk
     */
    public static ChunkedUploadRequest subsequentChunk(ChunkedUploadResult prevResult, int chunkNumber, String checksum,
                                                       String lastModified, ProgressListener progressListener) {
        return new ChunkedUploadRequest(prevResult.cloudPath, chunkNumber,
                prevResult.sourceFile, prevResult.chunkSize, prevResult.uploadId,
                checksum, lastModified, null, progressListener);
    }

    /**
     * Returns last chunk of upload based on given {@link ChunkedUploadRequest}
     *
     * @param prevResult       result of uploading previous chunk
     * @param chunkNumber      chunk number, 1-indexed
     * @param checksum         SHA512 hash of the chunk being uploaded, might be null
     * @param lastModified     indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     * @return
     */
    public static ChunkedUploadRequest lastChunk(ChunkedUploadResult prevResult, int chunkNumber, String checksum,
                                                 String lastModified, ProgressListener progressListener) {
        return new ChunkedUploadRequest(prevResult.cloudPath, chunkNumber,
                prevResult.sourceFile, prevResult.chunkSize, prevResult.uploadId,
                checksum, lastModified, true, progressListener);
    }

    /**
     * Consider using {@link #firstChunk(String, File, long, String, String, ProgressListener)} ,
     * {@link #subsequentChunk(ChunkedUploadResult, int, String, String, ProgressListener)} and
     * {@link #lastChunk(ChunkedUploadResult, int, String, String, ProgressListener)} instead
     *
     * @param cloudPath        path to upload to
     * @param chunkNumber      chunk number, 1-indexed
     * @param sourceFile       file to upload
     * @param chunkSize        size of single chunk, must be between 10485760 (1MB) and 1073741824 (1GB)
     * @param uploadId         upload id, obtained form {@link ChunkedUploadResult} of first chunk
     * @param checksum         SHA512 hash of the chunk being uploaded, might be null
     * @param lastModified     indicates last modified date for file. If omitted, the current time will be used as the last modified date, might be null. E.g. Sun, 26 Aug 2012 03:55:29 GMT
     * @param lastChunk        flag indicating if that's the last chunk
     * @param progressListener {@link ProgressListener} that will be notified, might be null. Note that {@link ProgressListener#onProgress(long)} will be called on the same thread that's executing request
     */
    public ChunkedUploadRequest(String cloudPath, int chunkNumber, File sourceFile, long chunkSize,
                                String uploadId, String checksum, String lastModified, Boolean lastChunk,
                                ProgressListener progressListener) {
        super("POST", "/pubapi/v1/fs-content-chunked", cloudPath, null,
                createHeaders(chunkNumber, checksum, uploadId, lastModified, lastChunk),
                new UploadRequestBody(
                        new FileInputStreamProvider(sourceFile),
                        progressListener,
                        (chunkNumber - 1) * chunkSize,
                        chunkSize)
        );
        this.cloudPath = cloudPath;
        this.sourceFile = sourceFile;
        this.chunkSize = chunkSize;
    }

    private static HashMap<String, String> createHeaders(int chunkNumber, String checksum, String uploadId,
                                                         String lastModified, Boolean lastChunk) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("X-Egnyte-Chunk-Num", String.valueOf(chunkNumber));
        if (checksum != null) {
            headers.put("X-Egnyte-Chunk-Sha512-Checksum", checksum.toLowerCase(Locale.US));
        }
        if (uploadId != null) {
            headers.put("X-Egnyte-Upload-Id", uploadId);
        }
        if (lastChunk != null) {
            headers.put("X-Egnyte-Last-Chunk", String.valueOf(lastChunk));
        }
        if (lastModified != null) {
            headers.put("Last-Modified", lastModified);
        }
        return headers;
    }

    @Override
    protected ChunkedUploadResult parseResponseBody(InputStream inputStream, CancelledState cancelledState,
                                                    Map<String, List<String>> headers) throws IOException, ResponseParsingException {
        String chunkNumString = null;
        String uploadId = null;
        try {
            uploadId = extractHeaderValue("X-Egnyte-Upload-Id", headers);
            chunkNumString = extractHeaderValue("X-Egnyte-Chunk-Num", headers);
        } catch (ResponseParsingException ignore) {
        }
        String checksum = extractHeaderValue("X-Egnyte-Chunk-Sha512-Checksum", headers);
        Integer chunkNum = null;
        if (chunkNumString != null) {
            try {
                chunkNum = Integer.parseInt(chunkNumString);
            } catch (NumberFormatException ex) {
                throw new ResponseParsingException(ex);
            }
        }
        return new ChunkedUploadResult(cloudPath, sourceFile, chunkSize, uploadId, chunkNum, checksum);
    }

    String extractHeaderValue(String name, Map<String, List<String>> headers) throws ResponseParsingException {
        List<String> list = headers.get(name);
        if (list == null || list.isEmpty()) {
            throw new ResponseParsingException(name + " header not found");
        }
        return list.get(0);
    }
}
