package com.egnyte.androidsdk.auth.egnyte;

import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class ExchangeCodeRequestTest {

    @Test
    public void testExchangeCoodeRequest() throws IOException {
        final OutputStream bodyStream = new ByteArrayOutputStream();
        TestApiClient testApiClient = new TestApiClient(UrlUtils.parseUrl("https://mycloud.egnyte.com"), new URLConnectionFactory() {
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
                        return new ByteArrayInputStream("{\"access_token\":\"Hello world\"}".getBytes());
                    }

                    @Override
                    public void setRequestMethod(String method) throws ProtocolException {
                        super.setRequestMethod(method);
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return bodyStream;
                    }
                };
            }
        });
        ExchangeCodeRequest exchangeCodeRequest = new ExchangeCodeRequest(
                "client id value",
                "client secret value",
                "code returned type previous auth step",
                new EgnyteAuthRequest.Scope[]{EgnyteAuthRequest.Scope.FILESYSTEM, EgnyteAuthRequest.Scope.BOOKMARK}
        );

        String accessToken = testApiClient.execute(exchangeCodeRequest);

        Assert.assertEquals("Hello world", accessToken);
        Assert.assertEquals("client_id=client+id+value" +
                        "&client_secret=client+secret+value" +
                        "&redirect_uri=https%3A%2F%2Fegnyte.com" +
                        "&code=code+returned+type+previous+auth+step" +
                        "&grant_type=authorization_codeEgnyte.filesystem Egnyte.bookmark",
                bodyStream.toString());
    }
}