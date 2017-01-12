package com.egnyte.androidsdk.apiclient.egnyte.client;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.egnyte.androidsdk.apiclient.egnyte.Callback;
import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.RequestMiddleware;
import com.egnyte.androidsdk.apiclient.egnyte.body.ResponseBodyParser;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.DailyQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.RequestCancelledException;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This class is an API Client that can execute requests both synchronously and asynchronously.
 * It also handles throttling of API calls respecting given callsPerSecond parameter.
 */
public class APIClient extends AbsClient {

    private final SemaphoreThrottler throttler;
    private final int maxRejectionCount;
    private final Handler handler;

    /**
     *
     * @param authResult {@link EgnyteAuthResult} obtained by {@link com.egnyte.androidsdk.auth.egnyte.EgnyteAuth#parseResponse(int, int, Intent)}
     * @param callsPerSecond {@link} this parameter is used for throttling requests, if null is passed throttling is disabled
     */
    public APIClient(EgnyteAuthResult authResult, Integer callsPerSeconds) {
        this(authResult, callsPerSeconds, null, null);
    }

    /**
     * @param callsPerSecond {@link} this parameter is used for throttling requests, if null is passed throttling is disabled
     * @param authResult {@link EgnyteAuthResult} obtained by {@link com.egnyte.androidsdk.auth.egnyte.EgnyteAuth#parseResponse(int, int, Intent)}
     * @param apiClientConfig {@link ApiClientConfig}, might be null
     * @param requestMiddlewares {@link List} of {@link RequestMiddleware} that will be used to modify requests objects, might be null
     */
    public APIClient(EgnyteAuthResult authResult, Integer callsPerSeconds, ApiClientConfig apiClientConfig, List<RequestMiddleware> requestMiddlewares) {
        this(authResult, callsPerSeconds, apiClientConfig, requestMiddlewares, new URLConnectionFactoryImpl(), new ErrorParser(), new Handler(Looper.getMainLooper()));
    }

    APIClient(EgnyteAuthResult authResult,
              Integer qps,
              ApiClientConfig apiClientConfig,
              List<RequestMiddleware> requestMiddlewares,
              URLConnectionFactory urlConnectionFactory,
              ConnectionErrorParser errorHandler,
              Handler handler) {
        super(authResult.getEgnyteDomainURL(), urlConnectionFactory,
                middleware(authResult.getAuthToken(), apiClientConfig, requestMiddlewares), errorHandler);
        this.throttler = new SemaphoreThrottler(qps);
        this.maxRejectionCount = apiClientConfig == null ? Defaults.MAX_REJECTION_COUNT : apiClientConfig.getMaxQPSRejectionCount();
        this.handler = handler;
    }

    private static ArrayList<RequestMiddleware> middleware(final String token, final ApiClientConfig apiClientConfig, List<RequestMiddleware> requestMiddlewares) {
        ArrayList<RequestMiddleware> result = new ArrayList<>();
        result.add(new RequestMiddleware() {
            @Override
            public void apply(HttpURLConnection connection) {
                connection.setReadTimeout(apiClientConfig == null ? Defaults.READ_TIMEOUT : apiClientConfig.getReadTimeout());
                connection.setConnectTimeout(apiClientConfig == null ? Defaults.CONNECT_TIMEOUT : apiClientConfig.getConnectTimeout());
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }
        });
        if (requestMiddlewares != null) {
            result.addAll(requestMiddlewares);
        }
        return result;
    }

    /**
     * Enqueues request for asynchronous execution
     * @param request request to execute
     * @param callback callback to call when request succeeds or fails
     * @param <Result> class representing successfull response
     */
    public <Result> void enqueueAsync(final AbsBaseRequest<Result> request, final Callback<Result> callback) {
        enqueueAsync(request, callback, null, null);
    }

    /**
     * Enqueues request for asynchronous execution
     * @param request request to execute
     * @param callback callback to call when request succeeds or fails
     * @param cancelledState {@link CancelledState} representing if request should be cancelled, might be null
     * @param <Result> class representing successfull response
     */
    public <Result> void enqueueAsync(final AbsBaseRequest<Result> request, final Callback<Result> callback, CancelledState cancelledState) {
        enqueueAsync(request, callback, cancelledState, null);
    }

