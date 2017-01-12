package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Interface for parsing errors in failed requests
 */
public interface ConnectionErrorParser {

    public IOException parse(HttpURLConnection connection, CancelledState cancelledState);
}
