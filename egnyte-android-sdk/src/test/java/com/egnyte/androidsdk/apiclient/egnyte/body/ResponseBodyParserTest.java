package com.egnyte.androidsdk.apiclient.egnyte.body;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;

import junit.framework.Assert;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResponseBodyParserTest {
    @Test
    public void parseErrorMessage() throws Exception {
        CancelledState letItGo = new LetItGo();
        Assert.assertEquals("Unknown error", ResponseBodyParser.parseErrorMessage(null, letItGo));

        InputStream emptyStream = new ByteArrayInputStream(new byte[]{});
        Assert.assertEquals("Unknown error", ResponseBodyParser.parseErrorMessage(emptyStream, letItGo));

        InputStream throwingInputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException();
            }
        };
        Assert.assertEquals("Unknown error", ResponseBodyParser.parseErrorMessage(throwingInputStream, letItGo));

        InputStream unexpectedJson = new ByteArrayInputStream("\"msg\":\"Something went wrong\"".getBytes());
        Assert.assertEquals("\"msg\":\"Something went wrong\"", ResponseBodyParser.parseErrorMessage(unexpectedJson, letItGo));

        InputStream validJsonErrorMessage = new ByteArrayInputStream("{\"errorMessage\": \"Sorry\"}".getBytes());
        Assert.assertEquals("Sorry", ResponseBodyParser.parseErrorMessage(validJsonErrorMessage, letItGo));

        InputStream validJsonMessage = new ByteArrayInputStream("{\"message\": \"Sorry\"}".getBytes());
        Assert.assertEquals("Sorry", ResponseBodyParser.parseErrorMessage(validJsonMessage, letItGo));
    }

    @Test
    public void parseString() throws Exception {
        InputStream inputStream = new ByteArrayInputStream("Hello world!".getBytes());
        String parsed = ResponseBodyParser.parseString(inputStream, new LetItGo());
        Assert.assertEquals("Hello world!", parsed);
    }

}