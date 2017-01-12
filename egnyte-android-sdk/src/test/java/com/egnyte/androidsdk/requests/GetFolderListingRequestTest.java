package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class GetFolderListingRequestTest {

    @Test
    public void testUrlWithoutParameters() throws IOException {
        GetFolderListingRequest getFolderListingRequest = new GetFolderListingRequest("/Shared");
        TestApiClient testApiClient = new TestApiClient(new URL("https://domain.egnyte.com"), new ConnectionFactory());
        HttpURLConnection preparedUrlConnection = testApiClient.getPreparedUrlConnection(getFolderListingRequest);
        Assert.assertEquals(
                new URL("https://domain.egnyte.com/pubapi/v1/fs/Shared?list_content=true"),
                preparedUrlConnection.getURL()
        );
    }

    @Test
    public void testUrlWithParameters() throws IOException {
        GetFolderListingRequest getFolderListingRequest = new GetFolderListingRequest(
                "/Shared/encode me",
                42,
                0,
                GetFolderListingRequest.SortBy.NAME,
                GetFolderListingRequest.SortDirection.ASCENDING
        );
        TestApiClient testApiClient = new TestApiClient(new URL("https://domain.egnyte.com"), new ConnectionFactory());
        URL expectedURL = new URL(
                "https://domain.egnyte.com/pubapi/v1/fs/Shared/encode%20me" +
                        "?list_content=true" +
                        "&count=42" +
                        "&offset=0" +
                        "&sort_by=name" +
                        "&sort_direction=ascending"
        );
        String[] expected = expectedURL.getQuery().split("&");
        Arrays.sort(expected);

        String[] actual = testApiClient.getPreparedUrlConnection(getFolderListingRequest).getURL().getQuery().split("&");
        Arrays.sort(actual);
        Assert.assertArrayEquals(expected, actual);
    }

    private class ConnectionFactory implements URLConnectionFactory {

        @Override
        public HttpURLConnection httpUrlConnection(URL url) throws IOException {
            return (HttpURLConnection) url.openConnection();
        }
    }
}