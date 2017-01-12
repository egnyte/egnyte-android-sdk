package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.CloseableUtils;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.RequestCancelledException;
import com.egnyte.androidsdk.entities.UploadResult;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadRequestTest {

    private static final String CHECKSUM = "6c206cdc62c1bebe39549646ab6035e5f16edb2691e878cbf57291705f734bf9d1248f81c3b56fd0731298ace529c47d25a6e948a2d71d329d44c2898f07e706";
    private static final String TIMESTAMP = "Sun, 26 Aug 2012 03:55:29 GMT";
    private static final String UPLOAD_PATH = "/Shared/Upload Test/42.txt";

    private ByteArrayOutputStream sink;
    private byte[] response;
    private HashMap<String, String> requestHeaders = new HashMap<>();
    private TestApiClient client;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setup() throws MalformedURLException {
        response = null;
        requestHeaders.clear();
        sink = new ByteArrayOutputStream();
        client = new TestApiClient(new URL("https://mycloud.egnyte.com"), new URLConnectionFactory() {
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
                        if (response == null) {
                            throw new IOException();
                        }
                        return new ByteArrayInputStream(response);
                    }

                    @Override
                    public void setRequestProperty(String key, String value) {
                        super.setRequestProperty(key, value);
                        requestHeaders.put(key, value);
                    }

                    @Override
                    public OutputStream getOutputStream() throws IOException {
                        return sink;
                    }
                };
            }
        });
    }

    @Test
    public void testFailingRequestBody() {
        UploadRequest uploadRequest = new UploadRequest(UPLOAD_PATH, new InputStreamProvider() {
            @Override
            public InputStream provideInputStream() throws IOException {
                return null;
            }
        }, null, null, null);

        try {
            client.execute(uploadRequest);
            Assert.fail("Request should have been failed");
        } catch (EgnyteException apiException) {
            Assert.assertEquals("java.io.IOException: Provided InputStream was null", apiException.getMessage());
        } catch (Exception e) {
            Assert.fail("Unexpected exception: " + e);
        }
    }

    @Test
    public void testWithCancellation() {
        final CancelledState cancellationQuard = new CancelledState();
        UploadRequest uploadRequest = new UploadRequest(UPLOAD_PATH, new InputStreamProvider() {
            @Override
            public InputStream provideInputStream() throws IOException {
                cancellationQuard.setCancelled();
                return new ByteArrayInputStream(new byte[4092]);
            }
        }, null, null, null);

        try {
            client.execute(uploadRequest, cancellationQuard);
            Assert.fail("Request should have been cancelled");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof EgnyteException);
            Assert.assertTrue(e.getCause() != null);
            Assert.assertTrue(e.getCause() instanceof RequestCancelledException);
        }
    }

    @Test
    public void testHeaders() throws IOException, JSONException {
        File tempFile = prepareFile("ignore.txt");
        UploadRequest uploadRequest = new UploadRequest(UPLOAD_PATH, tempFile, null, CHECKSUM, TIMESTAMP);

        try {
            client.execute(uploadRequest);
        } catch (IOException ignore) {
        }

        Assert.assertEquals(CHECKSUM, requestHeaders.get("X-Sha512-Checksum"));
        Assert.assertEquals(TIMESTAMP, requestHeaders.get("Last-Modified"));
    }

    @Test
    public void testSuccessful() throws IOException {
        File tempFile = prepareFile("42.txt");
        response = ("{" +
                "\"checksum\": \"6c206cdc62c1bebe39549646ab6035e5f16edb2691e878cbf57291705f734bf9d1248f81c3b56fd0731298ace529c47d25a6e948a2d71d329d44c2898f07e706\"," +
                "\"group_id\": \"6e434e84-152d-45f3-a57a-f4af36814fc9\"," +
                "\"entry_id\": \"802055e8-6300-4f3b-be10-d8a05ad2692a\"" +
                "}").getBytes();
        ArrayList<Long> notifiedProgress = new ArrayList<>();

        UploadRequest uploadRequest = new UploadRequest(UPLOAD_PATH, tempFile, new ProgressListener() {
            @Override
            public void onProgress(long bytesTotal) {

            }
        });
        UploadResult result = client.execute(uploadRequest);

        Assert.assertEquals(result.checksum, CHECKSUM);
        Assert.assertEquals(result.groupId, "6e434e84-152d-45f3-a57a-f4af36814fc9");
        Assert.assertEquals(result.entryId, "802055e8-6300-4f3b-be10-d8a05ad2692a");

        Assert.assertEquals(null, requestHeaders.get("X-Sha512-Checksum"));
        Assert.assertEquals(null, requestHeaders.get("Last-Modified"));

        for (int i = 1; i < notifiedProgress.size(); ++i) {
            Assert.assertTrue(notifiedProgress.get(i - 1) < notifiedProgress.get(i));
        }
    }

    private File prepareFile(String filename) throws IOException {
        final File tempFile = new File(temporaryFolder.getRoot(), filename);
        FileOutputStream fileOutputSteram = null;
        try {
            fileOutputSteram = new FileOutputStream(tempFile);
            for (int i = 0; i < 42 * 42; ++i) {
                fileOutputSteram.write("42☃\n".getBytes());
            }
            fileOutputSteram.close();
        } finally {
            CloseableUtils.closeSilently(fileOutputSteram);
        }
        return tempFile;
    }
}