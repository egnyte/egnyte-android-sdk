package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.EgnyteException;
import com.egnyte.androidsdk.entities.ChunkedUploadResult;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkedUploadRequestTest {

    private static final String CHECKSUM_VALUE = "checksum-value";
    private static final String LAST_MODIFIED_VALUE = "Sun, 26 Aug 2012 03:55:29 GMT";
    private static final String UPLOAD_ID_VALUE = "upload-id-value";
    private static final long CHUNK_SIZE = 10485760L;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private byte[] response;
    private TestApiClient client;
    private ByteArrayOutputStream sink;
    private HashMap<String, String> requestHeaders = new HashMap<>();
    private File tempFile;
    private Map<String, List<String>> responseHeaderFields = new HashMap<>();

    @Before
    public void setup() throws IOException {
        response = null;
        requestHeaders.clear();
        sink = new ByteArrayOutputStream();
        tempFile = new File(temporaryFolder.getRoot(), "file.txt");
        FileWriter fileWriter = new FileWriter(tempFile);
        fileWriter.write("Hello world!");
        response = "Hello world!".getBytes();
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

                    @Override
                    public Map<String, List<String>> getHeaderFields() {
                        return responseHeaderFields;
                    }
                };
            }
        });
    }

    @Test
    public void testParseHeadersInFirstChunk() throws Exception {
        responseHeaderFields.put("X-Egnyte-Upload-Id", listWithValue(UPLOAD_ID_VALUE));
        responseHeaderFields.put("X-Egnyte-Chunk-Num", listWithValue("1"));
        responseHeaderFields.put("X-Egnyte-Chunk-Sha512-Checksum", listWithValue(CHECKSUM_VALUE));

        File tempFile = new File(temporaryFolder.getRoot(), "file.txt");
        ChunkedUploadRequest chunkedUploadRequest = ChunkedUploadRequest.firstChunk(
                "/Shared/Upload Test/file.txt",
                tempFile,
                CHUNK_SIZE,
                CHECKSUM_VALUE,
                LAST_MODIFIED_VALUE,
                null
        );
        client.execute(chunkedUploadRequest);
        Assert.assertEquals(LAST_MODIFIED_VALUE, requestHeaders.get("Last-Modified"));
        Assert.assertEquals(CHECKSUM_VALUE, requestHeaders.get("X-Egnyte-Chunk-Sha512-Checksum"));
        Assert.assertEquals("1", requestHeaders.get("X-Egnyte-Chunk-Num"));
        Assert.assertNull("2", requestHeaders.get("X-Egnyte-Upload-Id"));
        Assert.assertNull(requestHeaders.get("X-Egnyte-Last-Chunk"));
    }

    private List<String> listWithValue(String uploadIdValue) {
        ArrayList<String> result = new ArrayList<>();
        result.add(uploadIdValue);
        return result;
    }

    @Test
    public void testParseHeadersInLastChunk() throws Exception {
        ChunkedUploadRequest chunkedUploadRequest = ChunkedUploadRequest.lastChunk(new ChunkedUploadResult(
                "/Shared/Upload Test/file.txt",
                tempFile,
                10485760L,
                UPLOAD_ID_VALUE,
                2,
                CHECKSUM_VALUE
        ), 2, null, null, null);
        try {
            client.execute(chunkedUploadRequest);
        } catch (EgnyteException ex) {
            Assert.assertEquals("X-Egnyte-Chunk-Sha512-Checksum header not found", ex.getCause().getMessage());
        }

        Assert.assertNull(requestHeaders.get("Last-Modified"));
        Assert.assertNull(requestHeaders.get("X-Egnyte-Chunk-Sha512-Checksum"));
        Assert.assertEquals("2", requestHeaders.get("X-Egnyte-Chunk-Num"));
        Assert.assertEquals(UPLOAD_ID_VALUE, requestHeaders.get("X-Egnyte-Upload-Id"));
        Assert.assertEquals("true", requestHeaders.get("X-Egnyte-Last-Chunk"));
    }
}