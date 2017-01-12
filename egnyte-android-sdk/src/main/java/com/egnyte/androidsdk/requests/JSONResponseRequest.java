package com.egnyte.androidsdk.requests;

import com.egnyte.androidsdk.apiclient.egnyte.RequestBody;
import com.egnyte.androidsdk.apiclient.egnyte.body.ResponseBodyParser;
import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;
import com.egnyte.androidsdk.apiclient.egnyte.client.BaseRequest;
import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is base class for requests expecting JSON in response body
 * @param <Result>
 */
public abstract class JSONResponseRequest<Result> extends BaseRequest<Result> {

    /**
     * @param method HTTP method
     * @param path API path
     * @param cloudPath path in Egnyte cloud, may be null
     * @param queryParams query parameters, may be null
     * @param headers headers, may be null
     * @param body body represented by {@link RequestBody}, may be null
     */
    public JSONResponseRequest(String method, String path, String cloudPath, HashMap<String, Object> queryParams, HashMap<String, String> headers, RequestBody body) {
        super(method, path, cloudPath, queryParams, headers, body);
    }

    @Override
    protected Result parseResponseBody(InputStream inputStream, CancelledState cancelledState, Map<String, List<String>> headers) throws ResponseParsingException {
        try {
            return parseJsonResponseBody(ResponseBodyParser.parseJSONObject(inputStream, cancelledState));
        } catch (Exception e) {
            throw new ResponseParsingException(e);
        }
    }

    protected abstract Result parseJsonResponseBody(JSONObject jsonObject) throws JSONException, ResponseParsingException;
}
