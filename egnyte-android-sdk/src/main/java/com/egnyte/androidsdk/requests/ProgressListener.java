package com.egnyte.androidsdk.requests;

/**
 * Interface monitoring progress
 */
public interface ProgressListener {

    /**
     * Notifies progress
     * @param bytesTotal total bytes transferred
     */
    public void onProgress(long bytesTotal);
}
