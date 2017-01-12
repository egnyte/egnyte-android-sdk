package com.egnyte.androidsdk.apiclient.egnyte.body;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.LetItGo;

import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

public class JSONBodyTest {
    @Test
    public void getContentType() throws Exception {
        assertEquals("application/json", new JSONBody(new JSONObject()).getContentType());

    }

    @Test
    public void getBytes() throws Exception {
        String json = "{\"greeting\":\"Hello World!\"}";
        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(jsonObject.toString()).thenReturn(json);

        JSONBody jsonBody = new JSONBody(new JSONObject(json));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        jsonBody.writeContent(new LetItGo(), baos);
        assertEquals(json, baos.toString());
    }

}