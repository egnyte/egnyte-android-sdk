package com.egnyte.androidsdk.apiclient.egnyte.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Interface that provides {@link HttpURLConnection}, useful for testing
 */
public interface URLConnectionFactory {

    HttpURLConnection httpUrlConnection(URL url) throws IOException;
}
