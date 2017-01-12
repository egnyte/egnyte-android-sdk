package com.egnyte.androidsdk.apiclient.egnyte.body;

import com.egnyte.androidsdk.apiclient.egnyte.cancellation.CancelledState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This is helper class for parsing {@link InputStream} into something more convenient
 */
public class ResponseBodyParser {

    private ResponseBodyParser() {
    }

    private static final String DEFAULT_ERROR_MESSAGE = "Unknown error";

    public static String parseErrorMessage(InputStream inputStream, CancelledState cancelledState) {
        if (inputStream == null) {
            return DEFAULT_ERROR_MESSAGE;
        }
        try {
            String parsedString = parseString(inputStream, cancelledState);
            try {
                JSONObject jsonObject = new JSONObject(parsedString);
                if (jsonObject.has("errorMessage")) {
                    return jsonObject.getString("errorMessage");
                } else if (jsonObject.has("message")) {
                    return jsonObject.getString("message");
                }
            } catch (JSONException ignore) {
            }
            return parsedString == null || parsedString.isEmpty() ? DEFAULT_ERROR_MESSAGE : parsedString;
        } catch (IOException e) {
            return DEFAULT_ERROR_MESSAGE;
        }
    }

    /**
     * Parses given {@link InputStream} into {@link JSONObject}
     *
     * @param inputStream    {@link InputStream} to parse
     * @param cancelledState {@link CancelledState} that controlls if process of parsing should be cancelled
     * @return parsed {@link JSONObject}
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject parseJSONObject(InputStream inputStream, CancelledState cancelledState) throws IOException, JSONException {
        return new JSONObject(parseString(inputStream, cancelledState));
    }

    /**
     * Parses given {@link InputStream} into {@link String}
     *
     * @param inputStream    {@link InputStream} to parse
     * @param cancelledState {@link CancelledState} that controlls if process of parsing should be cancelled
     * @return parsed {@link String}
     * @throws IOException
     */
    public static String parseString(InputStream inputStream, CancelledState cancelledState) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];
        int n;
        while ((n = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, n);
            cancelledState.throwIfCancelled();
        }
        return baos.toString();
    }
}

