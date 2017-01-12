package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class UploadRequestBody implements RequestBody {

    private final InputStreamProvider inputStreamProvider;
    private final ProgressListener progressListener;
    private final Long skip;
    private final Long limit;

    public UploadRequestBody(InputStreamProvider inputStreamProvider, ProgressListener progressListener) {
        this(inputStreamProvider, progressListener, 0L, Long.MAX_VALUE);
    }

    public UploadRequestBody(InputStreamProvider inputStreamProvider, ProgressListener progressListener, Long startAt,
                             Long writtenBytesCountLimit) {
        this.inputStreamProvider = inputStreamProvider;
        this.progressListener = progressListener;
        this.skip = startAt;
        this.limit = writtenBytesCountLimit;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public void writeContent(CancelledState cancelledState, OutputStream os) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = inputStreamProvider.provideInputStream();
            if (inputStream == null) {
                throw new IOException("Provided InputStream was null");
            }
            byte[] buffer = new byte[2048];
            long total = 0;
            long remaining = limit;
            int n, len;
            inputStream.skip(skip);
            while ((n = inputStream.read(buffer)) != -1) {
                cancelledState.throwIfCancelled();
                len = (int) Math.min(n, remaining);
                os.write(buffer, 0, len);
                remaining -= len;
                total += len;
                if (progressListener != null) {
                    progressListener.onProgress(total);
                }
            }
        } finally {
            CloseableUtils.closeSilently(inputStream);
        }
    }
}
