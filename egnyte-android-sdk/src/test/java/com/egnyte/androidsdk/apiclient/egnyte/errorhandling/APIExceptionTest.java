package com.egnyte.androidsdk.apiclient.egnyte.errorhandling;

public class APIExceptionTest {

//    @Test
//    public void parse() throws Exception {
//        final String errorMessage = "Server was too sad to process request.";
//        final String body = "{\"messageError\":\"" + errorMessage +  "\"}";
//
//        Response response = new Response() {
//
//            @Override
//            public int getResponseCode() {
//                return 500;
//            }
//
//            @Override
//            public InputStream getResponseBody() {
//                return new ByteArrayInputStream(body.getBytes());
//            }
//
//            @Override
//            public String getBodyAsString() {
//                return body;
//            }
//
//            @Override
//            public JSONObject getBodyAsJson() throws JSONException {
//                JSONObject mock = Mockito.mock(JSONObject.class);
//                Mockito.when(mock.getString("errorMessage")).thenReturn(errorMessage);
//                return mock;
//            }
//        };
//
//        EgnyteException parsed = EgnyteException.parse(response);
//        Assert.assertEquals(500, parsed.getCode());
//        Assert.assertEquals(errorMessage, parsed.getApiExceptionMessage());
//    }
//
//    @Test
//    public void parseWithNoMessageError() throws Exception {
//        final String errorMessage = "Server was too sad to process request.";
//        final String body = "{\"messageError\":\"" + errorMessage +  "\"}";
//
//        Response response = new Response() {
//
//            @Override
//            public int getResponseCode() {
//                return 500;
//            }
//
//            @Override
//            public InputStream getResponseBody() {
//                return new ByteArrayInputStream(body.getBytes());
//            }
//
//            @Override
//            public String getBodyAsString() {
//                return body;
//            }
//
//            @Override
//            public JSONObject getBodyAsJson() throws JSONException {
//                JSONObject mock = Mockito.mock(JSONObject.class);
//                Mockito.when(mock.getString("errorMessage")).thenThrow(new JSONException(""));
//                return mock;
//            }
//        };
//
//        EgnyteException parsed = EgnyteException.parse(response);
//        Assert.assertEquals("Unknown error", parsed.getApiExceptionMessage());
//        Assert.assertEquals("Unknown error", parsed.getMessage());
//    }
//
//    @Test
//    public void parseEmpty() throws Exception {
//        Response response = new Response() {
//
//            public static final String BODY = "{\"messageError\":\"Server was too sad to process request.\"}";
//
//            @Override
//            public int getResponseCode() {
//                return 500;
//            }
//
//            @Override
//            public InputStream getResponseBody() {
//                return new ByteArrayInputStream(new byte[]{});
//            }
//
//            @Override
//            public String getBodyAsString() {
//                return null;
//            }
//
//            @Override
//            public JSONObject getBodyAsJson() throws JSONException {
//                return null;
//            }
//        };
//
//        EgnyteException parsed = EgnyteException.parse(response);
//        Assert.assertEquals(500, parsed.getCode());
//        Assert.assertEquals("Unknown error", parsed.getApiExceptionMessage());
//        Assert.assertEquals("Unknown error", parsed.getMessage());
//    }

}