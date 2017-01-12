package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.RequestMiddleware;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.CallsPerSecondQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.DailyQuotaExceeded;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;
import com.egnyte.androidsdk.auth.egnyte.EgnyteAuthResult;
import com.egnyte.androidsdk.requests.GetFileMetadataRequest;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class APIClientTest {

    @Test
    public void testRetryCount() throws Exception {
        testRetryCount(0);
        testRetryCount(3);
    }

    private void testRetryCount(final int maxRetryCount) throws MalformedURLException {
        final int[] attemptsCounter = {0};
        APIClient APIClient = new APIClient(
                new EgnyteAuthResult("secret-token", new URL("https://mycloud.egnyte.com")),
                0,
                new ApiClientConfig(0, 0, maxRetryCount),
                null,
                new URLConnectionFactory() {
                    @Override
                    public HttpURLConnection httpUrlConnection(URL url) throws IOException {
                        return new HttpURLConnection(url) {
                            @Override
                            public void disconnect() {
                            }

                            @Override
                            public boolean usingProxy() {
                                return false;
                            }

                            @Override
                            public void connect() throws IOException {
                            }

                            @Override
                            public InputStream getInputStream() throws IOException {
                                attemptsCounter[0]++;
                                throw new IOException();
                            }
                        };
                    }
                },
                new ConnectionErrorParser() {

                    @Override
                    public IOException parse(HttpURLConnection connection, CancelledState cancellationGuard) {
                        return new CallsPerSecondQuotaExceeded();
                    }
                },
                null);
        try {
            APIClient.enqueueSync(new BaseRequest<Void>("GET", "/api", null, null, null, null) {
                @Override
                protected Void parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) throws IOException, ResponseParsingException {
                    return null;
                }
            });
        } catch (CallsPerSecondQuotaExceeded ex) {
            Assert.assertEquals(maxRetryCount + 1, attemptsCounter[0]);
        } catch (Exception ex) {
            fail(ex.toString());
        }
    }

    @Test
    public void testMiddlewareDecorators() throws Exception {
        ArrayList<RequestMiddleware> requestMiddlewares = new ArrayList<>();
        requestMiddlewares.add(new RequestMiddleware() {
            @Override
            public void apply(HttpURLConnection connection) {
                connection.setRequestProperty("Custom-Header", "Hello World");
            }
        });
        APIClient client = new APIClient(
                new EgnyteAuthResult("secret-token", new URL("https://mycloud.egnyte.com")), null, null, requestMiddlewares, null, null, null
        );
        final MockURLConnection mockedHttpUrlConnection = new MockURLConnection(new URL("https://mycloud.egnyte.com/api"));
        client.enqueueSync(new AbsBaseRequest<Void>() {
            @Override
            void prepare(URLConnectionFactory urlConnectionFactory, URL baseURL, ConnectionErrorParser absErrorHandler) throws IOException {
            }

            @Override
            HttpURLConnection getConnection() {
                return mockedHttpUrlConnection;
            }

            @Override
            Void execute(CancelledState cancelledState) throws IOException, CallsPerSecondQuotaExceeded, DailyQuotaExceeded {
                return null;
            }
        });

        assertEquals(1, mockedHttpUrlConnection.requestProperties.get("Authorization").size());
        assertEquals("Bearer secret-token", mockedHttpUrlConnection.requestProperties.get("Authorization").get(0));

        assertEquals(1, mockedHttpUrlConnection.requestProperties.get("Custom-Header").size());
        assertEquals("Hello World", mockedHttpUrlConnection.requestProperties.get("Custom-Header").get(0));
    }

    @Test
    public void testErrorParsing() throws Exception {
        APIClient apiClient = new APIClient(
                new EgnyteAuthResult("secret-token", new URL("https://mycloud.egnyte.com")),
                0,
                new ApiClientConfig(0, 0, 0),
                null,
                new URLConnectionFactory() {
                    @Override
                    public HttpURLConnection httpUrlConnection(URL url) throws IOException {
                        return new HttpURLConnection(url) {
                            @Override
                            public void disconnect() {
                            }

                            @Override
                            public boolean usingProxy() {
                                return false;
                            }

                            @Override
                            public void connect() throws IOException {
                            }

                            @Override
                            public InputStream getInputStream() throws IOException {
                                throw new IOException();
                            }

                            @Override
                            public InputStream getErrorStream() {
                                return new ByteArrayInputStream("{\"errorMessage\":\"File does not exist\"}".getBytes());
                            }

                            @Override
                            public int getResponseCode() throws IOException {
                                return 404;
                            }
                        };
                    }
                },
                new APIClient.ErrorParser(),
                null);
        EgnyteException catchedException = null;
        try {
            apiClient.enqueueSync(new GetFileMetadataRequest("/Shared/folder/file.txt"));
        } catch (EgnyteException e) {
            catchedException = e;
        }
        org.junit.Assert.assertNotNull(catchedException);
        org.junit.Assert.assertEquals("File does not exist", catchedException.getApiExceptionMessage());
        org.junit.Assert.assertEquals(404, catchedException.getCode());
    }

    @Test
    public void testGetRootCause() throws Exception {
        EgnyteException root = new EgnyteException(404, "File not found");
        IOException wrapper = new IOException(root);

        IllegalArgumentException anotherRoot = new IllegalArgumentException();
        IOException anotherWrapper = new IOException(anotherRoot);

        assertTrue(root == APIClient.getRootCause(wrapper));
        assertTrue(root == APIClient.getRootCause(root));
        assertTrue(anotherWrapper == APIClient.getRootCause(anotherWrapper));
    }


    private static class MockURLConnection extends HttpURLConnection {

        HashMap<String, ArrayList<String>> requestProperties = new HashMap<>();

        protected MockURLConnection(URL u) {
            super(u);
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public void setRequestProperty(String key, String value) {
            super.setRequestProperty(key, value);
            if (requestProperties.get(key) == null) {
                requestProperties.put(key, new ArrayList<String>());
            }
            requestProperties.get(key).add(value);
        }
    }
}