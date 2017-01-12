package com.egnyte.androidsdk.apiclient.egnyte.client;

import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BaseRequestTest {

    @Test
    public void testPrepare() throws Exception {
        BaseRequest<Void> get = new BaseRequest<Void>("GET", "/pubapi/v1/fs/", "Shared/encode  me/!@#", null, null, null) {

            @Override
            protected Void parseResponseBody(InputStream inputStream, CancelledState cancellationGuard, Map<String, List<String>> headers) throws IOException {
                return null;
            }
        };
        get.prepare(new URLConnectionFactoryImpl(), new URL("https://mycloud.egnyte.com"), null);

        URL expectedGetURL = new URL("https://mycloud.egnyte.com/pubapi/v1/fs/Shared/encode%20%20me/%21%40%23");
        assertEquals("URL creation went wrong", expectedGetURL, get.getConnection().getURL());
        assertEquals("Invalid connection settings", false, get.getConnection().getUseCaches());
        assertEquals("Invalid connection settings", false, get.getConnection().getAllowUserInteraction());
        assertEquals("Invalid connection settings", 0L, get.getConnection().getIfModifiedSince());
        assertEquals("Invalid method name", "GET", get.getConnection().getRequestMethod());
    }

    @Test
    public void testExecute() throws Exception {
        URL baseURl = new URL("https://mycloud.egnyte.com");
        final URL expectedURL = new URL("https://mycloud.egnyte.com/pubapi");
        final MockHttpUrlConnection mockedHttpUrlConnection = new MockHttpUrlConnection(
                expectedURL, new ByteArrayInputStream("HTTP/1.0 200 OK".getBytes())
        );
        final byte[] bodyBytes = "{\"action\":\"dance\"}".getBytes();
        URLConnectionFactory mockUrlConnectionFactory = new URLConnectionFactory() {
            @Override
            public HttpURLConnection httpUrlConnection(URL url) throws IOException {
                if (!url.equals(expectedURL)) {
                    throw new IOException("Unexpected URL");
                }
                return mockedHttpUrlConnection;
            }
        };
        BaseRequest<Void> post = new BaseRequest<Void>("POST", "/pubapi", null, null, null, new RequestBody() {
            @Override
            public String getContentType() {
                return "application/json";
            }

            @Override
            public void writeContent(CancelledState cancellationGuard, OutputStream os) throws IOException {
                os.write(bodyBytes);
            }
        }) {
            @Override
            protected Void parseResponseBody(InputStream inputStream, CancelledState cancellationGuard, Map<String, List<String>> headers) throws IOException {
                return null;
            }
        };

        post.prepare(mockUrlConnectionFactory, baseURl, null);
        post.execute(null);

        assertEquals("application/json", post.getConnection().getRequestProperty("Content-Type"));
        assertTrue(Arrays.equals(bodyBytes, mockedHttpUrlConnection.baos.toByteArray()));
        assertTrue(mockedHttpUrlConnection.connectCalled);
        assertTrue(mockedHttpUrlConnection.disconnectCalled);
    }

    private class MockHttpUrlConnection extends HttpURLConnection {

        boolean connectCalled = true;
        boolean disconnectCalled = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        InputStream inputStream;

        public MockHttpUrlConnection(URL url, InputStream inputStream) {
            super(url);
            this.inputStream = inputStream;
        }


        @Override
        public InputStream getInputStream() throws IOException {
            return inputStream;
        }

        @Override
        public void connect() throws IOException {
            connectCalled = true;
        }

        @Override
        public void disconnect() {
            disconnectCalled = true;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return baos;
        }

        @Override
        public boolean usingProxy() {
            return false;
        }
    }
}