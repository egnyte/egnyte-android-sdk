package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadRequestBodyTest {

    @Test
    public void testChunks() throws IOException {
        byte[] source = new byte[4096];
        for (int i = 0; i < source.length; ++i) {
            source[i] = (byte) (i / 1024);
        }
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
        final InputStreamProvider provider = new InputStreamProvider() {
            @Override
            public InputStream provideInputStream() throws IOException {
                return inputStream;
            }
        };
        UploadRequestBody uploadRequestBody = new UploadRequestBody(provider, null, 1024L, 2400L);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        uploadRequestBody.writeContent(new LetItGo(), byteArrayOutputStream);
        byte[] expected = new byte[2400];
        for (int i = 0; i < expected.length; ++i) {
            expected[i] = (byte) (i / 1024 + 1);
        }

        Assert.assertArrayEquals(expected, byteArrayOutputStream.toByteArray());
    }
}