package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class DeleteRequestTest {

    private URL requestUrl;
    private String requestMethod;

    @Before
    public void setup() {
        requestUrl = null;
        requestMethod = null;
    }

    @Test
    public void testDeleteRequest() throws IOException {
        TestApiClient client = new TestApiClient(new URL("https://mycloud.egnyte.com"), new URLConnectionFactory() {
            @Override
            public HttpURLConnection httpUrlConnection(URL url) throws IOException {
                requestUrl = url;
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
                    public void setRequestMethod(String method) throws ProtocolException {
                        requestMethod = method;
                    }

                    @Override
                    public InputStream getInputStream() throws IOException {
                        return new InputStream() {
                            @Override
                            public int read() throws IOException {
                                return -1;
                            }
                        };
                    }
                };
            }
        });
        client.execute(new DeleteRequest("/Shared/Delete Test/delete me"));

        Assert.assertEquals(new URL("https://mycloud.egnyte.com/pubapi/v1/fs/Shared/Delete%20Test/delete%20me"), requestUrl);
        Assert.assertEquals("DELETE", requestMethod);
    }
}