package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.DailyQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.RequestCancelledException;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is base class for implementing request. Use it to perform tasks which do not have dedicated requests.
 *
 * @param <Result> Class of successfull response
 */
public abstract class BaseRequest<Result> extends AbsBaseRequest<Result> {

    private final String method;
    private final String apiPath;
    private final String cloudPath;
    private final HashMap<String, Object> queryParams;
    private final HashMap<String, String> headers;
    private final RequestBody body;

    private HttpURLConnection connection;
    private ConnectionErrorParser errorParser;

    private ConnectionErrorParser customErrorParser;

    /**
     * @param method      HTTP method
     * @param path        API path
     * @param cloudPath   path in Egnyte cloud, may be null
     * @param queryParams query parameters, may be null
     * @param headers     headers, may be null
     * @param body        body represented by {@link RequestBody}, may be null
     */
    public BaseRequest(String method, String path, String cloudPath, HashMap<String, Object> queryParams, HashMap<String, String> headers, RequestBody body) {
        this.method = method;
        this.apiPath = path;
        this.cloudPath = neutralizeCloudPath(cloudPath);
        this.queryParams = queryParams;
        this.headers = headers;
        this.body = body;
    }

    /**
     * Set custom error parser. If it's {@link ConnectionErrorParser#parse(HttpURLConnection, CancelledState)}
     * implementation returns null, default error parsing will be applied.
     * @param errorParser Custom {@link ConnectionErrorParser}. If null, default error parsing will be applied.
     * @return this request
     */
    public BaseRequest<Result> setCustomErrorParser(ConnectionErrorParser errorParser) {
        this.customErrorParser = errorParser;
        return this;
    }

    private String neutralizeCloudPath(String cloudPath) {
        return "/".equals(cloudPath) ? "" : cloudPath;
    }

    @Override
    void prepare(URLConnectionFactory urlConnectionFactory, URL baseURL, ConnectionErrorParser errorHandler) throws IOException {
        HttpURLConnection connection = urlConnectionFactory.httpUrlConnection(createURL(baseURL));
        connection.setAllowUserInteraction(false);
        connection.setUseCaches(false);
        connection.setRequestMethod(method);
        if (headers != null) {
            for (Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        this.errorParser = errorHandler;
        this.connection = connection;
    }

    @Override
    HttpURLConnection getConnection() {
        return connection;
    }

    @Override
    Result execute(CancelledState cancelledState) throws EgnyteException, RequestCancelledException {
        if (cancelledState == null) {
            cancelledState = new LetItGo();
        }
        InputStream inputStream = null;
        try {
            setBodyIfPresent(cancelledState);
            inputStream = getInputStream(connection, cancelledState, customErrorParser, errorParser);
            Result result = parseResponseBody(inputStream, cancelledState, connection.getHeaderFields());
            return result;
        } catch (EgnyteException e) {
            throw e;
        } catch (ResponseParsingException e) {
            throw new EgnyteException(e);
        } catch (IOException e) {
            throw new EgnyteException(e);
        } finally {
            CloseableUtils.closeSilently(inputStream);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Parses response body into object representing result.
     *
     * @param inputStream    {@link InputStream} to parse
     * @param cancelledState {@link CancelledState} controlling if parsing should be cancelled
     * @param headers        reponse headers
     * @return object representing succesful response
     * @throws IOException              if reading from stream fails
     * @throws ResponseParsingException if parsing fails
     */
    protected abstract Result parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) throws IOException, ResponseParsingException;

    static InputStream getInputStream(HttpURLConnection connection, CancelledState cancelledState, ConnectionErrorParser priorityErrorParser, ConnectionErrorParser errorHandler) throws IOException, CallsPerSecondQuotaExceeded, DailyQuotaExceeded {
        InputStream inputStream = null;

        cancelledState.throwIfCancelled();
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            if (priorityErrorParser != null) {
                IOException customError = priorityErrorParser.parse(connection, cancelledState);
                if (customError != null) {
                    throw customError;
                }
            }
            throw errorHandler.parse(connection, cancelledState);
        }
        return inputStream;
    }

    private URL createURL(URL baseURL) throws MalformedURLException {
        StringBuilder sb = new StringBuilder();
        sb.append(baseURL.toString());
        sb.append(apiPath);
        if (cloudPath != null) {
            try {
                String[] cloudPathParts = cloudPath.split("/");
                for (String cloudPart : cloudPathParts) {
                    if (sb.charAt(sb.length() - 1) != '/') {
                        sb.append('/');
                    }
                    StringBuilder filenameBuilder = new StringBuilder();
                    String[] spaceSplitted = cloudPart.split(" ");
                    for (String s : spaceSplitted) {
                        if (filenameBuilder.length() != 0) {
                            filenameBuilder.append("%20");
                        }
                        filenameBuilder.append(URLEncoder.encode(s, "UTF-8"));
                    }
                    sb.append(filenameBuilder);
                }
            } catch (UnsupportedEncodingException ignore) {
            }
        }
        if (queryParams != null) {
            boolean queryParamPresent = false;
            for (Entry<String, Object> queryParam : queryParams.entrySet()) {
                if (queryParamPresent) {
                    sb.append("&");
                } else {
                    queryParamPresent = true;
                    sb.append("?");
                }
                sb.append(queryParam.getKey()).append("=").append(queryParam.getValue());
            }
        }
        return new URL(sb.toString());
    }

    private void setBodyIfPresent(CancelledState cancelledState) throws IOException {
        OutputStream os = null;
        try {
            if (body != null) {
                getConnection().setDoOutput(true);
                getConnection().setRequestProperty("Content-Type", body.getContentType());

                os = getConnection().getOutputStream();
                body.writeContent(cancelledState, os);
            }
        } finally {
            CloseableUtils.closeSilently(os);
        }
    }
}
