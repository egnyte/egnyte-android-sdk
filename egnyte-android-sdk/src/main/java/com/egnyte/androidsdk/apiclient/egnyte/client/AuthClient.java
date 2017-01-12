package com.egnyte.androidsdk.apiclient.egnyte.client;


import android.os.Handler;
import android.os.Looper;

import com.egnyte.androidsdk.apiclient.egnyte.Callback;
import com.egnyte.androidsdk.apiclient.egnyte.RequestMiddleware;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.RequestCancelledException;
import com.egnyte.androidsdk.auth.egnyte.ExchangeCodeRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Auth client used internally by {@link com.egnyte.androidsdk.auth.egnyte.EgnyteAuthActivity}
 */
public class AuthClient extends AbsClient {

    private final Executor executor;
    private final Handler handler;

    public AuthClient(URL baseURL) {
        super(baseURL, new URLConnectionFactoryImpl(), timeoutsMiddleware(), new ErrorParser());
        this.executor = Executors.newSingleThreadExecutor();
        this.handler = new Handler(Looper.getMainLooper());
    }

    private static ArrayList<RequestMiddleware> timeoutsMiddleware() {
        ArrayList<RequestMiddleware> requestMiddlewares = new ArrayList<>();
        requestMiddlewares.add(new RequestMiddleware() {
            @Override
            public void apply(HttpURLConnection connection) {
                connection.setConnectTimeout(Defaults.CONNECT_TIMEOUT);
                connection.setReadTimeout(Defaults.READ_TIMEOUT);
            }
        });
        return requestMiddlewares;
    }

    public void execute(final ExchangeCodeRequest exchangeCodeRequest,
                        final CancelledState cancelledState,
                        final Callback<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final String result;
                try {
                    result = execute((AbsBaseRequest<String>) exchangeCodeRequest, cancelledState);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(result);
                        }
                    });
                } catch (RequestCancelledException ignore) {
                } catch (final IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onError(e);
                        }
                    });
                }
            }
        });
    }

    static class ErrorParser implements ConnectionErrorParser {

        @Override
        public IOException parse(HttpURLConnection connection, CancelledState cancelledState) {
            return new IOException();
    }
    }
}
