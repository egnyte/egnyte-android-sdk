package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

abstract class AbsBaseRequest<Result> {

    abstract void prepare(URLConnectionFactory urlConnectionFactory, URL baseURL, ConnectionErrorParser absErrorHandler) throws IOException;

    abstract HttpURLConnection getConnection();

    abstract Result execute(CancelledState cancelledState) throws IOException;
}
