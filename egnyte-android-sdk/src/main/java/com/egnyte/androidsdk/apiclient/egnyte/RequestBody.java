package com.egnyte.androidsdk.apiclient.egnyte;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Represents request body
 */
public interface RequestBody {

    /**
     * Get content type
     * @return content type
     */
    String getContentType();

    /**
     * Writes content of body into {@link OutputStream}
     * @param cancelledState object controlling whether writing should be cancelled
     * @param os {@link OutputStream} to write contents of request body into
     * @throws IOException
     */
    void writeContent(CancelledState cancelledState, OutputStream os) throws IOException;
}
