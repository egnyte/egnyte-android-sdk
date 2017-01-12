package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.RequestMiddleware;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.DailyQuotaExceeded;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TestApiClient extends AbsClient {

    private final URL egnyteDomainURL;
    private final URLConnectionFactory urlConnectionFactory;

    public TestApiClient(URL egnyteDomainURL, URLConnectionFactory urlConnectionFactory) {
        super(egnyteDomainURL, urlConnectionFactory, new ArrayList<RequestMiddleware>(), new ConnectionErrorParser() {
            @Override
            public IOException parse(HttpURLConnection connection, CancelledState cancelledState) {
                return new IOException();
            }
        });
        this.egnyteDomainURL = egnyteDomainURL;
        this.urlConnectionFactory = urlConnectionFactory;
    }

    public <Result> Result execute(AbsBaseRequest<Result> request, CancelledState cancelledState) throws IOException, DailyQuotaExceeded, CallsPerSecondQuotaExceeded {
        return super.execute(request, cancelledState);
    }

    public <Result> Result execute(AbsBaseRequest<Result> request) throws IOException, DailyQuotaExceeded, CallsPerSecondQuotaExceeded {
        return this.execute(request, new LetItGo());
    }

    public HttpURLConnection getPreparedUrlConnection(AbsBaseRequest<?> request) throws IOException {
        request.prepare(urlConnectionFactory, egnyteDomainURL, null);
        return request.getConnection();
    }
}
