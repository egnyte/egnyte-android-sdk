package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;

/**
 * This class is used for configuring {@link APIClient}
 */
public class ApiClientConfig {
    private final int connectTimeout;
    private final int readTimeout;
    private final int maxRetryCount;


    /**
     *
     * @param connectTimeout connect timeout used for every request
     * @param readTimeout read timeout used for every request
     * @param maxRetryCount maximum number of retries in case of {@link CallsPerSecondQuotaExceeded}
     */
    public ApiClientConfig(int connectTimeout, int readTimeout, int maxRetryCount) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.maxRetryCount = maxRetryCount;
    }

    /**
     * Get connect timeout
     * @return
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Get read timeout
     * @return
     */
    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * Get maximum number of retries in case of {@link CallsPerSecondQuotaExceeded}
     * @return
     */
    public int getMaxQPSRejectionCount() {
        return maxRetryCount;
    }
}
