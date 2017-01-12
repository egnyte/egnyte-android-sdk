package com.egnyte.androidsdk.apiclient.egnyte.body;

import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents application/json request body
 */
public class JSONBody implements RequestBody {

    private final JSONObject json;

    public JSONBody(JSONObject json) {
        this.json = json;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public void writeContent(CancelledState cancelledState, OutputStream os) throws IOException {
        os.write(json.toString().getBytes());
    }
}