package com.egnyte.androidsdk.apiclient.egnyte.exceptions;

/**
 * A CallsPerSecondQuotaExceeded is thrown when calls per second quota is exceeded
 */
public class CallsPerSecondQuotaExceeded extends EgnyteException {

    public CallsPerSecondQuotaExceeded() {
        super(-1, "Exceeded queries per seconds quota");
    }
}
