package com.egnyte.androidsdk.apiclient.egnyte.exceptions;

/**
 * A DailyQuotaExceeded is thrown when daily quota is exceeded
 */
public class DailyQuotaExceeded extends EgnyteException {

    public DailyQuotaExceeded() {
        super(-1, "Exceeded daily quota");
    }
}
