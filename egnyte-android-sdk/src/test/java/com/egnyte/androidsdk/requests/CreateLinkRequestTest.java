package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.client.TestApiClient;
import com.egnyte.androidsdk.apiclient.egnyte.client.URLConnectionFactory;
import com.egnyte.androidsdk.entities.CreateLinkResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class CreateLinkRequestTest {

    private byte[] response;
    private ByteArrayOutputStream sink;
    private TestApiClient client;
    private ArrayList<String> recipients;
    private Date date;

    @Before
    public void setup() throws MalformedURLException {
        response = null;
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
                    public OutputStream getOutputStream() throws IOException {
                        return sink;
                    }
                };
            }
        });

        recipients = new ArrayList<>();
        recipients.add("5b140187-0d79-435a-b1db-23e206556323@a1ed3146-15c0-4dbd-9176-ffbd34857b8f.e8326301-f6ef-4b2b-b7fb-eb1c658acfc1");
        recipients.add("6ed248b6-b7da-49e8-b186-e90de2020632@2ea5497d-2234-4e3d-b438-351826dfe470.1a036745-3945-4fbf-9fb0-af161a48a450");

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.set(2091, 1, 21, 0, 0, 0);
        date = calendar.getTime();
    }


    @Test
    public void testFolderLinkBasic() throws IOException, JSONException {
        response = "{\"links\":[{\"id\":\"RTUvMMoenv\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/fl\\/RTUvMMoenv\",\"recipients\":[]}],\"path\":\"\\/Shared\\/Create Link Test\\/Folder\",\"type\":\"folder\",\"accessibility\":\"anyone\",\"notify\":false,\"link_to_current\":false,\"creation_date\":\"2016-11-18\",\"send_mail\":false,\"copy_me\":false}".getBytes();
        CreateLinkRequest request = new CreateFolderLinkRequestBuilder
                ("/Shared/Create Link Test/Folder", CreateLinkRequest.Accessibility.ANYONE)
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/Folder", body.getString("path"));
        assertEquals("anyone", body.getString("accessibility"));
        assertEquals("folder", body.getString("type"));

        assertEquals(1, result.links.size());
        assertEquals("/Shared/Create Link Test/Folder", result.path);
        assertEquals(CreateLinkRequest.Type.FOLDER, result.type);
        assertEquals(CreateLinkRequest.Accessibility.ANYONE, result.accessibility);
        assertEquals(false, result.notify);
        assertEquals(false, result.linkToCurrent);
        assertEquals(false, result.sendMail);
        assertEquals(false, result.copyMe);
        assertNotNull(result.creationDate);
        assertNull(result.expiryClicks);
        assertNull(result.expiryDate);
        assertNull(result.password);
    }

    @Test
    public void testFolderLinkComplex() throws IOException, JSONException {
        response = "{\"links\":[{\"id\":\"2FCxpGP63o\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/fl\\/2FCxpGP63o\",\"recipients\":[\"5b140187-0d79-435a-b1db-23e206556323@a1ed3146-15c0-4dbd-9176-ffbd34857b8f.e8326301-f6ef-4b2b-b7fb-eb1c658acfc1\"]},{\"id\":\"1BQEc2nEf3\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/fl\\/1BQEc2nEf3\",\"recipients\":[\"6ed248b6-b7da-49e8-b186-e90de2020632@2ea5497d-2234-4e3d-b438-351826dfe470.1a036745-3945-4fbf-9fb0-af161a48a450\"]}],\"path\":\"\\/Shared\\/Create Link Test\\/Folder\",\"type\":\"folder\",\"accessibility\":\"password\",\"notify\":true,\"password\":\"vgWZo25k\",\"link_to_current\":false,\"expiry_clicks\":10,\"creation_date\":\"2016-11-18\",\"send_mail\":true,\"copy_me\":true}".getBytes();
        CreateLinkRequest request = new CreateFolderLinkRequestBuilder
                ("/Shared/Create Link Test/Folder", CreateLinkRequest.Accessibility.PASSWORD)
                .setExpiration(CreateLinkRequestBuilder.Expiration.byClicks(10))
                .setNotify(true)
                .setEmailLink(new CreateLinkRequestBuilder.EmailLink(
                        recipients, "Hello", true
                ))
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/Folder", body.getString("path"));
        assertEquals("password", body.getString("accessibility"));
        assertEquals("folder", body.getString("type"));
        assertEquals(true, body.getBoolean("notify"));
        assertEquals(true, body.getBoolean("send_email"));
        assertEquals("Hello", body.getString("message"));
        assertEquals(true, body.getBoolean("copy_me"));
        assertEquals(recipients.get(0), body.getJSONArray("recipients").get(0));
        assertEquals(recipients.get(1), body.getJSONArray("recipients").get(1));

        assertEquals(2, result.links.size());
        assertEquals("/Shared/Create Link Test/Folder", result.path);
        assertEquals(CreateLinkRequest.Type.FOLDER, result.type);
        assertEquals(CreateLinkRequest.Accessibility.PASSWORD, result.accessibility);
        assertEquals(true, result.notify);
        assertEquals(false, result.linkToCurrent);
        assertEquals(true, result.sendMail);
        assertEquals(true, result.copyMe);
        assertNotNull(result.creationDate);
        assertEquals(Integer.valueOf(10), result.expiryClicks);
        assertNull(result.expiryDate);
        assertNotNull(result.password);
    }

    @Test
    public void testUploadLinkBasic() throws IOException, JSONException {
        response = "{\"links\":[{\"id\":\"AI9T6ZPzBb\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/ul\\/AI9T6ZPzBb\",\"recipients\":[]}],\"path\":\"\\/Shared\\/Create Link Test\\/Upload\",\"type\":\"upload\",\"notify\":false,\"link_to_current\":false,\"creation_date\":\"2016-11-18\",\"send_mail\":false,\"copy_me\":false}".getBytes();
        CreateLinkRequest request = new CreateUploadLinkRequestBuilder
                ("/Shared/Create Link Test/Upload")
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/Upload", body.getString("path"));
        assertEquals(null, body.optString("accessibility", null));
        assertEquals("upload", body.getString("type"));

        assertEquals(1, result.links.size());
        assertEquals("/Shared/Create Link Test/Upload", result.path);
        assertEquals(CreateLinkRequest.Type.UPLOAD, result.type);
        assertNull(result.accessibility);
        assertEquals(false, result.notify);
        assertEquals(false, result.linkToCurrent);
        assertEquals(false, result.sendMail);
        assertEquals(false, result.copyMe);
        assertNotNull(result.creationDate);
        assertNull(result.expiryClicks);
        assertNull(result.expiryDate);
        assertNull(result.password);
    }

    @Test
    public void testUploadLinkComplex() throws IOException, ParseException, JSONException {
        response = "{\"links\":[{\"id\":\"tDZv6I9484\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/ul\\/tDZv6I9484\",\"recipients\":[\"5b140187-0d79-435a-b1db-23e206556323@a1ed3146-15c0-4dbd-9176-ffbd34857b8f.e8326301-f6ef-4b2b-b7fb-eb1c658acfc1\"]},{\"id\":\"kUO6EdI8Dn\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/ul\\/kUO6EdI8Dn\",\"recipients\":[\"6ed248b6-b7da-49e8-b186-e90de2020632@2ea5497d-2234-4e3d-b438-351826dfe470.1a036745-3945-4fbf-9fb0-af161a48a450\"]}],\"path\":\"\\/Shared\\/Create Link Test\\/Upload\",\"type\":\"upload\",\"notify\":true,\"link_to_current\":false,\"expiry_date\":\"2091-02-21\",\"creation_date\":\"2016-11-18\",\"send_mail\":true,\"copy_me\":true}".getBytes();
        CreateLinkRequest request = new CreateUploadLinkRequestBuilder
                ("/Shared/Create Link Test/Upload")
                .setExpiration(CreateLinkRequestBuilder.Expiration.byDate(date))
                .setNotify(true)
                .setEmailLink(new CreateLinkRequestBuilder.EmailLink(
                        recipients, "Hello", true
                ))
                .setFolderPerReceipent(true)
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/Upload", body.getString("path"));
        assertEquals(null, body.optString("accessibility", null));
        assertEquals("upload", body.getString("type"));
        assertEquals(true, body.getBoolean("notify"));
        assertEquals(true, body.getBoolean("send_email"));
        assertEquals("Hello", body.getString("message"));
        assertEquals(true, body.getBoolean("copy_me"));
        assertEquals(recipients.get(0), body.getJSONArray("recipients").get(0));
        assertEquals(recipients.get(1), body.getJSONArray("recipients").get(1));
        assertEquals(true, body.getBoolean("folder_per_recipient"));

        assertEquals(2, result.links.size());
        assertEquals("/Shared/Create Link Test/Upload", result.path);
        assertEquals(CreateLinkRequest.Type.UPLOAD, result.type);
        assertNull(result.accessibility);
        assertEquals(true, result.notify);
        assertEquals(false, result.linkToCurrent);
        assertEquals(true, result.sendMail);
        assertEquals(true, result.copyMe);
        assertNotNull(result.creationDate);
        assertNull(result.expiryClicks);
        assertEquals("2091-02-21", result.expiryDate);
        assertNull(result.password);
    }

    @Test
    public void testFileLinkBasic() throws IOException, JSONException {
        response = "{\"links\":[{\"id\":\"uBPyJm23xk\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/dl\\/uBPyJm23xk\",\"recipients\":[]}],\"path\":\"\\/Shared\\/Create Link Test\\/File.txt\",\"type\":\"file\",\"accessibility\":\"anyone\",\"notify\":false,\"link_to_current\":false,\"creation_date\":\"2016-11-18\",\"send_mail\":false,\"copy_me\":false}".getBytes();
        CreateLinkRequest request = new CreateFileLinkRequestBuilder
                ("/Shared/Create Link Test/File.txt", CreateLinkRequest.Accessibility.ANYONE)
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/File.txt", body.getString("path"));
        assertEquals("anyone", body.optString("accessibility"));
        assertEquals("file", body.getString("type"));

        assertEquals(1, result.links.size());
        assertEquals("/Shared/Create Link Test/File.txt", result.path);
        assertEquals(CreateLinkRequest.Type.FILE, result.type);
        assertEquals(CreateLinkRequest.Accessibility.ANYONE, result.accessibility);
        assertEquals(false, result.notify);
        assertEquals(false, result.linkToCurrent);
        assertEquals(false, result.sendMail);
        assertEquals(false, result.copyMe);
        assertNotNull(result.creationDate);
        assertNull(result.expiryClicks);
        assertNull(result.expiryDate);
        assertNull(result.password);
    }

    @Test
    public void testFileLinkComplex() throws IOException, ParseException, JSONException {
        response = "{\"links\":[{\"id\":\"8OUc9NPgbO\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/dl\\/8OUc9NPgbO\\/File.txt_\",\"recipients\":[\"5b140187-0d79-435a-b1db-23e206556323@a1ed3146-15c0-4dbd-9176-ffbd34857b8f.e8326301-f6ef-4b2b-b7fb-eb1c658acfc1\"]},{\"id\":\"ywLIBktIRt\",\"url\":\"https:\\/\\/mobilesdk.qa-egnyte.com\\/dl\\/ywLIBktIRt\\/File.txt_\",\"recipients\":[\"6ed248b6-b7da-49e8-b186-e90de2020632@2ea5497d-2234-4e3d-b438-351826dfe470.1a036745-3945-4fbf-9fb0-af161a48a450\"]}],\"path\":\"\\/Shared\\/Create Link Test\\/File.txt\",\"type\":\"file\",\"accessibility\":\"password\",\"notify\":true,\"password\":\"EK68XMcu\",\"link_to_current\":true,\"expiry_date\":\"2091-02-21\",\"creation_date\":\"2016-11-18\",\"send_mail\":true,\"copy_me\":true}".getBytes();
        CreateLinkRequest request = new CreateFileLinkRequestBuilder
                ("/Shared/Create Link Test/File.txt", CreateLinkRequest.Accessibility.PASSWORD)
                .setExpiration(CreateLinkRequestBuilder.Expiration.byDate(date))
                .setNotify(true)
                .setEmailLink(new CreateLinkRequestBuilder.EmailLink(
                        recipients, "Hello", true
                ))
                .setLinkToCurrent(true)
                .setProtection(CreateLinkRequest.Protection.PREVIEW)
                .setAddFileName(true)
                .build();
        CreateLinkResult result = client.execute(request);

        JSONObject body = new JSONObject(sink.toString());
        assertEquals("/Shared/Create Link Test/File.txt", body.getString("path"));
        assertEquals("password", body.optString("accessibility"));
        assertEquals("file", body.getString("type"));
        assertEquals(true, body.getBoolean("notify"));
        assertEquals(true, body.getBoolean("send_email"));
        assertEquals("Hello", body.getString("message"));
        assertEquals(true, body.getBoolean("copy_me"));
        assertEquals(recipients.get(0), body.getJSONArray("recipients").get(0));
        assertEquals(recipients.get(1), body.getJSONArray("recipients").get(1));
        assertEquals(true, body.getBoolean("link_to_current"));
        assertEquals("PREVIEW", body.getString("protection"));
        assertEquals(true, body.getBoolean("add_file_name"));

        assertEquals(2, result.links.size());
        assertEquals("/Shared/Create Link Test/File.txt", result.path);
        assertEquals(CreateLinkRequest.Type.FILE, result.type);
        assertEquals(CreateLinkRequest.Accessibility.PASSWORD, result.accessibility);
        assertEquals(true, result.notify);
        assertEquals(true, result.linkToCurrent);
        assertEquals(true, result.sendMail);
        assertEquals(true, result.copyMe);
        assertNotNull(result.creationDate);
        assertNull(result.expiryClicks);
        assertEquals("2091-02-21", result.expiryDate);
        assertNotNull(result.password);
    }

}