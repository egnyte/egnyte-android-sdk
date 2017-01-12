package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadFileRequestTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private TestApiClient testApiClient;
    byte[] cloudFileContent;
    byte[] error;
    private int stubbedResponseCode;

    @Before
    public void setup() throws MalformedURLException {
        testApiClient = new TestApiClient(new URL("https://mycloud.egnyte.com"), new URLConnectionFactory() {
            @Override
            public HttpURLConnection httpUrlConnection(URL url) throws IOException {
                return new MockHttpUrlConnection(url);
            }
        });
        error = null;
        cloudFileContent = null;
        stubbedResponseCode = -1;
    }

    @Test
    public void testSimpleDownload() throws Exception {
        cloudFileContent = "Hello world".getBytes();
        File downloaded = new File(temporaryFolder.getRoot(), "downloadme.txt");
        DownloadFileRequest simpleDownloadRequest = new DownloadFileRequest("/Shared/Files/downloadme.txt", null, null, downloaded);
        testApiClient.execute(simpleDownloadRequest);
        compareFile(downloaded, "Hello world".getBytes());
    }

    @Test
    public void testSimpleDownloadWithListener() throws Exception {
        cloudFileContent = new byte[8000];
        for (int i = 0; i < cloudFileContent.length; ++i) {
            cloudFileContent[i] = (byte) i;
        }
        final long[] progressNotifications = new long[4];
        File downloaded = new File(temporaryFolder.getRoot(), "downloadme.txt");
        DownloadFileRequest simpleDownloadRequest = new DownloadFileRequest("/Shared/Files/downloadme.txt", null, new ProgressListener() {
            int progresNotificationIndex = 0;

            @Override
            public void onProgress(long bytesTotal) {
                progressNotifications[progresNotificationIndex++] = bytesTotal;
            }
        }, downloaded);
        testApiClient.execute(simpleDownloadRequest);
        compareFile(downloaded, cloudFileContent);
        Assert.assertArrayEquals(new long[]{2048L, 4096L, 6144L, 8000L}, progressNotifications);
    }

    private void compareFile(File first, byte[] cloudFileContent) throws IOException {
        int size = (int) first.length();
        byte[] downloadedFile = new byte[size];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(first);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            dataInputStream.readFully(downloadedFile);
            Assert.assertArrayEquals(cloudFileContent, downloadedFile);
        } finally {
            CloseableUtils.closeSilently(fileInputStream);
        }
    }

    private class MockHttpUrlConnection extends HttpURLConnection {
        public MockHttpUrlConnection(URL url) {
            super(url);
        }

        @Override
        public void connect() throws IOException {

        }

        @Override
        public int getResponseCode() throws IOException {
            return stubbedResponseCode;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (error != null) {
                throw new IOException();
            }
            return new ByteArrayInputStream(cloudFileContent);
        }

        @Override
        public InputStream getErrorStream() {
            if (error == null) {
                return null;
            }
            return new ByteArrayInputStream(error);
        }

        @Override
        public void disconnect() {
        }

        @Override
        public boolean usingProxy() {
            return false;
        }
    }
}