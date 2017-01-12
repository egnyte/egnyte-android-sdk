package com.egnyte.androidsdk.apiclient.egnyte;

import com.egnyte.androidsdk.apiclient.egnyte.exceptions.ResponseParsingException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for parsing {@link JSONObject} into given class
 * @param <T> result of parsing
 */
public interface JSONParser<T> {

    T parse(JSONObject jsonObject) throws JSONException, ResponseParsingException;
}