    /**
     * Enqueues request for asynchronous execution
     * @param request request to execute
     * @param callback callback to call when request succeeds or fails
     * @param executor {@link Executor} on which request will be executed, might be null. {@link AsyncTask#THREAD_POOL_EXECUTOR} by default.
     * @param <Result> class representing successfull response
     */
    public <Result> void enqueueAsync(final AbsBaseRequest<Result> request, final Callback<Result> callback, Executor executor) {
        enqueueAsync(request, callback, null, executor);
    }

    /**
     * Enqueues request for asynchronous execution
     * @param request request to execute
     * @param callback callback to call when request succeeds or fails
     * @param cancelledState {@link CancelledState} representing if request should be cancelled, might be null
     * @param executor {@link Executor} on which request will be executed, might be null. {@link AsyncTask#THREAD_POOL_EXECUTOR} by default.
     * @param <Result> class representing successfull response
     */
    public <Result> void enqueueAsync(final AbsBaseRequest<Result> request, final Callback<Result> callback, final CancelledState cancelledState, Executor executor) {
        (executor == null ? defaultExecutor() : executor).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Result response = enqueue(request, cancelledState, 0);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onSuccess(response);
                        }
                    });
                } catch (final IOException e) {
                    if (!(e instanceof RequestCancelledException)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onError(e);
                            }
                        });
                    }
                }
            }
        });
    }

    private Executor defaultExecutor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return threadPoolExecutor();
        } else {
            return Executors.newSingleThreadExecutor();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private Executor threadPoolExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    /**
     * Enqueues request for synchronous execution
     * @param request request to execute
     * @param <Result> class representing successfull response
     * @return object representing succesful response
     * @throws IOException if request fails
     */
    public <Result> Result enqueueSync(final AbsBaseRequest<Result> request) throws IOException {
        return enqueueSync(request, null);
    }

    /**
     * Enqueues request for synchronous execution
     * @param request request to execute
     * @param callback callback to call when request succeeds or fails
     * @param <Result> class representing successfull response
     * @return object representing succesful response
     * @throws IOException if request fails
     */
    public <Result> Result enqueueSync(final AbsBaseRequest<Result> request, CancelledState cancelledState) throws IOException {
        try {
            return enqueue(request, cancelledState, 0);
        } catch (IOException e) {
            throw getRootCause(e);
        }
    }

    private <Result> Result enqueue(AbsBaseRequest<Result> request, CancelledState cancelledState, int tryCount) throws IOException {
        cancelledState = cancelledState == null ? new LetItGo() : cancelledState;
        cancelledState.throwIfCancelled();
        throttler.waitForExecution();
        try {
            cancelledState.throwIfCancelled();
        } catch (RequestCancelledException cancelled) {
            throttler.onTaskCancelled();
            throw cancelled;
        }
        try {
            return execute(request, cancelledState);
        } catch (CallsPerSecondQuotaExceeded rejected) {
            if (tryCount >= maxRejectionCount) {
                throw rejected;
            }
            return enqueue(request, cancelledState, tryCount + 1);
        }
    }

    static IOException getRootCause(IOException ioException) {
        Throwable cause = ioException.getCause();
        if (cause == null) {
            return ioException;
        } else if (cause instanceof IOException) {
            return getRootCause((IOException) cause);
        } else {
            return ioException;
        }
    }

    static class ErrorParser implements ConnectionErrorParser {

        public IOException parse(HttpURLConnection connection, CancelledState cancelledState) {
            if ("ERR_403_DEVELOPER_OVER_QPS".equals(connection.getHeaderField("X-Mashery-Error-Code"))) {
                return new CallsPerSecondQuotaExceeded();
            }
            if ("ERR_403_DEVELOPER_OVER_RATE".equals(connection.getHeaderField("X-Mashery-Error-Code"))) {
                return new DailyQuotaExceeded();
            }
            InputStream errorStream = null;
            try {
                errorStream = connection.getErrorStream();
                return new EgnyteException(connection.getResponseCode(), ResponseBodyParser.parseErrorMessage(errorStream, cancelledState));
            } catch (IOException ex) {
                return ex;
            } finally {
                CloseableUtils.closeSilently(errorStream);
            }
        }
    }
}
