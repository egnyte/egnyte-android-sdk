package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.RequestMiddleware;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

abstract class AbsClient {

    private final URL baseUrl;
    private final URLConnectionFactory connectionsFactory;
    private final ArrayList<RequestMiddleware> requestMiddlewares;
    private final ConnectionErrorParser absErrorHandler;

    public AbsClient(URL baseUrl, URLConnectionFactory connectionsFactory, ArrayList<RequestMiddleware> requestMiddlewares, ConnectionErrorParser absErrorHandler) {
        this.baseUrl = baseUrl;
        this.connectionsFactory = connectionsFactory;
        this.requestMiddlewares = requestMiddlewares;
        this.absErrorHandler = absErrorHandler;
    }

    private void applyDecorators(HttpURLConnection connection) {
        for (RequestMiddleware requestMiddleware : requestMiddlewares) {
            requestMiddleware.apply(connection);
        }
    }

    <Result> Result execute(AbsBaseRequest<Result> request, CancelledState cancelledState) throws IOException {
        request.prepare(connectionsFactory, baseUrl, absErrorHandler);
        applyDecorators(request.getConnection());
        return request.execute(cancelledState);
    }
}
