package com.egnyte.androidsdk.apiclient.egnyte;

import java.net.HttpURLConnection;

/**
 * Interface that might be used to modify outgoing {@link HttpURLConnection]}.
 * {@see {@link com.egnyte.androidsdk.apiclient.egnyte.client.APIClient}}
 */
public interface RequestMiddleware {
    void apply(HttpURLConnection connection);
}
